/*
ACR-57bf50f00d284f8e85f267bcc7777503
ACR-6a189ea84ed44df2b29d6edddf71e5ac
ACR-66cd6b6b91b74bfe8e42603aec138ba9
ACR-15b7db809fba4c18acc0c9704828ce56
ACR-20ebd59e0cc140a28fd4b2b4da309ff7
ACR-d73a6c663d6e4fb3a51aa0dcc89abdf9
ACR-d8e06eaf6d4b464b95ed5d4b4c69265e
ACR-546c6dd7decb43babae262f609fb97aa
ACR-ef83d6ab03e04c509bee65252a89194b
ACR-8f2e90ae129447c89250aceb50cb9ba2
ACR-efe9108e0076403f8b265e58a65b16e9
ACR-eaed5720510c4036a1b5b33cf13b953e
ACR-de74c746706e4f3fb94c75da4618ee46
ACR-c3a8ab59208e414ba52a82e4288c6c01
ACR-78a78f6d2ccd48d0ae2a22b5a5cf0c65
ACR-8dce755eba6d4fa7932090e013f178aa
ACR-7330f32ea968480b85bf6fe3f8e6a35f
 */
package org.sonarsource.sonarlint.core.fs;

import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.ServerFileExclusions;
import org.sonarsource.sonarlint.core.analysis.api.TriggerType;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;
import org.sonarsource.sonarlint.core.commons.SmartCancelableLoadingCache;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FileUtils;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.file.WindowsShortcutUtils;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.FileStatusDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetFileExclusionsParams;
import org.sonarsource.sonarlint.core.serverconnection.AnalyzerConfiguration;
import org.sonarsource.sonarlint.core.serverconnection.IssueStorePaths;
import org.sonarsource.sonarlint.core.serverconnection.SonarServerSettingsChangedEvent;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.event.EventListener;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.commons.util.git.GitService.createSonarLintGitIgnore;

