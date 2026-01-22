/*
ACR-cad0051f2ade442ebcd28fc9ed701594
ACR-60e0fb3b2d544b22ad26677e44dde88b
ACR-01953f5bb980449da9ca989dc5edfb54
ACR-ef5b867e6c0b441ca23fc4f39b383e93
ACR-38b3b7569e024e88b1cab6110835a4ef
ACR-cca50756bb5a490a82d7a1f5d8e0ae3f
ACR-0846aa8c47dd476c8035d6fa912119c7
ACR-5187ffd8f4f24683af32c13920431587
ACR-0198358fad264a4099c3d231ddcd1878
ACR-9f4cb3d1bd3143688c6a9c4c2a08d445
ACR-4d47ad85ce7e4e4dbfd481dc2aef8ab6
ACR-ad53385cb97a4715824893648f967379
ACR-f46b806373f641a6b918f4239d6d5023
ACR-20871573dc234b5eb0b8e4e78454e168
ACR-675cb126ac814ea4968f82cc655012bc
ACR-1329076e43ba41729dd084055a483fbe
ACR-ff2a7061fde547abaa473930dd7d5130
 */
package mediumtest.synchronization;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidChangeCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.EffectiveRuleDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsParams;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.PROJECT_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture.newBackend;
import static org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture.newFakeClient;
import static org.sonarsource.sonarlint.core.test.utils.server.ServerFixture.newSonarQubeServer;

class ConnectionSyncMediumTests {
  public static final String CONNECTION_ID = "connectionId";
  public static final String SCOPE_ID = "scopeId";

  @SonarLintTest
  void it_should_cache_extracted_rule_metadata_per_connection(SonarLintTestHarness harness) {
    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .start(client);
    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Binding suggestion computation queued for config scopes 'scopeId'..."));

    assertThat(client.getLogMessages()).doesNotContain("Extracting rules metadata for connection 'connectionId'");

    //ACR-40ae1f1c6ccf43baab846a711569f810
    getEffectiveRuleDetails(backend, SCOPE_ID, "java:S106");
    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Extracting rules metadata for connection 'connectionId'"));

    //ACR-4435ba3bfd984baf867dd05cfaca6c58
    client.clearLogs();

    getEffectiveRuleDetails(backend, SCOPE_ID, "java:S106");
  }

  @SonarLintTest
  void it_should_evict_cache_when_connection_is_removed(SonarLintTestHarness harness) {
    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .start(client);
    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Binding suggestion computation queued for config scopes 'scopeId'..."));
    getEffectiveRuleDetails(backend, SCOPE_ID, "java:S106");

    backend.getConnectionService().didUpdateConnections(new DidUpdateConnectionsParams(List.of(), List.of()));

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Evict cached rules definitions for connection 'connectionId'"));
  }

  @SonarLintTest
  void it_should_sync_when_credentials_are_updated(SonarLintTestHarness harness) {
    var client = harness.newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var introductionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey",
        project -> project.withBranch("main",
          branch -> branch.withTaintIssue("issueKey", "rule:key", "message", "author", "file/path", "OPEN", null, introductionDate, new TextRange(1, 2, 3, 4),
            RuleType.VULNERABILITY)))
      .start();

    server.getMockServer().stubFor(get("/api/system/status").willReturn(aResponse().withStatus(404)));

    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .withBackendCapability(PROJECT_SYNCHRONIZATION, FULL_SYNCHRONIZATION)
      .start(client);
    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Error while checking if soon unsupported"));

    server.registerSystemApiResponses();

    backend.getConnectionService().didChangeCredentials(new DidChangeCredentialsParams(CONNECTION_ID));

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains(
      "Synchronizing connection 'connectionId' after credentials changed",
      "Synchronizing project branches for project 'projectKey'"));
  }

  @SonarLintTest
  void it_should_notify_client_on_invalid_token_exactly_once() {
    var status = 401;
    var client = newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var server = newSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main"))
      .withResponseCodes(responseCodes -> responseCodes.withStatusCode(status))
      .start();

    newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA).withProject("projectKey"))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .withBackendCapability(PROJECT_SYNCHRONIZATION, FULL_SYNCHRONIZATION)
      .start(client);

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Error during synchronization"));
    await().during(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getConnectionIdsWithInvalidToken(CONNECTION_ID)).isEqualTo(1));
  }

  @SonarLintTest
  void it_should_renotify_client_on_invalid_token_after_connection_is_recreated() {
    var status = 401;
    var client = newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var server = newSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main"))
      .withResponseCodes(responseCodes -> responseCodes.withStatusCode(status))
      .start();

    var backend = newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA).withProject("projectKey"))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .withBackendCapability(PROJECT_SYNCHRONIZATION, FULL_SYNCHRONIZATION)
      .start(client);
    await().untilAsserted(() -> assertThat(client.getConnectionIdsWithInvalidToken(CONNECTION_ID)).isEqualTo(1));
    backend.getConnectionService().didUpdateConnections(new DidUpdateConnectionsParams(List.of(), List.of()));
    backend.getConnectionService()
      .didUpdateConnections(new DidUpdateConnectionsParams(List.of(new SonarQubeConnectionConfigurationDto(CONNECTION_ID, server.baseUrl(), true)), List.of()));
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(SCOPE_ID, new BindingConfigurationDto(CONNECTION_ID, "projectKey", true)));

    await().untilAsserted(() -> assertThat(client.getConnectionIdsWithInvalidToken(CONNECTION_ID)).isEqualTo(2));
  }

  @SonarLintTest
  void it_should_renotify_client_on_invalid_token_after_connection_credentials_are_changed() {
    var status = 401;
    var client = newFakeClient()
      .withToken(CONNECTION_ID, "token")
      .build();

    var server = newSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main"))
      .withResponseCodes(responseCodes -> responseCodes.withStatusCode(status))
      .start();

    var backend = newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage.withPlugin(TestPlugin.JAVA).withProject("projectKey"))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(JAVA)
      .withBackendCapability(PROJECT_SYNCHRONIZATION, FULL_SYNCHRONIZATION)
      .start(client);
    await().untilAsserted(() -> assertThat(client.getConnectionIdsWithInvalidToken(CONNECTION_ID)).isEqualTo(1));
    backend.getConnectionService().didChangeCredentials(new DidChangeCredentialsParams(CONNECTION_ID));

    await().untilAsserted(() -> assertThat(client.getConnectionIdsWithInvalidToken(CONNECTION_ID)).isEqualTo(2));
  }

  private EffectiveRuleDetailsDto getEffectiveRuleDetails(SonarLintTestRpcServer backend, String configScopeId, String ruleKey) {
    try {
      return backend.getRulesService().getEffectiveRuleDetails(new GetEffectiveRuleDetailsParams(configScopeId, ruleKey, null)).get().details();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
