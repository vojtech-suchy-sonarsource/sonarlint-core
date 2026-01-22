/*
ACR-fec39150fe79424aae96cb16f9154fb1
ACR-6f0fb7dec0c64954bfd6ee3cc19a41b9
ACR-2db6e1a718f247d5ad0b61a583f4fe30
ACR-8d1b7bfe3d8d4f01abd210905d20c168
ACR-112335c8161c49b38d39a924e161da85
ACR-c4aa5fdc454c4d8f99bc16088ff9ef14
ACR-79602d7c5ff64f6fa7373b5fefd89cc0
ACR-b87675e1b5bf4ea68c2d6001daca3130
ACR-f388b2ff4a9f485a866edb185e3d6590
ACR-91b3d3746a634e70a5026a4de8e406b6
ACR-d645e9b4005848b98c8d726d1366d074
ACR-d000f27af62841bab690cd80ec5595f7
ACR-831839452bd140e89cded339f5b081e7
ACR-6db81fa9a37143ae92727ef54fc68a3c
ACR-e61bca13f66c4122935d09331a1226a2
ACR-bbd52ca18124479984306dba7cf953d1
ACR-1648ebd2609946e0abeee73238c63493
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.hotspot.HotspotApi;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProjectServerIssueStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ServerHotspotFixtures.aServerHotspot;

class ServerHotspotUpdaterTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String PROJECT_KEY = "module";
  private final ProjectServerIssueStore issueStore = mock(ProjectServerIssueStore.class);
  private final ProjectBinding projectBinding = new ProjectBinding(PROJECT_KEY, "", "");

  private ServerHotspotUpdater updater;
  private HotspotApi hotspotApi;
  private HotspotDownloader hotspotDownloader;

  @BeforeEach
  void setUp() {
    hotspotApi = mock(HotspotApi.class);
    hotspotDownloader = mock(HotspotDownloader.class);
    ConnectionStorage storage = mock(ConnectionStorage.class);
    var projectStorage = mock(SonarProjectStorage.class);
    when(storage.project(PROJECT_KEY)).thenReturn(projectStorage);
    when(projectStorage.findings()).thenReturn(issueStore);
    updater = new ServerHotspotUpdater(storage, hotspotDownloader);
  }

  @Test
  void should_sync_hotspots() {
    var timestamp = Instant.ofEpochMilli(123456789L);
    var hotspotKey = "hotspotKey";
    var hotspots = List.of(aServerHotspot(hotspotKey));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(hotspotDownloader.downloadFromPull(hotspotApi, PROJECT_KEY, "branch", Optional.empty(), cancelMonitor))
      .thenReturn(new HotspotDownloader.PullResult(timestamp, hotspots, Set.of()));

    updater.sync(hotspotApi, PROJECT_KEY, "branch", Set.of(SonarLanguage.C), cancelMonitor);

    var hotspotCaptor = ArgumentCaptor.forClass(List.class);
    verify(issueStore).mergeHotspots(eq("branch"), hotspotCaptor.capture(), eq(Set.of()), eq(timestamp), eq(Set.of(SonarLanguage.C)));
    assertThat(hotspotCaptor.getValue()).hasSize(1);
    var capturedHotspot = (ServerHotspot) (hotspotCaptor.getValue().get(0));
    assertThat(capturedHotspot.getKey()).isEqualTo(hotspotKey);
  }

  @Test
  void update_hotspots_with_pull_when_enabled_language_not_changed() {
    var timestamp = Instant.ofEpochMilli(123456789L);
    var lastHotspotEnabledLanguages = Set.of(SonarLanguage.C, SonarLanguage.GO);
    var hotspotKey = "hotspotKey";
    var hotspots = List.of(aServerHotspot(hotspotKey));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(hotspotDownloader.downloadFromPull(hotspotApi, PROJECT_KEY, "branch", Optional.of(timestamp), cancelMonitor))
      .thenReturn(new HotspotDownloader.PullResult(timestamp, hotspots, Set.of()));
    when(issueStore.getLastHotspotEnabledLanguages("branch")).thenReturn(lastHotspotEnabledLanguages);
    when(issueStore.getLastHotspotSyncTimestamp("branch")).thenReturn(Optional.of(timestamp));

    updater.sync(hotspotApi, PROJECT_KEY, "branch", Set.of(SonarLanguage.C, SonarLanguage.GO), cancelMonitor);

    var hotspotCaptor = ArgumentCaptor.forClass(List.class);
    verify(issueStore).mergeHotspots(eq("branch"), hotspotCaptor.capture(), eq(Set.of()), eq(timestamp), anySet());
    assertThat(hotspotCaptor.getValue()).hasSize(1);
    var capturedHotspot = (ServerHotspot) (hotspotCaptor.getValue().get(0));
    assertThat(capturedHotspot.getKey()).isEqualTo(hotspotKey);
    verify(hotspotDownloader).downloadFromPull(hotspotApi, projectBinding.projectKey(), "branch", Optional.of(timestamp), cancelMonitor);
  }

  @Test
  void update_hotspots_with_pull_when_enabled_language_changed() {
    var timestamp = Instant.ofEpochMilli(123456789L);
    var lastHotspotEnabledLanguages = Set.of(SonarLanguage.C);
    var hotspotKey = "hotspotKey";
    var hotspots = List.of(aServerHotspot(hotspotKey));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(hotspotDownloader.downloadFromPull(hotspotApi, PROJECT_KEY, "branch", Optional.empty(), cancelMonitor))
      .thenReturn(new HotspotDownloader.PullResult(timestamp, hotspots, Set.of()));
    when(issueStore.getLastHotspotEnabledLanguages("branch")).thenReturn(lastHotspotEnabledLanguages);
    when(issueStore.getLastHotspotSyncTimestamp("branch")).thenReturn(Optional.of(timestamp));

    updater.sync(hotspotApi, PROJECT_KEY, "branch", Set.of(SonarLanguage.C, SonarLanguage.GO), cancelMonitor);

    var hotspotCaptor = ArgumentCaptor.forClass(List.class);
    verify(issueStore).mergeHotspots(eq("branch"), hotspotCaptor.capture(), eq(Set.of()), eq(timestamp), anySet());
    assertThat(hotspotCaptor.getValue()).hasSize(1);
    var capturedHotspot = (ServerHotspot) (hotspotCaptor.getValue().get(0));
    assertThat(capturedHotspot.getKey()).isEqualTo(hotspotKey);
    verify(hotspotDownloader).downloadFromPull(hotspotApi, projectBinding.projectKey(), "branch", Optional.empty(), cancelMonitor);
  }

  @Test
  void update_hotspots_with_pull_when_last_enabled_language_were_not_there() {
    var timestamp = Instant.ofEpochMilli(123456789L);
    var lastHotspotEnabledLanguages = new HashSet<SonarLanguage>();
    var hotspotKey = "hotspotKey";
    var hotspots = List.of(aServerHotspot(hotspotKey));
    var cancelMonitor = new SonarLintCancelMonitor();
    when(hotspotDownloader.downloadFromPull(hotspotApi, PROJECT_KEY, "branch", Optional.empty(), cancelMonitor))
      .thenReturn(new HotspotDownloader.PullResult(timestamp, hotspots, Set.of()));
    when(issueStore.getLastHotspotEnabledLanguages("branch")).thenReturn(lastHotspotEnabledLanguages);
    when(issueStore.getLastHotspotSyncTimestamp("branch")).thenReturn(Optional.of(timestamp));

    updater.sync(hotspotApi, PROJECT_KEY, "branch", Set.of(SonarLanguage.C, SonarLanguage.GO), cancelMonitor);

    var hotspotCaptor = ArgumentCaptor.forClass(List.class);
    verify(issueStore).mergeHotspots(eq("branch"), hotspotCaptor.capture(), eq(Set.of()), eq(timestamp), anySet());
    assertThat(hotspotCaptor.getValue()).hasSize(1);
    var capturedHotspot = (ServerHotspot) (hotspotCaptor.getValue().get(0));
    assertThat(capturedHotspot.getKey()).isEqualTo(hotspotKey);
    verify(hotspotDownloader).downloadFromPull(hotspotApi, projectBinding.projectKey(), "branch", Optional.empty(), cancelMonitor);
  }
}
