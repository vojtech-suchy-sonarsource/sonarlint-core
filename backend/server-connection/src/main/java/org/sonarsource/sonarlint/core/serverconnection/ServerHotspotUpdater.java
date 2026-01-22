/*
ACR-a6bd38419c8e484c8ae1e8764a44c57a
ACR-a53cbee7f0364e9f8b596e738efeefbf
ACR-9c2a24cd24f545ad9d2edd7da002fac5
ACR-72bc4bdba57f4966aa41941de6190ee0
ACR-b738f1f0c4dc4783a04b86c033494e47
ACR-9b68dc90aba845df8710494aaaf49b48
ACR-6d3df88f80ee4d09994cb4e1ca3278d4
ACR-6f6918edf1f14884b571a9a04ccc25ab
ACR-b2c4c4870c36455f823313acb7efeb3e
ACR-8a5da630a3ee4d32b10f8b93ce416fee
ACR-432d1f850d1d46d4bbe39ac3af666aa8
ACR-fff2d34e558b449097cc9e4daf6a5e6e
ACR-c61d2b5f45a944dda613a26043fdc05e
ACR-316acec2e6394447bf5d9472bcefc7cc
ACR-1005cee99be84f63a96fadf91b2bb2df
ACR-9e1870da3a9f4e4ebccb55957ce6fa72
ACR-91489dec3df34686849e0509fb84dd1f
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.hotspot.HotspotApi;

import static org.sonarsource.sonarlint.core.serverconnection.ServerUpdaterUtils.computeLastSync;

public class ServerHotspotUpdater {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConnectionStorage storage;
  private final HotspotDownloader hotspotDownloader;

  public ServerHotspotUpdater(ConnectionStorage storage, HotspotDownloader hotspotDownloader) {
    this.storage = storage;
    this.hotspotDownloader = hotspotDownloader;
  }

  public void updateAll(HotspotApi hotspotApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, SonarLintCancelMonitor cancelMonitor) {
    var projectHotspots = hotspotApi.getAll(projectKey, branchName, cancelMonitor);
    storage.project(projectKey).findings().replaceAllHotspotsOfBranch(branchName, projectHotspots, enabledLanguages);
  }

  public void updateForFile(HotspotApi hotspotApi, String projectKey, Path serverFilePath, String branchName, Supplier<Version> serverVersionSupplier,
    SonarLintCancelMonitor cancelMonitor) {
    if (hotspotApi.supportHotspotsPull(serverVersionSupplier)) {
      LOG.debug("Skip downloading file hotspots on SonarQube 10.1+");
      return;
    }
    var fileHotspots = hotspotApi.getFromFile(projectKey, serverFilePath, branchName, cancelMonitor);
    storage.project(projectKey).findings().replaceAllHotspotsOfFile(branchName, serverFilePath, fileHotspots);
  }

  public void sync(HotspotApi hotspotApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, SonarLintCancelMonitor cancelMonitor) {
    var lastSync = storage.project(projectKey).findings().getLastHotspotSyncTimestamp(branchName);

    lastSync = computeLastSync(enabledLanguages, lastSync, storage.project(projectKey).findings().getLastHotspotEnabledLanguages(branchName));

    var result = hotspotDownloader.downloadFromPull(hotspotApi, projectKey, branchName, lastSync, cancelMonitor);
    storage.project(projectKey).findings().mergeHotspots(branchName, result.getChangedHotspots(), result.getClosedHotspotKeys(),
      result.getQueryTimestamp(), enabledLanguages);
  }
}
