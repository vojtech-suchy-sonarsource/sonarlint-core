/*
ACR-dff7cad4da2b417ebd2b47031cade5fc
ACR-c106435c42f44d6e9c78b25bdec347fd
ACR-d8edc347e149495bb60acd4eff5a43e9
ACR-9efd097d99dd4f7dbf31ca3e30f65fda
ACR-fef41e78f4334ae5818183e4ae76fad1
ACR-39b8d4e1f60142e3ab801d01288c7376
ACR-d6d2a7551b0e4d1da5512b48cfd4c7e3
ACR-58a3cdff91ef40c1a5946de281f48eac
ACR-ddafd58ee07e4287a0748215899a131e
ACR-8176f87a50414e7ba84b6cc2da67f557
ACR-75b6020cb9e245f79bd89cbbfc6dffd6
ACR-1e1f924232af425b925fe6273ed0fd75
ACR-35d813a3107549aabd44fbdd04c02aba
ACR-3a3450d1c4a044caa041367891d98c2a
ACR-29fc90f2c7f34392bff3c0ce0b83c634
ACR-df6a7dc3da554e95996e9f4447905770
ACR-463f9b98ff2d47c9bda98f2ab42de914
 */
package mediumtest;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.GetEffectiveIssueDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.DATAFLOW_BUG_DETECTION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static utils.AnalysisUtils.analyzeFileAndGetIssues;
import static utils.AnalysisUtils.createFile;

class ConnectedIssueMediumTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String CONFIG_SCOPE_ID = "configScopeId";
  private static final String CONNECTION_ID = "connectionId";
  //ACR-142583b2d03d4a79b0839d5649195a21
  //ACR-62d17ce391e942d89f17997b0f7e3312
  private static final boolean COMMERCIAL_ENABLED = System.getProperty("commercial") != null;

  @SonarLintTest
  void simpleJavaBound(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "Foo.java", """
      public class Foo {
        public void foo() {
          int x;
          System.out.println("Foo");
          System.out.println("Foo"); //NOSONAR
        }
      }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var projectKey = "projectKey";
    var branchName = "main";
    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile
        .withLanguage("java")
        .withActiveRule("java:S106", activeRule -> activeRule
          .withSeverity(IssueSeverity.MAJOR))
        .withActiveRule("java:S1220", activeRule -> activeRule
          .withSeverity(IssueSeverity.MINOR))
        .withActiveRule("java:S1481", activeRule -> activeRule
          .withSeverity(IssueSeverity.BLOCKER)))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION, SECURITY_HOTSPOTS)
      .withSonarQubeConnection(CONNECTION_ID, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, projectKey)
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);
    client.waitForSynchronization();

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());

    var issues = client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID);
    assertThat(issues).extracting("ruleKey", "severityMode.right.cleanCodeAttribute")
      .usingRecursiveFieldByFieldElementComparator()
      .containsOnly(
        tuple("java:S106", CleanCodeAttribute.MODULAR),
        tuple("java:S1220", CleanCodeAttribute.MODULAR),
        tuple("java:S1481", CleanCodeAttribute.CLEAR));
  }

  @SonarLintTest
  void simpleJavaSymbolicEngineBound(SonarLintTestHarness harness, @TempDir Path baseDir) {
    assumeTrue(COMMERCIAL_ENABLED);
    var inputFile = createFile(baseDir, "Foo.java",
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
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java"))
      .withProject("projectKey")
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server, storage -> storage
        .withPlugins(TestPlugin.JAVA, TestPlugin.JAVA_SE)
        .withProject("projectKey", project -> project.withRuleSet("java", ruleSet -> ruleSet
          .withActiveRule("java:S2589", "BLOCKER")
          .withActiveRule("java:S106", "BLOCKER"))
          .withMainBranch("main")))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIG_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .contains(
        tuple("java:S2589", new TextRangeDto(4, 8, 4, 9)),
        tuple("java:S106", new TextRangeDto(5, 6, 5, 16)));
  }

  @SonarLintTest
  void simpleJavaSymbolicEngineBoundWithDbd(SonarLintTestHarness harness, @TempDir Path baseDir) {
    assumeTrue(COMMERCIAL_ENABLED);
    var inputFile = createFile(baseDir, "Foo.java",
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
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java"))
      .withProject("projectKey")
      .start();
    var backend = harness.newBackend()
      .withBackendCapability(DATAFLOW_BUG_DETECTION)
      .withSonarQubeConnection("connectionId", server, storage -> storage
        .withPlugins(TestPlugin.PYTHON, TestPlugin.JAVA, TestPlugin.JAVA_SE, TestPlugin.DBD, TestPlugin.DBD_JAVA)
        .withProject("projectKey", project -> project.withRuleSet("java", ruleSet -> ruleSet
          .withActiveRule("java:S2589", "BLOCKER")
          .withActiveRule("java:S106", "BLOCKER"))
          .withMainBranch("main")))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, "projectKey")
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withEnabledLanguageInStandaloneMode(Language.PYTHON)
      .start(client);

    var issues = analyzeFileAndGetIssues(inputFile.toUri(), client, backend, CONFIG_SCOPE_ID);

    assertThat(issues).extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .contains(
        tuple("java:S2589", new TextRangeDto(4, 8, 4, 9)),
        tuple("java:S106", new TextRangeDto(5, 6, 5, 16)));
  }

  @SonarLintTest
  void emptyQPJava(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var inputFile = createFile(baseDir, "Foo.java", """
      public class Foo {
        public void foo() {
          int x;
          System.out.println("Foo");
          System.out.println("Foo"); //NOSONAR
        }
      }""");
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(
        new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var projectKey = "projectKey";
    var branchName = "main";
    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java"))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION, SECURITY_HOTSPOTS)
      .withSonarQubeConnection(CONNECTION_ID, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, projectKey)
      .withExtraEnabledLanguagesInConnectedMode(JAVA)
      .start(client);
    client.waitForSynchronization();

    var analysisId = UUID.randomUUID();
    var analysisResult = backend.getAnalysisService().analyzeFilesAndTrack(
      new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(inputFile.toUri()), Map.of(), true, System.currentTimeMillis()))
      .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await().during(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isEmpty());
  }

  @SonarLintTest
  void it_should_get_hotspot_details(SonarLintTestHarness harness, @TempDir Path baseDir) {
    var fileFoo = createFile(baseDir, "Foo.java", """
      public class Foo {
        void foo() {
          String password = "blue";
        }
      }""");
    var fileFooUri = fileFoo.toUri();

    var connectionId = "connectionId";
    var branchName = "branchName";
    var projectKey = "projectKey";
    var serverWithHotspots = harness.newFakeSonarQubeServer("10.4")
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java")
        .withActiveRule("java:S2068", activeRule -> activeRule.withSeverity(IssueSeverity.BLOCKER)))
      .withProject(projectKey,
        project -> project
          .withQualityProfile("qpKey")
          .withBranch(branchName))
      .start();
    var client = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(
        new ClientFileDto(fileFooUri, baseDir.relativize(fileFoo), CONFIG_SCOPE_ID, false, null, fileFoo, null, null, true)))
      .build();
    var backend = harness.newBackend()
      .withBackendCapability(FULL_SYNCHRONIZATION, SECURITY_HOTSPOTS)
      .withSonarQubeConnection(connectionId, serverWithHotspots,
        storage -> storage.withServerVersion("10.4").withProject(projectKey,
          project -> project.withRuleSet("java", ruleSet -> ruleSet.withActiveRule("java:S2068", "BLOCKER"))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
      .withConnectedEmbeddedPluginAndEnabledLanguage(TestPlugin.JAVA)
      .start(client);
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> Assertions.assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));

    var analysisId = UUID.randomUUID();

    backend.getAnalysisService().analyzeFilesAndTrack(new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(fileFooUri), Map.of(), true, System.currentTimeMillis()))
      .join();
    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> AssertionsForInterfaceTypes.assertThat(client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID)).hasSize(1));

    var raisedIssuesForFoo = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    var raisedHotspotsForFoo = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileFooUri);
    AssertionsForInterfaceTypes.assertThat(raisedIssuesForFoo).isEmpty();
    AssertionsForInterfaceTypes.assertThat(raisedHotspotsForFoo).hasSize(1);

    var result = backend.getIssueService().getEffectiveIssueDetails(new GetEffectiveIssueDetailsParams(CONFIG_SCOPE_ID, raisedHotspotsForFoo.get(0).getId())).join();
    assertThat(result.getDetails()).isNotNull();

    serverWithHotspots.shutdown();
  }
}
