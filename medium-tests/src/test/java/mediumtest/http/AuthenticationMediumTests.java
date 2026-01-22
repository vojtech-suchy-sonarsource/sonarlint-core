/*
ACR-f800bb1e1186486eb449bc35850d1579
ACR-d7535100679e41599b11061bbdfcdcce
ACR-af148241aa6a4619ada67ada97a68592
ACR-d9a0d524aa7649d8b42ef8bcb0932843
ACR-ada22bbae0bc4a95893b4777d870049f
ACR-4088d57ae6f747c4b62515175512b3c0
ACR-62325056b8134b39adb5357e2854755c
ACR-37c813efdf6c448cae1fbf4caedf3da1
ACR-f46f9e1bbba747598244654bf7042adb
ACR-345f68b4a5f644bb8dcf8d9bfb52d7b7
ACR-068365b0c48148e59c762e732794576c
ACR-62709d6cbbc24a0a8a9cdb7485eb5070
ACR-86ae145dac664aa09a338a10b05f3280
ACR-ac328cd9fc0c4f12aff1171d690a4fe3
ACR-9f83caf3e0c84b8d80524bf5df5c6c14
ACR-9c577486ecae456fa3a45e790fd230c4
ACR-085aadcb003545a78f5ff2045fd6520d
 */
package mediumtest.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;
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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class AuthenticationMediumTests {

  @RegisterExtension
  static WireMockExtension sonarqubeMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @SonarLintTest
  void it_should_authenticate_preemptively_on_sonarqube_with_login_password(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .withCredentials("connectionId", "myLogin", "myPassword")
      .build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": " +
        "\"UP\"}")));
    sonarqubeMock.stubFor(get("/api/rules/show.protobuf?key=python:S139")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Rules.ShowResponse.newBuilder()
        .setRule(Rules.Rule.newBuilder().setName("newName").setSeverity("INFO").setType(Common.RuleType.BUG).setLang("py").setHtmlNote("extendedDesc from server").build())
        .build()))));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    sonarqubeMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139"))
      .withHeader("Authorization", equalTo("Basic " + Base64.getEncoder().encodeToString("myLogin:myPassword".getBytes(StandardCharsets.UTF_8)))));
  }

  @SonarLintTest
  void it_should_authenticate_preemptively_on_sonarqube_9_9_with_token_and_basic_scheme(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .withToken("connectionId", "myToken")
      .build();
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
    sonarqubeMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"9.9\",\"status\": " +
        "\"UP\"}")));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    sonarqubeMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139"))
      .withHeader("Authorization", equalTo("Basic " + Base64.getEncoder().encodeToString("myToken:".getBytes(StandardCharsets.UTF_8)))));
  }

  @SonarLintTest
  void it_should_authenticate_preemptively_on_sonarqube_10_4_with_token_and_bearer_scheme(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .withToken("connectionId", "myToken")
      .build();
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
    sonarqubeMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.4\",\"status\": " +
        "\"UP\"}")));

    getEffectiveRuleDetails(backend, "scopeId", "python:S139");

    sonarqubeMock.verify(getRequestedFor(urlEqualTo("/api/rules/show.protobuf?key=python:S139"))
      .withHeader("Authorization", equalTo("Bearer myToken")));
  }

  @SonarLintTest
  void it_should_fail_the_request_if_credentials_are_not_returned_by_the_client(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getCredentials("connectionId")).thenReturn(null);
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status").willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": \"UP\"}")));

    var throwable = catchThrowable(() -> getEffectiveRuleDetails(backend, "scopeId", "python:S139"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .cause()
      .isInstanceOf(ResponseErrorException.class)
      .hasMessage("Internal error.");
  }

  @SonarLintTest
  void it_should_fail_the_request_if_dto_is_not_returned_by_the_client(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getCredentials("connectionId")).thenReturn(Either.forRight(null));
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status").willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": \"UP\"}")));

    var throwable = catchThrowable(() -> getEffectiveRuleDetails(backend, "scopeId", "python:S139"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .cause()
      .isInstanceOf(ResponseErrorException.class)
      .hasMessage("Internal error.");
  }

  @SonarLintTest
  void it_should_fail_the_request_if_token_is_not_returned_by_the_client(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getCredentials("connectionId")).thenReturn(Either.forLeft(new TokenDto(null)));
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status").willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": \"UP\"}")));

    var throwable = catchThrowable(() -> getEffectiveRuleDetails(backend, "scopeId", "python:S139"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .cause()
      .isInstanceOf(ResponseErrorException.class)
      .hasMessage("Internal error.");
  }

  @SonarLintTest
  void it_should_fail_the_request_if_username_is_not_returned_by_the_client(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getCredentials("connectionId")).thenReturn(Either.forRight(new UsernamePasswordDto(null, "pass")));
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status").willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": \"UP\"}")));

    var throwable = catchThrowable(() -> getEffectiveRuleDetails(backend, "scopeId", "python:S139"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .cause()
      .isInstanceOf(ResponseErrorException.class)
      .hasMessage("Internal error.");
  }

  @SonarLintTest
  void it_should_fail_the_request_if_password_is_not_returned_by_the_client(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    when(fakeClient.getCredentials("connectionId")).thenReturn(Either.forRight(new UsernamePasswordDto("user", null)));
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", sonarqubeMock.baseUrl(), storage -> storage.withProject("projectKey",
        projectStorage -> projectStorage.withRuleSet(SonarLanguage.PYTHON.getSonarLanguageKey(),
          ruleSet -> ruleSet.withActiveRule("python:S139", "INFO", Map.of("legalTrailingCommentPattern", "blah")))))
      .withBoundConfigScope("scopeId", "connectionId", "projectKey")
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(fakeClient);
    sonarqubeMock.stubFor(get("/api/system/status").willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": \"UP\"}")));

    var throwable = catchThrowable(() -> getEffectiveRuleDetails(backend, "scopeId", "python:S139"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .cause()
      .isInstanceOf(ResponseErrorException.class)
      .hasMessage("Internal error.");
  }

  private void getEffectiveRuleDetails(SonarLintTestRpcServer backend, String configScopeId, String ruleKey) {
    backend.getRulesService().getEffectiveRuleDetails(new GetEffectiveRuleDetailsParams(configScopeId, ruleKey, null)).join().details();
  }

}
