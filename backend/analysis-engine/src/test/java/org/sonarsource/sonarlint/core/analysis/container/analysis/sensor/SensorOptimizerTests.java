/*
ACR-e3496182ae204add8a8b44b3c438e6ec
ACR-e6809765fbc5490e99d5b581418f25fc
ACR-108553c4bb294832858a4e2f7a63c239
ACR-1864341d400a4b818fe1b789a396b4dc
ACR-3895782fb39a4b35be79ba3be6be9252
ACR-db7b05ade5de4277838e9c19aef47f4c
ACR-b6bc803e3ceb40a1ab837c3fccb8e0de
ACR-d31a9e40803a4eeb8de584067dc888eb
ACR-185cbf1751564be49ca11041aabd61b8
ACR-d4689710e2084799a300916646c4220c
ACR-af55ed77069d447cb5a7e1d82b19bcde
ACR-ab82202c8e984a82861bd1f0906e0fe0
ACR-08607d0c4cff49a2812ef44e3eb6ee80
ACR-7271eee89aee470e8ea1a1b17ec1c386
ACR-4598af598c6d460fb7fb4934f0ab80c7
ACR-1b868b7a23014df99400faee65bd530b
ACR-13aa04cba94541fda7f70bce6f0559a8
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
