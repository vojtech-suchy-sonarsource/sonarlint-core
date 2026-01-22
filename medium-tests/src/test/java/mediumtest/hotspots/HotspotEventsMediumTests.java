/*
ACR-aae2c6034db24fd08cf4ba6c5560fcf3
ACR-2aab0033be284e2090ee11de0badb950
ACR-65d95a7338824b558558d5ec332b77ae
ACR-aeb7bdb53c0d4423b786f29fb29d3f6e
ACR-c99ae5cc366049b2ab9ccf0b5d08c4e9
ACR-9f2e761c48e84a40ad0530434f565e18
ACR-82f05dc0371e4a829f07bc02e5aefd11
ACR-d70b3d559d6a4eeaab906fda06fe9f58
ACR-bfd3c9a5a9ef4be28f47881c24d8642c
ACR-a20e759ccb174d8ba9e5fcee9f1e088a
ACR-90705aa59d6d4d2e913e586637233dfe
ACR-52dfb6fb404b4cf6a30389094552c843
ACR-4b5472c90c2f4acab1aa6e6893f98a88
ACR-9f3725f4530e4c2c997335db7d68781b
ACR-44ee8e5cc92a4d5087c3e72612ba48c2
ACR-4150bb3de6d94cb08fd0b78d8700edf1
ACR-011e538fa887433da2b0e89656bcbaba
 */
package mediumtest.hotspots;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SECURITY_HOTSPOTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SERVER_SENT_EVENTS;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.JAVA;
import static org.sonarsource.sonarlint.core.test.utils.storage.ServerSecurityHotspotFixture.aServerHotspot;
import static utils.AnalysisUtils.analyzeFileAndGetHotspots;

class HotspotEventsMediumTests {

  @RegisterExtension
  static SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  @Nested
  class WhenReceivingSecurityHotspotRaisedEvent {
    @SonarLintTest
    void it_should_add_hotspot_in_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject("projectKey", project -> project.withMainBranch("branchName")))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: SecurityHotspotRaised
        data: {\
          "status": "TO_REVIEW",\
          "vulnerabilityProbability": "MEDIUM",\
          "creationDate": 1685006550000,\
          "mainLocation": {\
            "filePath": "file/path",\
            "message": "Make sure that using this pseudorandom number generator is safe here.",\
            "textRange": {\
              "startLine": 12,\
              "startLineOffset": 29,\
              "endLine": 12,\
              "endLineOffset": 36,\
              "hash": "43b5c9175984c071f30b873fdce0a000"\
            }\
          },\
          "ruleKey": "java:S2245",\
          "key": "AYhSN6mVrRF_krvNbHl1",\
          "projectKey": "projectKey",\
          "branch": "branchName"\
        }

