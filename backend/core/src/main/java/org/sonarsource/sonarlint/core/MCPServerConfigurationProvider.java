/*
ACR-bae798e71c834b72ba7f9a2b88d01f94
ACR-76a526bc31cb43258a44f3c3017c8c95
ACR-b9dfe5505b774faea050ca6a3319e266
ACR-f45e933abe00443f9fbff405fdbcb65b
ACR-28e923da00694d2393af9bfed4d0dd82
ACR-8574b209db0442b2b03b928bb3c0f70e
ACR-3ee9f12906d041c8a3d92aee3900b87b
ACR-11bb50d493f047708122941b84c12bf8
ACR-d50f829dc55c4ee9b045162e90793002
ACR-034c6e2ee04c49a9aefa6333ac7bc1d8
ACR-e60a48b14217467cbed7620630f377e9
ACR-5f3b9f2a0b8848a5a2811bbda37db659
ACR-7a7339f1856442309044b78e4426e6a1
ACR-62059d5b3af4407d81afc73dceffbb81
ACR-d8f03aa2b554489ab60e24f893a4e3a8
ACR-fb930be6c21d41dbba164bac1c019f92
ACR-845c2804063345aa946809f2ca0ae150
 */
package org.sonarsource.sonarlint.core;

import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.SonarLintException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static java.lang.String.format;

public class MCPServerConfigurationProvider {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String SONARCLOUD_MCP_CONFIG = """
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
    """;
  private static final String SONARQUBE_MCP_CONFIG = """
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
    """;

  private final ConnectionConfigurationRepository connectionRepository;
  private final TelemetryService telemetryService;
  private final EmbeddedServer embeddedServer;

  public MCPServerConfigurationProvider(ConnectionConfigurationRepository connectionRepository, TelemetryService telemetryService, EmbeddedServer embeddedServer) {
    this.connectionRepository = connectionRepository;
    this.telemetryService = telemetryService;
    this.embeddedServer = embeddedServer;
  }

  public String getMCPServerConfigurationJSON(String connectionId, String token) {
    var connection = connectionRepository.getConnectionById(connectionId);
    if (connection != null) {
      telemetryService.mcpServerConfigurationRequested();
      if (connection.getKind() == ConnectionKind.SONARCLOUD) {
        var sonarCloudConnection = (SonarCloudConnectionConfiguration) connection;
        var organization = sonarCloudConnection.getOrganization();
        var url = connection.getUrl();

        return format(SONARCLOUD_MCP_CONFIG, organization, url, token, embeddedServer.getPort());
      } else {
        var url = connection.getUrl();

        return format(SONARQUBE_MCP_CONFIG, url, token, embeddedServer.getPort());
      }
    } else {
      LOG.warn("Request for generating MCP server settings JSON failed; Connection not found for '{}'", connectionId);
      throw new SonarLintException(format("Connection not found for '%s'; Cannot generate MCP server settings JSON", connectionId));
    }
  }
}
