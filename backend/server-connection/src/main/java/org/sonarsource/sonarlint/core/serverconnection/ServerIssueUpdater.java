/*
ACR-eb071c3596f44cb4bf59f6f9ab29a983
ACR-8e2f2438df7a4e3f9ed62b5bf491b61f
ACR-ac4403be54ee495ebb95b5d1d3a90525
ACR-780bceb07dcb4f0cbd3df872bff51f38
ACR-c000485e6b1945d8ad3d3439e6b33889
ACR-8322967a471c401bbcc79143ebedbfda
ACR-20064b6f98d442dda4b24f803d26251a
ACR-17453f4e57144258a53830f2054c5839
ACR-a79689a5fe524ffcbadd0dadd8482ab7
ACR-25d1ac6f589646bc85eedf2b20c8a9d0
ACR-e2598c7a237f415e80eb6c17d5e70e18
ACR-a9aa7f5fe8ae4ebaaaf501ef565b549a
ACR-d2722b2d2ef14815a19c85df30df592f
ACR-6589893e887944ee8293826c78156787
ACR-02d2b2b6c1974295b4c532c87e33e3f6
ACR-9b5ca841afa247e593b1855cdfe4df7b
ACR-3f6930b3315f4b27aaa85a9f1f5c63d7
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;
import org.sonarsource.sonarlint.core.serverconnection.storage.UpdateSummary;

import static java.util.stream.Collectors.toSet;
import static org.sonarsource.sonarlint.core.serverconnection.ServerUpdaterUtils.computeLastSync;

public class ServerIssueUpdater {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConnectionStorage storage;
  private final IssueDownloader issueDownloader;
  private final TaintIssueDownloader taintIssueDownloader;

  public ServerIssueUpdater(ConnectionStorage storage, IssueDownloader issueDownloader, TaintIssueDownloader taintIssueDownloader) {
    this.storage = storage;
    this.issueDownloader = issueDownloader;
    this.taintIssueDownloader = taintIssueDownloader;
  }

  public void update(ServerApi serverApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, SonarLintCancelMonitor cancelMonitor) {
    if (serverApi.isSonarCloud()) {
      var issues = issueDownloader.downloadFromBatch(serverApi, projectKey, branchName, cancelMonitor);
      storage.project(projectKey).findings().replaceAllIssuesOfBranch(branchName, issues, enabledLanguages);
    } else {
      sync(serverApi, projectKey, branchName, issueDownloader.getEnabledLanguages(), cancelMonitor);
    }
  }

  public void sync(ServerApi serverApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, SonarLintCancelMonitor cancelMonitor) {
    var lastSync = storage.project(projectKey).findings().getLastIssueSyncTimestamp(branchName);

    lastSync = computeLastSync(enabledLanguages, lastSync, storage.project(projectKey).findings().getLastIssueEnabledLanguages(branchName));

    var result = issueDownloader.downloadFromPull(serverApi, projectKey, branchName, lastSync, cancelMonitor);
    storage.project(projectKey).findings().mergeIssues(branchName, result.getChangedIssues(), result.getClosedIssueKeys(),
      result.getQueryTimestamp(), enabledLanguages);
  }

  public UpdateSummary<ServerTaintIssue> syncTaints(ServerApi serverApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages,
    SonarLintCancelMonitor cancelMonitor) {
    var serverIssueStore = storage.project(projectKey).findings();

    var lastSync = serverIssueStore.getLastTaintSyncTimestamp(branchName);

    lastSync = computeLastSync(enabledLanguages, lastSync, storage.project(projectKey).findings().getLastTaintEnabledLanguages(branchName));

    var result = taintIssueDownloader.downloadTaintFromPull(serverApi, projectKey, branchName, lastSync, cancelMonitor);
    var previousTaintIssues = serverIssueStore.loadTaint(branchName);
    var previousTaintIssueKeys = previousTaintIssues.stream().map(ServerTaintIssue::getSonarServerKey).collect(toSet());
    serverIssueStore.mergeTaintIssues(branchName, result.getChangedTaintIssues(), result.getClosedIssueKeys(), result.getQueryTimestamp(), enabledLanguages);
    var deletedTaintVulnerabilityIds = previousTaintIssues.stream().filter(issue -> result.getClosedIssueKeys().contains(issue.getSonarServerKey())).map(ServerTaintIssue::getId)
      .collect(toSet());
    var addedTaintVulnerabilities = result.getChangedTaintIssues().stream().filter(issue -> !previousTaintIssueKeys.contains(issue.getSonarServerKey()))
      .toList();
    var updatedTaintVulnerabilities = result.getChangedTaintIssues().stream().filter(issue -> previousTaintIssueKeys.contains(issue.getSonarServerKey()))
      .toList();
    return new UpdateSummary<>(deletedTaintVulnerabilityIds, addedTaintVulnerabilities, updatedTaintVulnerabilities);
  }

  public void updateFileIssuesIfNeeded(ServerApi serverApi, String projectKey, Path serverFileRelativePath, String branchName, SonarLintCancelMonitor cancelMonitor) {
    if (serverApi.isSonarCloud()) {
      updateFileIssues(serverApi, projectKey, serverFileRelativePath, branchName, cancelMonitor);
    } else {
      LOG.debug("Skip downloading file issues on SonarQube ");
    }
  }

  public void updateFileIssues(ServerApi serverApi, String projectKey, Path serverFileRelativePath, String branchName, SonarLintCancelMonitor cancelMonitor) {
    var fileKey = IssueStorePaths.componentKey(projectKey, serverFileRelativePath);
    List<ServerIssue<?>> issues = new ArrayList<>();
    try {
      issues.addAll(issueDownloader.downloadFromBatch(serverApi, fileKey, branchName, cancelMonitor));
    } catch (Exception e) {
      //ACR-4dfec44edbb84df6a1f9983b408f5284
      throw new DownloadException("Failed to update file issues: " + e.getMessage(), null);
    }
    storage.project(projectKey).findings().replaceAllIssuesOfFile(branchName, serverFileRelativePath, issues);
  }

  public UpdateSummary<ServerTaintIssue> downloadProjectTaints(ServerApi serverApi, String projectKey, String branchName, Set<SonarLanguage> enabledLanguages,
    SonarLintCancelMonitor cancelMonitor) {
    List<ServerTaintIssue> newTaintIssues;
    try {
      newTaintIssues = new ArrayList<>(taintIssueDownloader.downloadTaintFromIssueSearch(serverApi, projectKey, branchName, cancelMonitor));
    } catch (Exception e) {
      //ACR-4d42cb04a2454221a4b959bae9754795
      throw new DownloadException("Failed to update file taint vulnerabilities: " + e.getMessage(), null);
    }
    var findingsStorage = storage.project(projectKey).findings();
    var previousTaintIssues = findingsStorage.loadTaint(branchName);
    var previousTaintIssueKeys = previousTaintIssues.stream().map(ServerTaintIssue::getSonarServerKey).collect(toSet());
    findingsStorage.replaceAllTaintsOfBranch(branchName, newTaintIssues, enabledLanguages);
    var newTaintIssueKeys = newTaintIssues.stream().map(ServerTaintIssue::getSonarServerKey).collect(toSet());
    var deletedTaintVulnerabilityIds = previousTaintIssues.stream().filter(issue -> !newTaintIssueKeys.contains(issue.getSonarServerKey())).map(ServerTaintIssue::getId)
      .collect(toSet());
    var addedTaintVulnerabilities = newTaintIssues.stream().filter(issue -> !previousTaintIssueKeys.contains(issue.getSonarServerKey()))
      .toList();
    var updatedTaintVulnerabilities = newTaintIssues.stream().filter(issue -> previousTaintIssueKeys.contains(issue.getSonarServerKey()))
      .toList();
    return new UpdateSummary<>(deletedTaintVulnerabilityIds, addedTaintVulnerabilities, updatedTaintVulnerabilities);
  }
}