public class FileExclusionService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  //ACR-43a39f01099f4842b4d55d906ceda7a0
  private static final long MAX_AUTO_ANALYSIS_FILE_SIZE_BYTES = 5L * 1024 * 1024;

  //ACR-498815f9804e4b868b9ff3eb6f6b19bd
  private static final Set<String> ALL_EXCLUSION_RELATED_SETTINGS = Set.of(
    CoreProperties.PROJECT_INCLUSIONS_PROPERTY,
    CoreProperties.PROJECT_TEST_INCLUSIONS_PROPERTY,
    CoreProperties.GLOBAL_EXCLUSIONS_PROPERTY,
    CoreProperties.PROJECT_EXCLUSIONS_PROPERTY,
    CoreProperties.GLOBAL_TEST_EXCLUSIONS_PROPERTY,
    CoreProperties.PROJECT_TEST_EXCLUSIONS_PROPERTY);

  private final ConfigurationRepository configRepo;
  private final StorageService storageService;
  private final PathTranslationService pathTranslationService;
  private final ClientFileSystemService clientFileSystemService;
  private final SonarLintRpcClient client;

  private final SmartCancelableLoadingCache<URI, Boolean> serverExclusionByUriCache = new SmartCancelableLoadingCache<>("sonarlint-file-exclusions", this::computeIfExcluded,
    (key, oldValue, newValue) -> {});

  public FileExclusionService(ConfigurationRepository configRepo, StorageService storageService, PathTranslationService pathTranslationService,
    ClientFileSystemService clientFileSystemService, SonarLintRpcClient client) {
    this.configRepo = configRepo;
    this.storageService = storageService;
    this.pathTranslationService = pathTranslationService;
    this.clientFileSystemService = clientFileSystemService;
    this.client = client;
  }

  public boolean computeIfExcluded(URI fileUri, SonarLintCancelMonitor cancelMonitor) {
    LOG.debug("Computing file exclusion for uri '{}'", fileUri);
    var clientFile = clientFileSystemService.getClientFile(fileUri);
    if (clientFile == null) {
      LOG.debug("Unable to find client file for uri {}", fileUri);
      return false;
    }
    var configScope = clientFile.getConfigScopeId();
    var effectiveBindingOpt = configRepo.getEffectiveBinding(configScope);
    if (effectiveBindingOpt.isEmpty()) {
      return false;
    }
    var analyzerStorage = storageService.connection(effectiveBindingOpt.get().connectionId())
      .project(effectiveBindingOpt.get().sonarProjectKey())
      .analyzerConfiguration();
    if (!analyzerStorage.isValid()) {
      LOG.warn("Unable to read settings in local storage, analysis storage is not ready");
      return false;
    }
    AnalyzerConfiguration analyzerConfig;
    try {
      analyzerConfig = analyzerStorage.read();
    } catch (StorageException e) {
      LOG.debug("Unable to read settings in local storage", e);
      return false;
    }
    var settings = new MapSettings(analyzerConfig.getSettings().getAll());
    var exclusionFilters = new ServerFileExclusions(settings.asConfig());
    exclusionFilters.prepare();
    var idePath = clientFile.getClientRelativePath();
    var pathTranslation = pathTranslationService.getOrComputePathTranslation(configScope);
    Path serverPath;
    if (pathTranslation.isPresent()) {
      serverPath = IssueStorePaths.idePathToServerPath(pathTranslation.get().getIdePathPrefix(), pathTranslation.get().getServerPathPrefix(), idePath);
      if (serverPath == null) {
        //ACR-c9d835ea8e7a4fcdaf63469735021803
        serverPath = idePath;
      }
    } else {
      serverPath = idePath;
    }
    var type = clientFile.isTest() ? InputFile.Type.TEST : InputFile.Type.MAIN;
    var result = !exclusionFilters.accept(serverPath.toString(), type);
    LOG.debug("File exclusion for uri '{}' is {}", fileUri, result);
    return result;
  }

  @EventListener
  public void onBindingChanged(BindingConfigChangedEvent event) {
    if (event.newConfig().isBound()) {
      var connectionId = requireNonNull(event.newConfig().connectionId());
      var projectKey = requireNonNull(event.newConfig().sonarProjectKey());
      //ACR-c9d52c801191470d914568574d9b4205
      if (storageService.connection(connectionId).project(projectKey).analyzerConfiguration().isValid()) {
        LOG.debug("Binding changed for config scope '{}', recompute file exclusions...", event.configScopeId());
        clientFileSystemService.getFiles(event.configScopeId()).forEach(f -> serverExclusionByUriCache.refreshAsync(f.getUri()));
      }
    } else {
      LOG.debug("Binding removed for config scope '{}', clearing file exclusions...", event.configScopeId());
      clientFileSystemService.getFiles(event.configScopeId()).forEach(f -> serverExclusionByUriCache.clear(f.getUri()));
    }
  }

  @EventListener
  public void onFileSystemUpdated(FileSystemUpdatedEvent event) {
    event.getRemoved().forEach(f -> serverExclusionByUriCache.clear(f.getUri()));
    //ACR-26465a4a88744ecaba009211e1dcb31e
    //ACR-6628c24330f841b1aaea28736e2de9ae
    Stream.concat(event.getAdded().stream(), event.getUpdated().stream())
      .forEach(f -> serverExclusionByUriCache.refreshAsync(f.getUri()));
  }

  @EventListener
  public void onFileExclusionSettingsChanged(SonarServerSettingsChangedEvent event) {
    var settingsDiff = event.getUpdatedSettingsValueByKey();
    if (isFileExclusionSettingsDifferent(settingsDiff)) {
      LOG.debug("File exclusion settings changed, recompute all file exclusions...");
      event.getConfigScopeIds().forEach(configScopeId -> clientFileSystemService.getFiles(configScopeId)
        .forEach(f -> serverExclusionByUriCache.refreshAsync(f.getUri())));
    }
  }

  private static boolean isFileExclusionSettingsDifferent(Map<String, String> updatedSettingsValueByKey) {
    return ALL_EXCLUSION_RELATED_SETTINGS.stream().anyMatch(updatedSettingsValueByKey::containsKey);
  }

  public Map<URI, FileStatusDto> getFilesStatus(Map<String, List<URI>> fileUrisByConfigScope) {
    var result = new HashMap<URI, FileStatusDto>();
    for (var entry : fileUrisByConfigScope.entrySet()) {
      var configScopeId = entry.getKey();
      var baseDir = clientFileSystemService.getBaseDir(configScopeId);
      var files = new HashSet<>(entry.getValue());
      var filteredFileUris = filterOutExcludedFiles(configScopeId, baseDir, files).stream().map(ClientFile::getUri).collect(Collectors.toSet());
      files.forEach(uri -> result.put(uri, new FileStatusDto(!filteredFileUris.contains(uri))));
    }
    return result;
  }

  public boolean isExcludedFromServer(URI fileUri) {
    return Boolean.TRUE.equals(serverExclusionByUriCache.get(fileUri));
  }

  public List<ClientFile> refineAnalysisScope(String configScopeId, Set<URI> requestedFileUris, TriggerType triggerType, Path baseDir) {
    if (!triggerType.shouldHonorExclusions()) {
      var filteredURIsNoFile = new ArrayList<URI>();
      var filesToAnalyze = requestedFileUris.stream().map(uri -> {
        var file = findFile(configScopeId, uri);
        if (file == null) {
          filteredURIsNoFile.add(uri);
        }
        return file;
      })
        .filter(Objects::nonNull)
        .toList();
      logFilteredURIs("Filtered out URIs having no file", filteredURIsNoFile);
      return filesToAnalyze;
    }
    return filterOutExcludedFiles(configScopeId, baseDir, requestedFileUris);
  }

  private List<ClientFile> filterOutExcludedFiles(String configurationScopeId, Path baseDir, Set<URI> files) {
    var sonarLintGitIgnore = createSonarLintGitIgnore(baseDir);
    //ACR-bc4a0c672cf6480ba7068b7f208be39f
    var filteredURIsFromServerExclusionService = new ArrayList<URI>();
    var filteredURIsFromGitIgnore = new ArrayList<URI>();
    var filteredURIsNotUserDefined = new ArrayList<URI>();
    var filteredURIsFromSymbolicLink = new ArrayList<URI>();
    var filteredURIsFromWindowsShortcut = new ArrayList<URI>();
    var filteredURIsNoFile = new ArrayList<URI>();
    var filteredURIsTooLarge = new ArrayList<URI>();

    var filesToExclude = files;

    if (configRepo.getEffectiveBinding(configurationScopeId).isEmpty()) {
      //ACR-96179fd83e644c2a98748b97f746f6f5
      filesToExclude = filterOutClientExcludedFiles(configurationScopeId, files);
    }

    //ACR-0352fe0def9b4e8ab1057b214e9b0b2b
    var actualFilesToAnalyze = filesToExclude
      .stream()
      .map(uri -> {
        var file = findFile(configurationScopeId, uri);
        if (file == null) {
          filteredURIsNoFile.add(uri);
        }
        return file;
      })
      .filter(Objects::nonNull)
      .filter(file -> {
        if (isExcludedFromServer(file.getUri())) {
          filteredURIsFromServerExclusionService.add(file.getUri());
          return false;
        }
        return true;
      })
      .filter(file -> {
        if (sonarLintGitIgnore.isFileIgnored(file.getClientRelativePath())) {
          filteredURIsFromGitIgnore.add(file.getUri());
          return false;
        }
        return true;
      })
      .filter(file -> {
        if (!file.isUserDefined()) {
          filteredURIsNotUserDefined.add(file.getUri());
          return false;
        }
        return true;
      })
      .filter(file -> {
        try {
          if (file.isLargerThan(MAX_AUTO_ANALYSIS_FILE_SIZE_BYTES)) {
            filteredURIsTooLarge.add(file.getUri());
            return false;
          }
        } catch (Exception e) {
          //ACR-7b78cabeba804a7d86fdf552bbf68dbe
        }
        return true;
      })
      .filter(file -> {
        //ACR-2cc2f91e4d594e10b4481870ba350310
        //ACR-3d8e57ee391b4e3799f7ea95efea13ed
        //ACR-bba57a2bc29e4f02a61881a531f02ac2
        //ACR-d6458f8518fb4ceba56cf40fe502baa9
        try {
          var uri = file.getUri();
          if (Files.isSymbolicLink(FileUtils.getFilePathFromUri(uri))) {
            filteredURIsFromSymbolicLink.add(uri);
            return false;
          } else if (WindowsShortcutUtils.isWindowsShortcut(uri)) {
            filteredURIsFromWindowsShortcut.add(uri);
            return false;
          }
          return true;
        } catch (FileSystemNotFoundException err) {
          LOG.debug("Checking for symbolic links or Windows shortcuts in the file system is not possible for the URI '" + file
            + "'. Therefore skipping the checks due to the underlying protocol / its scheme.", err);
          return true;
        }
      })
      .toList();

    //ACR-95c2d69dc07b4fb5ba445b08f573e72a
    logFilteredURIs("Filtered out URIs based on the server exclusion service", filteredURIsFromServerExclusionService);
    logFilteredURIs("Filtered out URIs ignored by Git", filteredURIsFromGitIgnore);
    logFilteredURIs("Filtered out URIs not user-defined", filteredURIsNotUserDefined);
    logFilteredURIs("Filtered out URIs exceeding max allowed size", filteredURIsTooLarge);
    logFilteredURIs("Filtered out URIs that are symbolic links", filteredURIsFromSymbolicLink);
    logFilteredURIs("Filtered out URIs that are Windows shortcuts", filteredURIsFromWindowsShortcut);
    logFilteredURIs("Filtered out URIs having no file", filteredURIsNoFile);

    return actualFilesToAnalyze;
  }

  @CheckForNull
  private ClientFile findFile(String configScopeId, URI fileUriToAnalyze) {
    var clientFile = clientFileSystemService.getClientFiles(configScopeId, fileUriToAnalyze);
    if (clientFile == null) {
      LOG.error("File to analyze was not found in the file system: {}", fileUriToAnalyze);
      return null;
    }
    return clientFile;
  }

  private void logFilteredURIs(String reason, ArrayList<URI> uris) {
    if (!uris.isEmpty()) {
      SonarLintLogger.get().debug(reason + ": " + String.join(", ", uris.stream().map(Object::toString).toList()));
    }
  }

  private Set<URI> filterOutClientExcludedFiles(String configurationScopeId, Set<URI> files) {
    var fileExclusionsGlobPatterns = getClientFileExclusionPatterns(configurationScopeId);
    var matchers = parseGlobPatterns(fileExclusionsGlobPatterns);
    Predicate<URI> fileExclusionFilter = uri -> matchers.stream().noneMatch(matcher -> matcher.matches(Paths.get(uri)));

    return files.stream()
      .filter(fileExclusionFilter)
      .collect(Collectors.toSet());
  }

  private Set<String> getClientFileExclusionPatterns(String configurationScopeId) {
    try {
      return client.getFileExclusions(new GetFileExclusionsParams(configurationScopeId)).join().getFileExclusionPatterns();
    } catch (Exception e) {
      LOG.error("Error when requesting the file exclusions", e);
      return Collections.emptySet();
    }
  }

  private static List<PathMatcher> parseGlobPatterns(Set<String> globPatterns) {
    var fs = FileSystems.getDefault();

    List<PathMatcher> parsedMatchers = new ArrayList<>(globPatterns.size());
    for (String pattern : globPatterns) {
      try {
        parsedMatchers.add(fs.getPathMatcher("glob:" + pattern));
      } catch (Exception e) {
        //ACR-cc2992cdde86495ba6e494f8bdc936e1
      }
    }
    return parsedMatchers;
  }
}
