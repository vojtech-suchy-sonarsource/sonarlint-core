/*
ACR-89db8f7342e049d0bf8682e33669671a
ACR-e6cda28b6a3a45ec9ba8320dee2f7513
ACR-a3eba0e6c3494af5980b168b9d8cbca6
ACR-0fc50277aede4397ae4002661c0634a8
ACR-58ffb7f6f8064fb5ab7b23b395ac297b
ACR-a1b91d3f019947b0986be47f4267701c
ACR-364b45f48a6d45f7b3b7513176697a6d
ACR-903e9a45b9404cba8e847617c95370dd
ACR-644d894aedb34470b25d8d72037af920
ACR-51dbb9b2c1c748988c2f1f9dd2b6498f
ACR-9873cabe59db4af0bc0ca736e3ae56a3
ACR-ab3d0aec7ffe4c9e99d369c33f8ff83c
ACR-272bc681f5ae498da4eb63ebc4121c26
ACR-d71a0b693f264e3abf0432e868970724
ACR-6871fa70eadf4dd38f79f9c69b384278
ACR-6c3d9be504f94cd8a7be52a40b80c0a2
ACR-fc69739e0eda450aa84ee5a05e778f3d
 */
package org.sonarsource.sonarlint.core.repository.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.rules.RulesExtractionHelper;
import org.sonarsource.sonarlint.core.serverconnection.ServerSettings;
import org.sonarsource.sonarlint.core.serverconnection.StoredServerInfo;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.event.EventListener;

public class RulesRepository {

  private final SonarLintLogger logger = SonarLintLogger.get();

  private final RulesExtractionHelper extractionHelper;
  private Map<String, SonarLintRuleDefinition> embeddedRulesByKey;
  private final Map<String, Map<String, SonarLintRuleDefinition>> rulesByKeyByConnectionId = new HashMap<>();
  private final Map<String, Map<String, String>> ruleKeyReplacementsByConnectionId = new HashMap<>();
  private final ConfigurationRepository configurationRepository;
  private final StorageService storageService;

  public RulesRepository(RulesExtractionHelper extractionHelper, ConfigurationRepository configurationRepository, StorageService storageService) {
    this.extractionHelper = extractionHelper;
    this.configurationRepository = configurationRepository;
    this.storageService = storageService;
  }

  public Collection<SonarLintRuleDefinition> getEmbeddedRules() {
    lazyInit();
    return embeddedRulesByKey.values();
  }

  public Optional<SonarLintRuleDefinition> getEmbeddedRule(String ruleKey) {
    lazyInit();
    return Optional.ofNullable(embeddedRulesByKey.get(ruleKey));
  }

  private synchronized void lazyInit() {
    if (embeddedRulesByKey == null) {
      this.embeddedRulesByKey = byKey(extractionHelper.extractEmbeddedRules());
    }
  }

  public Optional<SonarLintRuleDefinition> getRule(String connectionId, String ruleKey) {
    lazyInit(connectionId);
    var connectionRules = rulesByKeyByConnectionId.get(connectionId);
    return Optional.ofNullable(connectionRules.get(ruleKey))
      .or(() -> Optional.ofNullable(connectionRules.get(ruleKeyReplacementsByConnectionId.get(connectionId).get(ruleKey))));
  }

  private synchronized void lazyInit(String connectionId) {
    var rulesByKey = rulesByKeyByConnectionId.get(connectionId);
    if (rulesByKey == null) {
      var serverSettings = storageService.connection(connectionId).serverInfo().read().map(StoredServerInfo::globalSettings);
      setRules(connectionId, extractionHelper.extractRulesForConnection(connectionId, serverSettings.map(ServerSettings::globalSettings).orElseGet(Map::of)));
    }
  }

  private void setRules(String connectionId, Collection<SonarLintRuleDefinition> rules) {
    var rulesByKey = byKey(rules);
    var ruleKeyReplacements = new HashMap<String, String>();
    rules.forEach(rule -> rule.getDeprecatedKeys().forEach(deprecatedKey -> ruleKeyReplacements.put(deprecatedKey, rule.getKey())));
    rulesByKeyByConnectionId.put(connectionId, rulesByKey);
    ruleKeyReplacementsByConnectionId.put(connectionId, ruleKeyReplacements);
  }

  private static Map<String, SonarLintRuleDefinition> byKey(Collection<SonarLintRuleDefinition> rules) {
    return rules.stream()
      .collect(Collectors.toMap(SonarLintRuleDefinition::getKey, r -> r));
  }

  @EventListener
  public void connectionRemoved(ConnectionConfigurationRemovedEvent e) {
    evictAll(e.getRemovedConnectionId());
  }

  @EventListener
  public void configScopeRemoved(ConfigurationScopeRemovedEvent e) {
    var removedBindingConfiguration = e.getRemovedBindingConfiguration();
    var connectionId = removedBindingConfiguration.connectionId();
    if (removedBindingConfiguration.isBound() && hasNoMoreBindings(connectionId)) {
      evictAll(connectionId);
    }
  }

  private boolean hasNoMoreBindings(String connectionId) {
    return configurationRepository.getBoundScopesToConnection(connectionId).isEmpty();
  }

  private void evictAll(String connectionId) {
    logger.debug("Evict cached rules definitions for connection '{}'", connectionId);
    rulesByKeyByConnectionId.remove(connectionId);
    ruleKeyReplacementsByConnectionId.remove(connectionId);
  }
}
