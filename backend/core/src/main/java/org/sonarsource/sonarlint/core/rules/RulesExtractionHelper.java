/*
ACR-88d5ebfbd6b7438f8959393f5bce48b6
ACR-6ae86bba04bb4336b56689f7a5fb8905
ACR-3778222f38594a318a3a8912fc9b82db
ACR-e71cbf36ff714f45807a3952887301ff
ACR-a4c765bf05b14cebb16a113a2477bc10
ACR-3b49dbe66ea645f89eb30c9f79530001
ACR-150ba464006f47159e7d69e269e74597
ACR-3578e31d00aa4895be050bc9df72744f
ACR-d890ad4b445c450a884fed15d8b6fe0a
ACR-075862780bb84045a04e0635a07e9651
ACR-a71fd712faba45c490cce12aad21afd7
ACR-1cf772dfd3cd406cbd3ec1841e87e482
ACR-1927c4be07e743af907279912bb307ac
ACR-a0f5e08069c9433a8ae3884c264d0e6d
ACR-70ef2143d5704feea59c9f5f1b83131b
ACR-461a863dd16a48de8bb60d5ba53eae6c
ACR-1dff2c67a7354a5d934a7219b6c736d7
 */
package org.sonarsource.sonarlint.core.rules;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.plugin.PluginsService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rule.extractor.RuleSettings;
import org.sonarsource.sonarlint.core.rule.extractor.RulesDefinitionExtractor;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;

import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;

public class RulesExtractionHelper {

  private final SonarLintLogger logger = SonarLintLogger.get();

  private final PluginsService pluginsService;
  private final LanguageSupportRepository languageSupportRepository;
  private final RulesDefinitionExtractor ruleExtractor = new RulesDefinitionExtractor();
  private final boolean enableSecurityHotspots;

  public RulesExtractionHelper(PluginsService pluginsService, LanguageSupportRepository languageSupportRepository, InitializeParams params) {
    this.pluginsService = pluginsService;
    this.languageSupportRepository = languageSupportRepository;
    this.enableSecurityHotspots = params.getBackendCapabilities().contains(SECURITY_HOTSPOTS);
  }

  public List<SonarLintRuleDefinition> extractEmbeddedRules() {
    logger.debug("Extracting standalone rules metadata");
    return ruleExtractor.extractRules(pluginsService.getEmbeddedPlugins().getAllPluginInstancesByKeys(),
      languageSupportRepository.getEnabledLanguagesInStandaloneMode(), false, false, new RuleSettings(Map.of()));
  }

  public List<SonarLintRuleDefinition> extractRulesForConnection(String connectionId, Map<String, String> globalSettings) {
    logger.debug("Extracting rules metadata for connection '{}'", connectionId);
    var settings = new RuleSettings(globalSettings);
    return ruleExtractor.extractRules(pluginsService.getPlugins(connectionId).getAllPluginInstancesByKeys(),
      languageSupportRepository.getEnabledLanguagesInConnectedMode(), true, enableSecurityHotspots, settings);
  }

}
