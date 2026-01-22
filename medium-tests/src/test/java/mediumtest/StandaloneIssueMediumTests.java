/*
ACR-cf49e953d0ce427b90a300daaf4bd8ae
ACR-801ceb523ad64c35b9b36e0a0624248b
ACR-3096dfed2ba74b049b7ebff4e500cddf
ACR-55ce8e15c5a749c980fe97d7fb9d1c42
ACR-3d820c58e7034a4fa3bc61913a67349c
ACR-01a395858eba4e8b9e6584cd076277a0
ACR-334faec025d942b882176901e874ee0a
ACR-a12329e84d614122a14c3ce715b6eaeb
ACR-46abf6569e2049d39b03d67d3f88675f
ACR-3a70616adaed4381b8359431e6302c01
ACR-8841129ee7084940ad03e43765687704
ACR-c74f556141fe44f5944fd2d080f4cc1a
ACR-f3bf37c1d72f4b8897ae0ae7416f7fad
ACR-571c844e08ef4537bb3b29b3d431a95d
ACR-dad3b900b0574d2d93ed078698d8689e
ACR-f3bd95ced50c4998b170130fe5963c84
ACR-0e32371a530048d6959b8b1d09c254c6
 */
package mediumtest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.GetEffectiveIssueDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.UpdateStandaloneRulesConfigurationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.FileEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.QuickFixDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.TextEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.C;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.KOTLIN;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.PHP;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.PYTHON;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.TS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.XML;
import static utils.AnalysisUtils.analyzeFileAndGetIssues;
import static utils.AnalysisUtils.analyzeFilesAndVerifyNoIssues;
import static utils.AnalysisUtils.createFile;

class StandaloneIssueMediumTests {
  private static final String A_JAVA_FILE_PATH = "Foo.java";
  private static final String CONFIGURATION_SCOPE_ID = "configScopeId";
  //ACR-5876e7a038054d7d8ad99f59fdb838a9
  //ACR-4034316c302e4c07b8f2b2f84f058af3
  private static final boolean COMMERCIAL_ENABLED = System.getProperty("commercial") != null;

