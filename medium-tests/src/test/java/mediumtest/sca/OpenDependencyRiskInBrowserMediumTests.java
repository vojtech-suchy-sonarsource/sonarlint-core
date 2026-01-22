/*
ACR-1fdcb2f8bb9d4147a95d072940ec0cd3
ACR-8fa50000fd3c4459bcf87e176f90b4b8
ACR-64bd6a12376c47eb95fe121f65d711d8
ACR-711d4c544d2a4ff4aae878beb9eb2019
ACR-f3601bfafda64410aed383a8819bf94a
ACR-ff70ef1f230b41b988a758c9c280238d
ACR-259da630ad63407482dda4130fdaffed
ACR-95e64955e1dd4652ad99428deba046fe
ACR-384392e6c9364515998822ec61ebb4a7
ACR-65cfc2fa457f49e8a7216a5de96f0065
ACR-40801700498a4544a7b19c50384747be
ACR-af1201ab957b4abe8c5168cf88ae5720
ACR-bfb2a9e06fca4cad9ffdb2668bedb044
ACR-e47903585a284f8f8cf0b3fa8322f0ba
ACR-1aa8301e31d94e5abbc79ade4d9b940c
ACR-758cf85371644f1d9ba53e74939b861d
ACR-7b7188880ab34faea56666104c8f862d
 */
package mediumtest.sca;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.OpenDependencyRiskInBrowserParams;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;

class OpenDependencyRiskInBrowserMediumTests {
  static final String CONNECTION_ID = "connectionId";
  static final String SCOPE_ID = "scopeId";
  static final String PROJECT_KEY = "projectKey";
  static final UUID DEPENDENCY_KEY = UUID.randomUUID();
  static final String BRANCH_NAME = "master";

  @SonarLintTest
  void it_should_open_dependency_risk_in_sonarqube(SonarLintTestHarness harness) throws IOException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, "http://localhost:12345", storage -> storage.withProject(PROJECT_KEY, project -> project.withMainBranch(BRANCH_NAME)))
      .withBoundConfigScope(SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withTelemetryEnabled()
      .start(fakeClient);

    backend.getDependencyRiskService().openDependencyRiskInBrowser(new OpenDependencyRiskInBrowserParams(
      SCOPE_ID, DEPENDENCY_KEY)).join();

    var expectedUrl = String.format("http://localhost:12345/dependency-risks/%s/what?id=%s&branch=%s",
      urlEncode(DEPENDENCY_KEY.toString()), urlEncode(PROJECT_KEY), urlEncode(BRANCH_NAME));

    verify(fakeClient, timeout(5000)).openUrlInBrowser(new URL(expectedUrl));
    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getDependencyRiskInvestigatedRemotelyCount()).isEqualTo(1));
  }

  @SonarLintTest
  void it_should_not_open_dependency_risk_if_unbound(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(SCOPE_ID)
      .start(fakeClient);

    var result = backend.getDependencyRiskService().openDependencyRiskInBrowser(new OpenDependencyRiskInBrowserParams(
      SCOPE_ID, DEPENDENCY_KEY));

    assertThat(result).failsWithin(Duration.ofSeconds(2)).withThrowableOfType(ExecutionException.class)
      .withMessage("org.eclipse.lsp4j.jsonrpc.ResponseErrorException: Configuration scope 'scopeId' is not bound properly, unable to open dependency risk");
    verify(fakeClient, timeout(5000).times(0)).openUrlInBrowser(any(URL.class));
  }
}
