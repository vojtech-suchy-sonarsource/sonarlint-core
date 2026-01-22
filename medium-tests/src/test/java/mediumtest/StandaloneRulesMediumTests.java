/*
ACR-f45f9261293749b7b8202af2e2dfde02
ACR-ae65fdfbec2b4dce89580b043134ca21
ACR-2e10a96f3ac74b879cb0ab3c98680ac5
ACR-966ef7be15dd468bb3c4c2ed8864046f
ACR-af8c4fcbc71741c0b4d1aa135bdaa58f
ACR-a242731eb12946799c6810d17c7c6964
ACR-67498f56045a475aaeb3349137546343
ACR-8e588dd1d815416d99f7f82576f1db34
ACR-d54a48edacfd48a7a51d7e698d1a27d9
ACR-eeadb66aa0de498e912affaf7f24ae01
ACR-6b55e454496c4e638ff796ccd2d6248d
ACR-a653794400184fb5ab38e0507c591340
ACR-35f944325d24424db98762f8461e4a91
ACR-6d8088debff649e49cf1b766e2ea5653
ACR-4146f7199e754988b7356044fe66ed9d
ACR-7b2b0e3cb7714fb49c7db3d1fdb5353a
ACR-fa58733d334d4d2f837a677b84f73543
 */
package mediumtest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetStandaloneRuleDescriptionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ListAllStandaloneRulesDefinitionsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class StandaloneRulesMediumTests {

  @SonarLintTest
  void it_should_return_only_embedded_rules_of_enabled_languages(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .withStandaloneEmbeddedPlugin(TestPlugin.PHP)
      .start();

    var allRules = listAllStandaloneRulesDefinitions(backend).getRulesByKey().values();

    assertThat(allRules).extracting(RuleDefinitionDto::getLanguage).containsOnly(Language.PYTHON);
  }

  @SonarLintTest
  void it_should_return_param_definition(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start();

    var javaS1176 = listAllStandaloneRulesDefinitions(backend).getRulesByKey().get("java:S1176");

    assertThat(javaS1176.getParamsByKey()).containsOnlyKeys("exclusion", "forClasses");
    assertThat(javaS1176.getParamsByKey().get("forClasses"))
      .extracting(RuleParamDefinitionDto::getKey, RuleParamDefinitionDto::getName, RuleParamDefinitionDto::getDescription,
        RuleParamDefinitionDto::getType, RuleParamDefinitionDto::isMultiple, RuleParamDefinitionDto::getPossibleValues, RuleParamDefinitionDto::getDefaultValue)
      .containsExactly("forClasses",
        "forClasses",
        "Pattern of classes which should adhere to this constraint. Ex : **.api.**",
        RuleParamType.STRING,
        false,
        List.of(),
        "**.api.**");
  }

  @SonarLintTest
  void it_should_return_rule_details_with_definition_and_description(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start();

    var ruleDetails = backend.getRulesService().getStandaloneRuleDetails(new GetStandaloneRuleDescriptionParams("java:S1176")).get();

    assertThat(ruleDetails.getRuleDefinition().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.CLEAR);
    assertThat(ruleDetails.getRuleDefinition().getSoftwareImpacts())
      .extracting(ImpactDto::getSoftwareQuality, ImpactDto::getImpactSeverity)
      .containsExactly(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM));
    assertThat(ruleDetails.getRuleDefinition().getName()).isEqualTo("Public types, methods and fields (API) should be documented with Javadoc");
    assertThat(ruleDetails.getDescription().isRight()).isTrue();
    assertThat(ruleDetails.getDescription().getRight().getIntroductionHtmlContent())
      .startsWith("<p>A good API documentation is a key factor in the usability and success of a software API");
  }

  @SonarLintTest
  void it_should_not_contain_rule_templates(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start();

    var allRules = listAllStandaloneRulesDefinitions(backend).getRulesByKey().values();

    assertThat(allRules).extracting(RuleDefinitionDto::getKey).isNotEmpty().doesNotContain("python:XPath");
    assertThat(backend.getRulesService().getStandaloneRuleDetails(new GetStandaloneRuleDescriptionParams("python:XPath"))).failsWithin(1, TimeUnit.MINUTES);
  }

  @SonarLintTest
  void it_should_not_contain_hotspots_by_default(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start();

    var allRules = listAllStandaloneRulesDefinitions(backend).getRulesByKey().values();

    assertThat(allRules).extracting(RuleDefinitionDto::getKey).isNotEmpty().doesNotContain("java:S1313");
    assertThat(backend.getRulesService().getStandaloneRuleDetails(new GetStandaloneRuleDescriptionParams("java:S1313"))).failsWithin(1, TimeUnit.MINUTES);
  }

  private ListAllStandaloneRulesDefinitionsResponse listAllStandaloneRulesDefinitions(SonarLintTestRpcServer backend) {
    try {
      return backend.getRulesService().listAllStandaloneRulesDefinitions().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