  @SonarLintTest
  void simpleJavaScript(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var content = """
      function foo() {
        let x;
        let y; //NOSONAR
      }""";
    var inputFile = createFile(baseDir, "foo.js", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine(), RaisedIssueDto::getRuleDescriptionContextKey, StandaloneIssueMediumTests::extractMqrDetails)
      .containsOnly(tuple("javascript:S1481", 2, null, tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
    client.cleanRaisedIssues();

    //ACR-d70e4eee7fb44b97bdae36e53838b485
    var nodeModulesDir = Files.createDirectory(baseDir.resolve("node_modules"));

    inputFile = createFile(nodeModulesDir, "foo.js", content);

    analyzeFilesAndVerifyNoIssues(List.of(inputFile.toUri()), client, backend, CONFIGURATION_SCOPE_ID);
  }

  //ACR-725dd83876894b56b41c3931286a9baa
  //ACR-27ab52cfb29b447595e1ed67dc411f86
  @SonarLintTest
  void sonarjs_should_honor_global_and_analysis_level_properties(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var content = """
      function foo() {
        console.log(LOCAL1); // Noncompliant
        console.log(GLOBAL1); // GLOBAL1 defined as global variable in global settings
      }""";
    var inputFile = createFile(baseDir, "foo.js", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)

      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("javascript:S3827", new StandaloneRuleConfigDto(true, emptyMap()))));

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of("sonar.javascript.globals", "LOCAL1"), true,
        System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isNotEmpty());
    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID);
    assertThat(issues).hasSize(1);
    var issue = issues.get(0);
    assertThat(issue.getTextRange().getStartLine()).isEqualTo(3);
  }

  @SonarLintTest
  void simpleTypeScript(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    final var tsConfig = new File(baseDir.toFile(), "tsconfig.json");
    FileUtils.write(tsConfig, "{}", StandardCharsets.UTF_8);
    var tsConfigPath = tsConfig.toPath();
    var content = """
      function foo() {
        if(bar() && bar()) { return 42; }
      }""";
    var inputFile = createFile(baseDir, "foo.ts", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true),
        new ClientFileDto(tsConfigPath.toUri(), baseDir.relativize(tsConfigPath), CONFIGURATION_SCOPE_ID, false, null, tsConfigPath, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).containsOnly(
      tuple("typescript:S1764", 2));
  }

  @Disabled("https://sonarsource.atlassian.net/browse/SLCORE-873 - plug test YAML plugin")
  @SonarLintTest
  void simpleJavaScriptInYamlFile(SonarLintTestHarness harness, @TempDir Path baseDir) {
    String content = """
      Resources:
        LambdaFunction:
          Type: 'AWS::Lambda::Function'
          Properties:
            Code:
              ZipFile: >
                exports.handler = function(event, context) {
                  let x;
                };
            Runtime: nodejs8.10""";

    var inputFile = createFile(baseDir, "foo.yaml", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);
    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).containsOnly(
      tuple("javascript:S1481", 8));
  }

  @SonarLintTest
  void simpleC(SonarLintTestHarness harness, @TempDir Path baseDir) {
    assumeTrue(COMMERCIAL_ENABLED);
    var inputFile = createFile(baseDir, "foo.c", """
      #import "foo.h"
      #import "foo2.h" //NOSONAR
      """);
    var buildWrapperContent = "{\"version\":0,\"captures\":[" +
      "{" +
      "\"compiler\": \"clang\"," +
      "\"executable\": \"compiler\"," +
      "\"stdout\": \"#define __STDC_VERSION__ 201112L\n\"," +
      "\"stderr\": \"\"" +
      "}," +
      "{" +
      "\"compiler\": \"clang\"," +
      "\"executable\": \"compiler\"," +
      "\"stdout\": \"#define __cplusplus 201703L\n\"," +
      "\"stderr\": \"\"" +
      "}," +
      "{\"compiler\":\"clang\",\"cwd\":\"" +
      baseDir.toString().replace("\\", "\\\\") +
      "\",\"executable\":\"compiler\",\"cmd\":[\"cc\",\"foo.c\"]}]}";
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.CFAMILY)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of("sonar.cfamily.build-wrapper-content", buildWrapperContent), true,
        System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isNotEmpty());
    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID);
    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine(), i -> i.getTextRange().getStartLineOffset())
      .containsOnly(
        tuple("c:S3805", 1, 0));
  }

  @SonarLintTest
  void simplePhp(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "foo.php", """
      <?php
      function writeMsg($fname) {
          $i = 0; // NOSONAR
          echo "Hello world!";
      }
      ?>
      """);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine())
      .containsOnly(
        tuple("php:S1172", 2),
        tuple("php:S1780", 6));
  }

  @SonarLintTest
  void fileEncoding(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var content = """
      <?php
      function writeMsg($fname) {
          $i = 0; // NOSONAR
          echo "Hello world!";
      }
      ?>
      """;
    var inputFile = baseDir.resolve("foo.php");
    try {
      Files.writeString(inputFile, content, StandardCharsets.UTF_16);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, StandardCharsets.UTF_16.name(), inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).contains(tuple("php:S1172", 2));
  }

  @SonarLintTest
  void analysisErrors(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var content = """
      <?php
      function writeMsg($fname) {
          echo "Hello world!;
      }
      ?>""";
    var inputFile = createFile(baseDir, "foo.php", content);

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).containsExactly(inputFile.toUri());
    await().during(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isEmpty());
  }

  @SonarLintTest
  void simplePython(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "foo.py", """
      def my_function(name):
          print "Hello"
          print "world!" # NOSONAR
      
      """);
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).containsOnly(
      tuple("python:S1172", 1),
      tuple("python:PrintStatementUsage", 2));
  }

  @SonarLintTest
  void simpleKotlinKts(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "settings.gradle.kts", "description = \"SonarLint for IntelliJ IDEA\"");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.KOTLIN)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange).containsOnly(
      tuple("kotlin:S6625", null));
  }

  //ACR-aed7db4b5a214a11b7a7688def85e5e3
  @SonarLintTest
  void useRelativePathToEvaluatePathPatterns(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "foo.tmp", """
      def my_function(name):
          print "Hello"
          print "world!" # NOSONAR
      
      """);
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), Path.of("foo.py"), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine()).containsOnly(
      tuple("python:S1172", 1),
      tuple("python:PrintStatementUsage", 2));
  }

  @SonarLintTest
  void simpleJava(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
            System.out.println("Foo");
            // TODO full line issue
          }
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14), tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM)))),
        tuple("java:S1135", new TextRangeDto(5, 0, 5, 27), tuple(CleanCodeAttribute.COMPLETE, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.INFO)))));
  }

  @SonarLintTest
  void simpleJavaSymbolicEngine(SonarLintTestHarness harness, @TempDir Path baseDir) {
    assumeTrue(COMMERCIAL_ENABLED);
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            boolean a = true;
            if (a) {
               System.out.println( "Hello World!" );
            }
          }
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)
      ))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .withStandaloneEmbeddedPlugin(TestPlugin.JAVA_SE)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .contains(
        tuple("java:S2589", new TextRangeDto(4, 8, 4, 9)));
  }

  @SonarLintTest
  void simpleJavaWithQuickFix(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            \s
          }
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .contains(
        tuple("java:S1186", new TextRangeDto(2, 14, 2, 17), tuple(CleanCodeAttribute.COMPLETE, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH)))));

    assertThat(issues)
      .flatExtracting(RaisedIssueDto::getQuickFixes)
      .extracting(QuickFixDto::message)
      .containsOnly("Insert placeholder comment");
    assertThat(issues)
      .flatExtracting(RaisedIssueDto::getQuickFixes)
      .flatExtracting(QuickFixDto::fileEdits)
      .extracting(FileEditDto::target)
      .containsOnly(inputFile.toUri());
    assertThat(issues)
      .usingRecursiveFieldByFieldElementComparator()
      .flatExtracting(RaisedIssueDto::getQuickFixes)
      .flatExtracting(QuickFixDto::fileEdits)
      .flatExtracting(FileEditDto::textEdits)
      .extracting(TextEditDto::range, TextEditDto::newText)
      .containsOnly(
        tuple(new TextRangeDto(2, 21, 4, 2), "\n    // TODO document why this method is empty\n  "));
  }

  @SonarLintTest
  void simpleJavaWithCommaInClasspath(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
          }
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()),
        Map.of("sonar.java.libraries", "\"" + Paths.get("target/lib/guava,with,comma.jar").toAbsolutePath() + "\""), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isNotEmpty());
    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
  }

  @SonarLintTest
  void it_should_get_issue_details_for_standalone_issue(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "secret.py",
      "aws_secret_access_key=kHeUAwnSUizTWpSbyGAz4f+As5LshPIjvtpswqGb");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, baseDir,
        List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIGURATION_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.TEXT)
      .start(client);
    var analysisId = UUID.randomUUID();

    backend.getAnalysisService()
      .analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(fileUri), Map.of(), false, System.currentTimeMillis())).join();
    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIGURATION_SCOPE_ID).get(fileUri)).isNotEmpty());

    var issueId = client.getRaisedIssuesForScopeId(CONFIGURATION_SCOPE_ID).get(fileUri).get(0).getId();
    var result = backend.getIssueService().getEffectiveIssueDetails(new GetEffectiveIssueDetailsParams(CONFIGURATION_SCOPE_ID, issueId)).join();

    assertThat(result.getDetails()).isNotNull();
    //ACR-315a0587117a4d448fcc7749a5eab595
    assertThat(result.getDetails().getSeverityDetails().isRight()).isTrue();
    assertThat(result.getDetails().getSeverityDetails().getRight().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.TRUSTWORTHY);
    assertThat(result.getDetails().getRuleKey()).isEqualTo("secrets:S6290");
    assertThat(result.getDetails().getName()).isEqualTo("Amazon Web Services credentials should not be disclosed");
    assertThat(result.getDetails().getRuleDescriptionContextKey()).isNull();
    assertThat(result.getDetails().getVulnerabilityProbability()).isNull();
    assertThat(result.getDetails().getDescription().isRight()).isTrue();
    assertThat(result.getDetails().getDescription().getRight().getIntroductionHtmlContent()).contains("Secret leaks often occur");
    assertThat(result.getDetails().getLanguage().name()).isEqualTo("SECRETS");
  }

  @SonarLintTest
  void it_should_not_get_issue_details_for_non_existent_issue(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var filePath = createFile(baseDir, "secret.py",
      "KEY = \"AKIAIGKECZXA7AEIJLMQ\"");
    var fileUri = filePath.toUri();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, baseDir,
        List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIGURATION_SCOPE_ID, false, null, filePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.TEXT)
      .start(client);

    var issueId = UUID.randomUUID();
    var params = new GetEffectiveIssueDetailsParams(CONFIGURATION_SCOPE_ID, issueId);
    var issueService = backend.getIssueService();
    var detailsFuture = issueService.getEffectiveIssueDetails(params);
    assertThrows(CompletionException.class, detailsFuture::join);
  }

  //ACR-37b99293a16a429a8405463b8aedc180
  @SonarLintTest
  void noRuleTemplates(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .start(client);

    var response = backend.getRulesService().listAllStandaloneRulesDefinitions().get();
    assertThat(response.getRulesByKey()).doesNotContainKey("python:XPath");
  }

  @SonarLintTest
  void onlyLoadRulesOfEnabledLanguages(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backendBuilder = harness.newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVASCRIPT)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PHP)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.PYTHON)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.KOTLIN);

    if (COMMERCIAL_ENABLED) {
      backendBuilder = backendBuilder.withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.CFAMILY);
    }
    var backend = backendBuilder.start(client);

    var enabledLanguages = EnumSet.of(JAVA, JS, PHP, PYTHON, TS, XML, KOTLIN);

    if (COMMERCIAL_ENABLED) {
      enabledLanguages.add(C);
    }
    var response = backend.getRulesService().listAllStandaloneRulesDefinitions().get();
    assertThat(response.getRulesByKey().values())
      .flatExtracting(RuleDefinitionDto::getLanguage)
      .containsAll(enabledLanguages);
  }

  @SonarLintTest
  void simpleJavaNoHotspots(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var fooDir = Files.createDirectory(baseDir.resolve("foo"));
    var inputFile = createFile(fooDir, "Foo.java",
      """
        package foo;
        public class Foo {
          String ip = "192.168.12.42"; // Hotspots should not be reported in SonarLint
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("java:S1313", new StandaloneRuleConfigDto(true, emptyMap()))));

    analyzeFilesAndVerifyNoIssues(List.of(inputFile.toUri()), client, backend, CONFIGURATION_SCOPE_ID);
  }

  @SonarLintTest
  void simpleJavaPomXml(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "pom.xml",
      """
        <?xml version="1.0" encoding="UTF-8"?>
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>com.foo</groupId>
          <artifactId>bar</artifactId>
          <version>${pom.version}</version>
        </project>""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.XML)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, i -> i.getTextRange().getStartLine(), StandaloneIssueMediumTests::extractMqrDetails).containsOnly(
      tuple("xml:S3421", 6, tuple(CleanCodeAttribute.CONVENTIONAL, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
  }

  @SonarLintTest
  void supportJavaSuppressWarning(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          @SuppressWarnings("java:S106")
          public void foo() {
            int x;
            System.out.println("Foo");
            System.out.println("Foo"); //NOSONAR
          }
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(4, 8, 4, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
  }

  @SonarLintTest
  void simpleJavaWithBytecode(SonarLintTestHarness harness) {
    var projectWithByteCode = new File("src/test/projects/java-with-bytecode").getAbsoluteFile().toPath();
    var inputFile = projectWithByteCode.resolve("src/Foo.java");
    var binFile = projectWithByteCode.resolve("bin/Foo.class");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), projectWithByteCode.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true),
        new ClientFileDto(binFile.toUri(), projectWithByteCode.relativize(binFile), CONFIGURATION_SCOPE_ID, false, null, binFile, null, null, false)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of("sonar.java.binaries", projectWithByteCode.resolve("bin").toString()),
        true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isNotEmpty());
    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(5, 2, 5, 12)),
        tuple("java:S1220", null),
        tuple("java:S1144", new TextRangeDto(8, 14, 8, 17)),
        tuple("java:S1186", new TextRangeDto(8, 14, 8, 17)));
  }

  @SonarLintTest
  void simpleJavaWithExcludedRules(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
            System.out.println("Foo");
          }
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("java:S106", new StandaloneRuleConfigDto(false, emptyMap()))));

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
  }

  @SonarLintTest
  void simpleJavaWithExcludedRulesUsingDeprecatedKey(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
            System.out.println("Foo");
          }
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("squid:S106", new StandaloneRuleConfigDto(false, emptyMap()))));

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));

    assertThat(client.getLogMessages()).contains("Rule 'java:S106' was excluded using its deprecated key 'squid:S106'. Please fix your configuration.");
  }

  @SonarLintTest
  void simpleJavaWithIncludedRules(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        import java.util.Optional;
        public class Foo {
          public void foo(Optional<String> name) {  // for squid:3553, not in Sonar Way
            int x;
            System.out.println("Foo" + name.isPresent());
          }
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("java:S3553", new StandaloneRuleConfigDto(true, emptyMap()))));

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S3553", new TextRangeDto(3, 18, 3, 34), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM)))),
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14), tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM)))),
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(4, 8, 4, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));
  }

  @SonarLintTest
  void simpleJavaWithIncludedRulesUsingDeprecatedKey(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        import java.util.Optional;
        public class Foo {
          public void foo(Optional<String> name) {  // for squid:3553, not in Sonar Way
            int x;
            System.out.println("Foo" + name.isPresent());
          }
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("squid:S3553", new StandaloneRuleConfigDto(true, emptyMap()))));

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S3553", new TextRangeDto(3, 18, 3, 34), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM)))),
        tuple("java:S106", new TextRangeDto(5, 4, 5, 14), tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.MEDIUM)))),
        tuple("java:S1220", null, tuple(CleanCodeAttribute.MODULAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))),
        tuple("java:S1481", new TextRangeDto(4, 8, 4, 9), tuple(CleanCodeAttribute.CLEAR, List.of(tuple(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW)))));

    assertThat(client.getLogMessages()).contains("Rule 'java:S3553' was included using its deprecated key 'squid:S3553'. Please fix your configuration.");
  }

  @Disabled("Rule java:S1228 is not reported: Add a 'package-info.java' file to document the 'foo' package")
  @SonarLintTest
  void simpleJavaWithIssueOnDir(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var fooDir = Files.createDirectory(baseDir.resolve("foo"));
    var inputFile = createFile(fooDir, "Foo.java",
      """
        package foo;
        public class Foo {
        }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    backend.getRulesService()
      .updateStandaloneRulesConfiguration(new UpdateStandaloneRulesConfigurationParams(Map.of("java:S1228", new StandaloneRuleConfigDto(true, emptyMap()))));

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange, StandaloneIssueMediumTests::extractMqrDetails)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S2094", new TextRangeDto(2, 13, 2, 16), IssueSeverity.MINOR),
        tuple("java:S1228", null, IssueSeverity.MINOR));
  }

  @SonarLintTest
  void simpleJavaWithSecondaryLocations(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "Foo.java",
      """
        package foo;
        public class Foo {
          public void method() {
            String S1 = "duplicated";
            String S2 = "duplicated";
            String S3 = "duplicated";
          }\
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIGURATION_SCOPE_ID);

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .contains(tuple("java:S1192", new TextRangeDto(4, 16, 4, 28)));
    assertThat(issues)
      .filteredOn(issue -> issue.getRuleKey().equals("java:S1192"))
      .flatExtracting(RaisedIssueDto::getFlows)
      .hasSize(3);
  }

  @SonarLintTest
  void testJavaSurefireDontCrashAnalysis(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var surefireReport = new File(baseDir.toFile(), "reports/TEST-FooTest.xml");
    FileUtils.write(surefireReport, """
      <?xml version="1.0" encoding="UTF-8"?>
      <testsuite name="FooTest" time="0.121" tests="1" errors="0" skipped="0" failures="0">
      <testcase name="errorAnalysis" classname="FooTest" time="0.031"/>
      </testsuite>""", StandardCharsets.UTF_8);

    var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
            System.out.println("Foo");
            System.out.println("Foo"); //NOSONAR
          }
        }""");

    var inputFileTest = createFile(baseDir, "FooTest.java",
      """
        public class FooTest {
          public void testFoo() {
          }
        }""");

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true),
        new ClientFileDto(inputFileTest.toUri(), baseDir.relativize(inputFileTest), CONFIGURATION_SCOPE_ID, true, null, inputFileTest, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri(), inputFileTest.toUri()), Map.of("sonar.junit.reportsPath", "reports/"), true,
        System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID)).isNotEmpty());

    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIGURATION_SCOPE_ID);
    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", new TextRangeDto(4, 4, 4, 14)),
        tuple("java:S1220", null),
        tuple("java:S1220", null),
        tuple("java:S1186", new TextRangeDto(2, 14, 2, 21)),
        tuple("java:S1481", new TextRangeDto(3, 8, 3, 9)),
        tuple("java:S2187", new TextRangeDto(1, 13, 1, 20)));
  }

  @SonarLintTest
  void lazy_init_file_metadata(SonarLintTestHarness harness, @TempDir Path baseDir) {
    final var inputFile = createFile(baseDir, A_JAVA_FILE_PATH,
      """
        public class Foo {
          public void foo() {
            int x;
            System.out.println("Foo");
            System.out.println("Foo"); //NOSONAR
          }
        }""");
    var unexistingFile = new File(baseDir.toFile(), "missing.bin");
    var unexistingFilePath = unexistingFile.toPath();
    assertThat(unexistingFile).doesNotExist();

    var client = harness.newFakeClient()
      .withInitialFs(CONFIGURATION_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIGURATION_SCOPE_ID, false, null, inputFile, null, null, true),
        new ClientFileDto(unexistingFilePath.toUri(), baseDir.relativize(unexistingFilePath), CONFIGURATION_SCOPE_ID, false, null, unexistingFilePath, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIGURATION_SCOPE_ID)
      .withStandaloneEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIGURATION_SCOPE_ID, analysisId, List.of(inputFile.toUri(), unexistingFilePath.toUri()), Map.of("sonar.junit.reportsPath", "reports/"),
        true, System.currentTimeMillis()))
      .join();

    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    assertThat(client.getLogMessages())
      .contains("Initializing metadata of file " + inputFile.toUri())
      .doesNotContain("Initializing metadata of file " + unexistingFilePath.toFile());
  }

  private static Tuple extractMqrDetails(RaisedIssueDto raisedIssueDto) {
    assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
    var mqrModeDetails = raisedIssueDto.getSeverityMode().getRight();
    return tuple(mqrModeDetails.getCleanCodeAttribute(),
      mqrModeDetails.getImpacts().stream().map(i -> tuple(i.getSoftwareQuality(), i.getImpactSeverity())).toList());
  }
}
