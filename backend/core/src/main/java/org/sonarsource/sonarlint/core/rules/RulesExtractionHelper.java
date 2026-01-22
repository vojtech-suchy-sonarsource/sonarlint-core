/*
ACR-e6e37993c0954498ad13a176521263b3
ACR-b1a9d39813c14a3bbbbd7bccca34502a
ACR-05647f01d70e4b899074267e9e90e9ca
ACR-398b1b1c57df4eeeb2c7f3dfd4848512
ACR-521efa3d18de41cda18f32adc5ca7100
ACR-926a0496311243fdb337e84ace1cf80b
ACR-5f4f520a17af4bb1b1d5b7e55c987411
ACR-668ac47e94634830a28f3f514a499826
ACR-1e148e94640d489baec818973c67d5cb
ACR-2d9717624ceb43688ff8004a8a824fe6
ACR-e2ebb8b8ca3d48ceb3b56611acc65da7
ACR-63120dc3e348401db1d5f55217df73c7
ACR-61a7b66e8aba457183423f257564da74
ACR-8147aee1f48f4e94a2c4345dde774668
ACR-031b5c1496094811b8d3ab37b17f22d1
ACR-b95fc1a24e494793ba99a37803d4fd2a
ACR-9b845dc6b4b74064a6c82ed1c87f8538
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
