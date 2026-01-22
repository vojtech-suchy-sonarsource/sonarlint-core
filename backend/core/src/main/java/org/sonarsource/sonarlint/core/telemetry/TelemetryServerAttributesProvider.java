/*
ACR-6c294991ea4c48a79513ff96e96d19e6
ACR-ba2a3a0acaaf49a981b4d896982b1fb6
ACR-726e6d6189ae4138a335f37db0cbb52f
ACR-f1645b718b094f5ebb05f19a2b8c3b19
ACR-43d72a32939d4ecb9eec730edbd8c0cc
ACR-d7b21bb5303f4d88a240b53c97c7a261
ACR-cb084822a4684689b83c6a57d3016b08
ACR-625224a39a424d17b2ea2c2297130ef3
ACR-ab30a395ae154d6288457ee7abdc6690
ACR-061195be40864beb8a9f387bb8c76530
ACR-71da92b063984ae68cd523b9271e6162
ACR-6173583ef8c04c00b660f409b7541c03
ACR-99688625136747f7946980d55b2cf8e6
ACR-4154102fdd4c41c7a6a6f3e4949ade63
ACR-b0610d824d334923b7582c87cf01235d
ACR-e066e0c7537a44518459b0acc1504413
ACR-c47c0fa9450e4401aff7aca4ec5db47c
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

  //ACR-dd0d4cff4fde49da818c539b621d63e1
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
