/*
ACR-96c05f21bfa447559e6cd3d1431c4874
ACR-11508eaf7147412ab905c67cbfac2adc
ACR-a4386a4a150f463fba04d207c5ca6ece
ACR-4f0cd567479f4ed1a062b27ab3d82be9
ACR-082df1d43629434cb70b1cc1762edc49
ACR-b15e5fbb9a674fd18c41e6dff181a9f3
ACR-ea3e073fda874cdc8f47e709ccb207b3
ACR-9c8d491c87154c1aa34610b92782d4ed
ACR-0c348e5d842643b2a54bca546d03a09b
ACR-9f20b8ea83c44c67a9a93497aa9537a7
ACR-8e16d613809047e2bd7a83be40a2b36f
ACR-59692713713e4e7d890bb26e94430605
ACR-2eb24cf2ae034d308bcb8950cba697a2
ACR-061fcde0a45e4e62a46aa7428d6de51b
ACR-8031ac305bb945c285fa1b8631d5e95e
ACR-a10644395e2b4dffbbaa79ac85648303
ACR-d106a7caa5ca4383a8eed853d1c2c275
 */
package mediumtest.issues;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckAnticipatedStatusChangeSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckAnticipatedStatusChangeSupportedResponse;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;

class CheckAnticipatedStatusChangeSupportedMediumTests {

  @RegisterExtension
  public final MockWebServerExtensionWithProtobuf mockWebServerExtension = new MockWebServerExtensionWithProtobuf();

  @AfterEach
  void tearDown() {
    mockWebServerExtension.shutdown();
  }

  @SonarLintTest
  void it_should_fail_when_the_connection_is_unknown(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();

    assertThat(checkAnticipatedStatusChangeSupported(backend, "configScopeId"))
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("No binding for config scope 'configScopeId'");
  }

  @SonarLintTest
  void it_should_not_be_available_for_sonarcloud(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(mockWebServerExtension.endpointParams().getBaseUrl())
      .withSonarCloudConnection("connectionId", "orgKey")
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    assertThat(checkAnticipatedStatusChangeSupported(backend, "configScopeId"))
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckAnticipatedStatusChangeSupportedResponse::isSupported)
      .isEqualTo(false);
  }

  @SonarLintTest
  void it_should_not_be_available_for_sonarqube_prior_to_10_2(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", mockWebServerExtension.endpointParams().getBaseUrl(), storage -> storage.withServerVersion("10.1"))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    assertThat(checkAnticipatedStatusChangeSupported(backend, "configScopeId"))
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckAnticipatedStatusChangeSupportedResponse::isSupported)
      .isEqualTo(false);
  }

  @SonarLintTest
  void it_should_be_available_for_sonarqube_10_2_plus(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", mockWebServerExtension.endpointParams().getBaseUrl(),  storage -> storage.withServerVersion("10.2"))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    assertThat(checkAnticipatedStatusChangeSupported(backend, "configScopeId"))
      .succeedsWithin(Duration.ofSeconds(2))
      .extracting(CheckAnticipatedStatusChangeSupportedResponse::isSupported)
      .isEqualTo(true);
  }

  private CompletableFuture<CheckAnticipatedStatusChangeSupportedResponse> checkAnticipatedStatusChangeSupported(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getIssueService().checkAnticipatedStatusChangeSupported(new CheckAnticipatedStatusChangeSupportedParams(configScopeId));
  }
}
