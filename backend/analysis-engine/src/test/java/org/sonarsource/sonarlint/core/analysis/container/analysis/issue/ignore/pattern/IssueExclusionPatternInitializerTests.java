/*
ACR-223ac6dc2619469dab6469bc08637499
ACR-e99863c05edf416483acb9a9c847df7a
ACR-59904ce2ed2a4bd78931ff3103a7702f
ACR-38f2082e63f84dc2b0769e65f8aae873
ACR-6112e3e31b5d471784385e28ba45ba06
ACR-7e27cfd8691743dc8598deefbeac9386
ACR-c9e8f1e593fb449db3799b012af87976
ACR-ea7c1f53fba549429981a72921164433
ACR-16fb4b55af5b46f3ac8c319d2efdddb9
ACR-c166ea21829b484f879cb6124e350861
ACR-95a8edeceb1c4e30a336204886fb8420
ACR-40390e3d62884f679589bbe2e6178f0e
ACR-17523046b36b4560a1bea194b4e5b4d9
ACR-1ce99e98762d453c9ef409bbcdc14ec1
ACR-3c03c13cbfc94766a339b3075f33820f
ACR-cbde37436d934d83a2e36c274fd0e937
ACR-7737ab67853f430ab4402f7fa3a5fccc
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
