/*
ACR-458028049d424b9db358561d1cb2a02a
ACR-7b5d9553f0b64c4ba70e445d06564208
ACR-eeb743ea6c8f41d6a76d0e6fd4105159
ACR-c94ccbe8f23c4a3a94d091faf709bcb7
ACR-4399964c9e354ff28fcacb81f3f5e3eb
ACR-d844058eb7294a5bbaa397ad4e1ddd42
ACR-4feb4b801d544ae28c5015f2249093b6
ACR-cf56fee9b37e4776a7181d27cc647806
ACR-6ceb79f3a5b54962ae47e34efde5a79e
ACR-2acc9c7696c64a7081bd59ddcebf44d1
ACR-780502818c694e7bbeab5000fa4ba75e
ACR-2773106fc00046288b7674c42560dc36
ACR-2feb5d1694d04c718b67035b04c2a554
ACR-fd7a527fd4d54b6eae49c9a5089d7f68
ACR-fab1429462124bb396f1369203de1b04
ACR-cf626b6bf9c94f3fba20b79d2e710408
ACR-7ecd65c0cecc43a9a8b9f612934c15ee
 */
package org.sonarsource.sonarlint.core.active.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.RuleKey;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.mode.SeverityModeService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.EffectiveRuleDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetStandaloneRuleDescriptionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.rules.RuleDetails;
import org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter;
import org.sonarsource.sonarlint.core.rules.RuleNotFoundException;
import org.sonarsource.sonarlint.core.rules.RulesService;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.push.RuleSetChangedEvent;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerActiveRule;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerRule;
import org.sonarsource.sonarlint.core.serverconnection.AnalyzerConfiguration;
import org.sonarsource.sonarlint.core.serverconnection.RuleSet;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.sync.AnalyzerConfigurationSynchronized;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.sonarsource.sonarlint.core.commons.CleanCodeAttribute.CONVENTIONAL;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rules.RulesService.COULD_NOT_FIND_RULE;
import static org.sonarsource.sonarlint.core.rules.RulesService.IN_EMBEDDED_RULES;

