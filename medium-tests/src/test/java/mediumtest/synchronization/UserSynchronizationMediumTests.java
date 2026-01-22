/*
ACR-0857c38c210644d58398b295f1c6fcc8
ACR-98b3e7dbae3440b6bd964ded968f6a66
ACR-022f0d14a59342848e6728c9be2db373
ACR-79c899be5c07404c9a318d7781a752b1
ACR-91946e0f219d42c48be749decfda83b2
ACR-c0d84026de7d492a8870e2d11de4733f
ACR-eaef026ddb4b4219a8fe5dfd655970d7
ACR-71400de03d854f73bfa1d1f12a2bfc0a
ACR-5dc80ca4791743aea1b5a12b621aba2e
ACR-ab238bac4ea64475a003616d32e77c28
ACR-55459bc36b384611b6d8c18e5d7a1bac
ACR-0961e32a1dab43e68764c84f5105bd5f
ACR-2946d331b2984f8ab975155c92e12fcd
ACR-f574b538c040470b82398272f62a3d10
ACR-42ed73de5e754af1a954b7cdd5bcb75f
ACR-30c82cdca46441c48365b23b819a8c12
ACR-17ce0363185f49a3a4c274ded9823564
 */
package mediumtest.synchronization;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static java.util.List.of;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

class UserSynchronizationMediumTests {

  @SonarLintTest
  void it_should_store_user_id_on_sonarcloud(SonarLintTestHarness harness) {
    var scServer = harness.newFakeSonarCloudServer()
      .withOrganization("orgKey")
      .start();

    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarQubeCloudEuRegionApiUri(scServer.baseUrl())
      .withSonarCloudConnection("connectionId", "myOrg")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getUserFile(backend)).exists()
      .content().asString().contains("11111111-1111-1111-1111-111111111111"));
  }

  @SonarLintTest
  void it_should_store_user_id_on_sonarqube_server(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("10.3")
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();

    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    addConfigurationScope(backend, "configScopeId", "connectionId", "projectKey");

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(getUserFile(backend)).exists()
      .content().asString().contains("11111111-1111-1111-1111-111111111111"));
  }

  private void addConfigurationScope(SonarLintTestRpcServer backend, String configScopeId, String connectionId, String projectKey) {
    backend.getConfigurationService().didAddConfigurationScopes(
      new DidAddConfigurationScopesParams(of(new ConfigurationScopeDto(configScopeId, null, true, "name", new BindingConfigurationDto(connectionId, projectKey, true)))));
  }

  private Path getUserFile(SonarLintTestRpcServer backend) {
    return backend.getStorageRoot().resolve(encodeForFs("connectionId")).resolve("user.pb");
  }

}
