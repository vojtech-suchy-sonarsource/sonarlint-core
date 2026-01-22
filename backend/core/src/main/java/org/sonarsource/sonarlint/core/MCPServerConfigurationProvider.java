/*
ACR-93048e0d62314f9fb4568aba19eb208d
ACR-04f223e6a48e47e29099eeebf2343c26
ACR-fe75a86b0a224f2c811b225e80aa6191
ACR-859b0902def04afeb9a0a4a208772513
ACR-f5d832ec145a457687190b5b987ffc8a
ACR-076a782ce7bf4f65a68c73f90291100f
ACR-c093f8797eab40afab0fd924b4839fd2
ACR-f94dcb72d57a4a558073d481ff523a77
ACR-e061a842b9774d3caadfe0ea99d12e63
ACR-78c311ce08714646ade77455eddc950a
ACR-b27412e125f946dbbebf5fa4ebb9bad7
ACR-a31696a8128844f891a2aeebe9421689
ACR-f68523354ab44536987ad128998aed0f
ACR-8ad31ff843104faca43597186800a5ef
ACR-879c8b262e0f41afb335306139ef6421
ACR-e0a0154e98fa4400bfc8339a46e2a104
ACR-20d86b76c7bc46d08cf6f81a7c9b7706
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
