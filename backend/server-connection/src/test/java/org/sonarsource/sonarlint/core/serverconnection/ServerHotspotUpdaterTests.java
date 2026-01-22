/*
ACR-8f7867482ae945b58068d5250c25eb69
ACR-172fbeed80db4dbc9f4138a699afc43a
ACR-27db849ac06a49c180e96845ac8afaf0
ACR-146a762b06804c6eb24cf6d960279a80
ACR-8e1c53eeba1d4eb3be168a058e903741
ACR-bf962b8be81d48bc838cd1a6010cc199
ACR-f535bf0b558b484780b4693a23b29aac
ACR-afd8c38887f0473f96ac11f455ac8981
ACR-69014a6f57b544af81b9b09e265caeaf
ACR-2e426096efe147618a456ea942550341
ACR-0d7e4767c0a0487781c931f2598d823c
ACR-4e6e39c1e5c7455989a7c4481b55aa3d
ACR-d2b4a52fb29e4718bb40181e51df1eec
ACR-3908df8ec7414b3ba3c5dca1d691bf62
ACR-0df64b0b50cd4a16aa6ad7df64f87573
ACR-cef0148ddbe345d5860badd6a09275de
ACR-a506685b4a7a4f0c8012a0bf51b0e0a7
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
