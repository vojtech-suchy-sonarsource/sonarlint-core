/*
ACR-234e6b172bab474cbb3fedf12a797ea2
ACR-f93e18f0e524421184faa23718245e22
ACR-41c78e678ef046e987109fd91155e5cb
ACR-472c73bac30f4017973c6b7b8bd0fcaa
ACR-c785d85dfb984ee4a69e5cd029de8fab
ACR-2c057f94228948f8ac5657929b955421
ACR-802262015a8f4fa6a47ca68061e178f2
ACR-145b12988ebe48b788f5ca0b5f0be1db
ACR-f9e351430e284d6b8ff47c663be35938
ACR-cbb8f80f9f3842bc92c748facba5b5ec
ACR-85c9734717a84a0a9b19985cf0f03c67
ACR-990d8af8f9c641278856cb454b5e8ca0
ACR-2a5b978133a94c10a38023de2b147875
ACR-6133c1d285cf42f0a11732e21b0a39c1
ACR-db4a2acbc00244708930a2ead6b0ead9
ACR-e34e9c6802c94e4680c3589491a2b17a
ACR-3a442e4767ff4b67bd5e42e467753f78
 */
package mediumtest.rules;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SERVER_SENT_EVENTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;
import static utils.AnalysisUtils.analyzeFileAndGetIssues;
import static utils.AnalysisUtils.createFile;

