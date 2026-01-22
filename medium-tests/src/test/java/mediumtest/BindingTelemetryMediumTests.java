/*
ACR-66047ad500bf4a6293bddf2638c0f98a
ACR-acad6691a7054560ab68099ac2996e73
ACR-6745013fa2724a5fb9091ed90f33efd7
ACR-ca92cc2dc0f246b6b106f0446be6ad3c
ACR-2efafc75f004440c892192aa48f1e2b8
ACR-e5252cb64a814977971e34d8daa4c206
ACR-91df1b95152c4c86bb264b84ee483a19
ACR-7538e4b22cc14ef9964966f877335a55
ACR-7f8d4f59f3d3485bbf22933f235db0a3
ACR-5cc26f6285274858aec01822a0e2429b
ACR-1d866d523ef446ad99bc7ee9f4b99464
ACR-8ce8d22e3ee54d8c8b80b37e3dfb1896
ACR-35dabad1a8d944f4b99881175bf20355
ACR-8532f6e16f5b489298750f6fe2f7d32d
ACR-86ed3ac287e6491ab2dc191c44b7f5b6
ACR-2d8ee6e859a243e1bb4247c84d443544
ACR-dd87367682b94b1987bef66d3bb1f55a
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
