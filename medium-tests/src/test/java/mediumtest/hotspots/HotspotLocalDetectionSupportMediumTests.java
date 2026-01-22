/*
ACR-46c9c5c0f4b04be1b1f74a34db1ca930
ACR-3fcd52d4c3b64433b3742b123c2abf15
ACR-0843656b7f874ce7ab5ef432d0497d0f
ACR-191cbd42d52b4d419c9deafb405a59e5
ACR-f6d308baa8ea4d45bdf0bfd4455f3b5f
ACR-61189c7b0409416d9e2997ca5ea2ca22
ACR-7d4182799d9d43a0bd577aabeb106f31
ACR-9ded45e5e0fa403090145502facb3886
ACR-633f9546fd11412a8c37e04855a2194a
ACR-b265c65c08914706887cccc4e0691542
ACR-44a35b53025c4b2c9eef73e429593350
ACR-77d29ffd9570436283f4f2eb25d1a9b6
ACR-e040f9ef538f495b97ec151a86250dde
ACR-4200e7241e3744cc930357d1429bc6f3
ACR-78816d4419df4a54926ce1ef8b141aef
ACR-ef5a4b5f7cda4a96a6d4987cd5d7cefc
ACR-ca011d0d561a4fe3b3969ab6ff8f9de9
 */
package mediumtest.hotspots;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckLocalDetectionSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.CheckLocalDetectionSupportedResponse;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;

class HotspotLocalDetectionSupportMediumTests {

  @SonarLintTest
  void it_should_fail_when_the_configuration_scope_id_is_unknown(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();

    var future = backend.getHotspotService().checkLocalDetectionSupported(new CheckLocalDetectionSupportedParams("configScopeId"));

    assertThat(future)
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("The provided configuration scope does not exist: configScopeId");
  }

  @SonarLintTest
  void it_should_fail_when_the_configuration_scope_is_bound_to_an_unknown_connection(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var future = backend.getHotspotService().checkLocalDetectionSupported(new CheckLocalDetectionSupportedParams("configScopeId"));

    assertThat(future)
      .failsWithin(Duration.ofSeconds(2))
      .withThrowableOfType(ExecutionException.class)
      .havingCause()
      .isInstanceOf(ResponseErrorException.class)
      .withMessage("The provided configuration scope is bound to an unknown connection: connectionId");
  }

  @SonarLintTest
  void it_should_not_support_local_detection_in_standalone_mode(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withUnboundConfigScope("configScopeId")
      .start();

    var checkResponse = checkLocalDetectionSupported(backend, "configScopeId");

    assertThat(checkResponse)
      .extracting(CheckLocalDetectionSupportedResponse::isSupported, CheckLocalDetectionSupportedResponse::getReason)
      .containsExactly(false, "The project is not bound, please bind it to SonarQube (Server, Cloud)");
  }

  @SonarLintTest
  void it_should_support_local_detection_when_connected_to_sonarcloud(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarCloudConnection("connectionId", "orgKey")
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var checkResponse = checkLocalDetectionSupported(backend, "configScopeId");

    assertThat(checkResponse)
      .extracting(CheckLocalDetectionSupportedResponse::isSupported, CheckLocalDetectionSupportedResponse::getReason)
      .containsExactly(true, null);
  }

  @SonarLintTest
  void it_should_support_local_detection_when_connected_to_sonarqube(SonarLintTestHarness harness) {
    var configScopeId = "configScopeId";
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", storage -> storage.withServerVersion("9.9")
        .withProject("projectKey"))
      .withBoundConfigScope(configScopeId, "connectionId", "projectKey")
      .start();

    var checkResponse = checkLocalDetectionSupported(backend, configScopeId);

    assertThat(checkResponse)
      .extracting(CheckLocalDetectionSupportedResponse::isSupported, CheckLocalDetectionSupportedResponse::getReason)
      .containsExactly(true, null);
  }

  private CheckLocalDetectionSupportedResponse checkLocalDetectionSupported(SonarLintTestRpcServer backend, String configScopeId) {
    try {
      return backend.getHotspotService().checkLocalDetectionSupported(new CheckLocalDetectionSupportedParams(configScopeId)).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
