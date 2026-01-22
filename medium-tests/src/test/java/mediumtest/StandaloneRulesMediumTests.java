/*
ACR-15d5c0bcb2b3419e900bca3e4515ca3c
ACR-fbb73b39c30149049fb1eff258862c83
ACR-ccf38edef6944429bab6815d3c6d40de
ACR-0a53e82b53d349cb8949e9dd40202609
ACR-1854a51422094398a707ca802fadfd51
ACR-ee3e02411c3a4bf9ad613c3e9e7ff65c
ACR-cece45ad0bd54d459475e3dc6b1c51ad
ACR-821c161f29b84b38b786f04551105d57
ACR-a312fadeeb6b441d81d48827dfa1bc64
ACR-578a9f1112b340a38e83c46366f510c7
ACR-a3d00fb2058a422c9f35294fc969b0a3
ACR-40976b70db9f4797940405df25ac4a97
ACR-896e658026af4f20b3301ead92093514
ACR-4534f567a04844b0a11db24e330d103d
ACR-7f0d53a604054c389337fffc541c01a7
ACR-23da751ad70c4a03a2df895846985d2f
ACR-e6926300d4324fdaa134120e0663bc0c
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
