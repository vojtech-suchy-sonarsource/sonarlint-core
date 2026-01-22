/*
ACR-08cca62fae9d4d68bba8f439d12eb50c
ACR-27635e25354644448416ea804c943ae1
ACR-9cd6afad61584a70b44b605011216b18
ACR-921bbde74cf942cdac048e7cc2471432
ACR-00a0d02db4434e2095db3e0b6cdbe413
ACR-4c5fd7b72b30422397d64e4078be1b3f
ACR-db2592ff53ff496f9b46dd6ede844aa4
ACR-a2f3f503090f43dbaed3bc7744fa6636
ACR-dc72ccfe96504127bb2f75b7dea4c7c4
ACR-8eee1f4a1c8e4b28b937096091b02adb
ACR-f3290633fa7d4b0e935cb12dd92afb44
ACR-6d312351e76444b28781f3df50c53577
ACR-b39dacbb20fc4ef1b617bd605db77e8d
ACR-185c4d08b639499e8ecadfaa45c07699
ACR-28ba0eb3d01542278fb85c410f80a944
ACR-3360cdae8dbd42ed8d637c96dcf78d7f
ACR-07c93a078d03410abd3b0c599d5e36be
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
