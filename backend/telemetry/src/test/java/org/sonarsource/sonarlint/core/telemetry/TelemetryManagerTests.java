/*
ACR-8b2fbdb50acd4378899cd3fa0fd14477
ACR-a959e31fe9da43c88e5a39f5a09a8361
ACR-573cfd31402a4bbfb760644fa265d2f9
ACR-ac44f1ce6208440ab8a5589e791e55a4
ACR-dc45dcfd29f4473b9da07b322dd247f8
ACR-a86451ce24b94ad2b231607c5b6ea6c9
ACR-7e2cc00d8158468f961e63101d642db4
ACR-7866c26b050546fb92e2d129ebf5ef15
ACR-f9aae52d3d5c4813b288c3903cefe176
ACR-77ad5791464f48f18db8e24fb3b90d9e
ACR-7865c4b475f94dabadf9b5dcb24adb41
ACR-1b5de501e4f046dd8eed6ffcaebacd33
ACR-2f26343bf23a4d4a9b951b0fc7c5b20d
ACR-0e7c9f289c1445db807ff52040eeb827
ACR-094fea3f9c074bc7a526903bfce3612f
ACR-5316465d6df14f0b86da9cd9d4d86712
ACR-b49a9ee031eb4d1097ac8c7a5bdf69c9
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.stubbing.Answer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.FixSuggestionStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.McpTransportMode;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisReportingType.PRE_COMMIT_ANALYSIS_TYPE;

class TelemetryManagerTests {
  private static final int DEFAULT_NOTIF_CLICKED = 5;
  private static final int DEFAULT_NOTIF_COUNT = 10;
  private static final int DEFAULT_HELP_AND_FEEDBACK_COUNT = 12;
  private static final int DEFAULT_ANALYSIS_REPORTING_COUNT = 16;

  private static final String FOO_EVENT = "foo_event";
  private static final String SUGGEST_FEATURE = "suggestFeature";

  private TelemetryHttpClient client;
  private TelemetryManager telemetryManager;
  private TelemetryLocalStorageManager storageManager;

  @BeforeEach
  void setUp(@TempDir Path temp) {
    client = mock(TelemetryHttpClient.class);
    storageManager = new TelemetryLocalStorageManager(temp.resolve("storage"), mock(InitializeParams.class));
    telemetryManager = new TelemetryManager(storageManager, client);
  }

  @Test
  void enable_should_trigger_upload_once_per_day() {
    var telemetryPayload = getTelemetryLiveAttributesDto();

    telemetryManager.enable(telemetryPayload);
    telemetryManager.enable(telemetryPayload);

    verify(client).upload(any(TelemetryLocalStorage.class), eq(telemetryPayload));
    verifyNoMoreInteractions(client);
  }

  @Test
  void disable_should_trigger_optout() {
    var mockStorageManager = mockTelemetryStorage();
    var manager = new TelemetryManager(mockStorageManager, client);
    var telemetryPayload = getTelemetryLiveAttributesDto();

    manager.disable(telemetryPayload);

    verify(client).optOut(any(TelemetryLocalStorage.class), eq(telemetryPayload));
    verifyNoMoreInteractions(client);
  }

  @Test
  void uploadAndClearTelemetry_should_trigger_upload_once_per_day() {
    var telemetryPayload = getTelemetryLiveAttributesDto();

    storageManager.tryUpdateAtomically(d -> d.setUsedAnalysis("java", 1000));

    var data = storageManager.tryRead();
    assertThat(data.analyzers()).isNotEmpty();
    assertThat(data.lastUploadTime()).isNull();

    telemetryManager.uploadAndClearTelemetry(telemetryPayload);

    var reloaded = storageManager.tryRead();

    //ACR-b2deb7bf977f406fb08244dc814c174e
    assertThat(reloaded.analyzers()).isEmpty();

    var lastUploadTime = reloaded.lastUploadTime();
    assertThat(lastUploadTime).isNotNull();

    telemetryManager.uploadAndClearTelemetry(telemetryPayload);

    reloaded = storageManager.tryRead();

    assertThat(reloaded.lastUploadTime()).isEqualTo(lastUploadTime);
    verify(client).upload(any(TelemetryLocalStorage.class), eq(telemetryPayload));
    verifyNoMoreInteractions(client);
  }

  @Test
  void uploadAndClearTelemetry_should_trigger_upload_if_day_changed_and_hours_elapsed() {
    var telemetryPayload = getTelemetryLiveAttributesDto();

    createAndSaveSampleData(storageManager);
    storageManager.tryUpdateAtomically(telemetryLocalStorage -> telemetryLocalStorage.setEnabled(true));
    telemetryManager.uploadAndClearTelemetry(telemetryPayload);

    var data = storageManager.tryRead();

    var lastUploadTime = data.lastUploadTime()
      .minusDays(1)
      .minusHours(TelemetryManager.MIN_HOURS_BETWEEN_UPLOAD);
    storageManager.tryUpdateAtomically(d -> d.setLastUploadTime(lastUploadTime));

    telemetryManager.uploadAndClearTelemetry(telemetryPayload);

    verify(client, times(2)).upload(any(TelemetryLocalStorage.class), eq(telemetryPayload));
    verifyNoMoreInteractions(client);
  }

  @Test
  void uploadAndClearTelemetry_should_not_trigger_upload_if_telemetry_disabled_by_user() {
    createAndSaveSampleData(storageManager);
    TelemetryLiveAttributes telemetryLiveAttributesDto = getTelemetryLiveAttributesDto();

    telemetryManager.uploadAndClearTelemetry(telemetryLiveAttributesDto);

    assertThat(storageManager.isEnabled()).isFalse();
    verify(client, never()).upload(any(TelemetryLocalStorage.class), eq(telemetryLiveAttributesDto));
    verifyNoMoreInteractions(client);
  }

  @Test
  void updateTelemetry_should_not_trigger_update_if_telemetry_disabled_by_user() {
    createAndSaveSampleData(storageManager);

    telemetryManager.updateTelemetry(telemetryLocalStorage -> telemetryLocalStorage.setNumUseDays(10));

    TelemetryLocalStorage localStorage = storageManager.tryRead();
    assertThat(localStorage.enabled()).isFalse();
    assertThat(localStorage.numUseDays()).isEqualTo(5);
  }

  @Test
  void enable_should_not_wipe_out_more_recent_data() {
    var telemetryLiveAttributes = getTelemetryLiveAttributesDto();

    createAndSaveSampleData(storageManager);

    var data = storageManager.tryRead();
    assertThat(data.enabled()).isFalse();

    //ACR-b08973ab13674489a6cf948499e47e4d
    telemetryManager.enable(telemetryLiveAttributes);

    var reloaded = storageManager.tryRead();
    assertThat(reloaded.enabled()).isTrue();
    assertThat(reloaded.installTime()).isEqualTo(data.installTime().truncatedTo(ChronoUnit.MILLIS));
    assertThat(reloaded.lastUseDate()).isEqualTo(data.lastUseDate());
    assertThat(reloaded.numUseDays()).isEqualTo(data.numUseDays());
  }

  @Test
  void disable_should_not_wipe_out_more_recent_data() {
    var telemetryPayload = getTelemetryLiveAttributesDto();

    createAndSaveSampleData(storageManager);
    storageManager.tryUpdateAtomically(data -> data.setEnabled(true));

    var data = storageManager.tryRead();
    assertThat(data.enabled()).isTrue();

    //ACR-05b4bf6ddb724084a97b0eb381e0e6e7
    telemetryManager.disable(telemetryPayload);

    var reloaded = storageManager.tryRead();
    assertThat(reloaded.enabled()).isFalse();
    assertThat(reloaded.installTime()).isEqualTo(data.installTime());
    assertThat(reloaded.lastUseDate()).isEqualTo(data.lastUseDate());
    assertThat(reloaded.numUseDays()).isEqualTo(data.numUseDays());
    assertThat(reloaded.lastUploadTime()).isEqualTo(data.lastUploadTime());
    assertThat(reloaded.notifications().get(FOO_EVENT).getDevNotificationsCount()).isEqualTo(data.notifications().get(FOO_EVENT).getDevNotificationsCount());
    assertThat(reloaded.getHelpAndFeedbackLinkClickedCounter().get(SUGGEST_FEATURE).getHelpAndFeedbackLinkClickedCount())
      .isEqualTo(data.getHelpAndFeedbackLinkClickedCounter().get(SUGGEST_FEATURE).getHelpAndFeedbackLinkClickedCount());
    assertThat(reloaded.getAnalysisReportingCountersByType().get(PRE_COMMIT_ANALYSIS_TYPE).getAnalysisReportingCount())
      .isEqualTo(data.getAnalysisReportingCountersByType().get(PRE_COMMIT_ANALYSIS_TYPE).getAnalysisReportingCount());
  }

  @Test
  void uploadAndClearTelemetry_should_clear_accumulated_data() {
    var telemetryPayload = getTelemetryLiveAttributesDto();

    createAndSaveSampleData(storageManager);
    storageManager.tryUpdateAtomically(data -> {
      data.setEnabled(true);
      data.setUsedAnalysis("java", 1000);
      data.incrementHotspotStatusChangedCount();
      data.incrementOpenHotspotInBrowserCount();
      data.incrementShowHotspotRequestCount();
      data.incrementShowIssueRequestCount();
      data.addIssuesWithPossibleAiFixFromIde(Set.of(UUID.randomUUID(), UUID.randomUUID()));
      data.fixSuggestionReceived("suggestionId", AiSuggestionSource.SONARCLOUD, 2, true);
      data.fixSuggestionResolved("suggestionId", FixSuggestionStatus.ACCEPTED, 0);
      data.incrementTaintVulnerabilitiesInvestigatedLocallyCount();
      data.incrementTaintVulnerabilitiesInvestigatedRemotelyCount();
      data.setLastUploadTime(LocalDateTime.now().minusDays(2));
      data.setNumUseDays(5);
      data.notifications().put(FOO_EVENT, new TelemetryNotificationsCounter(DEFAULT_NOTIF_COUNT, DEFAULT_NOTIF_CLICKED));
      data.getHelpAndFeedbackLinkClickedCounter().put(SUGGEST_FEATURE, new TelemetryHelpAndFeedbackCounter(DEFAULT_HELP_AND_FEEDBACK_COUNT));
      data.getAnalysisReportingCountersByType().put(PRE_COMMIT_ANALYSIS_TYPE, new TelemetryAnalysisReportingCounter(DEFAULT_ANALYSIS_REPORTING_COUNT));
      data.findingsFiltered("severity");
      data.incrementFlightRecorderSessionsCount();
      data.setMcpIntegrationEnabled(true);
      data.setMcpTransportModeUsed(McpTransportMode.HTTPS);
    });

    telemetryManager.uploadAndClearTelemetry(telemetryPayload);

    var reloaded = storageManager.tryRead();
    assertThat(reloaded.analyzers()).isEmpty();
    assertThat(reloaded.showHotspotRequestsCount()).isZero();
    assertThat(reloaded.notifications()).isEmpty();
    assertThat(reloaded.taintVulnerabilitiesInvestigatedLocallyCount()).isZero();
    assertThat(reloaded.taintVulnerabilitiesInvestigatedRemotelyCount()).isZero();
    assertThat(reloaded.hotspotStatusChangedCount()).isZero();
    assertThat(reloaded.getShowIssueRequestsCount()).isZero();
    assertThat(reloaded.getCountIssuesWithPossibleAiFixFromIde()).isZero();
    assertThat(reloaded.getFixSuggestionReceivedCounter()).isEmpty();
    assertThat(reloaded.getFixSuggestionResolved()).isEmpty();
    assertThat(reloaded.openHotspotInBrowserCount()).isZero();
    assertThat(reloaded.getHelpAndFeedbackLinkClickedCounter()).isEmpty();
    assertThat(reloaded.getAnalysisReportingCountersByType()).isEmpty();
    assertThat(reloaded.getFindingsFilteredCountersByType()).isEmpty();
    assertThat(reloaded.getFlightRecorderSessionsCount()).isZero();
    assertThat(reloaded.isMcpIntegrationEnabled()).isFalse();
    assertThat(reloaded.getMcpTransportModeUsed()).isNull();
  }

  private void createAndSaveSampleData(TelemetryLocalStorageManager storage) {
    storage.tryUpdateAtomically(data -> {
      data.setEnabled(false);
      data.setInstallTime(OffsetDateTime.now().minusDays(10));
      data.setLastUseDate(LocalDate.now().minusDays(3));
      data.setLastUploadTime(LocalDateTime.now().minusDays(2));
      data.setNumUseDays(5);
      data.notifications().put(FOO_EVENT, new TelemetryNotificationsCounter(DEFAULT_NOTIF_COUNT, DEFAULT_NOTIF_CLICKED));
      data.getHelpAndFeedbackLinkClickedCounter().put(SUGGEST_FEATURE, new TelemetryHelpAndFeedbackCounter(DEFAULT_HELP_AND_FEEDBACK_COUNT));
      data.getAnalysisReportingCountersByType().put(PRE_COMMIT_ANALYSIS_TYPE, new TelemetryAnalysisReportingCounter(DEFAULT_ANALYSIS_REPORTING_COUNT));
    });
  }

  private TelemetryLocalStorageManager mockTelemetryStorage() {
    var storage = mock(TelemetryLocalStorageManager.class);
    when(storage.tryRead()).thenReturn(new TelemetryLocalStorage());
    doAnswer((Answer<Void>) invocation -> {
      var args = invocation.getArguments();
      ((Consumer) args[0]).accept(mock(TelemetryLocalStorage.class));
      return null;
    }).when(storage).tryUpdateAtomically(any(Consumer.class));
    return storage;
  }

  private static TelemetryLiveAttributes getTelemetryLiveAttributesDto() {
    var serverAttributes = new TelemetryServerAttributes(true, true, 1, 1, 1, 0, false, Collections.emptyList(), Collections.emptyList(), "3.1.7", Collections.emptyList());
    var clientAttributes = new TelemetryClientLiveAttributesResponse(emptyMap());
    return new TelemetryLiveAttributes(serverAttributes, clientAttributes);
  }
}
