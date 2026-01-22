/*
ACR-e1ee7c894f59437a915b4d72c7816565
ACR-fc6fae233b4c4a279de2f4d6597ac6d9
ACR-1e1b0f8fcd2f458cb276e2037a3b4351
ACR-6dc612fa49cf44fea165d2f45a3446ca
ACR-84b13d5096be41eba5b8c0886aa337c6
ACR-a52f7baf39e0465c81ac61ddb6078e49
ACR-b9734dedfb284b5ca51efcb7bf83f229
ACR-7bd17803d16e46998b6b30efbe112d7f
ACR-538f2b153afc4abb8ccda3e6692e5114
ACR-b627c3219c29407ab7b0a6783504f640
ACR-2bbda9f70ea047ecb66be7a0e6732bc5
ACR-2f1498b198054e49b87f7c72950f28fb
ACR-0133d85552054739a5d8604bec79dc2d
ACR-7114f2c215d446ffa5199dec4c22c706
ACR-b4a33b07821241b6a757d0f8264cd346
ACR-539bfdd8fe9c4cf2ba95b0c96ccd7f78
ACR-a82748fb61944019a90c66cedf898647
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
