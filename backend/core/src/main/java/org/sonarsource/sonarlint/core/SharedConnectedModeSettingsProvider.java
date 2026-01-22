/*
ACR-71592bf6eb65435e9b5a9b9859b8e0bf
ACR-94713dcca7a449b9bbb1a79dae66302c
ACR-d606e801e5764f77b495ddaa1df41467
ACR-7d40f60c34a84593a06fc5e631f95499
ACR-cb6d53159d594d79addad4e9d6af5745
ACR-82702de4d2874360bd6a143f970ca96c
ACR-86a2efb1d4424abd9fad40db9cc57ddc
ACR-5967139fbb144a38961935c2c0d07d9f
ACR-fa24c89653494946b9958324df207eca
ACR-5c957c4a3b8244649b45e0d5059e8875
ACR-99deb3ee0f0b46bbab42dd4f7e1cef3b
ACR-0e2217bc09134d30a5e1b136e373e1bb
ACR-3b2d4881f4f346e5bd5dbc286599d6e4
ACR-f6ab1b7257dc4b5bb02db6d0aa84c967
ACR-8037651a6c3c4483a5756d7151289de8
ACR-bee256c0659747589c7e83d1addbbd2a
ACR-7fd058a7ad6044bbb94ba24a7b134983
 */
package org.sonarsource.sonarlint.core;

import java.util.Objects;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.SonarLintException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static java.lang.String.format;

public class SharedConnectedModeSettingsProvider {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String SONARCLOUD_CONNECTED_MODE_CONFIG = """
    {
        "sonarCloudOrganization": "%s",
        "projectKey": "%s",
        "region": "%s"
    }""";
  private static final String SONARQUBE_CONNECTED_MODE_CONFIG = """
    {
        "sonarQubeUri": "%s",
        "projectKey": "%s"
    }""";

  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionRepository;
  private final TelemetryService telemetryService;

  public SharedConnectedModeSettingsProvider(ConfigurationRepository configurationRepository,
    ConnectionConfigurationRepository connectionRepository, TelemetryService telemetryService) {
    this.configurationRepository = configurationRepository;
    this.connectionRepository = connectionRepository;
    this.telemetryService = telemetryService;
  }

  public String getSharedConnectedModeConfigFileContents(String configScopeId) {
    var bindingConfiguration = configurationRepository.getBindingConfiguration(configScopeId);
    if (bindingConfiguration != null && bindingConfiguration.isBound()) {
      var projectKey = bindingConfiguration.sonarProjectKey();
      var connectionId = bindingConfiguration.connectionId();

      var connection =  Objects.requireNonNull(connectionRepository.getConnectionById(Objects.requireNonNull(connectionId)));
      telemetryService.exportedConnectedMode();
      if (connection.getKind() == ConnectionKind.SONARCLOUD) {
        var organization = ((SonarCloudConnectionConfiguration) connection).getOrganization();
        var region = ((SonarCloudConnectionConfiguration) connection).getRegion();

        return format(SONARCLOUD_CONNECTED_MODE_CONFIG, organization, projectKey, region);
      } else {
        return format(SONARQUBE_CONNECTED_MODE_CONFIG, connection.getUrl(), projectKey);
      }
    } else {
      LOG.warn("Request for generating shared Connected Mode configuration file content failed; Binding not yet available for '{}'", configScopeId);
      throw new SonarLintException(format("Binding not found for '%s'; Cannot generate shared Connected Mode file contents", configScopeId));
    }
  }
}
