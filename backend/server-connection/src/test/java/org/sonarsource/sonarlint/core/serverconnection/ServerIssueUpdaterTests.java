/*
ACR-cde521967d864a648b43097d9a8c196e
ACR-3c8da1075d1543ab97094bd82deb104e
ACR-d5d2e836b5da450e86b2e88727dc3808
ACR-712a68eedd674c7ca8c46c56b8ec7117
ACR-1f6c5f66230543d3893cb1c67dd35543
ACR-f58a4df16fa74176941d47f009d2fd71
ACR-d3c672c11e4d4c02b4d8bdc1bd868f2a
ACR-474d2b36490640ef917efc9471235cfe
ACR-fdabb04696c84e1cb4cdb43ad0e6629d
ACR-db44f5c58c754f70b11c844fcbe831f8
ACR-8a5841a3c6d642b18608ac728162de4d
ACR-215a0e862279457e89990b57cde4d28a
ACR-f7790da20bb24f55a1b15e2e280c1f76
ACR-7099e560f12a4412b9c0495c7b0905e4
ACR-32d1c9ec70bd40809bb45dc52eb27f38
ACR-005e0f4060f047cc8500931e6f40ac7c
ACR-99585cc1d3cd493f8bf7efbe120f62a3
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProjectServerIssueStore;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ServerIssueFixtures.aServerIssue;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ServerIssueFixtures.aServerTaintIssue;

class ServerIssueUpdaterTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String PROJECT_KEY = "module";
  private final IssueDownloader downloader = mock(IssueDownloader.class);
  private final TaintIssueDownloader taintDownloader = mock(TaintIssueDownloader.class);
  private final ProjectServerIssueStore issueStore = mock(ProjectServerIssueStore.class);
  private ProjectBinding projectBinding = new ProjectBinding(PROJECT_KEY, "", "");

  private ServerIssueUpdater updater;
  private ServerApi serverApi;

  @BeforeEach
  void setUp() {
    serverApi = new ServerApi(mock(ServerApiHelper.class));
    ConnectionStorage storage = mock(ConnectionStorage.class);
    var projectStorage = mock(SonarProjectStorage.class);
    when(storage.project(PROJECT_KEY)).thenReturn(projectStorage);
    when(projectStorage.findings()).thenReturn(issueStore);
    updater = new ServerIssueUpdater(storage, downloader, taintDownloader);
  }

  @Test
  void update_project_issues_sonarcloud() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromBatch(serverApi, "module:file", null, cancelMonitor)).thenReturn(issues);
    when(serverApi.isSonarCloud()).thenReturn(true);

    updater.update(serverApi, projectBinding.projectKey(), "branch", Set.of(), cancelMonitor);

    verify(issueStore).replaceAllIssuesOfBranch(eq("branch"), anyList(), eq(Set.of()));
  }

  @Test
  void update_project_issues_with_pull_first_time() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.<Instant>empty();
    when(issueStore.getLastIssueSyncTimestamp("master")).thenReturn(lastSync);
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor)).thenReturn(new IssueDownloader.PullResult(queryTimestamp, issues, Set.of()));

    updater.update(serverApi, projectBinding.projectKey(), "master", Set.of(), cancelMonitor);

    verify(issueStore).mergeIssues(eq("master"), anyList(), anySet(), eq(queryTimestamp), anySet());
  }

  @Test
  void update_project_issues_with_pull_using_last_sync() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    when(issueStore.getLastIssueEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    when(issueStore.getLastIssueSyncTimestamp("master")).thenReturn(lastSync);
    when(downloader.getEnabledLanguages()).thenReturn(Set.of(SonarLanguage.C, SonarLanguage.GO));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor)).thenReturn(new IssueDownloader.PullResult(queryTimestamp, issues, Set.of()));

    updater.update(serverApi, projectBinding.projectKey(), "master", Set.of(), cancelMonitor);

    verify(issueStore).mergeIssues(eq("master"), anyList(), anySet(), eq(queryTimestamp), anySet());
  }

  @Test
  void update_project_issues_with_pull_when_there_were_no_enabled_languages() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = new HashSet<SonarLanguage>();
    when(issueStore.getLastIssueSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastIssueEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    when(downloader.getEnabledLanguages()).thenReturn(Set.of(SonarLanguage.C));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor)).thenReturn(new IssueDownloader.PullResult(queryTimestamp, issues, Set.of()));
    updater.update(serverApi, projectBinding.projectKey(), "master", Set.of(), cancelMonitor);
    verify(downloader).downloadFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor);
  }

  @Test
  void update_project_issues_with_pull_when_enabled_language_changed() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    when(issueStore.getLastIssueSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastIssueEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    when(downloader.getEnabledLanguages()).thenReturn(Set.of(SonarLanguage.C));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor)).thenReturn(new IssueDownloader.PullResult(queryTimestamp, issues, Set.of()));
    updater.update(serverApi, projectBinding.projectKey(), "master", Set.of(), cancelMonitor);
    verify(downloader).downloadFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor);
  }

  @Test
  void update_project_issues_with_pull_when_enabled_language_not_changed() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    when(issueStore.getLastIssueSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastIssueEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    when(downloader.getEnabledLanguages()).thenReturn(Set.of(SonarLanguage.C, SonarLanguage.GO));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor)).thenReturn(new IssueDownloader.PullResult(queryTimestamp, issues, Set.of()));
    updater.update(serverApi, projectBinding.projectKey(), "master", Set.of(), cancelMonitor);
    verify(downloader).downloadFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor);
  }

  @Test
  void update_project_taints_with_pull_when_there_were_no_enabled_languages() {
    var issue = aServerTaintIssue();
    List<ServerTaintIssue> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = new HashSet<SonarLanguage>();
    when(issueStore.getLastTaintSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastTaintEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    var cancelMonitor = new SonarLintCancelMonitor();
    when(taintDownloader.downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor)).thenReturn(new TaintIssueDownloader.PullTaintResult(queryTimestamp, issues, Set.of()));

    updater.syncTaints(serverApi, projectBinding.projectKey(), "master", Set.of(SonarLanguage.C), cancelMonitor);
    verify(taintDownloader).downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor);
  }

  @Test
  void update_project_taints_with_pull_when_enabled_language_changed() {
    var issue = aServerTaintIssue();
    List<ServerTaintIssue> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    when(issueStore.getLastTaintSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastTaintEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    var cancelMonitor = new SonarLintCancelMonitor();
    when(taintDownloader.downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor)).thenReturn(new TaintIssueDownloader.PullTaintResult(queryTimestamp, issues, Set.of()));

    updater.syncTaints(serverApi, projectBinding.projectKey(), "master", Set.of(SonarLanguage.C), cancelMonitor);
    verify(taintDownloader).downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", Optional.empty(), cancelMonitor);
  }

  @Test
  void update_project_taints_with_pull_when_enabled_language_not_changed() {
    var issue = aServerTaintIssue();
    List<ServerTaintIssue> issues = Collections.singletonList(issue);
    var queryTimestamp = Instant.now();
    var lastSync = Optional.of(Instant.ofEpochMilli(123456789));
    var lastIssueEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    when(issueStore.getLastTaintSyncTimestamp("master")).thenReturn(lastSync);
    when(issueStore.getLastTaintEnabledLanguages("master")).thenReturn(lastIssueEnabledLanguages);
    var cancelMonitor = new SonarLintCancelMonitor();
    when(taintDownloader.downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor)).thenReturn(new TaintIssueDownloader.PullTaintResult(queryTimestamp, issues, Set.of()));

    updater.syncTaints(serverApi, projectBinding.projectKey(), "master", Set.of(SonarLanguage.C, SonarLanguage.GO), cancelMonitor);
    verify(taintDownloader).downloadTaintFromPull(serverApi, projectBinding.projectKey(), "master", lastSync, cancelMonitor);
  }

  @Test
  void update_file_issues_for_unknown_file() {
    projectBinding = new ProjectBinding(PROJECT_KEY, "", "ide_prefix");

    updater.updateFileIssuesIfNeeded(serverApi, PROJECT_KEY, Path.of("not_ide_prefix"), null, new SonarLintCancelMonitor());

    verifyNoInteractions(downloader);
    verifyNoInteractions(issueStore);
  }

  @Test
  void error_downloading_file_issues() {
    var cancelMonitor = new SonarLintCancelMonitor();
    when(serverApi.isSonarCloud()).thenReturn(true);
    when(downloader.downloadFromBatch(serverApi, "module:file", null, cancelMonitor)).thenThrow(IllegalArgumentException.class);
    var filePath = Path.of("file");

    assertThrows(DownloadException.class, () -> updater.updateFileIssuesIfNeeded(serverApi, PROJECT_KEY, filePath, null, cancelMonitor));
  }

  @Test
  void update_file_issues_sonarcloud() {
    var issue = aServerIssue();
    List<ServerIssue<?>> issues = Collections.singletonList(issue);
    when(serverApi.isSonarCloud()).thenReturn(true);

    var cancelMonitor = new SonarLintCancelMonitor();
    when(downloader.downloadFromBatch(serverApi, projectBinding.projectKey() + ":src/main/Foo.java", null, cancelMonitor)).thenReturn(issues);

    updater.updateFileIssuesIfNeeded(serverApi, PROJECT_KEY, Path.of("src/main/Foo.java"), "branch", cancelMonitor);

    verify(issueStore).replaceAllIssuesOfFile(eq("branch"), eq(Path.of("src/main/Foo.java")), anyList());
  }

  @Test
  void dont_update_file_issues_with_pull() {
    updater.updateFileIssuesIfNeeded(serverApi, PROJECT_KEY, Path.of("src/main/Foo.java"), "branch", new SonarLintCancelMonitor());

    verify(issueStore, never()).replaceAllIssuesOfFile(eq("branch"), any(), anyList());
  }
}
