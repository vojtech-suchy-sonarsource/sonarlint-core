/*
ACR-c4cc567a48064350b3c9ca4d3cc88b1a
ACR-76a6227869d54e9ca1e3cffcec4c5767
ACR-44e8393e583046e49ce15b46a8144ad1
ACR-02a7f48ef58f488482a978129ac70128
ACR-6cf38e7a481c4d56aa4bf405b96058c0
ACR-5543f24b69ee4b328f4fd22d590aaa4f
ACR-17530ea52ef34855b39206390c11e047
ACR-147b1a3649874cb58e556823c5adb19f
ACR-6f9fae0811754f97a207a93b610b4a9b
ACR-1d122accafca4fbdb868efe23ad5229b
ACR-5df3de5ca5594e799367be30adc93467
ACR-e3e5bdfd84154f5d876250fcf52a9d1a
ACR-d54bfe0f1f4b44e8865f7e9526e7ffca
ACR-813b97c19dd64117895a9b94dc4ba184
ACR-36ded4e4f06f4e5aa7955f115ee510aa
ACR-a84753fb894640098758d4d777aaae35
ACR-808421d01307479f829dec1f3440617b
 */
package org.sonar.samples.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.Version;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

/*ACR-83da892cf54e4c7c8ba40714866ae591
ACR-524534bb963f4da196d1bf43f804980c
ACR-17655f14ae954e869bef456bb8731fba
 */
public class MyJavaRulesDefinition implements RulesDefinition {

  //ACR-0b5e798035854c10b70cc9d8303b30c9
  private static final String RESOURCE_BASE_PATH = "org/sonar/l10n/java/rules/java";

  public static final String REPOSITORY_KEY = "mycompany-java";

  //ACR-c3970e8e935c4bdb9de0710dea3bdff4
  private static final Set<String> RULE_TEMPLATES_KEY = Collections.emptySet();

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY_KEY, "java").setName("MyCompany Custom Repository");

    //ACR-509d04e534234e0aa9989dc2b6b0fc64
    //ACR-0eceac7554084fb998097444d6488507
    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, getSonarLintRuntime(Version.parse("10.3.0")));

    ruleMetadataLoader.addRulesByAnnotatedClass(repository, new ArrayList<>(RulesList.getChecks()));

    setTemplates(repository);

    repository.createRule("markdown")
      .setName("A rule with Markdown description")
      .setMarkdownDescription("  = Title\n  * one\n* two");

    repository.done();
  }

  private static SonarRuntime getSonarLintRuntime(Version version) {
    return new SonarRuntime() {

      @Override
      public Version getApiVersion() {
        return version;
      }

      @Override
      public SonarProduct getProduct() {
        return SonarProduct.SONARLINT;
      }

      @Override
      public SonarQubeSide getSonarQubeSide() {
        return null;
      }

      @Override
      public SonarEdition getEdition() {
        return null;
      }
    };
  }

  private static void setTemplates(NewRepository repository) {
    RULE_TEMPLATES_KEY.stream()
      .map(repository::rule)
      .filter(Objects::nonNull)
      .forEach(rule -> rule.setTemplate(true));
  }
}
