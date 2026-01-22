/*
ACR-c7568f80529d41e093b9a74bea6cb24e
ACR-b28a687d624643d8b401992be009af1a
ACR-d3c79609e9dc4d6b991a2603fcf67887
ACR-b190b25284624ee0a4e1f02671910bad
ACR-1499a80442e842a5912f9f18dd418b55
ACR-56a98f58e50346b3bf6bffeff6b2c9f5
ACR-c98370db789c474ba7a43139b3ad8c58
ACR-f20fb395a299481db253738c0706a753
ACR-9036ff590cb5441389c40cd14056ee69
ACR-35d3e5c6ae864b7eb4e2168174f921ea
ACR-49c21da20f634d91b5806f6c75f750e3
ACR-2482e5b3c27449f59d82a672aaf5d51a
ACR-fc93f52b07954faf8f181f2c74c9b719
ACR-b1912aae62a04f548d3cb021294c87ad
ACR-e99a71b991d242c594dfe4beb42936af
ACR-730a6a45bcc04e8a812e59f0c3c36bbb
ACR-5e8fe2bf439d4cacad36ffc574257b00
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