        """);

      await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(readHotspots(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerHotspot::getKey)
        .containsOnly("AYhSN6mVrRF_krvNbHl1"));
    }

    @SonarLintTest
    void it_should_add_reviewed_hotspot_in_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject("projectKey", project -> project.withMainBranch("branchName")))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: SecurityHotspotRaised
        data: {\
          "status": "REVIEWED",\
          "resolution": "ACKNOWLEDGED",\
          "vulnerabilityProbability": "MEDIUM",\
          "creationDate": 1685006550000,\
          "mainLocation": {\
            "filePath": "file/path",\
            "message": "Make sure that using this pseudorandom number generator is safe here.",\
            "textRange": {\
              "startLine": 12,\
              "startLineOffset": 29,\
              "endLine": 12,\
              "endLineOffset": 36,\
              "hash": "43b5c9175984c071f30b873fdce0a000"\
            }\
          },\
          "ruleKey": "java:S2245",\
          "key": "AYhSN6mVrRF_krvNbHl1",\
          "projectKey": "projectKey",\
          "branch": "branchName"\
        }

        """);

      await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(readHotspots(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerHotspot::getKey, ServerHotspot::getStatus)
        .containsExactly(tuple("AYhSN6mVrRF_krvNbHl1", HotspotReviewStatus.ACKNOWLEDGED)));
    }
  }

  @Nested
  class WhenReceivingSecurityHotspotClosedEvent {
    @SonarLintTest
    void it_should_remove_hotspot_from_storage(SonarLintTestHarness harness) {
      var server = harness.newFakeSonarQubeServer("10.0")
        .withServerSentEventsEnabled()
        .withProject("projectKey",
          project -> project.withBranch("branchName"))
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS)
        .withSonarQubeConnection("connectionId", server,
          storage -> storage.withProject("projectKey", project -> project.withMainBranch("branchName", branch -> branch.withHotspot(aServerHotspot("hotspotKey")))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: SecurityHotspotClosed
        data: {\
            "key": "hotspotKey",\
            "projectKey": "projectKey",\
            "filePath": "file/path"\
        }

        """);

      await().atMost(Duration.ofSeconds(4)).untilAsserted(() -> assertThat(readHotspots(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .isEmpty());
    }

    @SonarLintTest
    void should_republish_hotspots_without_closed_one(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        """
          public class Foo {

            void foo() {
              String password = "blue";
              String passwordD = "red";
            }
          }
          """);
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverHotspotKey1 = "myHotspotKey1";
      var serverHotspotKey2 = "myHotspotKey2";
      var client = harness.newFakeClient()
        .withToken(connectionId, "token")
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithHotspots = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2068", activeRule -> activeRule
          .withSeverity(IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withHotspot(serverHotspotKey1, hotspot -> hotspot
                  .withFilePath(baseDir.relativize(filePath).toString())
                  .withStatus(HotspotReviewStatus.TO_REVIEW)
                  .withVulnerabilityProbability(VulnerabilityProbability.HIGH)
                  .withTextRange(new TextRange(4, 11, 4, 19))
                  .withRuleKey("java:S2068")
                  .withMessage("'password' detected in this expression, review this potentially hard-coded password.")
                  .withCreationDate(introductionDate)
                  .withAuthor("author"))
                .withHotspot(serverHotspotKey2, hotspot -> hotspot
                  .withFilePath(baseDir.relativize(filePath).toString())
                  .withStatus(HotspotReviewStatus.TO_REVIEW)
                  .withVulnerabilityProbability(VulnerabilityProbability.HIGH)
                  .withTextRange(new TextRange(5, 11, 5, 20))
                  .withRuleKey("java:S2068")
                  .withMessage("'password' detected in this expression, review this potentially hard-coded password.")
                  .withCreationDate(introductionDate)
                  .withAuthor("author"))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SECURITY_HOTSPOTS, SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithHotspots)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetHotspots(fileUri, client, backend, CONFIG_SCOPE_ID);
      var raisedHotspots = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileUri);
      assertThat(raisedHotspots).hasSize(2);
      client.cleanRaisedHotspots();

      serverWithHotspots.pushEvent("""
        event: SecurityHotspotClosed
        data: {\
            "key": "myHotspotKey1",\
            "projectKey": "projectKey",\
            "filePath": "Foo.java"\
        }

        """);

      await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      raisedHotspots = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileUri);

      assertThat(raisedHotspots).hasSize(1);
      var raisedHotspot = raisedHotspots.get(0);
      assertThat(raisedHotspot.getServerKey()).isEqualTo(serverHotspotKey2);
    }
  }

  @Nested
  class WhenReceivingSecurityHotspotChangedEvent {

    @SonarLintTest
    void it_should_update_hotspot_in_storage_when_changing_status(SonarLintTestHarness harness) {
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
            project -> project.withMainBranch("branchName", branch -> branch.withHotspot(aServerHotspot("AYhSN6mVrRF_krvNbHl1").withStatus(HotspotReviewStatus.TO_REVIEW)))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: SecurityHotspotChanged
        data: {\
          "key": "AYhSN6mVrRF_krvNbHl1",\
          "projectKey": "projectKey",\
          "updateDate": 1685007187000,\
          "status": "REVIEWED",\
          "assignee": "assigneeEmail",\
          "resolution": "SAFE",\
          "filePath": "file/path"\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readHotspots(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerHotspot::getKey, ServerHotspot::getStatus)
        .containsOnly(tuple("AYhSN6mVrRF_krvNbHl1", HotspotReviewStatus.SAFE)));
    }

    @SonarLintTest
    void it_should_update_known_findings_store_when_changing_status(SonarLintTestHarness harness, @TempDir Path baseDir) {
      //ACR-a737eb0eee124a52b01437372bd4b15b
      var filePath = createFile(baseDir, "Foo.java", """
        public class Foo {
          String ip = "192.168.12.42"; // Sensitive
          Socket socket = new Socket(ip, 6667);
        }
        """);
      var fileUri = filePath.toUri();

      var connectionId = "connectionId";
      var branchName = "branchName";
      String projectKey = "projectKey";
      var serverHotspotKey = "myHotspotKey";

      var client = harness.newFakeClient()
        .withToken(connectionId, "token")
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .withMatchedBranch(CONFIG_SCOPE_ID, branchName)
        .build();

      var server = harness.newFakeSonarQubeServer("10.4")
        .withProject(projectKey,
          project -> project
            .withBranch(branchName))
        .withServerSentEventsEnabled()
        .start();

      //ACR-719bc0e7b7c343c48cc07c1596d25d91
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SERVER_SENT_EVENTS, SECURITY_HOTSPOTS)
        .withSonarQubeConnection(connectionId, server,
          storage -> storage.withProject(projectKey,
              project -> project
                .withMainBranch(branchName, branch -> branch.withHotspot(aServerHotspot(serverHotspotKey)
                  .withStatus(HotspotReviewStatus.TO_REVIEW)
                  .withFilePath(baseDir.relativize(filePath).toString())
                  .withTextRange(new TextRangeWithHash(2, 14, 2, 29, "c50a46d24d0975188e037e408583ad30"))
                  .withRuleKey("java:S1313")
                  .withMessage("Make sure using this hardcoded IP address is safe here.")
                  .withVulnerabilityProbability(VulnerabilityProbability.LOW)
                ))
                .withRuleSet("java", ruleSetBuilder -> ruleSetBuilder.withActiveRule("java:S1313", "BLOCKER"))
            )
            .withPlugin(TestPlugin.JAVA))
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);

      //ACR-d344c2f9c51e4f28b5efb02165879d23
      analyzeFileAndGetHotspots(fileUri, client, backend, CONFIG_SCOPE_ID);

      //ACR-55d357ee32b54903aea5083238d1598c
      server.pushEvent(String.format("""
        event: SecurityHotspotChanged
        data: {\
          "key": "%s",\
          "projectKey": %s,\
          "updateDate": 1685007187000,\
          "status": "REVIEWED",\
          "assignee": "assigneeEmail",\
          "resolution": "SAFE",\
          "filePath": "%s"\
        }
        
        """, serverHotspotKey, projectKey, baseDir.relativize(filePath)));

      //ACR-b8d386de5a924b05a7e7794e89b8b639
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readHotspots(backend, connectionId, projectKey, branchName, baseDir.relativize(filePath).toString()))
        .extracting(ServerHotspot::getKey, ServerHotspot::getStatus)
        .containsOnly(tuple(serverHotspotKey, HotspotReviewStatus.SAFE)));

      //ACR-79f6372f74754e7bbc0cd14a35dca22a
      await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedHotspots = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileUri);

      assertThat(raisedHotspots).isNotEmpty();
      var raisedIssueDto = raisedHotspots.get(0);
      assertTrue(raisedIssueDto.isResolved());
      assertThat(raisedIssueDto.getServerKey()).isEqualTo(serverHotspotKey);
    }

    @SonarLintTest
    void it_should_update_hotspot_in_storage_when_changing_assignee(SonarLintTestHarness harness) {
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
            project -> project.withMainBranch("branchName", branch -> branch.withHotspot(aServerHotspot("AYhSN6mVrRF_krvNbHl1").withAssignee("previousAssignee")))))
        .withBoundConfigScope("configScope", "connectionId", "projectKey")
        .start();

      server.pushEvent("""
        event: SecurityHotspotChanged
        data: {\
          "key": "AYhSN6mVrRF_krvNbHl1",\
          "projectKey": "projectKey",\
          "updateDate": 1685007187000,\
          "status": "REVIEWED",\
          "assignee": "assigneeEmail",\
          "resolution": "SAFE",\
          "filePath": "file/path"\
        }

        """);

      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(readHotspots(backend, "connectionId", "projectKey", "branchName", "file/path"))
        .extracting(ServerHotspot::getKey, ServerHotspot::getAssignee)
        .containsOnly(tuple("AYhSN6mVrRF_krvNbHl1", "assigneeEmail")));
    }

    @SonarLintTest
    void should_raise_hotspot_with_changed_data(SonarLintTestHarness harness, @TempDir Path baseDir) {
      var filePath = createFile(baseDir, "Foo.java",
        """
          public class Foo {

            void foo() {
              String password = "blue";
            }
          }
          """);
      var fileUri = filePath.toUri();
      var connectionId = "connectionId";
      var branchName = "branchName";
      var projectKey = "projectKey";
      var serverHotspotKey = "myHotspotKey";
      var client = harness.newFakeClient()
        .withToken(connectionId, "token")
        .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(new ClientFileDto(fileUri, baseDir.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, null, true)))
        .build();
      when(client.matchSonarProjectBranch(eq(CONFIG_SCOPE_ID), eq("main"), eq(Set.of("main", branchName)), any())).thenReturn(branchName);
      var introductionDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);
      var serverWithHotspots = harness.newFakeSonarQubeServer("10.4")
        .withServerSentEventsEnabled()
        .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("java:S2068", activeRule -> activeRule
          .withSeverity(IssueSeverity.MAJOR)))
        .withProject(projectKey,
          project -> project
            .withQualityProfile("qpKey")
            .withBranch(branchName,
              branch -> branch.withHotspot(serverHotspotKey, hotspot -> hotspot
                .withFilePath(baseDir.relativize(filePath).toString())
                .withStatus(HotspotReviewStatus.TO_REVIEW)
                .withVulnerabilityProbability(VulnerabilityProbability.HIGH)
                .withTextRange(new TextRange(4, 11, 4, 19))
                .withRuleKey("java:S2068")
                .withMessage("'password' detected in this expression, review this potentially hard-coded password.")
                .withCreationDate(introductionDate)
                .withAuthor("author"))))
        .withPlugin(TestPlugin.JAVA)
        .start();
      var backend = harness.newBackend()
        .withExtraEnabledLanguagesInConnectedMode(JAVA)
        .withBackendCapability(SECURITY_HOTSPOTS, SERVER_SENT_EVENTS, FULL_SYNCHRONIZATION)
        .withSonarQubeConnection(connectionId, serverWithHotspots)
        .withBoundConfigScope(CONFIG_SCOPE_ID, connectionId, projectKey)
        .start(client);
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(client.getSynchronizedConfigScopeIds()).contains(CONFIG_SCOPE_ID));
      analyzeFileAndGetHotspots(fileUri, client, backend, CONFIG_SCOPE_ID);
      client.cleanRaisedHotspots();

      serverWithHotspots.pushEvent("event: SecurityHotspotChanged\n" +
        "data: {" +
        "  \"key\": \"myHotspotKey\"," +
        "  \"projectKey\": \"projectKey\"," +
        "  \"updateDate\": 1685007187000," +
        "  \"status\": \"REVIEWED\"," +
        "  \"assignee\": \"assigneeEmail\"," +
        "  \"resolution\": \"SAFE\"," +
        "  \"filePath\": \"" + baseDir.relativize(filePath) + "\"" +
        "}\n\n");

      await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(client.getRaisedHotspotsForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());
      var raisedHotspots = client.getRaisedHotspotsForScopeId(CONFIG_SCOPE_ID).get(fileUri);

      assertThat(raisedHotspots).hasSize(1);
      var raisedHotspot = raisedHotspots.get(0);
      assertThat(raisedHotspot.getServerKey()).isEqualTo(serverHotspotKey);
      assertThat(raisedHotspot.getStatus()).isEqualTo(HotspotStatus.SAFE);
    }
  }

  private static Path createFile(Path folderPath, String fileName, String content) {
    var filePath = folderPath.resolve(fileName);
    try {
      Files.writeString(filePath, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return filePath;
  }

  private Collection<ServerHotspot> readHotspots(SonarLintTestRpcServer backend, String connectionId, String projectKey, String branchName, String filePath) {
    return backend.getIssueStorageService().connection(connectionId).project(projectKey).findings().loadHotspots(branchName, Path.of(filePath));
  }
}