class RuleEventsMediumTests {

  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @Nested
  class WhenReceivingRuleSetChangedEvent {
    //ACR-346f6aaf569548dba5aeac96396a5ed8
    @SonarLintTest
    void it_should_create_the_ruleset_storage_if_does_not_exist_without_impacts(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server)
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "java:S0000",\
            "language": "java",\
            "severity": "MAJOR",\
            "params": [{\
              "key": "key1",\
              "value": "value1"\
            }]\
          }],\
          "deactivatedRules": ["java:S4321"]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("java")
        .isEqualTo(Sonarlint.RuleSet.newBuilder()
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("java:S0000").setSeverity("MAJOR").putParams("key1", "value1").build()).build()));
    }

    @SonarLintTest
    void it_should_create_the_ruleset_storage_if_does_not_exist(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server)
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "java:S0000",\
            "language": "java",\
            "severity": "MAJOR",\
            "params": [{\
              "key": "key1",\
              "value": "value1"\
            }],\
            "impacts": [{\
              "softwareQuality": "SECURITY",\
              "severity": "HIGH"\
            }]\
          }]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("java")
        .isEqualTo(Sonarlint.RuleSet.newBuilder()
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder()
            .setRuleKey("java:S0000")
            .setSeverity("MAJOR")
            .putParams("key1", "value1")
            .addOverriddenImpacts(Sonarlint.RuleSet.ActiveRule.newBuilder().addOverriddenImpactsBuilder()
              .setSoftwareQuality("SECURITY")
              .setSeverity("HIGH")
              .build())
            .build())
          .build()));
    }

    @SonarLintTest
    void it_should_update_existing_rule_in_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey",
          project -> project.withRuleSet("java",
            ruleSet -> ruleSet.withActiveRule("java:S0000", "INFO"))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "java:S0000",\
            "language": "java",\
            "severity": "MAJOR",\
            "params": [{\
              "key": "key1",\
              "value": "value1"\
            }]\
          }]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("java")
        .isEqualTo(Sonarlint.RuleSet.newBuilder()
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("java:S0000").setSeverity("MAJOR").putParams("key1", "value1").build()).build()));
    }

    @SonarLintTest
    void it_should_add_rule_to_existing_ruleset_in_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey",
          project -> project.withRuleSet("java",
            ruleSet -> ruleSet.withActiveRule("java:S0000", "INFO"))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "java:S0001",\
            "language": "java",\
            "severity": "MAJOR",\
            "params": [{\
              "key": "key1",\
              "value": "value1"\
            }]\
          }]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("java")
        .isEqualTo(Sonarlint.RuleSet.newBuilder()
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("java:S0000").setSeverity("INFO").build())
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("java:S0001").setSeverity("MAJOR").putParams("key1", "value1").build()).build()));
    }

    @SonarLintTest
    void it_should_reanalyze_open_files_on_new_rules_enabled(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        """
          public class Foo {
            void foo() {
              // TODO foo
              int i = 0;
            }
          }
          """);
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var client = harness.newFakeClient()
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var server = harness.newFakeSonarQubeServer()
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java")
          .withActiveRule("java:S1481", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey"))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, server)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);

      backend.getFileService().didOpenFile(new DidOpenFileParams(CONFIG_SCOPE_ID, fileUri));
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      var raisedIssues = analyzeFileAndGetIssues(fileUri, client, backend, CONFIG_SCOPE_ID);
      assertThat(raisedIssues).hasSize(1);
      client.cleanRaisedIssues();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "java:S1135",\
            "language": "java",\
            "severity": "MAJOR",\
            "params": []\
          }]\
        }

        """);

      await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).hasSize(2));
      raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues)
        .extracting(RaisedFindingDto::getRuleKey)
        .containsExactlyInAnyOrder("java:S1135", "java:S1481");
    }

    @SonarLintTest
    void it_should_add_rule_to_new_ruleset_in_existing_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey",
          project -> project.withRuleSet("java",
            ruleSet -> ruleSet.withActiveRule("java:S0000", "INFO"))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "deactivatedRules": [],\
          "activatedRules": [{\
            "key": "cs:S0000",\
            "language": "cs",\
            "severity": "MAJOR",\
            "params": [{\
              "key": "key1",\
              "value": "value1"\
            }]\
          }]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("cs")
        .isEqualTo(Sonarlint.RuleSet.newBuilder()
          .addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("cs:S0000").setSeverity("MAJOR").putParams("key1", "value1").build()).build()));
    }

    @SonarLintTest
    void it_should_remove_deactivated_rule_from_existing_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey",
          project -> project.withRuleSet("java",
            ruleSet -> ruleSet.withActiveRule("java:S0000", "INFO").withActiveRule("java:S0001", "INFO"))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "activatedRules": [],\
          "deactivatedRules": ["java:S0000"]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .extractingByKey("java")
        .isEqualTo(Sonarlint.RuleSet.newBuilder().addRule(Sonarlint.RuleSet.ActiveRule.newBuilder().setRuleKey("java:S0001").setSeverity("INFO").build()).build()));
    }

    @SonarLintTest
    void it_should_remove_ruleset_from_storage_when_deactivating_last_rule(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var client = harness.newFakeClient().build();
      when(client.matchSonarProjectBranch(eq("configScope"), any(), any(), any())).thenReturn("branchName");
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server, storage -> storage.withProject("projectKey",
          project -> project.withRuleSet("java",
            ruleSet -> ruleSet.withActiveRule("java:S0000", "INFO"))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey", "projectKey2"],\
          "activatedRules": [],\
          "deactivatedRules": ["java:S0000"]\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readRuleSets(backend, "connectionId", "projectKey"))
        .isEmpty());
    }

    @SonarLintTest
    void it_should_re_raise_issues_without_deactivated_rules(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        """
          public class Foo {
            void foo() {
              // TODO foo
              int i = 0;
              String password = "blue";
              String ip = "192.168.12.42";
            }
          }
          """);
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var client = harness.newFakeClient()
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var server = harness.newFakeSonarQubeServer()
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java")
          .withActiveRule("java:S1481", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR))
          .withActiveRule("java:S1135", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR))
          .withActiveRule("java:S1313", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR))
          .withActiveRule("java:S2068", activeRule -> activeRule.withSeverity(IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey"))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, SECURITY_HOTSPOTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, server)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      var raisedIssues = analyzeFileAndGetIssues(fileUri, client, backend, CONFIG_SCOPE_ID);
      assertThat(raisedIssues).hasSize(4);
      client.cleanRaisedIssues();
      client.cleanRaisedHotspots();

      server.pushEvent("""
        event: RuleSetChanged
        data: {\
          "projects": ["projectKey"],\
          "activatedRules": [],\
          "deactivatedRules": ["java:S1481", "java:S1313"]\
        }

        """);

      await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      raisedIssues = client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      var raisedHotspots = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedIssues)
        .extracting(RaisedIssueDto::getRuleKey)
        .containsExactly("java:S1135");
      assertThat(raisedHotspots)
        .extracting(RaisedHotspotDto::getRuleKey)
        .containsExactly("java:S2068");
    }
  }

  private Map<String, Sonarlint.RuleSet> readRuleSets(SonarLintTestRpcServer backend, String connectionId, String projectKey) {
    var path = backend.getStorageRoot().resolve(encodeForFs(connectionId)).resolve("projects").resolve(encodeForFs(projectKey)).resolve("analyzer_config.pb");
    if (path.toFile().exists()) {
      return ProtobufFileUtil.readFile(path, Sonarlint.AnalyzerConfiguration.parser()).getRuleSetsByLanguageKeyMap();
    }
    return Map.of();
  }
}
