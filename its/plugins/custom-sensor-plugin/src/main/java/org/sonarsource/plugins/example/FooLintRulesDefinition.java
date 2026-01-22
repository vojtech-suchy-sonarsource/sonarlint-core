/*
ACR-a3dd8b2ddbcb44b7a7b4448571a37c76
ACR-4aeb92162eb24e3988d9b6fd4a55b912
ACR-ba8c108477cb4d0886d2e447cbdeb244
ACR-856f32df53c5452a883f63d0b1272f74
ACR-abe0af30e42b41ceb4fe843d185adff2
ACR-206f546c7adc4b2f944b996fa6860b1d
ACR-de12274946c4499a8641ae2ea47ae017
ACR-ecad40a29feb46b1bd5fbf94653e44f7
ACR-3280d94692cb4865bfcf03c29bb6e646
ACR-4d36400c3507429d9525c4fb94dc33dd
ACR-0fe67bcce00c43d3ae59bfa36935ca74
ACR-6d666632745043b2b9c8f3fca8a5e2b5
ACR-117f22845f8a4d1191dd69dd7e321929
ACR-203cd6bf4e584d948f5490f3b9e38473
ACR-62118d887c0a4b839f72de9785413531
ACR-4e28764ce6694e6c9c128de0e621b181
ACR-1392d2b032b5468aa2fc93425037e406
 */
package org.sonarsource.plugins.example;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

public final class FooLintRulesDefinition implements RulesDefinition {

  private static final String PATH_TO_RULES_XML = "/example/foolint-rules.xml";

  static final String KEY = "foolint";
  private static final String NAME = "FooLint";

  private final RulesDefinitionXmlLoader xmlLoader;

  public FooLintRulesDefinition(RulesDefinitionXmlLoader xmlLoader) {
    this.xmlLoader = xmlLoader;
  }

  private String rulesDefinitionFilePath() {
    return PATH_TO_RULES_XML;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(KEY, "java").setName(NAME);

    InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath());
    if (rulesXml != null) {
      xmlLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
    }

    repository.done();
  }

}
