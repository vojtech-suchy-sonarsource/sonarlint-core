/*
ACR-7f75f581463f4754a7801fe74f572388
ACR-ccad8635a60d4d9c835718dabb8a3c84
ACR-5b43edfadf994e34a20979ea8a0f98ab
ACR-3575e0db21364e98a279f272cacc96ff
ACR-6901a8568bc340468ea67896b07d44b4
ACR-f4cb706777eb4df68ccd97b8fd23ce69
ACR-bf5b74cf375a42bdb8b40d87e82c36cd
ACR-baf866098916447bb10d884d95032dee
ACR-2367953b083e4b91955ac75e303e226d
ACR-aeffb595289046b0b9cb6be5e5397012
ACR-900629847a66449ca43610cb74b9c857
ACR-40c2e246a8f246f7b525b17ab9e8b473
ACR-936de513d3664829a029292803aaca90
ACR-e44c24bddf174f4099ffa18cc91fe144
ACR-74c758de09e642a693e6474fcacc039b
ACR-eacba39d302c49b4b9ce336827a217a7
ACR-41272a256afe4a9291ec8ed135814385
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.active.rules.ActiveRulesService;
import org.sonarsource.sonarlint.core.analysis.NodeJsService;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.serverconnection.Organization;
import org.sonarsource.sonarlint.core.serverconnection.StoredServerInfo;
import org.sonarsource.sonarlint.core.storage.StorageService;

public class TelemetryServerAttributesProvider {

  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final ActiveRulesService activeRulesService;
  private final RulesRepository rulesRepository;
  private final NodeJsService nodeJsService;
  private final StorageService storageService;

  public TelemetryServerAttributesProvider(ConfigurationRepository configurationRepository,
    ConnectionConfigurationRepository connectionConfigurationRepository, ActiveRulesService activeRulesService, RulesRepository rulesRepository,
    NodeJsService nodeJsService, StorageService storageService) {
    this.configurationRepository = configurationRepository;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.activeRulesService = activeRulesService;
    this.rulesRepository = rulesRepository;
    this.nodeJsService = nodeJsService;
    this.storageService = storageService;
  }

  public TelemetryServerAttributes getTelemetryServerLiveAttributes() {
    var allBindings = configurationRepository.getAllBoundScopes();

    var usesConnectedMode = !allBindings.isEmpty();
    var usesSonarCloud = allBindings.stream().anyMatch(isSonarCloudConnectionConfiguration());

    var childBindingCount = countChildBindings();
    var sonarQubeServerBindingCount = countSonarQubeServerBindings(allBindings);
    var sonarQubeCloudEUBindingCount = countSonarQubeCloudBindings(allBindings, SonarCloudRegion.EU);
    var sonarQubeCloudUSBindingCount = countSonarQubeCloudBindings(allBindings, SonarCloudRegion.US);

    var devNotificationsDisabled = allBindings.stream().anyMatch(this::hasDisableNotifications);

    var nonDefaultEnabledRules = new ArrayList<String>();
    var defaultDisabledRules = new ArrayList<String>();

    activeRulesService.getStandaloneRuleConfig().forEach((ruleKey, standaloneRuleConfigDto) -> {
      var optionalEmbeddedRule = rulesRepository.getEmbeddedRule(ruleKey);
      if (optionalEmbeddedRule.isEmpty()) {
        return;
      }
      var activeByDefault = optionalEmbeddedRule.get().isActiveByDefault();
      var isActive = standaloneRuleConfigDto.isActive();
      if (activeByDefault && !isActive) {
        defaultDisabledRules.add(ruleKey);
      } else if (!activeByDefault && isActive) {
        nonDefaultEnabledRules.add(ruleKey);
      }
    });

    var nodeJsVersion = getNodeJsVersion();

    var connectionsAttributes = connectionConfigurationRepository.getConnectionsById().keySet().stream()
      .map(storageService::connection)
      .map(c -> {
        var userId = c.user().read().orElse(null);
        var serverId = c.serverInfo().read().map(StoredServerInfo::serverId).orElse(null);
        var orgId = c.organization().read().map(Organization::id).orElse(null);

        if (userId == null && serverId == null && orgId == null) {
          return null;
        }

        return new TelemetryConnectionAttributes(userId, serverId, orgId);
      })
      .filter(Objects::nonNull)
      .toList();

    return new TelemetryServerAttributes(usesConnectedMode, usesSonarCloud, childBindingCount, sonarQubeServerBindingCount,
      sonarQubeCloudEUBindingCount, sonarQubeCloudUSBindingCount, devNotificationsDisabled, nonDefaultEnabledRules,
      defaultDisabledRules, nodeJsVersion, connectionsAttributes);
  }

  private int countSonarQubeCloudBindings(Collection<BoundScope> allBindings, SonarCloudRegion region) {
    return (int) allBindings.stream()
      .filter(binding -> {
        if (connectionConfigurationRepository.getConnectionById(binding.getConnectionId()) instanceof SonarCloudConnectionConfiguration scBinding) {
          return region.equals(scBinding.getRegion());
        }
        return false;
      }).count();
  }

  private int countSonarQubeServerBindings(Collection<BoundScope> allBindings) {
    return (int) allBindings.stream()
      .filter(binding -> connectionConfigurationRepository.getConnectionById(binding.getConnectionId()) instanceof SonarQubeConnectionConfiguration)
      .count();
  }

  //ACR-1c5eaa961fd647d3a51e4242974f7da7
  private int countChildBindings() {
    return (int) configurationRepository.getLeafConfigScopeIds().stream()
      .filter(scopeId -> {
        var configScope = configurationRepository.getConfigurationScope(scopeId);
        if (configScope != null && configScope.parentId() != null) {
          var parentBindingConfig = configurationRepository.getBindingConfiguration(configScope.parentId());
          var leafBindingConfig = configurationRepository.getBindingConfiguration(scopeId);
          if (parentBindingConfig != null && leafBindingConfig != null) {
            var parentProjectKey = parentBindingConfig.sonarProjectKey();
            var leafProjectKey = leafBindingConfig.sonarProjectKey();
            return parentProjectKey != null && leafProjectKey != null && !parentProjectKey.equals(leafProjectKey);
          }
        }
        return false;
      })
      .count();
  }

  @CheckForNull
  private String getNodeJsVersion() {
    return nodeJsService.getActiveNodeJsVersion().map(Objects::toString).orElse(null);
  }

  private boolean hasDisableNotifications(BoundScope binding) {
    return Objects.requireNonNull(connectionConfigurationRepository.getConnectionById(binding.getConnectionId())).isDisableNotifications();
  }

  private Predicate<BoundScope> isSonarCloudConnectionConfiguration() {
    return binding -> connectionConfigurationRepository.getConnectionById(binding.getConnectionId()) instanceof SonarCloudConnectionConfiguration;
  }
}
