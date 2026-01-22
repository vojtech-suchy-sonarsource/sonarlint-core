/*
ACR-6f6f586375944fdd80da40ff2c00ac5d
ACR-fc696d141bc943a5b91bb403735d7e6a
ACR-7f3b3185da61418186968fbb3953776f
ACR-611e7bba89ae4e92a0167a95ae744f70
ACR-d4ff3e7578e746b18726a538eb25d8ef
ACR-296517d77ab2473fbf6835b0ead478a8
ACR-73886198aaf842e0a025b11f3271df1e
ACR-e64838ac6bca406e91ea403bba30c9e6
ACR-ae4bdb0fabbb4817a5b6b42d3250f3f8
ACR-124d4d60815740e18b8fb359574ff68e
ACR-78ebefc94b2c4c1a9f0ddeed31c6ec37
ACR-5f623cc526ca4d0aa3ef90cfa6a7db03
ACR-9deadedc5f8741d99b779328ca9e27b2
ACR-3f85f79c4cbf48299f610bf0ce9f18ba
ACR-e36125f2aef2432fb9bac857cc4df4e4
ACR-0f67434233344b8982e9d10519f70d43
ACR-397ddee8b6c14414a553a7c7525fa0ce
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
