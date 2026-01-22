/*
ACR-82b7d449c052482296183df8b4ebc595
ACR-73c754a794974fcd811d762be84c9df5
ACR-1c0bce6489e54c2398f0639a6d727f5e
ACR-f479a4a7e8c94881bbf0d561c81dfffd
ACR-c03e71be99fd4b08b485efd55e3820ec
ACR-dcd82cc002da4316b003289a5e58d0e0
ACR-0688937d693c45548b142b2cfc829ce1
ACR-a320c890434143e2b6bc0cd23f04d643
ACR-b8f3fa0ef6974d048175496a02c55e16
ACR-8d4f2656aeb84bf5b62febb39acd22ef
ACR-a2152f1b612d475b803b138e3809ec39
ACR-7bd7c1e2b1dc4479bc1484dcc8676960
ACR-ce2e6d10dc8747dab2572f93057ba844
ACR-0d3938f340d1476ca8b3a59dc7b60aaf
ACR-26a2f1b83df74bf2826da3bd71e2f364
ACR-ad377a9857794bc290b8eeda2ba39f3c
ACR-f1a163ed02fd45aea65a865c5a4dc4dc
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.MessageFormatting;
import org.sonar.api.batch.sensor.issue.fix.NewInputFileEdit;
import org.sonar.api.batch.sensor.issue.fix.NewQuickFix;
import org.sonar.api.batch.sensor.issue.fix.NewTextEdit;
import org.sonar.api.issue.impact.SoftwareQuality;
import org.sonar.api.rule.RuleKey;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputDir;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import testutils.TestInputFileBuilder;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultSonarLintIssueTests {

  private SonarLintInputProject project;

  private final InputFile inputFile = new TestInputFileBuilder("src/Foo.php")
    .initMetadata("Foo\nBar\n")
    .build();

  @TempDir
  private Path baseDir;

  @BeforeEach
  void prepare() {
    project = new SonarLintInputProject();
  }

  @Test
  void build_file_issue() {
    var storage = mock(SensorStorage.class);
    var range = inputFile.selectLine(1);
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(inputFile)
        .at(range)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .gap(10.0)
      .overrideImpact(SoftwareQuality.SECURITY, org.sonar.api.issue.impact.Severity.HIGH);

    assertThat(issue.primaryLocation().inputComponent()).isEqualTo(inputFile);
    assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("repo", "rule"));
    assertThat(issue.primaryLocation().textRange().start().line()).isEqualTo(1);
    assertThat(issue.primaryLocation().message()).isEqualTo("Wrong way!");
    assertThat(issue.overridenImpacts()).containsExactly(entry(SoftwareQuality.SECURITY, org.sonar.api.issue.impact.Severity.HIGH));

    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(issue::gap)
      .withMessage("No gap in SonarLint");

    var newQuickFix = issue.newQuickFix().message("Fix this issue");
    var newInputFileEdit = newQuickFix.newInputFileEdit().on(inputFile);
    newInputFileEdit.addTextEdit((NewTextEdit) newInputFileEdit.newTextEdit().at(range).withNewText("// Fixed!"));
    newQuickFix.addInputFileEdit((NewInputFileEdit) newInputFileEdit);
    issue.addQuickFix((NewQuickFix) newQuickFix);

    var quickFixes = issue.quickFixes();
    assertThat(quickFixes).hasSize(1);
    var quickFix = quickFixes.get(0);
    assertThat(quickFix.message()).isEqualTo("Fix this issue");
    var inputFileEdits = quickFix.inputFileEdits();
    assertThat(inputFileEdits).hasSize(1);
    var inputFileEdit = inputFileEdits.get(0);
    assertThat(inputFileEdit.target()).isEqualTo(inputFile);
    assertThat(inputFileEdit.textEdits()).hasSize(1);
    var textEdit = inputFileEdit.textEdits().get(0);
    assertThat(textEdit.range().start().line()).isEqualTo(range.start().line());
    assertThat(textEdit.range().start().lineOffset()).isEqualTo(range.start().lineOffset());
    assertThat(textEdit.range().end().line()).isEqualTo(range.end().line());
    assertThat(textEdit.range().end().lineOffset()).isEqualTo(range.end().lineOffset());
    assertThat(textEdit.newText()).isEqualTo("// Fixed!");

    issue.save();

    verify(storage).store(issue);
  }

  @Test
  void replace_null_characters() {
    var storage = mock(SensorStorage.class);
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(inputFile)
        .message("Wrong \u0000 use of NULL\u0000"))
      .forRule(RuleKey.of("repo", "rule"));

    assertThat(issue.primaryLocation().message()).isEqualTo("Wrong [NULL] use of NULL[NULL]");

    issue.save();

    verify(storage).store(issue);
  }

  @Test
  void truncate_and_trim() {
    var storage = mock(SensorStorage.class);
    var prefix = "prefix: ";
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(inputFile)
        .message("   " + prefix + repeat("a", 4000)))
      .forRule(RuleKey.of("repo", "rule"));

    var ellipse = "...";
    assertThat(issue.primaryLocation().message()).isEqualTo(prefix + repeat("a", 4000 - prefix.length() - ellipse.length()) + ellipse);

    issue.save();

    verify(storage).store(issue);
  }

  @Test
  void ignore_formatting_and_keep_unformatted_message() {
    var storage = mock(SensorStorage.class);
    var location = new DefaultSonarLintIssueLocation();
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(location
        .on(inputFile)
        .message("formattedMessage", List.of(location.newMessageFormatting()
          .start(1)
          .end(2)
          .type(MessageFormatting.Type.CODE))))
      .forRule(RuleKey.of("repo", "rule"));

    assertThat(issue.primaryLocation().message()).isEqualTo("formattedMessage");
    assertThat(issue.primaryLocation().messageFormattings()).isEmpty();
  }

  @Test
  void move_directory_issue_to_project_root() {
    var storage = mock(SensorStorage.class);
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(new SonarLintInputDir(baseDir.resolve("src/main")))
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .overrideSeverity(Severity.BLOCKER);

    assertThat(issue.primaryLocation().inputComponent()).isEqualTo(project);
    assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("repo", "rule"));
    assertThat(issue.primaryLocation().textRange()).isNull();
    assertThat(issue.primaryLocation().message()).isEqualTo("[src/main] Wrong way!");
    assertThat(issue.overriddenSeverity()).isEqualTo(Severity.BLOCKER);

    issue.save();

    verify(storage).store(issue);
  }

  @Test
  void build_project_issue() {
    var storage = mock(SensorStorage.class);
    var issue = new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(project)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .gap(10.0);

    assertThat(issue.primaryLocation().inputComponent()).isEqualTo(project);
    assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("repo", "rule"));
    assertThat(issue.primaryLocation().textRange()).isNull();
    assertThat(issue.primaryLocation().message()).isEqualTo("Wrong way!");

    issue.save();

    verify(storage).store(issue);
  }

  @Test
  void does_not_support_variants() {
    var storage = mock(SensorStorage.class);
    var issue = (DefaultSonarLintIssue) new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(project)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .setCodeVariants(List.of("variant1", "variant2"))
      .gap(10.0);

    assertThat(issue.codeVariants()).isEmpty();
  }

  @Test
  void supports_adding_internal_tags_one_by_one() {
    var storage = mock(SensorStorage.class);
    var issue = (DefaultSonarLintIssue) new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(project)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .addInternalTag("tag1")
      .addInternalTag("tag2");

    assertThat(issue.internalTags()).containsExactly("tag1", "tag2");
  }

  @Test
  void supports_adding_many_internal_tags() {
    var storage = mock(SensorStorage.class);
    var issue = (DefaultSonarLintIssue) new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(project)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .addInternalTags(List.of("tag1", "tag2"))
      .addInternalTags(List.of("tag3"));

    assertThat(issue.internalTags()).containsExactly("tag1", "tag2", "tag3");
  }

  @Test
  void supports_setting_many_internal_tags() {
    var storage = mock(SensorStorage.class);
    var issue = (DefaultSonarLintIssue) new DefaultSonarLintIssue(project, baseDir, storage)
      .at(new DefaultSonarLintIssueLocation()
        .on(project)
        .message("Wrong way!"))
      .forRule(RuleKey.of("repo", "rule"))
      .setInternalTags(List.of("tag1", "tag2"));

    assertThat(issue.internalTags()).containsExactly("tag1", "tag2");
  }

}
