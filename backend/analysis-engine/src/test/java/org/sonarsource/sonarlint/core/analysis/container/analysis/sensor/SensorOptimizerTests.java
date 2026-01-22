/*
ACR-4a4d42584c3349cbb67a3f3a7dfd1d02
ACR-fb8a673ed7e14033b7b830ca66a966c0
ACR-efb2481dfa4f4c0f99d3ad62293686e2
ACR-703e2e79dc764942b43ad683ce45486e
ACR-67b02a0892ef4e91a16a76e0c180c246
ACR-9a8bd6a7b7834f9facd0f9d1c5b5c8fb
ACR-c705499d3a394e7aaf6385433a0f5c81
ACR-5f3fb7bea2fe40299ccfc7c3e5aae76b
ACR-b63e80cb94d24f0184808cbf561afd9d
ACR-cee408cc167d466699143178a3ff6758
ACR-199732274073442b881be3c36b2ced7a
ACR-8e6970f49d2547edbaf723b80381741a
ACR-2c56e3667f7e47d384257286388469f5
ACR-147eb26589c14840938a4a0debb2d395
ACR-fcd1b31977a2446f8f93965bd19d05de
ACR-22c7ba77c112492f92b255843e8a6eff
ACR-373b6bad671b4a609527b4e424dad551
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.InputFileIndex;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintFileSystem;
import org.sonarsource.sonarlint.core.analysis.sonarapi.ActiveRulesAdapter;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorDescriptor;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;
import testutils.TestInputFileBuilder;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorOptimizerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private FileSystem fs;
  private SensorOptimizer optimizer;
  private MapSettings settings;
  private final InputFileIndex inputFileCache = new InputFileIndex();

  @BeforeEach
  void prepare() {
    fs = new SonarLintFileSystem(mock(AnalysisConfiguration.class), inputFileCache);
    settings = new MapSettings(Map.of());
    optimizer = new SensorOptimizer(fs, mock(ActiveRules.class), settings.asConfig());
  }

  @Test
  void should_run_analyzer_with_no_metadata() {
    var descriptor = new DefaultSensorDescriptor();

    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

  @Test
  void should_optimize_on_language() {
    var descriptor = new DefaultSensorDescriptor()
      .onlyOnLanguages("java", "php");
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    inputFileCache.doAdd(new TestInputFileBuilder("src/Foo.java").setLanguage(SonarLanguage.JAVA).build());
    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

  @Test
  void should_optimize_on_type() {
    var descriptor = new DefaultSensorDescriptor()
      .onlyOnFileType(InputFile.Type.MAIN);
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    inputFileCache.doAdd(new TestInputFileBuilder("tests/FooTest.java").setType(InputFile.Type.TEST).build());
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    inputFileCache.doAdd(new TestInputFileBuilder("src/Foo.java").setType(InputFile.Type.MAIN).build());
    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

  @Test
  void should_optimize_on_both_type_and_language() {
    var descriptor = new DefaultSensorDescriptor()
      .onlyOnLanguages("java", "php")
      .onlyOnFileType(InputFile.Type.MAIN);
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    inputFileCache.doAdd(new TestInputFileBuilder("tests/FooTest.java").setLanguage(SonarLanguage.JAVA).setType(InputFile.Type.TEST).build());
    inputFileCache.doAdd(new TestInputFileBuilder("src/Foo.cbl").setLanguage(SonarLanguage.COBOL).setType(InputFile.Type.MAIN).build());
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    inputFileCache.doAdd(new TestInputFileBuilder("src/Foo.java").setLanguage(SonarLanguage.JAVA).setType(InputFile.Type.MAIN).build());
    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

  @Test
  void should_optimize_on_repository() {
    var descriptor = new DefaultSensorDescriptor()
      .createIssuesForRuleRepositories("squid");
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    var ruleAnotherRepo = mock(ActiveRule.class);
    when(ruleAnotherRepo.ruleKey()).thenReturn(RuleKey.of("repo1", "foo"));
    ActiveRules activeRules = new ActiveRulesAdapter(List.of(ruleAnotherRepo));
    optimizer = new SensorOptimizer(fs, activeRules, settings.asConfig());

    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    var ruleSquid = mock(ActiveRule.class);
    when(ruleSquid.ruleKey()).thenReturn(RuleKey.of("squid", "rule"));

    activeRules = new ActiveRulesAdapter(asList(ruleSquid, ruleAnotherRepo));

    optimizer = new SensorOptimizer(fs, activeRules, settings.asConfig());

    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

  @Test
  void should_optimize_on_settings() {
    var descriptor = new DefaultSensorDescriptor().onlyWhenConfiguration(c -> c.hasKey("sonar.foo.reportPath"));
    assertThat(optimizer.shouldExecute(descriptor)).isFalse();

    settings = new MapSettings(Map.of("sonar.foo.reportPath", "foo"));
    optimizer = new SensorOptimizer(fs, mock(ActiveRules.class), settings.asConfig());
    assertThat(optimizer.shouldExecute(descriptor)).isTrue();
  }

}
