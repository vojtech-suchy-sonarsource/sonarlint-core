/*
ACR-fde90f2cd6854d4b80230c7ef422ca7e
ACR-0454502288cf4faa816789a959ffb4b2
ACR-b6c967f45d8d40be9453f634237a9b47
ACR-afac89213cc741e5af5e8f44ceebb1ce
ACR-9919dae3c82f48a19f9a48c9d8763fc8
ACR-32ef3fb4f66b4dacba3dd45dfdff2872
ACR-ba8a5186f3764dd281c4bbe5a9c6676e
ACR-c2eeddb4cbcb457c8e2c7f3bce9c798c
ACR-e1a93fc2d2ad487a8a0595bd617c8309
ACR-63c68c9db4534e85bab20e2009850c7e
ACR-2ee8dc66bade42f8b726a9090700b0b1
ACR-786bcf2536ec4256b556941585d2db72
ACR-dceefa4397864db2a288039c2f9d605d
ACR-c5ea5c2cfd9a48818100e4d913682fb6
ACR-e9cbfe21968e437bbc409a382c09bca4
ACR-4ac3ddbf34c94d28a9158f3e8e3f7a90
ACR-a808def4fc0e46e9a332705dcae4285a
 */
package mediumtest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetSharedConnectedModeConfigFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetSharedConnectedModeConfigFileResponse;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;

class SharedConnectedModeSettingsMediumTests {

  @SonarLintTest
  void should_throw_when_not_bound(SonarLintTestHarness harness) {
    var configScopeId = "file:///my/folder";
    var backend = harness.newBackend()
      .start();

    var fileContents = getFileContents(backend, configScopeId);

    assertThat(fileContents).failsWithin(1, TimeUnit.SECONDS);
  }

  @SonarLintTest
  void should_return_sc_config_when_bound_to_sonarcloud(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var configScopeId = "file:///my/workspace/folder";
    var connectionId = "scConnection";
    var organizationKey = "myOrg";
    var projectKey = "projectKey";

    var expectedFileContent = String.format("""
      {
          "sonarCloudOrganization": "%s",
          "projectKey": "%s",
          "region": "EU"
      }""", organizationKey, projectKey);

    var server = harness.newFakeSonarCloudServer().start();

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection(connectionId, organizationKey)
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withTelemetryEnabled()
      .start();

    var result = getFileContents(backend, configScopeId);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonFileContent()).isEqualTo(expectedFileContent);
    assertThat(backend.telemetryFileContent().getExportedConnectedModeCount()).isEqualTo(1);
  }

  @SonarLintTest
  void should_return_wrong_sc_config_when_bound_to_sonarcloud_us(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var configScopeId = "file:///my/workspace/folder";
    var connectionId = "scConnection";
    var organizationKey = "myOrg";
    var projectKey = "projectKey";

    var expectedFileContent = String.format("""
      {
          "sonarCloudOrganization": "%s",
          "projectKey": "%s",
          "region": "US"
      }""", organizationKey, projectKey);

    var server = harness.newFakeSonarCloudServer().start();

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection(connectionId, organizationKey, "US")
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withTelemetryEnabled()
      .start();

    var result = getFileContents(backend, configScopeId);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonFileContent()).isEqualTo(expectedFileContent);
    assertThat(backend.telemetryFileContent().getExportedConnectedModeCount()).isEqualTo(1);
  }

  @SonarLintTest
  void should_return_sq_config_when_bound_to_sonarqube(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var configScopeId = "file:///my/workspace/folder";
    var connectionId = "scConnection";
    var projectKey = "projectKey";

    var server = harness.newFakeSonarQubeServer().start();

    var expectedFileContent = String.format("""
      {
          "sonarQubeUri": "%s",
          "projectKey": "%s"
      }""", Strings.CS.removeEnd(server.baseUrl(), "/"), projectKey);

    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, server)
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withTelemetryEnabled()
      .start();

    var result = getFileContents(backend, configScopeId);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonFileContent()).isEqualTo(expectedFileContent);
    assertThat(backend.telemetryFileContent().getExportedConnectedModeCount()).isEqualTo(1);
  }

  private CompletableFuture<GetSharedConnectedModeConfigFileResponse> getFileContents(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getBindingService()
      .getSharedConnectedModeConfigFileContents(
        new GetSharedConnectedModeConfigFileParams(configScopeId));
  }

}
