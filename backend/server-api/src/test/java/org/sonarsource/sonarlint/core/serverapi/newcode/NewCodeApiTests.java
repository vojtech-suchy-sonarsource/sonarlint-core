/*
ACR-329c81d938a447c7bc9496e4a3494195
ACR-e85d6b46aa3944a0852a9f479880c1da
ACR-de7609528bc74abba23215f6a4cbd143
ACR-5223d1cac3394b44b88560f7efc4b5a6
ACR-292231ed3c0c456797c71203a84bdc47
ACR-4378e466c48e470f87b9e811941416b4
ACR-d6781c2ccb364d94819dbb58b1d785a1
ACR-4d6b86c1f9ae4d838c4ceaf23088b8a0
ACR-97433b0f7d084959b15cbe18c33348fe
ACR-1de7602afb7641d791c09cfa590627cf
ACR-ed81974080494b4ba520416c9324261a
ACR-40ab9f11034447ef85e60f24c7a327cf
ACR-703df79704394b8f9c8f3e80ac67a86f
ACR-1a905d1d33084d8096ac9652670b8b57
ACR-52f4028d336e4c3098836590b55949a2
ACR-83fc8512afec4735b70293f80204ffb7
ACR-9c5b0565f1864d96af68ed8ac6abc5e4
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
