/*
ACR-7a62a79e3d0643618cdd8e0383ae3028
ACR-96a820a1cb3d43ed8ca21ab3f78dfe52
ACR-b2e1e00b62ef428fa15cfa95c096808c
ACR-d0e518f241d1492bbd18352d4216d6d4
ACR-74403506c9c84728a903487c6f7f7109
ACR-7d67448887b7480294a8d8e8a2cdae7d
ACR-cc20b7960a4548e8b2cb4f66bdb8325b
ACR-bc9e652222794387a0a4016398d8b941
ACR-7894ce10a2a246cea3fc13dbb429b793
ACR-01c1cbd82a114f228ec05376b6f756c6
ACR-92952044bab34ef389bb2852db16fd73
ACR-5495eaf3b4644356adf210c1bc5b87d6
ACR-5fcfcabd11ed401d8a9e6cd9d0938d33
ACR-1ce604f879764c438c4aae9bd99e3cc5
ACR-84610e915e274476ab3a849ab43d5651
ACR-0455008e25094416aea93884c5b0b604
ACR-63a14e46abb74df7b2d91ee67d045741
 */
package mediumtest.issues;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.scanner.protocol.Constants;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SERVER_SENT_EVENTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JS;
import static org.sonarsource.sonarlint.core.test.utils.storage.ServerIssueFixtures.aServerIssue;
import static utils.AnalysisUtils.analyzeFileAndGetIssue;
import static utils.AnalysisUtils.createFile;

class IssueEventsMediumTests {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();

