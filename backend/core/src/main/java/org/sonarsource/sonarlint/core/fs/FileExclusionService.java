/*
ACR-9efda97b048344feacccab6b70309102
ACR-b0ca99d2e1ff471a9b5ade42cea185ca
ACR-972c73fc85b74076ab8da98b0333f93f
ACR-0b4658cf139b430db22b246d19a8225e
ACR-70be12afb67e40a7b012f81d8fa299cd
ACR-979fa16dda404f7f84c00af92e0a9a6a
ACR-30412287065643af86a4863547e9db1e
ACR-372787b66b66449a985885859c4db476
ACR-bf943ca4dddb42ab9584c77641059562
ACR-5208fc3481d64e30ba99345e9b90448f
ACR-34a4c72bb7394b3888ce8037c88ea309
ACR-dcdeb6c1fa6d4150a2f87d49c82b9dcc
ACR-74187b22a08a4630bbd9319228eaf584
ACR-503cb0c67cdf4908b291547f781df99e
ACR-51f1a8403cb04443a0426fe3de0db711
ACR-fd2cdc9894b54c49b2ead030c9f72c27
ACR-3dbaa567e76d486a9cec6e37e86d3464
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
  //ACR-3fb23fed8dc54bbb9ddf719f5f20b85c
  private static final long MAX_AUTO_ANALYSIS_FILE_SIZE_BYTES = 5L * 1024 * 1024;

  //ACR-246a2a2746124508bb05df2178d89e1f
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
        //ACR-cdd4713a62714733a87a59c8e1e054f7
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
      //ACR-dfa5cd8f6d5447d682bfe782a3394b12
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
    //ACR-fe7fcd983a234527a7ee5e1f1eb2636e
    //ACR-2d1b2d189c64404e95c8512cd86303f9
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
    //ACR-7ea57112ab9e47d9b69916fcb8e4c107
    var filteredURIsFromServerExclusionService = new ArrayList<URI>();
    var filteredURIsFromGitIgnore = new ArrayList<URI>();
    var filteredURIsNotUserDefined = new ArrayList<URI>();
    var filteredURIsFromSymbolicLink = new ArrayList<URI>();
    var filteredURIsFromWindowsShortcut = new ArrayList<URI>();
    var filteredURIsNoFile = new ArrayList<URI>();
    var filteredURIsTooLarge = new ArrayList<URI>();

    var filesToExclude = files;

    if (configRepo.getEffectiveBinding(configurationScopeId).isEmpty()) {
      //ACR-8a1721e0a64d4b018bfff0a716dd4359
      filesToExclude = filterOutClientExcludedFiles(configurationScopeId, files);
    }

    //ACR-8115d80f22884ef0a6b088a76d1b0c2a
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
          //ACR-73b2e9a80c4441caa55cd48d7ea33eed
        }
        return true;
      })
      .filter(file -> {
        //ACR-0e711605bd2044eb813f5fbd70b58da2
        //ACR-23a880ae96da48da95330975ac109564
        //ACR-1a76eeaa45924c23aa4fc524571dda62
        //ACR-cd09c82ff6704bb6a6e5257396c1fc98
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

    //ACR-c4b2389b555c4938ac40faf3360b28df
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
        //ACR-cf8e37bd58334f7caf4a4e937a5beef8
      }
    }
    return parsedMatchers;
  }
}
