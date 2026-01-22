/*
ACR-3ce241705279412e9778f8a98a2d5cf7
ACR-fe42a768009240ef968156edd3389b28
ACR-ae5921b731fc426eaa13c6b561d89d8f
ACR-15dd20d8b5b44188b8ba741e31a4e3bf
ACR-ae46c68fd6f14628a5adca723cb889c4
ACR-90791ec4977a4fcd877be2a56c001e4b
ACR-0b61fda74aa047d58ad9a1a4c3b04fc8
ACR-0271cfca3e7345a2af34cb81f0f024b2
ACR-51e0a65285dc417e8b8dc05bd87d1b85
ACR-7d3f0d3db6084c09af889ae165aaff32
ACR-d9a0addf1cd140088c3613c1e46e0837
ACR-759e35dcdbd643b488c942fa2b07af92
ACR-c01fc9ca67e348209300e00433dcbc27
ACR-475ff9942dc54ed59af203a973200b3e
ACR-944d8ca29dd246a788bbd0099f7a2186
ACR-89f2457908604f53b9e717de96c91db3
ACR-bae879da34164504ae1cf42eb591b2c3
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sonar.api.Plugin;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class RulesDefinitionExtractor {

  public List<SonarLintRuleDefinition> extractRules(Map<String, Plugin> pluginInstancesByKeys, Set<SonarLanguage> enabledLanguages,
    boolean includeTemplateRules, boolean includeSecurityHotspots, RuleSettings settings) {
    Context context;
    try {
      var container = new RulesDefinitionExtractorContainer(pluginInstancesByKeys, settings);
      container.execute(null);
      context = container.getRulesDefinitionContext();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to extract rules metadata", e);
    }

    List<SonarLintRuleDefinition> rules = new ArrayList<>();

    for (var repoDef : context.repositories()) {
      if (repoDef.isExternal()) {
        continue;
      }
      var repoLanguage = SonarLanguage.forKey(repoDef.language());
      if (repoLanguage.isEmpty() || !enabledLanguages.contains(repoLanguage.get())) {
        continue;
      }
      for (RulesDefinition.Rule ruleDef : repoDef.rules()) {
        if (shouldIgnoreAsHotspot(includeSecurityHotspots, ruleDef) || shouldIgnoreAsTemplate(includeTemplateRules, ruleDef)) {
          continue;
        }
        rules.add(new SonarLintRuleDefinition(ruleDef));
      }
    }

    return rules;

  }

  private static boolean shouldIgnoreAsTemplate(boolean includeTemplateRules, RulesDefinition.Rule ruleDef) {
    return ruleDef.template() && !includeTemplateRules;
  }

  private static boolean shouldIgnoreAsHotspot(boolean hotspotsEnabled, RulesDefinition.Rule ruleDef) {
    return ruleDef.type() == RuleType.SECURITY_HOTSPOT && !hotspotsEnabled;
  }

}
