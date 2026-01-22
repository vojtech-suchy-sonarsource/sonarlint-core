/*
ACR-7507efe22d5349368c568900ba90baac
ACR-2ecac997fbfb4d99ad56f4b8d63977ad
ACR-097e257c03484b18b2c6a0390e0a9792
ACR-1e5b903239b84c32ad681c9d814b053f
ACR-011ff245252543188c9e7511d14d8e61
ACR-5f45810fa3c843449b74c96c2f8fbc84
ACR-14aadc66a3434f038d9296480a889199
ACR-9d0629fd51b747c7a26fa30b5236347e
ACR-00eefae1972348069e8f07b938a0233a
ACR-c770915451944320a9f013f748334f2a
ACR-0d12f79a6db04d4f9efec3a5baf063c7
ACR-56009a8797f34fbfb3a7455481caace1
ACR-374056cb887542b18e61d315db0f24f1
ACR-7ca38388f92f48e0a970f4865da38d92
ACR-eaac85abc1c548669e36f7c23e8a1336
ACR-34c6578eb48a45cbb72cf3f59da54033
ACR-86a9fef3a02c42ee9046a494fdd268e9
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
  //ACR-c66234c93fc34cfc81feda1ebdf2c46c
  //ACR-1de5d1d801474eb39d33b864920648c1
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
      //ACR-9fc309058a3d443faaac17cf2952b151
      SonarLanguage.JS,
      SonarLanguage.PHP,
      SonarLanguage.PYTHON);

    if (COMMERCIAL_ENABLED) {
      //ACR-3cf4211b85e24f489ef26f859941d73b
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
