/*
ACR-b51dbf44e6754f6db4a289f990709fd1
ACR-d9e60e57f8234cc086bbe328c75395c9
ACR-918dc84e14054d779df276c439a188ae
ACR-d3a2d6ca47df4252a3d0d6be505be658
ACR-34cf3ec625dc47ffbcc19dbf600afec0
ACR-34163074135443a0a39bc06e4e295717
ACR-ba1dafd5a7e948828c57a7fe72963d50
ACR-9b5c9a46fb7f4ef2a8edfa6cdfd13211
ACR-0327cf9bf1614ed5ab5de10372dd0ecd
ACR-fa7077a48bc34833bf0b2f75c63277a0
ACR-c1d3b0fd435943ce8343a456edb25b13
ACR-371798e648154c53974efc7e90e617b2
ACR-154475b03b3d423489d6d09d6cf6af97
ACR-c99be4f73a324976806e289c46db9a62
ACR-d066bcf75a1f4710ab139a028f033f40
ACR-debb00bad7114d8a8451bb26144c7a81
ACR-90c6a29ad8654186a7bca1a8193ae9d4
 */
package org.sonarsource.sonarlint.core.fs;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import org.sonarsource.sonarlint.core.commons.SmartCancelableLoadingCache;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.GetBaseDirParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.ListFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public class ClientFileSystemService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SonarLintRpcClient rpcClient;
  private final ApplicationEventPublisher eventPublisher;
  private final Map<URI, ClientFile> filesByUri = new ConcurrentHashMap<>();
  private final Map<String, Path> baseDirPerConfigScopeId = new ConcurrentHashMap<>();
  private final OpenFilesRepository openFilesRepository;
  private final TelemetryService telemetryService;
  private final SmartCancelableLoadingCache<String, Map<URI, ClientFile>> filesByConfigScopeIdCache =
    new SmartCancelableLoadingCache<>("sonarlint-filesystem", this::initializeFileSystem);

  public ClientFileSystemService(SonarLintRpcClient rpcClient, ApplicationEventPublisher eventPublisher, OpenFilesRepository openFilesRepository,
    TelemetryService telemetryService) {
    this.rpcClient = rpcClient;
    this.eventPublisher = eventPublisher;
    this.openFilesRepository = openFilesRepository;
    this.telemetryService = telemetryService;
  }

  public List<ClientFile> getFiles(String configScopeId) {
    return List.copyOf(filesByConfigScopeIdCache.get(configScopeId).values());
  }

  private static ClientFile fromDto(ClientFileDto clientFileDto) {
    var charset = charsetFromDto(clientFileDto.getCharset());
    var forcedLanguage = clientFileDto.getDetectedLanguage();
    var forcedSonarLanguage = forcedLanguage == null ? null : SonarLanguage.valueOf(forcedLanguage.name());
    var file = new ClientFile(clientFileDto.getUri(), clientFileDto.getConfigScopeId(),
      clientFileDto.getIdeRelativePath(),
      clientFileDto.isTest(),
      charset,
      clientFileDto.getFsPath(),
      forcedSonarLanguage,
      clientFileDto.isUserDefined());
    if (clientFileDto.getContent() != null) {
      file.setDirty(clientFileDto.getContent());
    }
    return file;
  }

  @Nullable
  private static Charset charsetFromDto(@Nullable String dtoCharset) {
    if (dtoCharset == null) {
      return null;
    }
    try {
      return Charset.forName(dtoCharset);
    } catch (Exception e) {
      return null;
    }
  }

  public List<ClientFile> findFilesByNamesInScope(String configScopeId, List<String> filenames) {
    return getFiles(configScopeId).stream()
      .filter(f -> filenames.contains(f.getClientRelativePath().getFileName().toString()))
      .toList();
  }

  public List<ClientFile> findSonarlintConfigurationFilesByScope(String configScopeId) {
    return getFiles(configScopeId).stream()
      .filter(ClientFile::isSonarlintConfigurationFile)
      .toList();
  }

  public Map<URI, ClientFile> initializeFileSystem(String configScopeId, SonarLintCancelMonitor cancelMonitor) {
    var result = new ConcurrentHashMap<URI, ClientFile>();
    var files = getClientFileDtos(configScopeId, cancelMonitor);
    files.forEach(clientFileDto -> {
      var clientFile = fromDto(clientFileDto);
      filesByUri.put(clientFileDto.getUri(), clientFile);
      result.put(clientFileDto.getUri(), clientFile);
    });
    return result;
  }

  private List<ClientFileDto> getClientFileDtos(String configScopeId, SonarLintCancelMonitor cancelMonitor) {
    var startTime = System.currentTimeMillis();
    var future = rpcClient.listFiles(new ListFilesParams(configScopeId));
    cancelMonitor.onCancel(() -> future.cancel(true));
    var files = future.join().getFiles();
    var endTime = System.currentTimeMillis();
    telemetryService.updateListFilesPerformance(files.size(), endTime - startTime);
    return files;
  }

  public void didUpdateFileSystem(DidUpdateFileSystemParams params) {
    var removed = new ArrayList<ClientFile>();
    params.getRemovedFiles().forEach(uri -> {
      var clientFile = filesByUri.remove(uri);
      if (clientFile != null) {
        filesByConfigScopeIdCache.get(clientFile.getConfigScopeId()).remove(uri);
        removed.add(clientFile);
      }
    });

    var added = new ArrayList<ClientFile>();
    params.getAddedFiles().forEach(clientFileDto -> {
      var clientFile = fromDto(clientFileDto);
      var previousFile = filesByUri.put(clientFileDto.getUri(), clientFile);
      //ACR-06e153a77c304c3198e86c94678fa258
      if (previousFile == null) {
        added.add(clientFile);
      }
      var byScope = filesByConfigScopeIdCache.get(clientFileDto.getConfigScopeId());
      byScope.put(clientFileDto.getUri(), clientFile);
    });

    var updated = new ArrayList<ClientFile>();
    params.getChangedFiles().forEach(clientFileDto -> {
      var clientFile = fromDto(clientFileDto);
      var previousFile = filesByUri.put(clientFileDto.getUri(), clientFile);
      //ACR-6c6ef104cee24734bad05ce894c25d41
      if (previousFile != null) {
        updated.add(clientFile);
      } else {
        added.add(clientFile);
      }
      var byScope = filesByConfigScopeIdCache.get(clientFileDto.getConfigScopeId());
      byScope.put(clientFileDto.getUri(), clientFile);
    });

    eventPublisher.publishEvent(new FileSystemUpdatedEvent(removed, added, updated));
  }

  @EventListener
  public void onConfigurationScopeRemoved(ConfigurationScopeRemovedEvent event) {
    var removedFilesByURI = filesByConfigScopeIdCache.get(event.getRemovedConfigurationScopeId());
    filesByConfigScopeIdCache.clear(event.getRemovedConfigurationScopeId());
    if (removedFilesByURI != null) {
      removedFilesByURI.keySet().forEach(filesByUri::remove);
    }
  }

  @PreDestroy
  public void shutdown() {
    filesByConfigScopeIdCache.close();
  }

  /*ACR-21f88ac5557b43ea98a3013c087cdf49
ACR-6d4a71c7873c417f9b9be3ef90d17848
   */
  @CheckForNull
  public ClientFile getClientFiles(String configScopeId, URI fileUri) {
    return filesByConfigScopeIdCache.get(configScopeId).get(fileUri);
  }

  /*ACR-682bbe77a94b4e54b19503899b221c66
ACR-810ebeeba52e457095729e4031f72cf1
   */
  @CheckForNull
  public ClientFile getClientFile(URI fileUri) {
    return filesByUri.get(fileUri);
  }

  @CheckForNull
  public Path getBaseDir(String configurationScopeId) {
    return baseDirPerConfigScopeId.computeIfAbsent(configurationScopeId, k -> {
      try {
        return rpcClient.getBaseDir(new GetBaseDirParams(configurationScopeId)).join().getBaseDir();
      } catch (Exception e) {
        LOG.error("Error when getting the base dir from the client", e);
        return null;
      }
    });
  }

  public void didOpenFile(String configurationScopeId, URI fileUri) {
    var isNewlyOpenedFile = openFilesRepository.considerOpened(configurationScopeId, fileUri);
    if (isNewlyOpenedFile) {
      eventPublisher.publishEvent(new FileOpenedEvent(configurationScopeId, fileUri));
    }
  }

  public void didCloseFile(String configurationScopeId, URI fileUri) {
    openFilesRepository.considerClosed(configurationScopeId, fileUri);
  }

  public Map<String, Set<URI>> groupFilesByConfigScope(Set<URI> fileUris) {
    return fileUris.stream()
      .map(filesByUri::get)
      .filter(Objects::nonNull)
      .collect(Collectors.groupingBy(
        ClientFile::getConfigScopeId,
        Collectors.mapping(
          ClientFile::getUri,
          Collectors.toSet()
        )
      ));
  }

}
