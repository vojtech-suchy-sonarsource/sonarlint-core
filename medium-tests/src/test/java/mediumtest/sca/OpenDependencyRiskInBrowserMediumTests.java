/*
ACR-a5564f5118094cce943e00d04e81419c
ACR-555b90e8d98f405caa5bb1994fb0bc99
ACR-8bc763e4a52e4b1fb394d8175a5491cf
ACR-73c5bee7313b4026af46e7355cce8565
ACR-104cbea4f3c14d72919d2251e590ffb7
ACR-b605cce8cd4a4709a9607782d08ad2bb
ACR-a6e9e4d00b3c42aa8594b0b0959fe337
ACR-078428645bf745aea6d6edf1c54340e8
ACR-721cf635508548f59f2f85d21509bfd3
ACR-82389f5c829b485bb05af6a37e7e5b59
ACR-056e85a1841f4e7b83ab89ad10dd99d7
ACR-4abc2b3e8dc2491d9e39d460b4a5f25c
ACR-7b0b3b67d72c4a5ba468fce86f8cee26
ACR-4de2cd1391ce4cb3875ea24eed4c2732
ACR-5d9b4fba78c948779589c870ebe8315d
ACR-79d7b97729f84767a860081aa189b0dc
ACR-04c8a5caa39344f69346c464d6843076
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
