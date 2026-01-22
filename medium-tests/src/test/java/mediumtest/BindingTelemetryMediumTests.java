/*
ACR-1c8fa18feddc42c29a951fa9c671d1ee
ACR-6758166a69d54278b9bfdee4a0bb2590
ACR-227698c16caf40acbbcaf06db83fc608
ACR-758776d855ac4fd99e74ea934e6ec81c
ACR-ce1d2ada5ebe431b9c6ee7ae2e0cc49e
ACR-eba323fb283e4ae4a2c41ef1e333bc3d
ACR-af3f77210dbb4d71946c0b9e85402e38
ACR-45afef2028dc4822ab53b83925b1d743
ACR-01943ba17e79498bbccc686b09599fca
ACR-a2206186b0d84566bcba024e8ddd367b
ACR-5015be3d55fb47af834282df9865d763
ACR-f40fee2fb22c4fc6818320b84bd402ff
ACR-e15d2bc4ad1c460a8e6a4481bda06ee4
ACR-6a9fab18cda5454cace39bb649d4211c
ACR-869c2eb08b9e43d4945a495b2a5a8a9f
ACR-6bc3da50b6a047c2bc8a27c948b9416d
ACR-dece5e24292f4c5cacf269b76564cd23
 */
package mediumtest;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingMode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AcceptedBindingSuggestionParams;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class BindingTelemetryMediumTests {

  private static final String CONNECTION_ID = "connectionId";
  private static final String CONFIG_SCOPE_ID = "scopeId";
  private static final String PROJECT_KEY = "projectKey";

  @SonarLintTest
  void should_count_new_binding_from_suggestion_remote_url(SonarLintTestHarness harness) {
    var backend = setupBackendUnboundWithTelemetry(harness);

    backend.getTelemetryService().acceptedBindingSuggestion(new AcceptedBindingSuggestionParams(
      BindingSuggestionOrigin.REMOTE_URL));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getNewBindingsRemoteUrlCount()).isEqualTo(1));
  }

  @SonarLintTest
  void should_count_new_binding_from_suggestion_project_name(SonarLintTestHarness harness) {
    var backend = setupBackendUnboundWithTelemetry(harness);

    backend.getTelemetryService().acceptedBindingSuggestion(new AcceptedBindingSuggestionParams(
      BindingSuggestionOrigin.PROJECT_NAME));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getNewBindingsProjectNameCount()).isEqualTo(1));
  }

  @SonarLintTest
  void should_count_new_binding_from_suggestion_shared_configuration(SonarLintTestHarness harness) {
    var backend = setupBackendUnboundWithTelemetry(harness);

    backend.getTelemetryService().acceptedBindingSuggestion(new AcceptedBindingSuggestionParams(
      BindingSuggestionOrigin.SHARED_CONFIGURATION));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getNewBindingsSharedConfigurationCount()).isEqualTo(1));
  }

  @SonarLintTest
  void should_count_new_binding_from_suggestion_properties_file(SonarLintTestHarness harness) {
    var backend = setupBackendUnboundWithTelemetry(harness);

    backend.getTelemetryService().acceptedBindingSuggestion(new AcceptedBindingSuggestionParams(
      BindingSuggestionOrigin.PROPERTIES_FILE));

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getNewBindingsPropertiesFileCount()).isEqualTo(1));
  }

  @SonarLintTest
  void should_not_count_when_suggestion_origin_is_missing(SonarLintTestHarness harness) {
    var backend = setupBackendUnboundWithTelemetry(harness);

    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(
      CONFIG_SCOPE_ID,
      new BindingConfigurationDto(CONNECTION_ID, PROJECT_KEY, true),
      BindingMode.FROM_SUGGESTION,
      null
    ));

    await().untilAsserted(() -> {
      assertThat(backend.telemetryFileContent().getNewBindingsRemoteUrlCount()).isZero();
      assertThat(backend.telemetryFileContent().getNewBindingsProjectNameCount()).isZero();
      assertThat(backend.telemetryFileContent().getNewBindingsSharedConfigurationCount()).isZero();
      assertThat(backend.telemetryFileContent().getNewBindingsPropertiesFileCount()).isZero();
    });
  }

  private SonarLintTestRpcServer setupBackendUnboundWithTelemetry(SonarLintTestHarness harness) {
    return harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .withTelemetryEnabled()
      .start();
  }
}
