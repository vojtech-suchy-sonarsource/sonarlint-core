/*
ACR-442afc16da7e4002bec72d69d6d2a0b9
ACR-99ef87164bf042abbba2d26d04045467
ACR-22a4d0d3db8345b380fdf69536a0bad1
ACR-b9aa3ccf82954923a94c1ad6fa881a9a
ACR-f31446fbceae4c2f914cb0d05198fffc
ACR-3661cb5d8b6b47209c212900fef684af
ACR-070d7aafa2f243a2a253d43ff7f679ba
ACR-1229f4808fdb479a8352fa193fb1eece
ACR-f580ee2c7b634e2a872473bb7388d404
ACR-212d0f01c2434c4ca44a088f2c126217
ACR-ae300bb19f8b4a0b9bea549df1e916d6
ACR-70f585e1265c41c9a863c5db71331eaa
ACR-628125c3745e4671b64c0cfd92aceb92
ACR-b8eb0194f8dd4580b24b2ff6975c6e1f
ACR-f47052499ba24c2189196f24149a8c66
ACR-3880bd6a31ad473a8f90523f7a28fe59
ACR-0c7d8f76befd4c52867d64b7e90a8c78
 */
package org.sonarsource.sonarlint.core.rules;

import org.sonar.api.rules.CleanCodeAttribute;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;

public class RulesFixtures {
  public static SonarLintRuleDefinition aRule() {
    RulesDefinition.Context c = new RulesDefinition.Context();
    var repository = c.createRepository("repo", SonarLanguage.JAVA.getSonarLanguageKey());
    repository.createRule("ruleKey")
      .setName("ruleName")
      .setType(RuleType.BUG)
      .setCleanCodeAttribute(CleanCodeAttribute.TRUSTWORTHY)
      .setHtmlDescription("Hello, world!")
        .createParam("paramKey")
          .setName("paramName")
          .setType(RuleParamType.TEXT)
          .setDescription("paramDesc")
          .setDefaultValue("defaultValue");
    repository.done();
    var rule = c.repositories().get(0).rule("ruleKey");
    return new SonarLintRuleDefinition(rule);
  }
}
