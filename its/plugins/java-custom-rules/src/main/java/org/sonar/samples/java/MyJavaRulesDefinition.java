/*
ACR-df41734a056d4b9ca793126b70a78805
ACR-4e369c7785d34165beb3d48c2b62f96c
ACR-3f55af18bab64eb0aa4cf45d032e9f33
ACR-c30660602d094bf4aea999f99929fab0
ACR-c260d458f743446595653b24262f2b95
ACR-5199ab09e83e4ab48336254cf66baf69
ACR-37ed60f8a2944568b99f4db50a236a6c
ACR-b45ead4abfe94b2ea147ae84c69d1254
ACR-79817f182c7540ee98e52c01f31d0be9
ACR-da9c7d6bc9f54bebb2a15d3bedd9a077
ACR-07520d95f1a44d9f9172780c70d16130
ACR-65d6baa9ee9d412fb9de3ca104a3eb9c
ACR-9dc5c3e2652b477faf31db759fdd9140
ACR-b1f3f82025e84539a978b3fc8aa77863
ACR-ad1cc5b623724cda81e82909ce43d24d
ACR-3217934a010940c49937a7591cde4672
ACR-d71bc23d41e44c349de3bc4c1081c4cf
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

/*ACR-0c027a9ab26f4dfabccfe286e4abd374
ACR-0aca39dc9ea94264890a38920ba4617b
ACR-f6f532d1720443fa9f2bd00580b24db6
 */
public class MyJavaRulesDefinition implements RulesDefinition {

  //ACR-9f8cc6bf02284ead8009b5b1e85b8190
  private static final String RESOURCE_BASE_PATH = "org/sonar/l10n/java/rules/java";

  public static final String REPOSITORY_KEY = "mycompany-java";

  //ACR-23f2b6e49b7b4c3d8873c91af2c5539c
  private static final Set<String> RULE_TEMPLATES_KEY = Collections.emptySet();

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY_KEY, "java").setName("MyCompany Custom Repository");

    //ACR-f884f33b4a9c4fa6aca3396144fde81b
    //ACR-10fa3b29ddf74ed0bd73d5ae98137fa0
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
