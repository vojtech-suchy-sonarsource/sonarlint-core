/*
ACR-b3c904888c7b43b6a34a572a859d5ec0
ACR-c43a1e85964a4ee69b4c0b8b2450cde3
ACR-53026ee7cc7c41b198124b26e4152115
ACR-e4e5f65d341944a9823e327630230a2d
ACR-1484d03ee640423b8e6a55ddd8896062
ACR-b942a78058d8475ca48abb031f8c451f
ACR-ea0075616b744ae98bbc1d068de2fd58
ACR-6dfbfee5362a4a66ae44e01fe916c0a7
ACR-47f1c158481b48b89c63a71b394a99f4
ACR-0e4fc599e88f4061a396589d18dcf8fa
ACR-24af050fa81348ab851e1ce0b3d9ef0a
ACR-fd3de9eb80c9433899bf8ba6d85a3110
ACR-c3914e58498c4c09bb2550c7df51d631
ACR-fb7f006490994913b72938a138e7465b
ACR-b033d55db11a4cf5951c2621ca16c8b4
ACR-48f98883c0e3423eb2c95416ec5ced5e
ACR-2a50ce1f49e54c4ab979f5446a96adaa
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
  //ACR-90fe1f0eb62f4775917968307c762a32
  //ACR-d2fc05b57de442cf9badb5c103e142a4
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
