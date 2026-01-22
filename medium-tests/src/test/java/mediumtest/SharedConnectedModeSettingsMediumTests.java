/*
ACR-60d994a8971b4bd3ad733cfa911997b3
ACR-b6a792025bf54fbda30415d3466a285c
ACR-de5dacf44df244858aa0a0c236d8df8f
ACR-d89138eeae144948a3d92862edfe36a7
ACR-158afcc5c0584c29956e2cf1e1615050
ACR-e55acbe0475c4d819fbe31bdd68635b9
ACR-5315f4b912434a07b1820f2abbe21b07
ACR-4a91b56e1f454d2b80f66402ef928b7d
ACR-b540b9afc30346b3a5ca211cca5ef9ce
ACR-32bdd8924f4d498c9ab07a29dbd31cab
ACR-bac0481c8c374f99b0d17e5755367809
ACR-f1756069f27a4835bd9af3cc61e4821a
ACR-a424f2c5f78843df9ce475635336caff
ACR-7863142031504294b110a8e20417b365
ACR-2dc9e259beb8424d8e90c20f5a76e59a
ACR-6aec93abe86e4ab9bdfc4b194d39da53
ACR-6842df89e2eb4464a7eea8897b0780fc
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
