/*
ACR-ebfee2e04e194aab99ec33e135527452
ACR-18d09a10d2304a0eb99727249ca0793d
ACR-016ea34e78064ec79d724654517e4151
ACR-5cafc1c0e7f04b2589a6be355847d1da
ACR-b354e7d2abfc4b28bd5da39fd4b61be5
ACR-373711e266ab453e9d838369eb2e813c
ACR-3c72bbad2cf147cba5555a868443b6b3
ACR-0fc901b0de4f4e75ae83db483fcf5c93
ACR-58f4947b64764997a26b75e8bf204b54
ACR-4b991a9047e34bbb9df5399d0ac83eac
ACR-5e6bab69509a4851a72ebd58b705266d
ACR-db52876ada984e97839a0545f79a237f
ACR-b10afa3ccab440a39cd3974ba834b261
ACR-6fe412ff0768499ab26307e528fe777f
ACR-7cd98d94c768411ababbafe56a6d7cb0
ACR-1421d4948f754adbbb73cbc72810ec92
ACR-d5c9687db46e40cf98ff02d57d553dc9
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
