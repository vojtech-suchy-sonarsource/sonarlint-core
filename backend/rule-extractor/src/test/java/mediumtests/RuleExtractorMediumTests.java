/*
ACR-ae368c16df97444a961895f53ead3cbe
ACR-55292a1c7b004803aae39fb6aaa1d970
ACR-bfeacc38615e46ab996dfdf80d37ae61
ACR-b09eb4d5a63b49ad85e0083786a602ca
ACR-750ec1d9d2334ba1adad6928c66402f5
ACR-a9747104155e49539ccc5b425f5daff9
ACR-7e3fef8ed723427baaa11b9d8367fceb
ACR-60fb16e6e1c040539f16299902d58c50
ACR-293d173a9a2145f0b6613c41ede02cf2
ACR-d407e5b60d004282bcd467b9ed66cf26
ACR-d5744ef81257476a8a3124a047426cce
ACR-ec06155e25684c0ab203aa08a230bdeb
ACR-69c88ea78d2e4e4aadb95306f0d2615d
ACR-a530bec7968943b3aae41cfe25716b49
ACR-9e919401c0af448db04a2b8e735a284b
ACR-91079849b9134b74882609a50b9cd0c9
ACR-c6caa3a06db648e9b3f1560201b70262
 */
package mediumtests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.plugin.commons.PluginsLoader;
import org.sonarsource.sonarlint.core.rule.extractor.RuleSettings;
import org.sonarsource.sonarlint.core.rule.extractor.RulesDefinitionExtractor;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleParamType;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class RuleExtractorMediumTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final int COMMERCIAL_RULE_TEMPLATES_COUNT = 11;
  private static final int NON_COMMERCIAL_RULE_TEMPLATES_COUNT = 16;
  private static final int COMMERCIAL_SECURITY_HOTSPOTS_COUNT = 88;
  private static final int NON_COMMERCIAL_SECURITY_HOTSPOTS_COUNT = 326;
  private static final int ALL_RULES_COUNT_WITHOUT_COMMERCIAL = 2661;
  private static final int ALL_RULES_COUNT_WITH_COMMERCIAL = 4746;
  //ACR-16b14122cc7e4d2b8cbe14317973ac9e
  //ACR-632f5cd25ded4b578ce12867ae00c555
  private static final boolean COMMERCIAL_ENABLED = System.getProperty("commercial") != null;
  private static final Optional<Version> NODE_VERSION = Optional.of(Version.create("20.12.0"));
  private static final RuleSettings EMPTY_SETTINGS = new RuleSettings(Map.of());
  private static Set<Path> allJars;

  @BeforeAll
  static void prepare() throws IOException {
    var dir = Paths.get("target/plugins/");
    try (var files = Files.list(dir)) {
      allJars = files.filter(x -> x.getFileName().toString().endsWith(".jar")).collect(toSet());
    }
  }

  @Test
  void extractAllRules() {
    var enabledLanguages = Set.of(SonarLanguage.values());
    var config = new PluginsLoader.Configuration(allJars, enabledLanguages, false, NODE_VERSION);
    var result = new PluginsLoader().load(config, Set.of());

    var allRules = new RulesDefinitionExtractor().extractRules(result.getLoadedPlugins().getAllPluginInstancesByKeys(), enabledLanguages, false, false, EMPTY_SETTINGS);
    if (COMMERCIAL_ENABLED) {
      assertThat(allJars).hasSize(18);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITH_COMMERCIAL);
    } else {
      assertThat(allJars).hasSize(9);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITHOUT_COMMERCIAL);
    }

    var pythonRule = allRules.stream().filter(r -> r.getKey().equals("python:S139")).findFirst();
    assertThat(pythonRule).hasValueSatisfying(rule -> {
      assertThat(rule.getKey()).isEqualTo("python:S139");
      assertThat(rule.getType()).isEqualTo(RuleType.CODE_SMELL);
      assertThat(rule.getDefaultSeverity()).isEqualTo(IssueSeverity.MINOR);
      assertThat(rule.getLanguage()).isEqualTo(SonarLanguage.PYTHON);
      assertThat(rule.getName()).isEqualTo("Comments should not be located at the end of lines of code");
      assertThat(rule.isActiveByDefault()).isFalse();
      assertThat(rule.getParams())
        .hasSize(1)
        .hasEntrySatisfying("legalTrailingCommentPattern", param -> {
          assertThat(param.defaultValue()).isEqualTo("^#\\s*+([^\\s]++|fmt.*|type.*|noqa.*)$");
          assertThat(param.description())
            .isEqualTo("Pattern for text of trailing comments that are allowed. By default, Mypy and Black pragma comments as well as comments containing only one word.");
          assertThat(param.key()).isEqualTo("legalTrailingCommentPattern");
          assertThat(param.multiple()).isFalse();
          assertThat(param.name()).isEqualTo("legalTrailingCommentPattern");
          assertThat(param.possibleValues()).isEmpty();
          assertThat(param.type()).isEqualTo(SonarLintRuleParamType.STRING);
        });
      assertThat(rule.getDefaultParams()).containsOnly(entry("legalTrailingCommentPattern", "^#\\s*+([^\\s]++|fmt.*|type.*|noqa.*)$"));
      assertThat(rule.getDeprecatedKeys()).isEmpty();
      assertThat(rule.getHtmlDescription()).contains("<p>This rule verifies that single-line comments are not located");
      assertThat(rule.getTags()).containsOnly("convention");
      assertThat(rule.getInternalKey()).isEmpty();
    });

    var ruleWithInternalKey = allRules.stream().filter(r -> r.getKey().equals("java:NoSonar")).findFirst();
    assertThat(ruleWithInternalKey).isNotEmpty();
    assertThat(ruleWithInternalKey.get().getInternalKey()).contains("S1291");
  }

  @Test
  void extractAllRules_include_rule_templates() {
    var enabledLanguages = Set.of(SonarLanguage.values());
    var config = new PluginsLoader.Configuration(allJars, enabledLanguages, false, NODE_VERSION);
    var result = new PluginsLoader().load(config, Set.of());

    var allRules = new RulesDefinitionExtractor().extractRules(result.getLoadedPlugins().getAllPluginInstancesByKeys(), enabledLanguages, true, false, EMPTY_SETTINGS);
    if (COMMERCIAL_ENABLED) {
      assertThat(allJars).hasSize(18);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITH_COMMERCIAL + NON_COMMERCIAL_RULE_TEMPLATES_COUNT + COMMERCIAL_RULE_TEMPLATES_COUNT);
    } else {
      assertThat(allJars).hasSize(9);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITHOUT_COMMERCIAL + NON_COMMERCIAL_RULE_TEMPLATES_COUNT);
    }
  }

  @Test
  void extractAllRules_include_security_hotspots() {
    var enabledLanguages = Set.of(SonarLanguage.values());
    var config = new PluginsLoader.Configuration(allJars, enabledLanguages, false, NODE_VERSION);
    var result = new PluginsLoader().load(config, Set.of());

    var allRules = new RulesDefinitionExtractor().extractRules(result.getLoadedPlugins().getAllPluginInstancesByKeys(), enabledLanguages, false, true, EMPTY_SETTINGS);
    if (COMMERCIAL_ENABLED) {
      assertThat(allJars).hasSize(18);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITH_COMMERCIAL + NON_COMMERCIAL_SECURITY_HOTSPOTS_COUNT + COMMERCIAL_SECURITY_HOTSPOTS_COUNT);
    } else {
      assertThat(allJars).hasSize(9);
      assertThat(allRules).hasSize(ALL_RULES_COUNT_WITHOUT_COMMERCIAL + NON_COMMERCIAL_SECURITY_HOTSPOTS_COUNT);
    }
  }

  @Test
  void onlyLoadRulesOfEnabledLanguages() {
    Set<SonarLanguage> enabledLanguages = EnumSet.of(
      SonarLanguage.JAVA,
      //ACR-638f3837a4a945c183427da0bd93238a
      SonarLanguage.JS,
      SonarLanguage.PHP,
      SonarLanguage.PYTHON);

    if (COMMERCIAL_ENABLED) {
      //ACR-0af89967ec5f482dab43110faf46c781
      enabledLanguages.add(SonarLanguage.C);
    }
    var config = new PluginsLoader.Configuration(allJars, enabledLanguages, false, NODE_VERSION);
    var result = new PluginsLoader().load(config, Set.of());

    var allRules = new RulesDefinitionExtractor().extractRules(result.getLoadedPlugins().getAllPluginInstancesByKeys(), enabledLanguages, false, false, EMPTY_SETTINGS);

    assertThat(allRules.stream().map(SonarLintRuleDefinition::getLanguage).distinct()).hasSameElementsAs(enabledLanguages);
  }

  @Test
  void loadNoRuleIfThereIsNoPlugin() {
    var enabledLanguages = Set.of(SonarLanguage.values());
    var config = new PluginsLoader.Configuration(Set.of(), enabledLanguages, false, NODE_VERSION);
    var result = new PluginsLoader().load(config, Set.of());
    var allRules = new RulesDefinitionExtractor().extractRules(result.getLoadedPlugins().getAllPluginInstancesByKeys(), enabledLanguages, false, false, EMPTY_SETTINGS);

    assertThat(allRules).isEmpty();
  }

}
