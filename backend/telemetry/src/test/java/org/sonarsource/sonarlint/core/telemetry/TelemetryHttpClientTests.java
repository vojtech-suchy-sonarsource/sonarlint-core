/*
ACR-c75d26a49b52491987aa9ab81495f04e
ACR-35ade207b97a4d4e9f61cf1bdbf1c5ec
ACR-e0029dcaf6194a4783a4114698a75f44
ACR-64a56ab36d6548ce8360f50d74153e05
ACR-58e5a19137324609976930ee19d6774e
ACR-37781c02949f437c9ee566ca7f75e4b0
ACR-6cbae83dcf194eeb94e387d428cbd40f
ACR-aa025ed533724b7db44bea568cdb4d20
ACR-09df22d4dbb34dcea46bbab1a0a087eb
ACR-add24a642a234f2f98fcc43a56610d0c
ACR-10dcdc2d8c1342dcb8040bb4e01d59cf
ACR-a45e3a1fa6db4b0684168267cf0bef48
ACR-6f95293f74ca45fa971742b192d24186
ACR-ade4635c848a41778dd3664c3da9926f
ACR-53241c0a31404014ae9c2c23dc0c838a
ACR-86fc2154d0894d82858545276c255ecb
ACR-0a57b0551c584f62b20c6677404a6c20
 */
