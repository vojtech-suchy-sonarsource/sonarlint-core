/*
ACR-023aed003751426f8c413168f5b1e8bd
ACR-ba512b656b594e89bd5365b341371f8a
ACR-eed34ea1fa124529854bf004c6975ef6
ACR-773ec23e51d44dbb8b84ed98f927546e
ACR-7ae42295fae44ea9b89133b799927518
ACR-620cf6be6f0f4cfaad77fab14f00fa69
ACR-af7bb6e766064b2e8f0851e00ece4db5
ACR-c4acdaef2abe4b64ad0a454c473803ad
ACR-a7335064138840fe9db28aca8a86d407
ACR-42a04d38b1fc4e6fa45d7bde4551efc7
ACR-bfe5951eedf04e3582a832cb270a72ee
ACR-ebe105b26e4c40cc8f1f7cddf51d3bd8
ACR-81a9591561544416af9d3e25622507fe
ACR-150c06ddef61459fab053195de181be7
ACR-1bc50afe5ed14a13a625b63ffb3ec861
ACR-98ba1a40961c44ab98e7979b9f0a2fdb
ACR-e314dce2e48449d5ae7c7388dc17cb2b
 */
package mediumtest.sca;

import java.util.concurrent.CompletionException;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedResponse;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CheckDependencyRisksSupportedMediumTests {

  private static final String CONFIG_SCOPE_ID = "configScopeId";
  private static final String CONNECTION_ID = "connectionId";
  private static final String PROJECT_KEY = "projectKey";

  @SonarLintTest
  void it_should_fail_when_config_scope_not_found() {
    var harness = new SonarLintTestHarness();
    var backend = harness.newBackend().start();

    var throwable = catchThrowable(() -> checkSupported(backend, "unknown-scope"));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .hasCauseInstanceOf(ResponseErrorException.class);
    var responseErrorException = (ResponseErrorException) throwable.getCause();
    assertThat(responseErrorException.getResponseError().getCode()).isEqualTo(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_FOUND);
    assertThat(responseErrorException.getResponseError().getMessage()).contains("does not exist: unknown-scope");
  }

  @SonarLintTest
  void it_should_fail_when_config_scope_not_bound() {
    var harness = new SonarLintTestHarness();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isFalse();
    assertThat(response.getReason()).contains("not bound").contains("2025.4");
  }

  @SonarLintTest
  void it_should_fail_when_connection_not_found() {
    var harness = new SonarLintTestHarness();
    var backend = harness.newBackend()
      .withBoundConfigScope(CONFIG_SCOPE_ID, "missing-connection", PROJECT_KEY)
      .start();

    var throwable = catchThrowable(() -> checkSupported(backend, CONFIG_SCOPE_ID));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .hasCauseInstanceOf(ResponseErrorException.class);
    var responseErrorException = (ResponseErrorException) throwable.getCause();
    assertThat(responseErrorException.getResponseError().getCode()).isEqualTo(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND);
    assertThat(responseErrorException.getResponseError().getMessage()).contains("unknown connection");
  }

  @SonarLintTest
  void it_should_succeed_on_sonarcloud() {
    var harness = new SonarLintTestHarness();
    var backend = harness.newBackend()
      .withSonarCloudConnection(CONNECTION_ID, storage -> storage
        .withServerFeature(Feature.SCA))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isTrue();
    assertThat(response.getReason()).isNull();
  }

  @SonarLintTest
  void it_should_fail_when_server_version_too_old() {
    var harness = new SonarLintTestHarness();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withServerVersion("2025.3"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isFalse();
    assertThat(response.getReason()).contains("lower than the minimum supported version 2025.4");
  }

  @SonarLintTest
  void it_should_fail_when_sca_disabled() {
    var harness = new SonarLintTestHarness();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerVersion("2025.4"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isFalse();
    assertThat(response.getReason()).contains("does not have Advanced Security enabled");
  }

  @SonarLintTest
  void it_should_fail_when_server_info_missing() {
    var harness = new SonarLintTestHarness();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var throwable = catchThrowable(() -> checkSupported(backend, CONFIG_SCOPE_ID));

    assertThat(throwable)
      .isInstanceOf(CompletionException.class)
      .hasCauseInstanceOf(ResponseErrorException.class);
    var responseErrorException = (ResponseErrorException) throwable.getCause();
    assertThat(responseErrorException.getResponseError().getCode()).isEqualTo(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND);
    assertThat(responseErrorException.getResponseError().getMessage()).contains("Could not retrieve server information");
  }

  @SonarLintTest
  void it_should_succeed_when_all_conditions_met() {
    var harness = new SonarLintTestHarness();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withServerVersion("2025.4"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isTrue();
    assertThat(response.getReason()).isNull();
  }

  @SonarLintTest
  void it_should_succeed_with_newer_server_version() {
    var harness = new SonarLintTestHarness();
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withServerVersion("2025.5"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isTrue();
    assertThat(response.getReason()).isNull();
  }

  private CheckDependencyRiskSupportedResponse checkSupported(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getDependencyRiskService().checkSupported(new CheckDependencyRiskSupportedParams(configScopeId)).join();
  }
}
