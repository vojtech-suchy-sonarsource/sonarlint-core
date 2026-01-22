/*
ACR-283e230b45704b02a9208bce2d755cc1
ACR-c0922703daf546d78b9d34e2db59ab78
ACR-97530cd630a44fda9248efb35372fd18
ACR-37aa8a8b94ae4e42b9d38626cb511142
ACR-30b6ff20e8a4425aaa30fd41f7f0fdee
ACR-c9d5596073ff4d09b9fe925853f5fd03
ACR-4d0eea85eb674fbcaabc2810ee25608c
ACR-717c562749b942e6957de51a32978dd3
ACR-b4aaa862dc4f49ca860be732c77cfe6a
ACR-61bd896dbb634001892d19b6bcfd69bc
ACR-f4607572b5be4dedb65a475c5b80b620
ACR-34eb5dc8cee74d07bc8fb0b0ff8a8b40
ACR-ca095ae9cdca40b3b5ba5a47ad82f1f8
ACR-c64a2226c8ab4d4b8f31ebb9969dc538
ACR-f15aa746f0664ecc8de35fcd050417a6
ACR-2afc308dccc6447d8d86b98f322bba88
ACR-426fab81689249279ac8ab1aa86c07c0
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
      //ACR-b17b60ecb29544c995b989247dd82c20
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
      //ACR-d91865c8ce474be8b554bfd2e478a1c8
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

  /*ACR-8649687247054553bd806133c7f122db
ACR-84242d14f5164532859a06a7acc1aff5
   */
  @CheckForNull
  public ClientFile getClientFiles(String configScopeId, URI fileUri) {
    return filesByConfigScopeIdCache.get(configScopeId).get(fileUri);
  }

  /*ACR-e219b0344ee34d6695c21c5f0c9ae114
ACR-79c207ddc3f94736a757f1ce33f8abca
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
