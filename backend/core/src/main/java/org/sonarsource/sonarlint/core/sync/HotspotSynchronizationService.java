/*
ACR-ded21e0c6c274e1b8d8c004408b11bf6
ACR-5aae04e952834637895c08a0a9b5c858
ACR-5a63b1e31d6b4614b9c482158205ab12
ACR-4e03f87907744fe5b86f2865bff42ee7
ACR-7827b2c9ce92484eac9080981c6b554e
ACR-dcedb1bd13b1433488d5591868d3aba8
ACR-f128bd1c7c90459cbbfaa80f730644df
ACR-5d374ba03de44f8286de910ccd7cceb0
ACR-b491348a054b4c7091eb3310fc3f4d70
ACR-5724fac342364bbe85670e47f6b0851e
ACR-54d0540f47474093b0035649c1c4b602
ACR-5117b47586d2476687ab196d6868cb17
ACR-ba02c745d0b046f48d08b60aef37fa35
ACR-91b87571864d4a0894331cc18353ce24
ACR-9337da1ca1124a07a3d07e46c1180165
ACR-5e834b095a094681b3cc40bd78aa4eb9
ACR-44568d2b7f534387bbc560d6b1623ff5
 */
package org.sonarsource.sonarlint.core.sync;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.hotspot.HotspotApi;
import org.sonarsource.sonarlint.core.serverconnection.ConnectionStorage;
import org.sonarsource.sonarlint.core.serverconnection.HotspotDownloader;
import org.sonarsource.sonarlint.core.serverconnection.ServerHotspotUpdater;
import org.sonarsource.sonarlint.core.serverconnection.ServerInfoSynchronizer;
import org.sonarsource.sonarlint.core.storage.StorageService;

public class HotspotSynchronizationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final StorageService storageService;
  private final LanguageSupportRepository languageSupportRepository;
  private final SonarQubeClientManager sonarQubeClientManager;

  public HotspotSynchronizationService(StorageService storageService, LanguageSupportRepository languageSupportRepository, SonarQubeClientManager sonarQubeClientManager) {
    this.storageService = storageService;
    this.languageSupportRepository = languageSupportRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
  }

  public void syncServerHotspotsForProject(ServerApi serverApi, String connectionId, String projectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var serverVersion = getSonarServerVersion(serverApi, storage, cancelMonitor);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var hotspotsUpdater = new ServerHotspotUpdater(storage, new HotspotDownloader(enabledLanguagesToSync));
    if (HotspotApi.supportHotspotsPull(serverApi.isSonarCloud(), serverVersion)) {
      LOG.info("[SYNC] Synchronizing hotspots for project '{}' on branch '{}'", projectKey, branchName);
      hotspotsUpdater.sync(serverApi.hotspot(), projectKey, branchName, enabledLanguagesToSync, cancelMonitor);
    } else {
      LOG.debug("Incremental hotspot sync is not supported. Skipping.");
    }
  }

  private static Version getSonarServerVersion(ServerApi serverApi, ConnectionStorage storage, SonarLintCancelMonitor cancelMonitor) {
    var serverInfoSynchronizer = new ServerInfoSynchronizer(storage);
    return serverInfoSynchronizer.readOrSynchronizeServerInfo(serverApi, cancelMonitor).version();
  }

  public void fetchProjectHotspots(Binding binding, String activeBranch, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(binding.connectionId(), serverApi ->
      downloadAllServerHotspots(binding.connectionId(), serverApi, binding.sonarProjectKey(), activeBranch, cancelMonitor));
  }

  private void downloadAllServerHotspots(String connectionId, ServerApi serverApi, String projectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var hotspotsUpdater = new ServerHotspotUpdater(storage, new HotspotDownloader(enabledLanguagesToSync));
    hotspotsUpdater.updateAll(serverApi.hotspot(), projectKey, branchName, enabledLanguagesToSync, cancelMonitor);
  }

  public void fetchFileHotspots(Binding binding, String activeBranch, Path serverFilePath, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(binding.connectionId(), serverApi ->
      downloadAllServerHotspotsForFile(binding.connectionId(), serverApi, binding.sonarProjectKey(), serverFilePath, activeBranch, cancelMonitor));
  }

  private void downloadAllServerHotspotsForFile(String connectionId, ServerApi serverApi, String projectKey, Path serverRelativeFilePath, String branchName,
    SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var serverVersion = getSonarServerVersion(serverApi, storage, cancelMonitor);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var hotspotsUpdater = new ServerHotspotUpdater(storage, new HotspotDownloader(enabledLanguagesToSync));
    hotspotsUpdater.updateForFile(serverApi.hotspot(), projectKey, serverRelativeFilePath, branchName, () -> serverVersion, cancelMonitor);
  }

}
