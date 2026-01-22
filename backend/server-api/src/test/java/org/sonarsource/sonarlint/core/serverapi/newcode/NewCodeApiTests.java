/*
ACR-0a487ee5d0f544a3b97997f3522b092f
ACR-cba08c5e7c924694b56051d6b0652483
ACR-d59e87de891f4298ab27f6d13dd61184
ACR-16441a519ce744eba6d5e5f173dfce6a
ACR-7585658810b7499c819c36243a3b0d52
ACR-57693023d2f842c79c499721b6ae255e
ACR-b41152b7e2744b679139219ffae47f91
ACR-33426248c317438b835407f9b5386714
ACR-d0fb4160073449bab81b4a73aad1c2b8
ACR-5cc76bc311f74d368d0901176e85854b
ACR-5575df0de5674646990efba15a608e7e
ACR-fa2f4bdf149c46b69e86c327db98d316
ACR-231e3d540cf14510be4ffc4e116cc639
ACR-b90b9ce059644be8b1a7fe94ad5710fc
ACR-75984e40c8ee4ac3a7e5cf39ae0ad99e
ACR-7fe4834a21944a1fb84777d7ffc8d01b
ACR-fe90667e4cc54ac5b3a002863d5c2064
 */
package org.sonarsource.sonarlint.core.serverapi.newcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Measures;
import org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.serverapi.newcode.NewCodeApi.getPeriodForServer;
import static org.sonarsource.sonarlint.core.serverapi.newcode.NewCodeApi.getPeriodFromWs;

class NewCodeApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String PROJECT = "project";
  private static final String BRANCH = "branch";
  private static final Version RECENT_SQ_VERSION = Version.create("10.2");
  private static final Version SC_VERSION = Version.create("8.0.0.46314");
  private static final String SOME_DATE = "2023-08-29T09:37:59+0000";
  private static final long SOME_DATE_EPOCH_MILLIS = ServerApiUtils.parseOffsetDateTime(SOME_DATE).toInstant().toEpochMilli();

  private ServerApiHelper mockApiHelper;

  private NewCodeApi underTest;

  @BeforeEach
  void setup() {
    mockApiHelper = mock(ServerApiHelper.class);
    underTest = new NewCodeApi(mockApiHelper);
  }

  @Test
  void getPeriodForNewSonarQube() {
    var response = Measures.ComponentWsResponse
      .newBuilder().setPeriod(Measures.Period.newBuilder()
        .setDate(SOME_DATE).build())
      .build();

    var period = getPeriodFromWs(response);

    assertThat(period.getDate()).isEqualTo(SOME_DATE);
  }

  @Test
  void getPeriodsForOldSonarQubeOrSonarCloud() {
    var response = Measures.ComponentWsResponse
      .newBuilder().setPeriods(Measures.Periods.newBuilder().addPeriods(Measures.Period.newBuilder()
        .setDate(SOME_DATE).build()).build())
      .build();

    var period = getPeriodFromWs(response);

    assertThat(period.getDate()).isEqualTo(SOME_DATE);
  }

  @Test
  void getPeriodFromServer() {
    var serverApiHelper = mock(ServerApiHelper.class);
    when(serverApiHelper.isSonarCloud()).thenReturn(true);

    var sonarCloud = getPeriodForServer(serverApiHelper, Version.create("9.2"));
    when(serverApiHelper.isSonarCloud()).thenReturn(false);
    var sonarQubeOld = getPeriodForServer(serverApiHelper, Version.create("8.0"));
    var sonarQubeNew = getPeriodForServer(serverApiHelper, Version.create("8.1"));

    assertThat(sonarCloud).isEqualTo("periods");
    assertThat(sonarQubeOld).isEqualTo("periods");
    assertThat(sonarQubeNew).isEqualTo("period");
  }

  @Test
  void parseReferenceBranchPeriod() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("REFERENCE_BRANCH")
      .setParameter("referenceBranch")
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeReferenceBranch.class)
      .hasToString("Current new code definition (reference branch) is not supported");
    assertThat(newCodeDefinition.isOnNewCode(0)).isTrue();
    assertThat(newCodeDefinition.isSupported()).isFalse();

  }

  @Test
  void parseNumberOfDaysPeriodFromSq() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("NUMBER_OF_DAYS")
      .setParameter("42")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeNumberOfDaysWithDate.class)
      .hasToString("From last 42 days");
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parseNumberOfDaysPeriodFromSc() {
    prepareScWsResponseWithPeriods(Measures.Period.newBuilder()
      .setMode("days")
      .setParameter("42")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, SC_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeNumberOfDaysWithDate.class)
      .hasToString("From last 42 days");
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parsePreviousVersionPeriodFromSq() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("PREVIOUS_VERSION")
      .setParameter("version")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodePreviousVersion.class)
      .hasToString("Since version version");
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parsePreviousVersionPeriodWithoutVersionFromSq() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("PREVIOUS_VERSION")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodePreviousVersion.class)
      .hasToString("Since " + NewCodeDefinition.formatEpochToDate(SOME_DATE_EPOCH_MILLIS));
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parsePreviousVersionPeriodFromSc() {
    prepareScWsResponseWithPeriods(Measures.Period.newBuilder()
      .setMode("previous_version")
      .setParameter("version")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, SC_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodePreviousVersion.class)
      .hasToString("Since version version");
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parseSpecificAnalysisPeriodFromSq() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("SPECIFIC_ANALYSIS")
      .setParameter("someAnalysisKey")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    var date = NewCodeDefinition.formatEpochToDate(SOME_DATE_EPOCH_MILLIS);
    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeSpecificAnalysis.class)
      .hasToString("Since analysis from " + date);
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parseSpecificVersionPeriodFromSc() {
    prepareScWsResponseWithPeriods(Measures.Period.newBuilder()
      .setMode("version")
      .setParameter("X.Y.Z")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, SC_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    var date = NewCodeDefinition.formatEpochToDate(SOME_DATE_EPOCH_MILLIS);
    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeSpecificAnalysis.class)
      .hasToString("Since analysis from " + date);
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parseSpecificDatePeriodFromSc() {
    prepareScWsResponseWithPeriods(Measures.Period.newBuilder()
      .setMode("date")
      .setDate(SOME_DATE)
      .build());

    var newCodeDefinition = underTest.getNewCodeDefinition(PROJECT, BRANCH, SC_VERSION, new SonarLintCancelMonitor()).orElseThrow();

    var date = NewCodeDefinition.formatEpochToDate(SOME_DATE_EPOCH_MILLIS);
    assertThat(newCodeDefinition).isInstanceOf(NewCodeDefinition.NewCodeSpecificAnalysis.class)
      .hasToString("Since analysis from " + date);
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS + 1)).isTrue();
    assertThat(newCodeDefinition.isOnNewCode(SOME_DATE_EPOCH_MILLIS - 1)).isFalse();
    assertThat(newCodeDefinition.isSupported()).isTrue();
  }

  @Test
  void parseUnknownModePeriod() {
    prepareSqWsResponseWithPeriod(Measures.Period.newBuilder()
      .setMode("Definitely not a supported mode")
      .setParameter("Whatever")
      .build());
    assertThat(underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor())).isEmpty();
  }

  @Test
  void failHttpCall() {
    when(mockApiHelper.get(anyString(), any(SonarLintCancelMonitor.class)))
      .thenThrow(new RuntimeException("Not good"));
    assertThat(underTest.getNewCodeDefinition(PROJECT, BRANCH, RECENT_SQ_VERSION, new SonarLintCancelMonitor())).isEmpty();
  }

  void prepareSqWsResponseWithPeriod(Measures.Period period) {
    when(mockApiHelper.isSonarCloud()).thenReturn(false);
    var httpResponse = mock(HttpClient.Response.class);
    when(httpResponse.bodyAsStream()).thenReturn(Measures.ComponentWsResponse.newBuilder()
      .setPeriod(period)
      .build().toByteString().newInput());
    when(mockApiHelper.get(eq("/api/measures/component.protobuf?additionalFields=period&metricKeys=projects&component=" + PROJECT + "&branch=" + BRANCH), any(SonarLintCancelMonitor.class)))
      .thenReturn(httpResponse);
  }

  void prepareScWsResponseWithPeriods(Measures.Period period) {
    when(mockApiHelper.isSonarCloud()).thenReturn(true);
    var httpResponse = mock(HttpClient.Response.class);
    when(httpResponse.bodyAsStream()).thenReturn(Measures.ComponentWsResponse.newBuilder()
      .setPeriods(Measures.Periods.newBuilder()
        .addPeriods(period)
        .build())
      .build().toByteString().newInput());
    when(mockApiHelper.get(eq("/api/measures/component.protobuf?additionalFields=periods&metricKeys=projects&component=" + PROJECT + "&branch=" + BRANCH), any(SonarLintCancelMonitor.class)))
      .thenReturn(httpResponse);
  }
}
