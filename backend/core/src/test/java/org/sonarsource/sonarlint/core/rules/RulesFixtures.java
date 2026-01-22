/*
ACR-342795a3bdb74c99adb6ffb01f832cbd
ACR-64a5f1c49f984703ac51e7d9451da978
ACR-4e6287951eab47f198e11450b6c0da87
ACR-c20edc88d824472796e7e3163ec67432
ACR-45cc4b5d6fa5428daf106901d73fa720
ACR-52bb8d7c41ae41769bab78dd80c8dc41
ACR-fb484a9b41774447bfe289c2ea8f71a7
ACR-172c655f31b7403b86e3793759cee287
ACR-720a1ccff52e4f2e9f6b4061b7f99749
ACR-cd85167456c841d0b3bb2392fe5a2819
ACR-b6a20c5f75174e7ebfb9dd9b3d616372
ACR-01225bf69fb0438c9f15768df02ee6c4
ACR-1ddfa998b61049d1a086a53c627d1d87
ACR-4b3ebd2e5861428686d14ae54f1abfb3
ACR-7f243044b9b343b489e253146bf56f4c
ACR-ba2d7b5ec42545948057b28d0ab84d5e
ACR-a8ce6796dbb14c099aa08b69da8c39bb
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
