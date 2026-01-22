/*
ACR-116a61298c2e46c1a297f15ba9bc7e99
ACR-83e1ec4d0adb4ffeaaa1a16bf1e80e5d
ACR-e1cdd31f0c934bee9e96549ce863bba7
ACR-ced91e79b2014156a3b0be2f1c3001fe
ACR-3934ff06441a4db88c0547e41a41cf37
ACR-d865993d33264250bab4bda3f3f1f0e8
ACR-bc6b9ffaf6b0432ba8a140b7fefa6b9f
ACR-1be761f5bd61463a8261603b74d7fd0d
ACR-3b4e051795904cfab64a8611d4ecec77
ACR-e33852bda8fb404ab8395a2c4802754c
ACR-61c7f156a7ac4aa69c26fed4f1a0ca44
ACR-163d2cfca421468b92839ccf53dc39fb
ACR-2dbc025dd94e48aaa0fa443303e19cd1
ACR-8a07d37d997547f99cba0d8ba99e260c
ACR-a38abafc55d64200b823ba5a765244d8
ACR-445349712cc6466baa0ce2950b4e56a7
ACR-2c72a683454c4c118b902914a87911a3
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
