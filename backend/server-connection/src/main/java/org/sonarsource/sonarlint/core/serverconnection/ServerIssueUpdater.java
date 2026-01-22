/*
ACR-a4c0a540227f4cf0bd679a8c412c3fdd
ACR-f0722a6627fc4897a850844f0b93cfdc
ACR-1ede7eaa28634919bab975c88fa70ba7
ACR-596a7781e1ac4228a80d1ec4abd37d4d
ACR-0c0012ca2c1046f1b9584e4c51451491
ACR-03d874b3b4b244208992286adad02e47
ACR-4ba973f6f378494cbf90ccc0eac8f234
ACR-80329fc06e4e44448ebca81f9f96bd69
ACR-6d2cd824cc7742a5adcf884956e684f6
ACR-a2d911cdfb574beab36b5d51c5ae4697
ACR-225b83ee728f4718a0d7f70a9ca91fde
ACR-1a88bc0d70124d23a0bccd2ff2be874e
ACR-ddbe235278a9495aaa2c9dcf9b9ed0e8
ACR-b145545fa4bb452db23ce8bcc4a4d34b
ACR-57cf791e262a4624b36c781d2718c6df
ACR-b3958839f9b546e4ac0d82f89253b142
ACR-e0b6c2896450405e8c6b148e637eeef7
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
      //ACR-50fe1f54e6f548b9bcf1ac490738532b
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
      //ACR-5836e0022e0d44d49b92915674aeaf0b
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