package org.sonarsource.sonarlint.core.telemetry;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AnalysisReportingType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.McpTransportMode;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class TelemetryHttpClientTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String PLATFORM = SystemUtils.OS_NAME;
  private static final String ARCHITECTURE = SystemUtils.OS_ARCH;

  private TelemetryHttpClient underTest;

  @RegisterExtension
  static WireMockExtension telemetryMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @BeforeEach
  void setUp() {
    InitializeParams initializeParams = mock(InitializeParams.class);
    when(initializeParams.getTelemetryConstantAttributes())
      .thenReturn(new TelemetryClientConstantAttributesDto(null, "product", "version", "ideversion", Map.of("additionalKey", "additionalValue")));

    underTest = new TelemetryHttpClient(initializeParams, HttpClientProvider.forTesting(), telemetryMock.baseUrl());
  }

  @Test
  void opt_out() {
    telemetryMock.stubFor(delete("/")
      .willReturn(aResponse()));

    underTest.optOut(new TelemetryLocalStorage(), getTelemetryLiveAttributesDto());

    await().untilAsserted(() -> telemetryMock.verify(deleteRequestedFor(urlEqualTo("/"))
      .withRequestBody(
        equalToJson(
          "{\"days_since_installation\":0,\"days_of_use\":0,\"sonarlint_version\":\"version\",\"sonarlint_product\":\"product\",\"ide_version\":\"ideversion\",\"platform\":\"" + PLATFORM + "\",\"architecture\":\"" + ARCHITECTURE + "\"}",
          true, true))));
  }

  @Test
  void upload() {
    await().untilAsserted(() -> {
      assertTelemetryUploaded(false);
      assertThat(logTester.logs(Level.INFO)).noneMatch(l -> l.matches("Sending telemetry payload."));
    });
  }

  @Test
  void upload_with_telemetry_debug_enabled() {
    await().untilAsserted(() -> {
      assertTelemetryUploaded(true);
      assertThat(logTester.logs(Level.INFO)).anyMatch(l -> l.matches("Sending telemetry payload."));
      assertThat(logTester.logs(Level.INFO)).anyMatch(l -> l.contains("{\"days_since_installation\":0,\"days_of_use\":1,\"sonarlint_version\":\"version\",\"sonarlint_product\":\"product\",\"ide_version\":\"ideversion\",\"platform\":\""+ PLATFORM +"\",\"architecture\":\""+ ARCHITECTURE +"\""));
    });
  }

  private void assertTelemetryUploaded(boolean isDebugEnabled) {
    var spy = spy(underTest);
    doReturn(isDebugEnabled).when(spy).isTelemetryLogEnabled();
    telemetryMock.stubFor(post("/")
      .willReturn(aResponse()));
    var telemetryLocalStorage = new TelemetryLocalStorage();
    telemetryLocalStorage.helpAndFeedbackLinkClicked("docs");
    telemetryLocalStorage.analysisReportingTriggered(AnalysisReportingType.PRE_COMMIT_ANALYSIS_TYPE);
    telemetryLocalStorage.addQuickFixAppliedForRule("java:S107");
    telemetryLocalStorage.addQuickFixAppliedForRule("python:S107");
    telemetryLocalStorage.addNewlyFoundIssues(1);
    telemetryLocalStorage.incrementToolCalledCount("tool_name", true);
    telemetryLocalStorage.incrementToolCalledCount("tool_name", false);
    telemetryLocalStorage.addFixedIssues(2);
    telemetryLocalStorage.findingsFiltered("severity");
    telemetryLocalStorage.incrementFlightRecorderSessionsCount();
    telemetryLocalStorage.incrementMcpServerConfigurationRequestedCount();
    telemetryLocalStorage.incrementMcpRuleFileRequestedCount();
    telemetryLocalStorage.setMcpIntegrationEnabled(true);
    telemetryLocalStorage.setMcpTransportModeUsed(McpTransportMode.STDIO);
    telemetryLocalStorage.ideLabsLinkClicked("changed_file_analysis_doc");
    telemetryLocalStorage.ideLabsLinkClicked("privacy_policy");
    telemetryLocalStorage.ideLabsLinkClicked("privacy_policy");
    telemetryLocalStorage.ideLabsFeedbackLinkClicked("connected_mode");
    telemetryLocalStorage.ideLabsFeedbackLinkClicked("manage_dependency_risk");
    telemetryLocalStorage.ideLabsFeedbackLinkClicked("manage_dependency_risk");
    telemetryLocalStorage.aiHookInstalled(AiAgent.WINDSURF);
    telemetryLocalStorage.aiHookInstalled(AiAgent.WINDSURF);
    telemetryLocalStorage.campaignShown("feedback_2026_01");
    telemetryLocalStorage.campaignResolved("feedback_2026_01", "MAYBE_LATER");
    telemetryLocalStorage.campaignShown("feedback_2077_03");
    telemetryLocalStorage.campaignResolved("feedback_2077_03", "IGNORE");
    spy.upload(telemetryLocalStorage, getTelemetryLiveAttributesDto());

    telemetryMock.verify(postRequestedFor(urlEqualTo("/"))
      .withRequestBody(
        equalToJson(
          "{\"days_since_installation\":0,\"days_of_use\":1,\"sonarlint_version\":\"version\",\"sonarlint_product\":\"product\",\"ide_version\":\"ideversion\",\"platform\":\"" + PLATFORM + "\",\"architecture\":\""+ ARCHITECTURE + "\",\"additionalKey\" : \"additionalValue\",\"help_and_feedback\":{\"count_by_link\":{\"docs\":1}}}",
          true, true)));

    telemetryMock.verify(postRequestedFor(urlEqualTo("/metrics"))
      .withRequestBody(
        equalToJson(
          String.format("""
          {"sonarlint_product":"product","os":"%s","dimension":"installation", "metric_values": [
            {"key":"shared_connected_mode.manual","value":"0","type":"integer","granularity":"daily"},
            {"key":"help_and_feedback.docs","value":"1","type":"integer","granularity":"daily"},
            {"key":"analysis_reporting.trigger_count_pre_commit","value":"1","type":"integer","granularity":"daily"},
            {"key":"quick_fix.applied_count","value":"2","type":"integer","granularity":"daily"},
            {"key":"connections.attributes","value":"[{\\"userId\\":\\"user-id-sqc\\",\\"organizationId\\":\\"org-id\\"},{\\"serverId\\":\\"server-id\\"}]","type":"string","granularity":"daily"},
            {"key":"ide_issues.found","value":"1","type":"integer","granularity":"daily"},
            {"key":"ide_issues.fixed","value":"2","type":"integer","granularity":"daily"},
            {"key":"tools.tool_name_success_count","value":"1","type":"integer","granularity":"daily"},
            {"key":"tools.tool_name_error_count","value":"1","type":"integer","granularity":"daily"},
            {"key":"findings_filtered.severity","value":"1","type":"integer","granularity":"daily"},
            {"key":"flight_recorder.sessions_count","value":"1","type":"integer","granularity":"daily"},
            {"key":"mcp.configuration_requested","value":"1","type":"integer","granularity":"daily"},
            {"key":"mcp.rule_file_requested","value":"1","type":"integer","granularity":"daily"},
            {"key":"mcp.integration_enabled","value":"true","type":"boolean","granularity":"daily"},
            {"key":"mcp.transport_mode","value":"STDIO","type":"string","granularity":"daily"},
            {"key":"ide_labs.joined","value":"true","type":"boolean","granularity":"daily"},
            {"key":"ide_labs.enabled","value":"false","type":"boolean","granularity":"daily"},
            {"key":"ide_labs.link_clicked_count_changed_file_analysis_doc","value":"1","type":"integer","granularity":"daily"},
            {"key":"ide_labs.link_clicked_count_privacy_policy","value":"2","type":"integer","granularity":"daily"},
            {"key":"ide_labs.feedback_link_clicked_count_connected_mode","value":"1","type":"integer","granularity":"daily"},
            {"key":"ide_labs.feedback_link_clicked_count_manage_dependency_risk","value":"2","type":"integer","granularity":"daily"},
            {"key":"campaigns.feedback_2026_01_shown", "value":"1", "type": "integer", "granularity":"daily"},
            {"key":"campaigns.feedback_2026_01_resolution", "value":"MAYBE_LATER", "type": "string", "granularity":"daily"},
            {"key":"campaigns.feedback_2077_03_shown", "value":"1", "type": "integer", "granularity":"daily"},
            {"key":"campaigns.feedback_2077_03_resolution", "value":"IGNORE", "type": "string", "granularity":"daily"},
            {"key":"ai_hooks.windsurf_installed","value":"2","type":"integer","granularity":"daily"}
          ]}
          """, PLATFORM),
          true, true)));
  }

  @Test
  void should_not_crash_when_cannot_upload() {
    telemetryMock.stubFor(post("/")
      .willReturn(aResponse().withStatus(500)));

    underTest.upload(new TelemetryLocalStorage(), getTelemetryLiveAttributesDto());

    await().untilAsserted(() -> telemetryMock.verify(postRequestedFor(urlEqualTo("/"))));
  }

  @Test
  void should_not_crash_when_cannot_opt_out() {
    telemetryMock.stubFor(delete("/")
      .willReturn(aResponse().withStatus(500)));

    underTest.optOut(new TelemetryLocalStorage(), getTelemetryLiveAttributesDto());

    await().untilAsserted(() -> telemetryMock.verify(deleteRequestedFor(urlEqualTo("/"))));
  }

  @Test
  void failed_upload_should_log_if_debug() {
    InternalDebug.setEnabled(true);

    underTest.upload(new TelemetryLocalStorage(), getTelemetryLiveAttributesDto());

    await().untilAsserted(() -> assertThat(logTester.logs(Level.ERROR)).anyMatch(l -> l.matches("Failed to upload telemetry data: .*404.*")));
  }

  @Test
  void failed_optout_should_log_if_debug() {
    InternalDebug.setEnabled(true);

    underTest.optOut(new TelemetryLocalStorage(), getTelemetryLiveAttributesDto());

    await().untilAsserted(() -> assertThat(logTester.logs(Level.ERROR)).anyMatch(l -> l.matches("Failed to upload telemetry opt-out: .*404.*")));
  }

  private static TelemetryLiveAttributes getTelemetryLiveAttributesDto() {
    var connectionsAttributes = new ArrayList<TelemetryConnectionAttributes>();
    connectionsAttributes.add(new TelemetryConnectionAttributes("user-id-sqc", null, "org-id"));
    connectionsAttributes.add(new TelemetryConnectionAttributes(null, "server-id", null));
    var serverAttributes = new TelemetryServerAttributes(true, true, 1, 1, 1, 1, false, Collections.emptyList(), Collections.emptyList(), "3.1.7", connectionsAttributes);
    var clientAttributes = new TelemetryClientLiveAttributesResponse(Map.of("joinedIdeLabs", true, "enabledIdeLabs", false));
    return new TelemetryLiveAttributes(serverAttributes, clientAttributes);
  }
}