  @Nested
  class WhenReceivingIssueChangedEvent {
    private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_resolution(SonarLintTestHarness harness) {
      var projectKey = "projectKey";
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject(projectKey,
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject(projectKey, project -> project.withMainBranch("branchName", branch -> branch.withIssue(aServerIssue("key1").open()))))
        .withBoundConfigScope("configScope", "connectionId", projectKey)
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName"\
        }],\
        "resolved": true\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", projectKey, "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::isResolved)
        .containsOnly(tuple("key1", true)));
    }

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_impacts(SonarLintTestHarness harness) {
      var projectKey = "projectKey";
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject(projectKey,
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject(projectKey,
            project -> project.withMainBranch("branchName",
              branch -> branch.withIssue(aServerIssue("key1").withImpacts(Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH))))))
        .withBoundConfigScope("configScope", "connectionId", projectKey)
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName",\
          "impacts": [ { "softwareQuality": "MAINTAINABILITY", "severity": "BLOCKER" } ]\
        }],\
        "resolved": true\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", projectKey, "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::isResolved, ServerIssue::getImpacts)
        .containsOnly(tuple("key1", true, Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.BLOCKER))));
    }

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_impacts_when_it_does_not_exist_in_storage(SonarLintTestHarness harness) {
      var projectKey = "projectKey";
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject(projectKey,
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject(projectKey, project -> project.withMainBranch("branchName", branch -> branch.withIssue(aServerIssue("key1")))))
        .withBoundConfigScope("configScope", "connectionId", projectKey)
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName",\
          "impacts": [ { "softwareQuality": "SECURITY", "severity": "BLOCKER" } ]\
        }],\
        "resolved": true\
        }

        """);

      await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", projectKey, "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::isResolved, ServerIssue::getImpacts)
        .containsOnly(tuple("key1", true, Map.of(SoftwareQuality.SECURITY, ImpactSeverity.BLOCKER))));
    }

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_impacts_on_different_software_quality(SonarLintTestHarness harness) {
      var projectKey = "projectKey";
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject(projectKey,
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject(projectKey,
            project -> project.withMainBranch("branchName",
              branch -> branch.withIssue(aServerIssue("key1").withImpacts(Map.of(SoftwareQuality.SECURITY, ImpactSeverity.BLOCKER))))))
        .withBoundConfigScope("configScope", "connectionId", projectKey)
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName",\
          "impacts": [ { "softwareQuality": "MAINTAINABILITY", "severity": "HIGH" } ]\
        }],\
        "resolved": true\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", projectKey, "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::isResolved, ServerIssue::getImpacts)
        .containsOnly(tuple("key1", true, Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH, SoftwareQuality.SECURITY, ImpactSeverity.BLOCKER))));
    }

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_severity(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject("projectKey",
            project -> project.withMainBranch("branchName", branch -> branch.withIssue(aServerIssue("key1").withSeverity(IssueSeverity.INFO)))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName"\
        }],\
        "userSeverity": "CRITICAL"\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::getUserSeverity)
        .containsOnly(tuple("key1", IssueSeverity.CRITICAL)));
    }

    @SonarLintTest
    void it_should_update_issue_in_storage_with_new_type(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject("projectKey",
            project -> project.withMainBranch("branchName", branch -> branch.withIssue(aServerIssue("key1").withType(RuleType.VULNERABILITY)))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "key1",\
          "branchName": "branchName"\
        }],\
        "userType": "BUG"\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readIssues(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerIssue::getKey, ServerIssue::getType)
        .containsOnly(tuple("key1", RuleType.BUG)));
    }

    @SonarLintTest
    void should_raise_issue_with_changed_rule_type(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        "public class Foo {\n}");
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverIssueKey = "myIssueKey";
      var client = harness.newFakeClient()
        .withToken(connectionId, "token")
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithIssues = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2094", activeRule -> activeRule
          .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withIssue(serverIssueKey, "java:S2094", "Remove this empty class, write its code or make it an \"interface\".",
                "author", baseDir.relativize(filePath).toString(), "1356c67d7ad1638d816bfb822dd2c25d", Constants.Severity.MAJOR, RuleType.CODE_SMELL,
                "OPEN", null, introductionDate, new TextRange(1, 13, 1, 16))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withEnabledLanguageInStandaloneMode(JS)
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithIssues)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofMinutes(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetIssue(fileUri, client, backend, CONFIG_SCOPE_ID);
      client.cleanRaisedIssues();

      serverWithIssues.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "myIssueKey",\
          "branchName": "branchName"\
        }],\
        "userType": "BUG"\
        }

        """);

      await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues).isNotEmpty();
      var raisedIssueDto = raisedIssues.get(0);
      assertThat(raisedIssueDto.getServerKey()).isEqualTo(serverIssueKey);
      assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
      assertThat(raisedIssueDto.getSeverityMode().getRight().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.CLEAR);
      assertThat(raisedIssueDto.getSeverityMode().getRight().getImpacts())
        .extracting(ImpactDto::getSoftwareQuality, ImpactDto::getImpactSeverity)
        .containsExactly(
          tuple(org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality.MAINTAINABILITY, org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity.LOW));
    }

    @SonarLintTest
    void should_raise_issue_with_changed_resolution(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        "public class Foo {\n}");
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverIssueKey = "myIssueKey";
      var client = harness.newFakeClient()
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithIssues = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2094", activeRule -> activeRule
          .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withIssue(serverIssueKey, "java:S2094", "Remove this empty class, write its code or make it an \"interface\".",
                "author", baseDir.relativize(filePath).toString(), "1356c67d7ad1638d816bfb822dd2c25d", Constants.Severity.MAJOR, RuleType.CODE_SMELL,
                "OPEN", null, introductionDate, new TextRange(1, 13, 1, 16))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withEnabledLanguageInStandaloneMode(JS)
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithIssues)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofMinutes(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetIssue(fileUri, client, backend, CONFIG_SCOPE_ID);
      client.cleanRaisedIssues();

      serverWithIssues.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "myIssueKey",\
          "branchName": "branchName"\
        }],\
        "resolved": "true"\
        }

        """);

      await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues).isNotEmpty();
      var raisedIssueDto = raisedIssues.get(0);
      assertThat(raisedIssueDto.getServerKey()).isEqualTo(serverIssueKey);
      assertThat(raisedIssueDto.isResolved()).isTrue();
      assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
    }

    @SonarLintTest
    void should_raise_issue_with_changed_impacts(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        "public class Foo {\n}");
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverIssueKey = "myIssueKey";
      var client = harness.newFakeClient()
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithIssues = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2094", activeRule -> activeRule
          .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withIssue(serverIssueKey, "java:S2094", "Remove this empty class, write its code or make it an \"interface\".",
                "author", baseDir.relativize(filePath).toString(), "1356c67d7ad1638d816bfb822dd2c25d", Constants.Severity.MAJOR, RuleType.CODE_SMELL,
                "OPEN", null, introductionDate, new TextRange(1, 13, 1, 16), Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withEnabledLanguageInStandaloneMode(JS)
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithIssues)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofMinutes(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetIssue(fileUri, client, backend, CONFIG_SCOPE_ID);
      client.cleanRaisedIssues();

      serverWithIssues.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "myIssueKey",\
          "branchName": "branchName",\
          "impacts": [ { "softwareQuality": "MAINTAINABILITY", "severity": "BLOCKER" } ]\
        }],\
        "resolved": "true"\
        }

        """);

      await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues).isNotEmpty();
      var raisedIssueDto = raisedIssues.get(0);
      assertThat(raisedIssueDto.getServerKey()).isEqualTo(serverIssueKey);
      assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
      assertThat(raisedIssueDto.getSeverityMode().getRight().getImpacts().get(0))
        .extracting("softwareQuality", "impactSeverity")
        .containsOnly(
          org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality.MAINTAINABILITY,
          org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity.BLOCKER);
    }

    @SonarLintTest
    void should_raise_issue_with_changed_severity(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        "public class Foo {\n}");
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverIssueKey = "myIssueKey";
      var client = harness.newFakeClient()
        .withToken(connectionId, "token")
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithIssues = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2094", activeRule -> activeRule
          .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withIssue(serverIssueKey, "java:S2094", "Remove this empty class, write its code or make it an \"interface\".",
                "author", baseDir.relativize(filePath).toString(), "1356c67d7ad1638d816bfb822dd2c25d", Constants.Severity.MAJOR, RuleType.CODE_SMELL,
                "OPEN", null, introductionDate, new TextRange(1, 13, 1, 16))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withEnabledLanguageInStandaloneMode(JS)
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithIssues)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofMinutes(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetIssue(fileUri, client, backend, CONFIG_SCOPE_ID);
      client.cleanRaisedIssues();

      serverWithIssues.pushEvent("""
        event: IssueChanged
        data: {\
        "projectKey": "projectKey",\
        "issues": [{\
          "issueKey": "myIssueKey",\
          "branchName": "branchName"\
        }],\
        "userSeverity": "MINOR"\
        }

        """);

      await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues).isNotEmpty();
      var raisedIssueDto = raisedIssues.get(0);
      assertThat(raisedIssueDto.getServerKey()).isEqualTo(serverIssueKey);
      assertThat(raisedIssueDto.getSeverityMode().isRight()).isTrue();
      assertThat(raisedIssueDto.getSeverityMode().getRight().getCleanCodeAttribute()).isEqualTo(CleanCodeAttribute.CLEAR);
      assertThat(raisedIssueDto.getSeverityMode().getRight().getImpacts())
        .extracting(ImpactDto::getSoftwareQuality, ImpactDto::getImpactSeverity)
        .containsExactly(
          tuple(org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality.MAINTAINABILITY, org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity.LOW));
    }

  }

  private List<ServerIssue<?>> readIssues(SonarLintTestRpcServer backend, String connectionId, String projectKey, String branchName, String filePath) {
    return backend.getIssueStorageService().connection(connectionId).project(projectKey).findings().load(branchName, Path.of(filePath));
  }
}
