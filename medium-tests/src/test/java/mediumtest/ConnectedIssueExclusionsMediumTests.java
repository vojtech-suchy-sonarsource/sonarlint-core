/*
ACR-8d0f5baa17bd4cd3bc5e41840347bb1e
ACR-14fc3037dca94e76b7756b18855fcf4a
ACR-a9200905285a48ea87845c4403f52db0
ACR-2d4edf0179c148c2b6709585e429ced8
ACR-250ff144f55247c9b2a93e93c7736064
ACR-8e166ab5644740ec9e911f5252f6bbee
ACR-1a7d6f83c3044411bcdf8be1fdf62153
ACR-16b4365c09744c40b5f272098a4f1d05
ACR-0d21bee8c5414654a5b6dd952f6f7537
ACR-d98b921b317d4599b6852a76b9195136
ACR-e4a7abf88a70434397e03b3db0acd920
ACR-6238a9fe933141f7952764d1f2481b7a
ACR-76788f8ef1154a8da19baa974874f021
ACR-8c2a368508ce4bb19e7e2875f91cee7e
ACR-b1f7a88f81784e3385b407748a59f552
ACR-5f8eeb0a96854b0ca000b7263532b62f
ACR-ed1f0f24ab2e48f4afef6fb7f408d553
 */
package mediumtest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;
import static utils.AnalysisUtils.analyzeFilesAndGetIssuesAsMap;
import static utils.AnalysisUtils.analyzeFilesAndVerifyNoIssues;
import static utils.AnalysisUtils.createFile;

class ConnectedIssueExclusionsMediumTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  @RegisterExtension
  private static final SonarLintTestHarness harness = new SonarLintTestHarness();

  private static final String FILE1_PATH = "Foo.java";
  private static final String FILE2_PATH = "Foo2.java";
  private static Path inputFile1;
  private static Path inputFile2;
  private static final String CONNECTION_ID = "local";
  private static final String JAVA_MODULE_KEY = "test-project-2";
  private static SonarLintTestRpcServer backend;
  private static SonarLintBackendFixture.FakeSonarLintRpcClient client;

  @BeforeAll
  static void prepare(@TempDir Path baseDir) {
    inputFile1 = prepareJavaInputFile1(baseDir, FILE1_PATH);
    inputFile2 = prepareJavaInputFile2(baseDir, FILE2_PATH);

    client = harness.newFakeClient()
      .withInitialFs(JAVA_MODULE_KEY, List.of(
        new ClientFileDto(inputFile1.toUri(), baseDir.relativize(inputFile1), JAVA_MODULE_KEY, false, null, inputFile1, null, null, true),
        new ClientFileDto(inputFile2.toUri(), baseDir.relativize(inputFile2), JAVA_MODULE_KEY, false, null, inputFile2, null, null, true)
      ))
      .build();
    var server = harness.newFakeSonarQubeServer().start();
    backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server, storage -> storage
        .withPlugin(TestPlugin.JAVA)
        .withProject("test-project")
        .withProject(JAVA_MODULE_KEY, project -> project
          .withMainBranch("main")
          .withRuleSet("java", ruleSet -> ruleSet
            .withActiveRule("java:S106", "MAJOR")
            .withActiveRule("java:S1220", "MINOR")
            .withActiveRule("java:S1481", "BLOCKER"))))
      .withBoundConfigScope(JAVA_MODULE_KEY, CONNECTION_ID, JAVA_MODULE_KEY)
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .start(client);
  }

  @BeforeEach
  void restoreConfig() {
    updateIssueExclusionsSettings(Map.of());
  }

  @Test
  void issueExclusions() {
    var issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    var issuesFile1 = issues.get(inputFile1.toUri());
    var issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    client.cleanRaisedIssues();

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.multicriteria", "1",
      "sonar.issue.ignore.multicriteria.1.resourceKey", "*",
      "sonar.issue.ignore.multicriteria.1.ruleKey", "*"));
    analyzeFilesAndVerifyNoIssues(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.multicriteria", "1",
      "sonar.issue.ignore.multicriteria.1.resourceKey", "*",
      "sonar.issue.ignore.multicriteria.1.ruleKey", "*S1481"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null));
    client.cleanRaisedIssues();

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.multicriteria", "1",
      "sonar.issue.ignore.multicriteria.1.resourceKey", FILE2_PATH,
      "sonar.issue.ignore.multicriteria.1.ruleKey", "*"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).isNullOrEmpty();
    client.cleanRaisedIssues();

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.multicriteria", "1,2",
      "sonar.issue.ignore.multicriteria.1.resourceKey", FILE2_PATH,
      "sonar.issue.ignore.multicriteria.1.ruleKey", "java:S1481",
      "sonar.issue.ignore.multicriteria.2.resourceKey", FILE1_PATH,
      "sonar.issue.ignore.multicriteria.2.ruleKey", "java:S106"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null));
  }

  @Test
  void issueExclusionsByRegexp() {
    var issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    var issuesFile1 = issues.get(inputFile1.toUri());
    var issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    client.cleanRaisedIssues();

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.allfile", "1",
      "sonar.issue.ignore.allfile.1.fileRegexp", "NOSL1"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile1).isNullOrEmpty();
    client.cleanRaisedIssues();

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.allfile", "1",
      "sonar.issue.ignore.allfile.1.fileRegexp", "NOSL(1|2)"));
    analyzeFilesAndVerifyNoIssues(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
  }

  @Test
  void issueExclusionsByBlock() {
    var issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    var issuesFile1 = issues.get(inputFile1.toUri());
    var issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));

    updateIssueExclusionsSettings(Map.of("sonar.issue.ignore.block", "1",
      "sonar.issue.ignore.block.1.beginBlockRegexp", "SON.*-OFF",
      "sonar.issue.ignore.block.1.endBlockRegexp", "SON.*-ON"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
  }

  @Test
  void issueInclusions() {
    updateIssueExclusionsSettings(Map.of("sonar.issue.enforce.multicriteria", "1",
      "sonar.issue.enforce.multicriteria.1.resourceKey", "Foo*.java",
      "sonar.issue.enforce.multicriteria.1.ruleKey", "*"));
    var issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    var issuesFile1 = issues.get(inputFile1.toUri());
    var issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));

    updateIssueExclusionsSettings(Map.of("sonar.issue.enforce.multicriteria", "1",
      "sonar.issue.enforce.multicriteria.1.resourceKey", FILE2_PATH,
      "sonar.issue.enforce.multicriteria.1.ruleKey", "*S1481"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));

    updateIssueExclusionsSettings(Map.of("sonar.issue.enforce.multicriteria", "1",
      "sonar.issue.enforce.multicriteria.1.resourceKey", FILE2_PATH,
      "sonar.issue.enforce.multicriteria.1.ruleKey", "*"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).isEmpty();
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));

    updateIssueExclusionsSettings(Map.of("sonar.issue.enforce.multicriteria", "1,2",
      "sonar.issue.enforce.multicriteria.1.resourceKey", FILE2_PATH,
      "sonar.issue.enforce.multicriteria.1.ruleKey", "java:S1481",
      "sonar.issue.enforce.multicriteria.2.resourceKey", FILE1_PATH,
      "sonar.issue.enforce.multicriteria.2.ruleKey", "java:S106"));
    issues = analyzeFilesAndGetIssuesAsMap(List.of(inputFile1.toUri(), inputFile2.toUri()), client, backend, JAVA_MODULE_KEY);
    issuesFile1 = issues.get(inputFile1.toUri());
    issuesFile2 = issues.get(inputFile2.toUri());
    assertThat(issuesFile1).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14)),
        tuple("java:S1220", null));
    assertThat(issuesFile2).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)));
  }

  private void updateIssueExclusionsSettings(Map<String, String> settings) {
    var analyzerConfigPath = backend.getStorageRoot().resolve(encodeForFs(CONNECTION_ID)).resolve("projects").resolve(encodeForFs(JAVA_MODULE_KEY)).resolve("analyzer_config.pb");
    Sonarlint.AnalyzerConfiguration.Builder analyzerConfigurationBuilder;
    if (Files.exists(analyzerConfigPath)) {
      analyzerConfigurationBuilder = Sonarlint.AnalyzerConfiguration.newBuilder(ProtobufFileUtil.readFile(analyzerConfigPath, Sonarlint.AnalyzerConfiguration.parser()));
      analyzerConfigurationBuilder.clearSettings();
    } else {
      analyzerConfigurationBuilder = Sonarlint.AnalyzerConfiguration.newBuilder();
    }
    analyzerConfigurationBuilder.putAllSettings(settings);
    ProtobufFileUtil.writeToFile(analyzerConfigurationBuilder.build(), analyzerConfigPath);
  }

  private static Path prepareJavaInputFile1(Path baseDir, String filePath) {
    return createFile(baseDir, filePath, """
      /*NOSL1*/ public class Foo {
        public void foo() {
          int x;
          // SONAR-OFF
          System.out.println("Foo");
          // SONAR-ON
        }
      }""");
  }

  private static Path prepareJavaInputFile2(Path baseDir, String filePath) {
    return createFile(baseDir, filePath, """
      /*NOSL2*/ public class Foo2 {
        public void foo() {
          int x;
          System.out.println("Foo");
        }
      }""");
  }

}
