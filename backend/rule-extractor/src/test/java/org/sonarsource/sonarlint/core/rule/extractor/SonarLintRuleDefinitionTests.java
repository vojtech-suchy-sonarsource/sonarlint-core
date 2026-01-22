/*
ACR-b5426b1316d94f9c979af0b285fe62d0
ACR-c40adb4947e74cb29ab3d9857a2de249
ACR-50a251bb898e4340ba053355bc044acb
ACR-1e1d74580340468ca42b95316d61d949
ACR-bb5b13c356e64715af7963658d976706
ACR-f906e5c02dc54f21a03c395b190816ba
ACR-e7f94e7f691a4f6c8c58118a0e7abd91
ACR-d9e43fef8df84b3ab17de6d39d87597a
ACR-a9e3d36ec2c54e238be327464e2d6958
ACR-35f0a0ada3b04b40a5eacaf3efdb4bfd
ACR-87ff9321d789475fbc9ceba82f898080
ACR-ee5a1b84264a4603bd2377f3afc4c020
ACR-de5a017a254c45489644b033739dc820
ACR-e806082960014dd4bff55afc72bf1391
ACR-fd396182af2c481a8da6928d65a089fc
ACR-d9c53569c1ba48159a041758d26baac1
ACR-db061fe7a97741d2931a991d791213a6
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintRuleDefinitionTests {

  @Test
  void convertMarkdownDescriptionToHtml() {
    RulesDefinition.Context context = new RulesDefinition.Context();
    NewRepository newRepository = context.createRepository("my-repo", "java");
    NewRule createRule = newRepository.createRule("my-rule-with-markdown-description")
      .setName("My Rule");
    createRule.setMarkdownDescription("  = Title\n  * one\n* two");
    newRepository.done();

    Rule rule = context.repositories().get(0).rule("my-rule-with-markdown-description");

    SonarLintRuleDefinition underTest = new SonarLintRuleDefinition(rule);

    assertThat(underTest.getHtmlDescription()).isEqualTo("<h1>Title</h1><ul><li>one</li>\n"
      + "<li>two</li></ul>");
  }

}
