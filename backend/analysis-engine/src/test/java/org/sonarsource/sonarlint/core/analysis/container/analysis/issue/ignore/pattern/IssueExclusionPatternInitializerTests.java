/*
ACR-027f204ad6f445d793d902e4688b055a
ACR-99cce026793c496db633d20df0fee377
ACR-a7d6971784644eceb7579cb93accfeea
ACR-924d20f912ad4e9d99c2792987c78641
ACR-335bf311a6ff40dc88826681d26658c5
ACR-ea1a7f478cd14e5ebf822b13c65980c7
ACR-33b776266c30487a80522bedeedbfee6
ACR-d3cb1164c74e4ffbb6fc86bf13657a02
ACR-be4f281f0f844bf9ab7d820950ecce85
ACR-24dc59fbf338407b99b3ae8e576a01f2
ACR-0327965940b04e80a40ec812649ae7b9
ACR-7bd3c4ec71964cf7a57a5b58ab845f0d
ACR-beb388c2038440ec9952cf71a89f515c
ACR-fe382ccf7e0f4413bebe9e265ba638c6
ACR-c037dee1ad9c44e0a6d1dc18ee4df4a5
ACR-228b8ee4faa4458d9d3b42872e6fcfb3
ACR-b7257f2178ed4a3c970c62ad86dec7e2
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class IssueExclusionPatternInitializerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void testNoConfiguration() {
    var patternsInitializer = new IssueExclusionPatternInitializer(new MapSettings(Map.of()).asConfig());
    assertThat(patternsInitializer.hasConfiguredPatterns()).isFalse();
    assertThat(patternsInitializer.getMulticriteriaPatterns()).isEmpty();
  }

  @Test
  void shouldLogInvalidResourceKey() {
    Map<String, String> settings = new HashMap<>();
    settings.put("sonar.issue.ignore" + ".multicriteria", "1");
    settings.put("sonar.issue.ignore" + ".multicriteria" + ".1." + "resourceKey", "");
    settings.put("sonar.issue.ignore" + ".multicriteria" + ".1." + "ruleKey", "*");
    new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(logTester.logs()).containsExactly("Issue exclusions are misconfigured. File pattern is mandatory for each entry of 'sonar.issue.ignore.multicriteria'");
  }

  @Test
  void shouldLogInvalidRuleKey() {
    Map<String, String> settings = new HashMap<>();
    settings.put("sonar.issue.ignore" + ".multicriteria", "1");
    settings.put("sonar.issue.ignore" + ".multicriteria" + ".1." + "resourceKey", "*");
    settings.put("sonar.issue.ignore" + ".multicriteria" + ".1." + "ruleKey", "");
    new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(logTester.logs()).containsExactly("Issue exclusions are misconfigured. Rule key pattern is mandatory for each entry of 'sonar.issue.ignore.multicriteria'");
  }

  @Test
  void shouldReturnBlockPattern() {
    Map<String, String> settings = new HashMap<>();
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY, "1,2,3");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".1." + IssueExclusionPatternInitializer.BEGIN_BLOCK_REGEXP, "// SONAR-OFF");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".1." + IssueExclusionPatternInitializer.END_BLOCK_REGEXP, "// SONAR-ON");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".2." + IssueExclusionPatternInitializer.BEGIN_BLOCK_REGEXP, "// FOO-OFF");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".2." + IssueExclusionPatternInitializer.END_BLOCK_REGEXP, "// FOO-ON");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".3." + IssueExclusionPatternInitializer.BEGIN_BLOCK_REGEXP, "// IGNORE-TO-EOF");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".3." + IssueExclusionPatternInitializer.END_BLOCK_REGEXP, "");
    var patternsInitializer = new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(patternsInitializer.hasConfiguredPatterns()).isTrue();
    assertThat(patternsInitializer.hasFileContentPattern()).isTrue();
    assertThat(patternsInitializer.hasMulticriteriaPatterns()).isFalse();
    assertThat(patternsInitializer.getMulticriteriaPatterns()).isEmpty();
    assertThat(patternsInitializer.getBlockPatterns().size()).isEqualTo(3);
    assertThat(patternsInitializer.getAllFilePatterns()).isEmpty();
  }

  @Test
  void shouldLogInvalidStartBlockPattern() {
    Map<String, String> settings = new HashMap<>();
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY, "1");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".1." + IssueExclusionPatternInitializer.BEGIN_BLOCK_REGEXP, "");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_BLOCK_KEY + ".1." + IssueExclusionPatternInitializer.END_BLOCK_REGEXP, "// SONAR-ON");
    new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(logTester.logs()).containsExactly("Issue exclusions are misconfigured. Start block regexp is mandatory for each entry of 'sonar.issue.ignore.block'");
  }

  @Test
  void shouldReturnAllFilePattern() {
    Map<String, String> settings = new HashMap<>();
    settings.put(IssueExclusionPatternInitializer.PATTERNS_ALLFILE_KEY, "1,2");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_ALLFILE_KEY + ".1." + IssueExclusionPatternInitializer.FILE_REGEXP, "@SONAR-IGNORE-ALL");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_ALLFILE_KEY + ".2." + IssueExclusionPatternInitializer.FILE_REGEXP, "//FOO-IGNORE-ALL");
    var patternsInitializer = new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(patternsInitializer.hasConfiguredPatterns()).isTrue();
    assertThat(patternsInitializer.hasFileContentPattern()).isTrue();
    assertThat(patternsInitializer.hasMulticriteriaPatterns()).isFalse();
    assertThat(patternsInitializer.getMulticriteriaPatterns()).isEmpty();
    assertThat(patternsInitializer.getBlockPatterns()).isEmpty();
    assertThat(patternsInitializer.getAllFilePatterns().size()).isEqualTo(2);
  }

  @Test
  void shouldLogInvalidAllFilePattern() {
    Map<String, String> settings = new HashMap<>();
    settings.put(IssueExclusionPatternInitializer.PATTERNS_ALLFILE_KEY, "1");
    settings.put(IssueExclusionPatternInitializer.PATTERNS_ALLFILE_KEY + ".1." + IssueExclusionPatternInitializer.FILE_REGEXP, "");
    new IssueExclusionPatternInitializer(new MapSettings(settings).asConfig());

    assertThat(logTester.logs()).containsExactly("Issue exclusions are misconfigured. Remove blank entries from 'sonar.issue.ignore.allfile'");
  }
}
