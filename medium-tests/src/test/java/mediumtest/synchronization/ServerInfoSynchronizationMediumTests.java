/*
ACR-b55a307c82054176876596dd887b6404
ACR-52776bd0437841f6911980fddd36157c
ACR-fc36aa8befc14d58aadb2cdddc3881df
ACR-92ef6fc75872436c8a3395e25a30e962
ACR-08d08f7b188b41efad235ce7e3f34ce9
ACR-cd4f71fe558943c5823504c77bafc502
ACR-38237a1b87d74e7182d2707c03a8be49
ACR-9800e43df37f4e4f93e70b60f2f58596
ACR-b7b6e0e257844d1191fe072e99c8b7be
ACR-b00dfb3711b34ec88269776296974a7f
ACR-23843ba64c3e43848553b9b392f38b70
ACR-07cfeb4b4abe4ff08af26545883d25e2
ACR-78302ac4429146f98aae5c0e523c402f
ACR-ca811bcaa8284e0a9ffe2ec1f3b8fbda
ACR-9b6c10ddaa334b6f92b132d433d867aa
ACR-99e9fd67ce1244089dfa8688a311eb9c
ACR-3b75742a0ad24238adb553d33ac0b517
 */
package mediumtest.synchronization;

import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.serverconnection.ServerSettings;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;
import static org.sonarsource.sonarlint.core.test.utils.server.ServerFixture.ServerStatus.DOWN;

class ServerInfoSynchronizationMediumTests {

  @SonarLintTest
  void it_should_pull_server_info_when_bound_configuration_scope_is_added(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.3")
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getServerInfoFile(backend))
      .exists()
      .extracting(this::readServerVersion, this::readServerMode)
      .containsExactly("10.3", null));
  }

  @SonarLintTest
  void it_should_pull_old_server_info_and_mode_should_be_missing(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.1")
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getServerInfoFile(backend))
      .exists()
      .extracting(this::readServerVersion, this::readServerMode)
      .containsExactly("10.1", null));
  }

  @SonarLintTest
  void it_should_synchronize_with_sonarcloud_and_mode_should_be_missing(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer()
      .withOrganization("test", organization -> organization
        .withProject("projectKey", project -> project.withBranch("main")))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarQubeCloudEuRegionApiUri(server.baseUrl())
      .withSonarCloudConnection("connectionId", "test")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getServerInfoFile(backend))
      .exists()
      .extracting(this::readServerMode)
      .isNull());
  }

  @SonarLintTest
  void it_should_synchronize_with_recent_sonarqube_and_return_mode(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.8")
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getServerInfoFile(backend))
      .exists()
      .extracting(this::readServerMode)
      .isEqualTo(true));
  }

  @SonarLintTest
  void it_should_stop_synchronization_if_server_is_down(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.3")
      .withStatus(DOWN)
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> {
      assertThat(getServerInfoFile(backend)).doesNotExist();
      assertThat(client.getLogMessages()).contains("Error during synchronization");
    });
  }

  @SonarLintTest
  void it_should_stop_synchronization_if_server_version_is_unsupported(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("7.8")
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> {
      assertThat(getServerInfoFile(backend)).doesNotExist();
      assertThat(client.getLogMessages()).contains("Error during synchronization");
    });
  }

  private void addConfigurationScope(SonarLintTestRpcServer backend, String configScopeId, String connectionId, String projectKey) {
    backend.getConfigurationService().didAddConfigurationScopes(
      new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto(configScopeId, null, true, "name", new BindingConfigurationDto(connectionId, projectKey, true)))));
  }

  private Path getServerInfoFile(SonarLintTestRpcServer backend) {
    return backend.getStorageRoot().resolve(encodeForFs("connectionId")).resolve("server_info.pb");
  }

  private String readServerVersion(Path protoFilePath) {
    return ProtobufFileUtil.readFile(protoFilePath, Sonarlint.ServerInfo.parser()).getVersion();
  }

  @Nullable
  private Boolean readServerMode(Path protoFilePath) {
    var serverInfo = ProtobufFileUtil.readFile(protoFilePath, Sonarlint.ServerInfo.parser());
    var mqrModeSetting = serverInfo.getGlobalSettingsMap().get(ServerSettings.MQR_MODE_SETTING);
    return mqrModeSetting == null ? null : Boolean.valueOf(mqrModeSetting);
  }
}
