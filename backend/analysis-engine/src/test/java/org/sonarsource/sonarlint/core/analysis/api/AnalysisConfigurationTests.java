/*
ACR-098b2aa0bc944cdeb9eae94fcf305c35
ACR-0a693fe1149947109b3c9c7db66d1bef
ACR-bef869eee26a4f87991b673cc7702535
ACR-429e3a38d0754d38991726fa4c49ef70
ACR-abf60fba70074c298d122ba12d58a591
ACR-c67653cb28844b04a4ea355503e675f8
ACR-ce733f66f21b4a4ea7f836af36ec49a5
ACR-251efd9ca1794a5cba0a08034062b1cc
ACR-c8708c951a3246c185cd1e0b7dae5069
ACR-cffc4562d7b8453a93c95d637f21d26b
ACR-20253453333549afb9f681b7197c71b3
ACR-d15bf35ca7cc48e389d2d305ef6e8b32
ACR-98c60bb1157843819d280fa2c75106ac
ACR-ed6c37a0f6944e278fcf322ea36e5271
ACR-98cc7bfe7499408684bd9dcaba0183c1
ACR-83f3c50b0262444c9236296aa14a2a29
ACR-de47505fa07b497ab4d267be507ed475
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import testutils.TestClientInputFile;

import static java.nio.file.Files.createDirectory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class AnalysisConfigurationTests {

  @AfterEach
  void init_property() {
    System.clearProperty("sonarlint.debug.active.rules");
  }

  @Test
  void testToString_and_getters(@TempDir Path temp) throws Exception {
    Map<String, String> props = new HashMap<>();
    props.put("sonar.java.libraries", "foo bar");

    final var srcFile1 = createDirectory(temp.resolve("src1"));
    final var srcFile2 = createDirectory(temp.resolve("src2"));
    final var srcFile3 = createDirectory(temp.resolve("src3"));
    ClientInputFile inputFile = new TestClientInputFile(temp, srcFile1, false, StandardCharsets.UTF_8, null);
    ClientInputFile inputFileWithLanguage = new TestClientInputFile(temp, srcFile2, false, StandardCharsets.UTF_8, SonarLanguage.JAVA);
    ClientInputFile testInputFile = new TestClientInputFile(temp, srcFile3, true, null, SonarLanguage.PHP);
    var baseDir = createDirectory(temp.resolve("baseDir"));
    var activeRuleWithParams = newActiveRule("php:S123", Map.of("param1", "value1"));
    var config = AnalysisConfiguration.builder()
      .setBaseDir(baseDir)
      .addInputFile(inputFile)
      .addInputFiles(inputFileWithLanguage)
      .addInputFiles(List.of(testInputFile))
      .putAllExtraProperties(props)
      .putExtraProperty("sonar.foo", "bar")
      .addActiveRules(List.of(newActiveRule("java:S123"), newActiveRule("java:S456")))
      .addActiveRule(activeRuleWithParams)
      .addActiveRules(newActiveRule("python:S123"), newActiveRule("python:S456"))
      .build();
    assertThat(config).hasToString("[\n" +
      "  baseDir: " + baseDir + "\n" +
      "  extraProperties: {sonar.java.libraries=foo bar, sonar.foo=bar}\n" +
      "  activeRules: [2 python, 2 java, 1 php]\n" +
      "  inputFiles: [\n" +
      "    " + srcFile1.toUri() + " (UTF-8)\n" +
      "    " + srcFile2.toUri() + " (UTF-8) [java]\n" +
      "    " + srcFile3.toUri() + " (default) [test] [php]\n" +
      "  ]\n" +
      "]\n");
    assertThat(config.baseDir()).isEqualTo(baseDir);
    assertThat(config.inputFiles()).containsExactly(inputFile, inputFileWithLanguage, testInputFile);
    assertThat(config.extraProperties()).containsExactly(entry("sonar.java.libraries", "foo bar"), entry("sonar.foo", "bar"));
    assertThat(config.activeRules()).extracting(ActiveRule::ruleKey).map(RuleKey::toString).containsExactly("java:S123", "java:S456", "php:S123", "python:S123", "python:S456");
  }

  @Test
  void testToString_and_getters_when_empty() {
    var config = AnalysisConfiguration.builder().build();
    assertThat(config).hasToString("""
      [
        baseDir: null
        extraProperties: {}
        activeRules: []
        inputFiles: [
        ]
      ]
      """);
    assertThat(config.baseDir()).isNull();
    assertThat(config.inputFiles()).isEmpty();
    assertThat(config.activeRules()).isEmpty();
  }

  @Test
  void testToString_and_getters_when_active_rules_verbose() {
    System.setProperty("sonarlint.debug.active.rules", "true");

    var activeRuleWithParams = newActiveRule("php:S123", Map.of("param1", "value1"));
    var config = AnalysisConfiguration.builder()
      .addActiveRules(List.of(newActiveRule("java:S123"), newActiveRule("java:S456")))
      .addActiveRules(activeRuleWithParams)
      .addActiveRules(newActiveRule("python:S123"), newActiveRule("python:S456"))
      .build();
    assertThat(config).hasToString("""
      [
        baseDir: null
        extraProperties: {}
        activeRules: [java:S123, java:S456, php:S123{param1=value1}, python:S123, python:S456]
        inputFiles: [
        ]
      ]
      """);
    assertThat(config.baseDir()).isNull();
    assertThat(config.inputFiles()).isEmpty();
    assertThat(config.activeRules()).extracting(ActiveRule::ruleKey).map(RuleKey::toString).containsExactly("java:S123", "java:S456", "php:S123", "python:S123", "python:S456");
  }

  @Test
  void testToString_and_getters_when_active_rules_not_verbose() {
    var activeRuleWithParams = newActiveRule("php:S123", Map.of("param1", "value1"));
    var config = AnalysisConfiguration.builder()
      .addActiveRules(List.of(newActiveRule("java:S123"), newActiveRule("java:S456")))
      .addActiveRules(activeRuleWithParams)
      .addActiveRules(newActiveRule("python:S123"), newActiveRule("python:S456"))
      .build();
    assertThat(config).hasToString("""
      [
        baseDir: null
        extraProperties: {}
        activeRules: [2 python, 2 java, 1 php]
        inputFiles: [
        ]
      ]
      """);
    assertThat(config.baseDir()).isNull();
    assertThat(config.inputFiles()).isEmpty();
    assertThat(config.activeRules()).extracting(ActiveRule::ruleKey).map(RuleKey::toString).containsExactly("java:S123", "java:S456", "php:S123", "python:S123", "python:S456");
  }

  private static ActiveRule newActiveRule(String ruleKey) {
    return newActiveRule(ruleKey, Map.of());
  }

  private static ActiveRule newActiveRule(String ruleKey, Map<String, String> params) {
    return new ActiveRule() {

      @Override
      public RuleKey ruleKey() {
        return RuleKey.parse(ruleKey);
      }

      @Override
      public String severity() {
        return "";
      }

      @Override
      public String language() {
        return "";
      }

      @CheckForNull
      @Override
      public String param(String key) {
        return params().get(key);
      }

      @Override
      public Map<String, String> params() {
        return params;
      }

      @CheckForNull
      @Override
      public String internalKey() {
        return "";
      }

      @CheckForNull
      @Override
      public String templateRuleKey() {
        return "";
      }

      @Override
      public String qpKey() {
        return "";
      }

      @Override
      public String toString() {
        var sb = new StringBuilder();
        sb.append(ruleKey);
        if (!params.isEmpty()) {
          sb.append(params);
        }
        return sb.toString();
      }
    };
  }

}
