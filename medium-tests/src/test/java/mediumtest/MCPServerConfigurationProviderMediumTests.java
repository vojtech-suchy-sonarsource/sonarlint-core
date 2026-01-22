/*
ACR-81ca44c796c34dc68e02abb41706ba82
ACR-f94f6c55938b4171aa8c6429c3459bd1
ACR-bd385e412d8b49d08d2b4b6fb2441c1e
ACR-5c7fcc171c61478da2f56cdaa3ded032
ACR-6b495e25a3744e86ba3071e344a0cf2e
ACR-d17e07de1a1c461ead7b866233bb0284
ACR-1584af699cae426fb5ef99125034bd33
ACR-bbd769b5bec8480da38dd56c001d5e84
ACR-807c0a9ee1ed4d8fb9293eaa0604cbb0
ACR-ee72692a863d47379dfe930fc0d7dd35
ACR-ddafb9ae188e41b7a9b43ca669339cce
ACR-02276b366b0041f0a00d48383d46e3b2
ACR-124f32536d7b47e1b8425ba6affd033a
ACR-12096fb4d8184b7e8bac402782d8678f
ACR-4e8c621ec0b4498bb743948f79393960
ACR-d53a5e9e1cb5442994c55f6f35f7961d
ACR-b7a39a8574bb471e95d65ba1c5272dcc
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
