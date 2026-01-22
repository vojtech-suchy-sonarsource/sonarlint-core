/*
ACR-b05490e8b74246c5928ebbc793142734
ACR-5f3e77ce47fb459691132362b648bd58
ACR-0f2b054d8d1c4f9cb682e1eb7dbf740f
ACR-441ed07ce02c43fc97220fb24c033720
ACR-5856af2fa6f348b198cb41b17835cc6d
ACR-d82393cce4a54818b67755b7fcd33adf
ACR-2f1aee900d4942eba9b2e78cc37df04a
ACR-12c4e77a8838459e84f9c8b7bdaff108
ACR-f5397987ef9344b68f4d3dbbeff1f975
ACR-ca1fa050e4df430391ea568fa2511d35
ACR-5f62f69edaf2437b91dc88ae901f5cb3
ACR-7662d55757374edd80e47c264b7186f7
ACR-bbd8122237544ca792c876d0873bb766
ACR-26041e0dbda242179a1ff14349114fdf
ACR-d8ed45551bf14101b7c3ea064eeba965
ACR-089e6e7b8af148338c4e36e66eb4dc5f
ACR-4899d8c10aa343b9b17d85baf605f868
 */
package mediumtest.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.ProxyDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Rules;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class ProxyMediumTests {

  public static final String PROXY_AUTH_ENABLED = "proxy-auth";

  @RegisterExtension
  static WireMockExtension sonarqubeMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @RegisterExtension
  static WireMockExtension proxyMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @BeforeEach
  void configureProxy(TestInfo info) {
    sonarqubeMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": " +
        "\"UP\"}")));
    proxyMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": " +
        "\"UP\"}")));

    if (info.getTags().contains(PROXY_AUTH_ENABLED)) {
      proxyMock.stubFor(get(urlMatching("/api/rules/.*"))
        .inScenario("Proxy Auth")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse()
          .withStatus(407)
          .withHeader("Proxy-Authenticate", "Basic realm=\"Access to the proxy\""))
        .willSetStateTo("Challenge returned"));
      proxyMock.stubFor(get(urlMatching("/api/rules/.*"))
        .inScenario("Proxy Auth")
        .whenScenarioStateIs("Challenge returned")
        .willReturn(aResponse().proxiedFrom(sonarqubeMock.baseUrl())));
    } else {
      proxyMock.stubFor(get(urlMatching("/api/rules/.*")).willReturn(aResponse().proxiedFrom(sonarqubeMock.baseUrl())));
    }
  }

  @SonarLintTest
  void it_should_honor_http_proxy_settings(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(new ProxyDto(Proxy.Type.HTTP, "localhost", proxyMock.getPort())));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    proxyMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139")));
  }

  @SonarLintTest
  void it_should_honor_http_direct_proxy_settings(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(ProxyDto.NO_PROXY));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    sonarqubeMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139")));
    proxyMock.verify(0, getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139")));
  }

  @SonarLintTest
  @Tag(PROXY_AUTH_ENABLED)
  void it_should_honor_http_proxy_authentication(SonarLintTestHarness harness) {
    var proxyLogin = "proxyLogin";
    var proxyPassword = "proxyPassword";
    var fakeClient = harness.newFakeClient().build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(new ProxyDto(Proxy.Type.HTTP, "localhost", proxyMock.getPort())));
    when(fakeClient.getProxyPasswordAuthentication(anyString(), anyInt(), anyString(), anyString(), anyString(), any()))
      .thenReturn(new GetProxyPasswordAuthenticationResponse(proxyLogin, proxyPassword));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    proxyMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139"))
      .withHeader("Proxy-Authorization", equalTo("Basic " + Base64.getEncoder().encodeToString((proxyLogin + ":" + proxyPassword).getBytes(StandardCharsets.UTF_8)))));
  }

  @SonarLintTest
  @Tag(PROXY_AUTH_ENABLED)
  void it_should_honor_http_proxy_authentication_with_null_password(SonarLintTestHarness harness) {
    var proxyLogin = "proxyLogin";
    var fakeClient = harness.newFakeClient().build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(new ProxyDto(Proxy.Type.HTTP, "localhost", proxyMock.getPort())));
    when(fakeClient.getProxyPasswordAuthentication(anyString(), anyInt(), anyString(), anyString(), anyString(), any()))
      .thenReturn(new GetProxyPasswordAuthenticationResponse(proxyLogin, null));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    proxyMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139"))
      .withHeader("Proxy-Authorization", equalTo("Basic " + Base64.getEncoder().encodeToString((proxyLogin + ":").getBytes(StandardCharsets.UTF_8)))));
  }

  @SonarLintTest
  @Tag(PROXY_AUTH_ENABLED)
  void it_should_fail_if_proxy_port_is_smaller_than_valid_range(SonarLintTestHarness harness) {
    var proxyLogin = "proxyLogin";
    var fakeClient = harness.newFakeClient().build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(new ProxyDto(Proxy.Type.HTTP, "localhost", -1)));
    when(fakeClient.getProxyPasswordAuthentication(anyString(), anyInt(), anyString(), anyString(), anyString(), any()))
      .thenReturn(new GetProxyPasswordAuthenticationResponse(proxyLogin, null));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    assertThat(fakeClient.getLogs())
      .anySatisfy(
        l -> {
          assertThat(l.getMessage()).isEqualTo("Unable to get proxy");
          assertThat(l.getStackTrace()).contains("Port is outside the valid range for hostname: localhost");
        }
      );
  }

  @SonarLintTest
  @Tag(PROXY_AUTH_ENABLED)
  void it_should_fail_if_proxy_port_is_higher_than_valid_range(SonarLintTestHarness harness) {
    var proxyLogin = "proxyLogin";
    var fakeClient = harness.newFakeClient().build();

    when(fakeClient.selectProxies(any())).thenReturn(List.of(new ProxyDto(Proxy.Type.HTTP, "localhost", 70000)));
    when(fakeClient.getProxyPasswordAuthentication(anyString(), anyInt(), anyString(), anyString(), anyString(), any()))
      .thenReturn(new GetProxyPasswordAuthenticationResponse(proxyLogin, null));

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    assertThat(fakeClient.getLogs())
      .anySatisfy(
        l -> {
          assertThat(l.getMessage()).isEqualTo("Unable to get proxy");
          assertThat(l.getStackTrace()).contains("Port is outside the valid range for hostname: localhost");
        }
      );
  }

  private void getEffectiveRuleDetails(SonarLintTestRpcServer backend, String configScopeId, String ruleKey) {
    try {
      backend.getRulesService().getEffectiveRuleDetails(new GetEffectiveRuleDetailsParams(configScopeId, ruleKey, null)).get().details();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
