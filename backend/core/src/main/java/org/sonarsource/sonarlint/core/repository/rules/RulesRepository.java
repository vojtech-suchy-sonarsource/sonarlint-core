/*
ACR-7419e13ca9a64605a67076e22fc738a3
ACR-7daea6e7b9e54c979fca7d1f142be24c
ACR-08c229d4b7224de09fad7cbafd9ed825
ACR-89848fdc609c4b7895f2acb1840f272b
ACR-c36149b72bc2494fb394c2ad7bd9b9fe
ACR-afe981c6e3f040649cf288cde73f8992
ACR-0cc76981d8984c9f9af8af8595a1d3c8
ACR-c31217d99020456cabf8ecea413a211a
ACR-06fd09178ff24a01b3bd787b3c15d805
ACR-cd70627e1fe8429d96dbc0422026c005
ACR-1e1bce3668bf4896881130234108c06c
ACR-a3f8046a80e84227a639f378d5e2a03b
ACR-6f6be0fb21844ed09767f3214f307d4b
ACR-4ad5e41383c048e9ba0c72bf4c2e3c58
ACR-328af27772ba4b299cd0a3cb77000a8b
ACR-0e71819d518c4242972757ea04086031
ACR-20ede5b107a8432d91aed7448806002b
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
