/*
ACR-802a7753e20e44ee9275894ee5fbb706
ACR-c76a90199921471d9a76c40417fec92e
ACR-f7404cd9a5aa45658c6b3cecf1a566f3
ACR-7f9be3ee539144b89c025fa6a748ba61
ACR-7a3e49daef7247c1878f7f12ee9a5c22
ACR-4e154a1546fb4d4682ab41a314bf5f6c
ACR-3e78c2e06ba443babebcdd8819ce1d4b
ACR-4ae87346ea5f41a7ad1045d02e9f9d70
ACR-d015e2e30cff46bf87c3dc8a5b84e95e
ACR-743f8a1e749e460b94c0534c1a239033
ACR-81abd29bbbe8447fb8ebc7510ee1584f
ACR-aff7c4c379bf4caf92cc88377238ef2f
ACR-380bf5dfa3dc465d9af99a25306e1422
ACR-aa9e92c1c94d439a9e206aaa2d14d222
ACR-b98ce8759d9d407f9ff3d9c9efdd9a50
ACR-77f2cfee47744d229cbe0c1e06a5a3f9
ACR-4b86a915263b444899b96e6c95c81b6f
 */
package mediumtest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.GetMCPServerConfigurationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.GetMCPServerConfigurationResponse;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MCPServerConfigurationProviderMediumTests {

  @SonarLintTest
  void should_throw_when_connection_does_not_exist(SonarLintTestHarness harness) {
    var connectionId = "nonExistingConnection";
    var token = "nonExistingToken";
    var backend = harness.newBackend()
      .start();

    var fileContents = getSettings(backend, connectionId, token);

    assertThat(fileContents).failsWithin(1, TimeUnit.SECONDS);
  }

  @SonarLintTest
  void should_return_sonarcloud_config_for_sonarcloud_eu_connection(SonarLintTestHarness harness) throws Exception {
    var connectionId = "scConnection";
    var organizationKey = "myOrg";
    var token = "token123";
    var embeddedServerPort = 0;

    var server = harness.newFakeSonarCloudServer().start();
    var cloudUrl = server.baseUrl().replaceAll("/$", "");

    var expectedSettings = String.format("""
      {
        "command": "docker",
        "args": [
          "run",
          "-i",
          "--rm",
          "-e",
          "SONARQUBE_TOKEN",
          "-e",
          "SONARQUBE_ORG",
          "-e",
          "SONARQUBE_CLOUD_URL",
          "-e",
          "SONARQUBE_IDE_PORT",
          "mcp/sonarqube"
        ],
        "env": {
          "SONARQUBE_ORG": "%s",
          "SONARQUBE_CLOUD_URL": "%s",
          "SONARQUBE_TOKEN": "%s",
          "SONARQUBE_IDE_PORT": "%s"
        }
      }
      """, organizationKey, cloudUrl, token, embeddedServerPort);

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection(connectionId, organizationKey)
      .withTelemetryEnabled()
      .start();

    var result = getSettings(backend, connectionId, token);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonConfiguration()).isEqualTo(expectedSettings);
    assertThat(backend.telemetryFileContent().getMcpServerConfigurationRequestedCount()).isEqualTo(1);
  }

  @SonarLintTest
  void should_return_sonarcloud_config_for_sonarcloud_us_connection(SonarLintTestHarness harness) throws Exception {
    var connectionId = "scUsConnection";
    var organizationKey = "myOrg";
    var token = "token123";
    var embeddedServerPort = 0;

    var server = harness.newFakeSonarCloudServer().start();
    var cloudUrl = server.baseUrl().replaceAll("/$", "");

    var expectedSettings = String.format("""
      {
        "command": "docker",
        "args": [
          "run",
          "-i",
          "--rm",
          "-e",
          "SONARQUBE_TOKEN",
          "-e",
          "SONARQUBE_ORG",
          "-e",
          "SONARQUBE_CLOUD_URL",
          "-e",
          "SONARQUBE_IDE_PORT",
          "mcp/sonarqube"
        ],
        "env": {
          "SONARQUBE_ORG": "%s",
          "SONARQUBE_CLOUD_URL": "%s",
          "SONARQUBE_TOKEN": "%s",
          "SONARQUBE_IDE_PORT": "%s"
        }
      }
      """, organizationKey, cloudUrl, token, embeddedServerPort);

    var backend = harness.newBackend()
      .withSonarQubeCloudUsRegionUri(server.baseUrl())
      .withSonarCloudConnection(connectionId, organizationKey, "US")
      .withTelemetryEnabled()
      .start();

    var result = getSettings(backend, connectionId, token);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonConfiguration()).isEqualTo(expectedSettings);
    assertThat(backend.telemetryFileContent().getMcpServerConfigurationRequestedCount()).isEqualTo(1);
  }

  @SonarLintTest
  void should_return_sonarqube_config_for_sonarqube_connection(SonarLintTestHarness harness) throws Exception {
    var connectionId = "scConnection";
    var organizationKey = "myOrg";
    var connectionId2 = "sqConnection";
    var serverUrl = "http://my-sonarqube";
    var token = "token123";
    var embeddedServerPort = 0;

    var expectedSettings = String.format("""
      {
        "command": "docker",
        "args": [
          "run",
          "-i",
          "--rm",
          "-e",
          "SONARQUBE_TOKEN",
          "-e",
          "SONARQUBE_URL",
          "-e",
          "SONARQUBE_IDE_PORT",
          "mcp/sonarqube"
        ],
        "env": {
          "SONARQUBE_URL": "%s",
          "SONARQUBE_TOKEN": "%s",
          "SONARQUBE_IDE_PORT": "%s"
        }
      }
      """, serverUrl, token, embeddedServerPort);

    var server = harness.newFakeSonarCloudServer().start();

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .withSonarCloudConnection(connectionId, organizationKey)
      .withSonarQubeConnection(connectionId2, serverUrl)
      .withTelemetryEnabled()
      .start();

    var result = getSettings(backend, connectionId2, token);

    assertThat(result).succeedsWithin(3, TimeUnit.SECONDS);
    assertThat(result.get().getJsonConfiguration()).isEqualTo(expectedSettings);
    assertThat(backend.telemetryFileContent().getMcpServerConfigurationRequestedCount()).isEqualTo(1);
  }

  private CompletableFuture<GetMCPServerConfigurationResponse> getSettings(SonarLintTestRpcServer backend, String connectionId, String token) {
    return backend.getConnectionService().getMCPServerConfiguration(new GetMCPServerConfigurationParams(connectionId, token));
  }

}
