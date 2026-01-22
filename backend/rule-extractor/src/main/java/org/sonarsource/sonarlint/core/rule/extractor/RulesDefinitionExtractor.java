/*
ACR-22960fa3ec6f4e5ca8fbdc64fdbdeaa6
ACR-b0fd6611daaa49869d7922f911afb80b
ACR-f4f7b48584de42f8a13ba38bf8aad23c
ACR-fead572245ff4a238a07e8389dd8d3cb
ACR-dc967bcae83d4450ac8f1959e94531be
ACR-bfa410cf80b14eb39806d89c4cab8c0b
ACR-6224df823b6a46b59e2df1e4628669ee
ACR-44613542f5594e8a9a3be6bb08eed85d
ACR-175b083db165452593ccd2b5e6c84b83
ACR-bfd6339e45604570a1277e68b2e17cf9
ACR-ca944a620be940efbedaaceaba56b524
ACR-25edb4643c204cbd93422cb11c3db05d
ACR-e593d5a7f6e243488e281117a7cd498d
ACR-7cfa8a7def104d3db4ff43a7d9c9bfdc
ACR-bc27d9f26b494c25a9d8a11754773dc5
ACR-9fe43af08dee428380517a76b324bdde
ACR-e79b296893cf48158920f51815d793a6
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
