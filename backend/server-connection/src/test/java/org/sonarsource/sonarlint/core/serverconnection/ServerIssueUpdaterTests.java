/*
ACR-757253c7cc994887990c3dc08739c5eb
ACR-ebedc029205043a58d0b2c17d6d999ce
ACR-b465f402f9634f39abef9d23f240f4d1
ACR-b6ab94824ffc435ba6c6b22d647f3a06
ACR-dec56393039d4a709f5c68198d100b8b
ACR-a782b0185bed4ba0929d8b0f019e4a1f
ACR-468d81fa2abd482989fb2211c94e2218
ACR-9c8c4bb0cc1641209ca8944356d61e22
ACR-494bcb8b8ad94abb9b45fbc324169819
ACR-a242f3559b854fd08120e1da98a2cd24
ACR-c408394b253e4a9cac8b4ea83b8be1db
ACR-e29771d830a14c5a969adb2c6c48bfe8
ACR-2c4c13915d504f8d852dbf6ca29e2643
ACR-f47427b588ad46308f25673ac9b48b0b
ACR-0cd085222e35498bb9f58237575c3275
ACR-44454e63984d4bfb8c4834a2261bc7a2
ACR-e737c49f446d48128e7cad3c3121d2fe
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
