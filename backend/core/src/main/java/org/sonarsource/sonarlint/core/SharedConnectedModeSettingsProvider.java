/*
ACR-f251b5607bab467e9f8d6bcda34b4449
ACR-ff051722b9864ed5b2df0ff81e0f8596
ACR-0997fa1614f0406fba3617ae3bf5dc19
ACR-787c613b21a647daa2a0f96ec380295b
ACR-28af1cc69e1940a09f5b13c605faf48f
ACR-ce5ebc250289415cb57f2339d6a724f6
ACR-4e93082a2daf42bc89e346db647012c8
ACR-517303b2b2ce4811a14dbd4a8ff7d33a
ACR-c5b35de771514d00b5a2c4489e254448
ACR-2ec2549743774787883d61100eeb76d0
ACR-be6f0222d5f445e98ba393c4bc93d499
ACR-5f6fb0ef6d454a25be4f6c99cff10bd9
ACR-e8024cb7e5c04ed7bd1d2c12e463a56f
ACR-325e2a5169e241f8a8407792f553718c
ACR-55a88053d94443baad3a2d7e8953a7e1
ACR-fd115aee01f64676ad3af4b2086e789e
ACR-165d290c980946dc8d88e9ac077e1153
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
