/*
ACR-531fa74abe274f05acebc4265e5e943b
ACR-b96eb36a43b74ca794ae84de19d43852
ACR-0e2f126dc7b64aa799efce817d4883d7
ACR-cd45fb471a1a4962862366c7f5299bb2
ACR-5e9682311ad84fe1ba3adfcaa568075b
ACR-c37058bba9c14010b83bf4b9986a4ed2
ACR-90e7f554f14349fbb1580757212e4594
ACR-7c4e6717eb2d402e9bff1dbdfd584bf2
ACR-1bffd7a889144363a870caf298e014f4
ACR-ee1f1b5f9e2e4c6dbb0fc7a121200779
ACR-b7a35c401b274383b6767eb44d9b20bc
ACR-29fc6f247630403a8421659a85aaf05d
ACR-6eeeda39f1c243daa82040742e2796fc
ACR-bd80071f79ce42e49160efdd128e0eac
ACR-4b596724b241465f9791d90afb49f85a
ACR-3bf4ac8172e34005a498cd448fd9be04
ACR-5a783b945462402591f8514d9f94ef1e
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
