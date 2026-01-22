/*
ACR-8dee70b945e949719f0f8d37442d49a8
ACR-5274e0e4c615421281a2472c5b40c9f4
ACR-d9f56f87cd3a487c8ed07c161512486b
ACR-fb03556ee1944a5e9a63a4eb0d89cb36
ACR-378ed57574164f9fbe7394fda61bb17a
ACR-8a83a82957d7427a9cc922bcc38d4e73
ACR-0446e7a294e34258b7889b5306b7c4ec
ACR-8906cca2352a4faa8eb2a7163dc69eed
ACR-9f0c81d0279a4fb08948a75bfcad4829
ACR-269351d6c44a4f4fb910c6520382a9d1
ACR-248da6bb530b40008860beae0dea0907
ACR-4876e8cff8f640719ece294298db57fc
ACR-16ce4d4ccc2242e192837df848689147
ACR-c561b4db992c4568ba8ae535de3fc8db
ACR-ee43335e889e46e58d1e963b295ce0fc
ACR-7686ee24f8ba472ea7728dbbbc0caab1
ACR-bfbddd1e30f04f80bb651bff88f78fc5
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