public class ActiveRulesService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConfigurationRepository configurationRepository;
  private final LanguageSupportRepository languageSupportRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final SeverityModeService severityModeService;
  private final StorageService storageService;
  private final RulesRepository rulesRepository;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final boolean hotspotEnabled;
  private final ApplicationEventPublisher eventPublisher;

  private final Map<String, StandaloneRuleConfigDto> standaloneRuleConfig = new ConcurrentHashMap<>();
  private List<ActiveRuleDetails> standaloneActiveRules;
  private final Map<Binding, List<ActiveRuleDetails>> activeRulesPerBinding = new ConcurrentHashMap<>();

  public ActiveRulesService(ConfigurationRepository configurationRepository, LanguageSupportRepository languageSupportRepository, SonarQubeClientManager sonarQubeClientManager,
    SeverityModeService severityModeService, StorageService storageService, RulesRepository rulesRepository, ConnectionConfigurationRepository connectionConfigurationRepository,
    InitializeParams initializeParams, ApplicationEventPublisher eventPublisher) {
    this.configurationRepository = configurationRepository;
    this.languageSupportRepository = languageSupportRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.severityModeService = severityModeService;
    this.storageService = storageService;
    this.rulesRepository = rulesRepository;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.hotspotEnabled = initializeParams.getBackendCapabilities().contains(SECURITY_HOTSPOTS);
    this.eventPublisher = eventPublisher;
    this.standaloneRuleConfig.putAll(initializeParams.getStandaloneRuleConfigByKey());
  }

  public void updateStandaloneRulesConfiguration(Map<String, StandaloneRuleConfigDto> standaloneRuleConfig) {
    this.standaloneRuleConfig.clear();
    this.standaloneRuleConfig.putAll(standaloneRuleConfig);
    this.standaloneActiveRules = null;
    eventPublisher.publishEvent(new StandaloneRulesConfigurationChanged(standaloneRuleConfig));
  }

  public Map<String, StandaloneRuleConfigDto> getStandaloneRuleConfig() {
    return Collections.unmodifiableMap(standaloneRuleConfig);
  }

  public GetStandaloneRuleDescriptionResponse getStandaloneRuleDescription(String ruleKey) {
    var embeddedRule = rulesRepository.getEmbeddedRule(ruleKey);
    if (embeddedRule.isEmpty()) {
      var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, COULD_NOT_FIND_RULE + ruleKey + IN_EMBEDDED_RULES, new Object[] {ruleKey});
      throw new ResponseErrorException(error);
    }
    var ruleDefinition = embeddedRule.get();
    var ruleDetails = RuleDetails.from(ruleDefinition, standaloneRuleConfig.get(ruleKey));

    return new GetStandaloneRuleDescriptionResponse(RulesService.convert(ruleDefinition), RuleDetailsAdapter.transformDescriptions(ruleDetails, null));
  }

  public synchronized List<ActiveRuleDetails> getStandaloneActiveRules() {
    if (standaloneActiveRules == null) {
      standaloneActiveRules = buildStandaloneActiveRules();
    }
    return standaloneActiveRules;
  }

  private List<ActiveRuleDetails> buildStandaloneActiveRules() {
    Set<String> excludedRules = standaloneRuleConfig.entrySet().stream()
      .filter(not(e -> e.getValue().isActive()))
      .map(Map.Entry::getKey).collect(toSet());
    Set<String> includedRules = standaloneRuleConfig.entrySet().stream()
      .filter(e -> e.getValue().isActive())
      .map(Map.Entry::getKey)
      .filter(r -> !excludedRules.contains(r))
      .collect(toSet());

    var filteredActiveRules = new ArrayList<SonarLintRuleDefinition>();

    var allRulesDefinitions = rulesRepository.getEmbeddedRules().stream()
      .filter(rule -> !rule.getType().equals(RuleType.SECURITY_HOTSPOT))
      .toList();

    filteredActiveRules.addAll(allRulesDefinitions.stream()
      .filter(SonarLintRuleDefinition::isActiveByDefault)
      .filter(isExcludedByConfiguration(excludedRules))
      .toList());
    filteredActiveRules.addAll(allRulesDefinitions.stream()
      .filter(r -> !r.isActiveByDefault())
      .filter(isIncludedByConfiguration(includedRules))
      .toList());

    return filteredActiveRules.stream().map(ruleDefinition -> {
      Map<String, String> effectiveParams = new HashMap<>(ruleDefinition.getDefaultParams());
      ofNullable(standaloneRuleConfig.get(ruleDefinition.getKey())).ifPresent(config -> effectiveParams.putAll(config.getParamValueByKey()));
      //ACR-fedb9800328c4bf8817e0c9d92f81cd3
      return new ActiveRuleDetails(ruleDefinition.getKey(), ruleDefinition.getLanguage().getSonarLanguageKey(), effectiveParams, null, ruleDefinition.getDefaultSeverity(),
        ruleDefinition.getType(), ruleDefinition.getCleanCodeAttribute().orElse(CONVENTIONAL), ruleDefinition.getDefaultImpacts(),
        ruleDefinition.getVulnerabilityProbability().orElse(null));
    })
      .toList();
  }

  public List<ActiveRuleDetails> getConnectedActiveRules(Binding binding) {
    return activeRulesPerBinding.computeIfAbsent(binding, k -> buildConnectedActiveRules(binding));
  }

  private List<ActiveRuleDetails> buildConnectedActiveRules(Binding binding) {
    var analyzerConfig = storageService.binding(binding).analyzerConfiguration().read();
    var ruleSetByLanguageKey = analyzerConfig.getRuleSetByLanguageKey();
    var result = new ArrayList<ActiveRuleDetails>();
    ruleSetByLanguageKey.entrySet()
      .stream().filter(e -> SonarLanguage.forKey(e.getKey()).filter(l -> languageSupportRepository.getEnabledLanguagesInConnectedMode().contains(l)).isPresent())
      .forEach(e -> {
        var languageKey = e.getKey();
        var ruleSet = e.getValue();

        LOG.debug("  * {}: {} active rules", languageKey, ruleSet.getRules().size());
        var missingRuleOrTemplateDefinitions = new LinkedHashSet<>();
        for (ServerActiveRule possiblyDeprecatedActiveRuleFromStorage : ruleSet.getRules()) {
          var activeRuleFromStorage = tryConvertDeprecatedKeys(binding.connectionId(), possiblyDeprecatedActiveRuleFromStorage);
          SonarLintRuleDefinition ruleOrTemplateDefinition;
          if (StringUtils.isNotBlank(activeRuleFromStorage.getTemplateKey())) {
            ruleOrTemplateDefinition = rulesRepository.getRule(binding.connectionId(), activeRuleFromStorage.getTemplateKey()).orElse(null);
            if (ruleOrTemplateDefinition == null) {
              LOG.debug("Rule {} is enabled on the server, but its template {} is not available in SonarLint", activeRuleFromStorage.getRuleKey(),
                activeRuleFromStorage.getTemplateKey());
              continue;
            }
          } else {
            ruleOrTemplateDefinition = rulesRepository.getRule(binding.connectionId(), activeRuleFromStorage.getRuleKey()).orElse(null);
            if (ruleOrTemplateDefinition == null) {
              missingRuleOrTemplateDefinitions.add(activeRuleFromStorage.getRuleKey());
              continue;
            }
          }
          if (shouldIncludeRuleForAnalysis(binding.connectionId(), ruleOrTemplateDefinition)) {
            result.add(buildActiveRule(ruleOrTemplateDefinition, activeRuleFromStorage));
          }
        }
        if (!missingRuleOrTemplateDefinitions.isEmpty()) {
          LOG.debug("The following rules are enabled on the server, but not available in SonarLint: {}", missingRuleOrTemplateDefinitions);
        }
      });
    if (languageSupportRepository.getEnabledLanguagesInConnectedMode().contains(SonarLanguage.IPYTHON)) {
      //ACR-bf447d84e5964d03887843f4f37202b5
      var iPythonRules = getStandaloneActiveRules()
        .stream().filter(rule -> rule.languageKey().equals(SonarLanguage.IPYTHON.getSonarLanguageKey()))
        .toList();
      result.addAll(iPythonRules);
    }
    return result;
  }

  public ActiveRuleDetails buildActiveRule(SonarLintRuleDefinition ruleOrTemplateDefinition, ServerActiveRule activeRule) {
    return new ActiveRuleDetails(activeRule.getRuleKey(),
      ruleOrTemplateDefinition.getLanguage().getSonarLanguageKey(),
      getEffectiveParams(ruleOrTemplateDefinition, activeRule),
      trimToNull(activeRule.getTemplateKey()), activeRule.getSeverity(), ruleOrTemplateDefinition.getType(),
      ruleOrTemplateDefinition.getCleanCodeAttribute().orElse(CONVENTIONAL),
      RuleDetails.mergeImpacts(ruleOrTemplateDefinition.getDefaultImpacts(), activeRule.getOverriddenImpacts()),
      ruleOrTemplateDefinition.getVulnerabilityProbability().orElse(null));
  }

  private static Map<String, String> getEffectiveParams(SonarLintRuleDefinition ruleOrTemplateDefinition, ServerActiveRule activeRule) {
    Map<String, String> effectiveParams = new HashMap<>(ruleOrTemplateDefinition.getDefaultParams());
    activeRule.getParams().forEach((paramName, paramValue) -> {
      if (!ruleOrTemplateDefinition.getParams().containsKey(paramName)) {
        LOG.debug("Rule parameter '{}' for rule '{}' does not exist in embedded analyzer, ignoring.", paramName, ruleOrTemplateDefinition.getKey());
        return;
      }
      effectiveParams.put(paramName, paramValue);
    });
    return effectiveParams;
  }

  private boolean shouldIncludeRuleForAnalysis(String connectionId, SonarLintRuleDefinition ruleDefinition) {
    var isHotspot = ruleDefinition.getType().equals(RuleType.SECURITY_HOTSPOT);
    return !isHotspot || (hotspotEnabled && isHotspotTrackingPossible(connectionId));
  }

  private boolean isHotspotTrackingPossible(String connectionId) {
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    if (connection == null) {
      //ACR-ffeada392f554b73af5a8b94002384b2
      return false;
    }
    //ACR-b5c4d16af6074afdab8eae458dbf4b42
    return storageService.connection(connectionId).serverInfo().read().isPresent();
  }

  private static Predicate<? super SonarLintRuleDefinition> isExcludedByConfiguration(Set<String> excludedRules) {
    return r -> {
      if (excludedRules.contains(r.getKey())) {
        return false;
      }
      for (String deprecatedKey : r.getDeprecatedKeys()) {
        if (excludedRules.contains(deprecatedKey)) {
          LOG.warn("Rule '{}' was excluded using its deprecated key '{}'. Please fix your configuration.", r.getKey(), deprecatedKey);
          return false;
        }
      }
      return true;
    };
  }

  private static Predicate<? super SonarLintRuleDefinition> isIncludedByConfiguration(Set<String> includedRules) {
    return r -> {
      if (includedRules.contains(r.getKey())) {
        return true;
      }
      for (String deprecatedKey : r.getDeprecatedKeys()) {
        if (includedRules.contains(deprecatedKey)) {
          LOG.warn("Rule '{}' was included using its deprecated key '{}'. Please fix your configuration.", r.getKey(), deprecatedKey);
          return true;
        }
      }
      return false;
    };
  }

  @EventListener
  public void onConnectionRemoved(ConnectionConfigurationRemovedEvent event) {
    var iterator = activeRulesPerBinding.entrySet().iterator();
    while (iterator.hasNext()) {
      var binding = iterator.next().getKey();
      if (binding.connectionId().equals(event.getRemovedConnectionId())) {
        //ACR-cd907a14cb2c4533ba0326442edb0f1a
        iterator.remove();
      }
    }
  }

  @EventListener
  public void onBindingUpdated(BindingConfigChangedEvent event) {
    var previousBinding = event.previousConfig();
    var previousConnectionId = previousBinding.connectionId();
    var previousProjectKey = previousBinding.sonarProjectKey();
    if (previousConnectionId != null && previousProjectKey != null
      && configurationRepository.getBoundScopesToConnectionAndSonarProject(previousConnectionId, previousProjectKey).isEmpty()) {
      //ACR-b8a3320274c64cdb8faf8d9c6991a378
      activeRulesPerBinding.remove(new Binding(previousConnectionId, previousProjectKey));
    }
  }

  @EventListener
  public void onAnalyzerConfigurationSynchronized(AnalyzerConfigurationSynchronized event) {
    //ACR-981ea7f1b5944d0490d9e755297988c8
    activeRulesPerBinding.remove(event.binding());
  }

  @EventListener
  public void onServerEventReceived(SonarServerEventReceivedEvent eventReceived) {
    var connectionId = eventReceived.getConnectionId();
    var serverEvent = eventReceived.getEvent();
    if (serverEvent instanceof RuleSetChangedEvent ruleSetChangedEvent) {
      //ACR-6cc3b39a8aeb432baf9dd4e24a46b589
      ruleSetChangedEvent.getProjectKeys().forEach(projectKey -> activeRulesPerBinding.remove(new Binding(connectionId, projectKey)));
      updateStorage(connectionId, ruleSetChangedEvent);
      eventPublisher.publishEvent(
        new ServerActiveRulesChanged(connectionId, ruleSetChangedEvent.getProjectKeys(), ruleSetChangedEvent.getActivatedRules(), ruleSetChangedEvent.getDeactivatedRules()));
    }
  }

  private void updateStorage(String connectionId, RuleSetChangedEvent event) {
    event.getProjectKeys().forEach(projectKey -> storageService.connection(connectionId).project(projectKey).analyzerConfiguration().update(currentConfiguration -> {
      var newRuleSetByLanguageKey = incorporate(event, currentConfiguration.getRuleSetByLanguageKey());
      return new AnalyzerConfiguration(currentConfiguration.getSettings(), newRuleSetByLanguageKey, currentConfiguration.getSchemaVersion());
    }));
  }

  private static Map<String, RuleSet> incorporate(RuleSetChangedEvent event, Map<String, RuleSet> ruleSetByLanguageKey) {
    Map<String, RuleSet> resultingRuleSetsByLanguageKey = new HashMap<>(ruleSetByLanguageKey);
    event.getDeactivatedRules().forEach(deactivatedRule -> deactivate(deactivatedRule, resultingRuleSetsByLanguageKey));
    event.getActivatedRules().forEach(activatedRule -> activate(activatedRule, resultingRuleSetsByLanguageKey));
    return resultingRuleSetsByLanguageKey;
  }

  private static void activate(RuleSetChangedEvent.ActiveRule activatedRule, Map<String, RuleSet> ruleSetsByLanguageKey) {
    var ruleLanguageKey = activatedRule.getLanguageKey();
    var currentRuleSet = ruleSetsByLanguageKey.computeIfAbsent(ruleLanguageKey, k -> new RuleSet(Collections.emptyList(), ""));
    var languageRulesByKey = new HashMap<>(currentRuleSet.getRulesByKey());
    var ruleTemplateKey = activatedRule.getTemplateKey();
    languageRulesByKey.put(activatedRule.getKey(), new ServerActiveRule(
      activatedRule.getKey(),
      activatedRule.getSeverity(),
      activatedRule.getParameters(),
      ruleTemplateKey == null ? "" : ruleTemplateKey,
      activatedRule.getOverriddenImpacts()));
    ruleSetsByLanguageKey.put(ruleLanguageKey, new RuleSet(new ArrayList<>(languageRulesByKey.values()), currentRuleSet.getLastModified()));
  }

  private static void deactivate(String deactivatedRuleKey, Map<String, RuleSet> ruleSetsByLanguageKey) {
    var ruleSetsIterator = ruleSetsByLanguageKey.entrySet().iterator();
    while (ruleSetsIterator.hasNext()) {
      var ruleSetEntry = ruleSetsIterator.next();
      var ruleSet = ruleSetEntry.getValue();
      var newRules = new HashMap<>(ruleSet.getRulesByKey());
      newRules.remove(deactivatedRuleKey);
      if (newRules.isEmpty()) {
        ruleSetsIterator.remove();
      } else {
        ruleSetsByLanguageKey.put(ruleSetEntry.getKey(), new RuleSet(List.copyOf(newRules.values()), ruleSet.getLastModified()));
      }
    }
  }

  public EffectiveRuleDetailsDto getEffectiveRuleDetails(String configurationScopeId, String ruleKey, @Nullable String contextKey,
    SonarLintCancelMonitor cancelMonitor) throws RuleNotFoundException {
    var ruleDetails = getActiveRuleDetails(configurationScopeId, ruleKey, cancelMonitor);
    return RuleDetailsAdapter.transform(ruleDetails, contextKey);
  }

  public RuleDetails getActiveRuleDetails(String configurationScopeId, String ruleKey, SonarLintCancelMonitor cancelMonitor) throws RuleNotFoundException {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configurationScopeId);
    RuleDetails ruleDetails;
    if (effectiveBinding.isEmpty()) {
      var embeddedRule = rulesRepository.getEmbeddedRule(ruleKey);
      if (embeddedRule.isEmpty()) {
        throw new RuleNotFoundException(COULD_NOT_FIND_RULE + ruleKey + IN_EMBEDDED_RULES, ruleKey);
      }
      ruleDetails = RuleDetails.from(embeddedRule.get(), standaloneRuleConfig.get(ruleKey));
    } else {
      ruleDetails = getActiveRuleForBinding(ruleKey, effectiveBinding.get(), cancelMonitor);
    }
    return ruleDetails;
  }

  private RuleDetails getActiveRuleForBinding(String ruleKey, Binding binding, SonarLintCancelMonitor cancelMonitor) {
    var connectionId = binding.connectionId();
    sonarQubeClientManager.getClientOrThrow(connectionId);

    var serverUsesStandardSeverityMode = !severityModeService.isMQRModeForConnection(connectionId);

    return findServerActiveRuleInStorage(binding, ruleKey)
      .map(storageRule -> hydrateDetailsWithServer(connectionId, storageRule, serverUsesStandardSeverityMode, cancelMonitor))
      //ACR-cbfb05ec7b8b42718a660dbb957f3e3b
      .orElseGet(() -> rulesRepository.getRule(connectionId, ruleKey)
        .map(r -> RuleDetails.from(r, standaloneRuleConfig.get(ruleKey)))
        .orElseThrow(() -> ruleNotFoundInPlugins(ruleKey, connectionId)));
  }

  private Optional<ServerActiveRule> findServerActiveRuleInStorage(Binding binding, String ruleKey) {
    AnalyzerConfiguration analyzerConfiguration;
    try {
      analyzerConfiguration = storageService.binding(binding).analyzerConfiguration().read();
    } catch (StorageException e) {
      //ACR-36afb0526efe4da3beb44c648a390071
      return Optional.empty();
    }
    return analyzerConfiguration.getRuleSetByLanguageKey().values().stream()
      .flatMap(s -> s.getRules().stream())
      //ACR-f2b2afd32852452ba10c923c6b4462da
      .filter(r -> tryConvertDeprecatedKeys(binding.connectionId(), r).getRuleKey().equals(ruleKey)).findFirst();
  }

  private ServerActiveRule tryConvertDeprecatedKeys(String connectionId, ServerActiveRule possiblyDeprecatedActiveRuleFromStorage) {
    SonarLintRuleDefinition ruleOrTemplateDefinition;
    if (StringUtils.isNotBlank(possiblyDeprecatedActiveRuleFromStorage.getTemplateKey())) {
      ruleOrTemplateDefinition = rulesRepository.getRule(connectionId, possiblyDeprecatedActiveRuleFromStorage.getTemplateKey()).orElse(null);
      if (ruleOrTemplateDefinition == null) {
        //ACR-e1df3cc918a547bea0f60de63099036e
        return possiblyDeprecatedActiveRuleFromStorage;
      }
      var ruleKeyPossiblyWithDeprecatedRepo = RuleKey.parse(possiblyDeprecatedActiveRuleFromStorage.getRuleKey());
      var templateRuleKeyWithCorrectRepo = RuleKey.parse(ruleOrTemplateDefinition.getKey());
      var ruleKey = new RuleKey(templateRuleKeyWithCorrectRepo.repository(), ruleKeyPossiblyWithDeprecatedRepo.rule()).toString();
      return new ServerActiveRule(ruleKey, possiblyDeprecatedActiveRuleFromStorage.getSeverity(), possiblyDeprecatedActiveRuleFromStorage.getParams(),
        ruleOrTemplateDefinition.getKey(), possiblyDeprecatedActiveRuleFromStorage.getOverriddenImpacts());
    } else {
      ruleOrTemplateDefinition = rulesRepository.getRule(connectionId, possiblyDeprecatedActiveRuleFromStorage.getRuleKey()).orElse(null);
      if (ruleOrTemplateDefinition == null) {
        //ACR-f64da2cea2f142feaf5affbb573fd803
        return possiblyDeprecatedActiveRuleFromStorage;
      }
      return new ServerActiveRule(ruleOrTemplateDefinition.getKey(), possiblyDeprecatedActiveRuleFromStorage.getSeverity(), possiblyDeprecatedActiveRuleFromStorage.getParams(),
        null, possiblyDeprecatedActiveRuleFromStorage.getOverriddenImpacts());
    }
  }

  private RuleDetails hydrateDetailsWithServer(String connectionId, ServerActiveRule activeRuleFromStorage, boolean skipCleanCodeTaxonomy, SonarLintCancelMonitor cancelMonitor) {
    var ruleKey = activeRuleFromStorage.getRuleKey();
    var templateKey = activeRuleFromStorage.getTemplateKey();
    var serverConnection = sonarQubeClientManager.getClientOrThrow(connectionId);
    if (StringUtils.isNotBlank(templateKey)) {
      var templateRule = rulesRepository.getRule(connectionId, templateKey);
      if (templateRule.isEmpty()) {
        throw ruleDefinitionNotFound(templateKey);
      }
      var serverRule = serverConnection.withClientApiAndReturn(serverApi -> fetchRuleFromServer(connectionId, ruleKey, serverApi, cancelMonitor));
      return RuleDetails.merging(activeRuleFromStorage, serverRule, templateRule.get(), skipCleanCodeTaxonomy);
    } else {
      var serverRule = serverConnection.withClientApiAndReturn(serverApi -> fetchRuleFromServer(connectionId, ruleKey, serverApi, cancelMonitor));
      var ruleDefFromPluginOpt = rulesRepository.getRule(connectionId, ruleKey);
      return ruleDefFromPluginOpt
        .map(ruleDefFromPlugin -> RuleDetails.merging(serverRule, ruleDefFromPlugin, skipCleanCodeTaxonomy))
        .orElseGet(() -> RuleDetails.merging(activeRuleFromStorage, serverRule));
    }
  }

  private static ServerRule fetchRuleFromServer(String connectionId, String ruleKey, ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    return serverApi.rules().getRule(ruleKey, cancelMonitor).orElseThrow(() -> ruleNotFoundOnServer(ruleKey, connectionId));
  }

  private static ResponseErrorException ruleDefinitionNotFound(String templateKey) {
    var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, "Unable to find rule definition for rule template " + templateKey, templateKey);
    return new ResponseErrorException(error);
  }

  @NotNull
  private static ResponseErrorException ruleNotFoundInPlugins(String ruleKey, String connectionId) {
    var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, COULD_NOT_FIND_RULE + ruleKey + "' in plugins loaded from '" + connectionId + "'",
      new Object[] {connectionId, ruleKey});
    return new ResponseErrorException(error);
  }

  private static ResponseErrorException ruleNotFoundOnServer(String ruleKey, String connectionId) {
    var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, COULD_NOT_FIND_RULE + ruleKey + "' on server '" + connectionId + "'",
      new Object[] {connectionId, ruleKey});
    return new ResponseErrorException(error);
  }
}
