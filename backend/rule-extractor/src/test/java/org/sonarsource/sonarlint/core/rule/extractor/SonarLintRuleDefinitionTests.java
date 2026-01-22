/*
ACR-069cb67907f34203b7244e33508a4659
ACR-2a7f8b0bc9fa40bc85507095312e2a01
ACR-679aee19cbbe41cfaec02f94d1d8fa74
ACR-217c057cf5294874b541cde0329ddf4d
ACR-589685cf1a8a4a5099de577bd872728d
ACR-5b8a29e4d703473e9ac8d30af3a48704
ACR-95d729a5439348bcadf51aa04fb291e1
ACR-830791e5ddfb421ab6fcfd243e3d9f6f
ACR-2436e0a6e75240dc81cb2eafacfeda79
ACR-620ceadbb4674bfeb451c1cff265ba23
ACR-9f5c44d9c8074a0aa440c14acfeba732
ACR-4436ac3fcc184883b8c6ff30bf69219c
ACR-0ed4896732714c9c8180473b9c773b74
ACR-af15b920eb0d465ea92f3fa135cc1437
ACR-80768650d5d6466890ab31d4a31998ac
ACR-9600007fd502483b83e53edb769ef76e
ACR-83fda4ee454d49a5bb6b90daad0a66d2
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
