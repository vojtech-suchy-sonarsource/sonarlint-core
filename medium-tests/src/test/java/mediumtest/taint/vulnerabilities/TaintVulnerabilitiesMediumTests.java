/*
ACR-5b10c423ff264133a818e719137086bf
ACR-a998e239d8b04abc856e114322c6e90b
ACR-3b98c1a2b3f14f19ae6c32ac9a41689e
ACR-1959ad5cfa60444f8560e341fdbc6313
ACR-2fc90417f63f47ed99795dcb96cfe70a
ACR-e5f8ddd2c9834564a7337fbef2e16242
ACR-ee8e2af4c9f84911acb5e4d4aba611ca
ACR-1b7e21a5bab948f6b0547e96290e1300
ACR-115bc764c0ac423fa4131229c8900eec
ACR-f8687a304e7741fc9cbf67b2aa41d3dc
ACR-6d889e23622a4d9f9eed49839d177c14
ACR-db1b7f218b7f4394889289643bf4222d
ACR-ae6f95e4561f4191868e3affbcd4a79b
ACR-0ab345ffab794f0c89d419d69b6d5199
ACR-edc5dfd42e724fb0817f22399d198efe
ACR-8d0dacaa4b1f4766a4e7bababee297e8
ACR-95115a5e559f4dd4bd137db5ee450553
 */
package mediumtest.taint.vulnerabilities;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.GetEffectiveIssueDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.ListAllParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import utils.TestPlugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.test.utils.storage.ServerTaintIssueFixtures.aServerTaintIssue;

class TaintVulnerabilitiesMediumTests {

  @SonarLintTest
  void it_should_return_no_taint_vulnerabilities_if_the_scope_is_not_bound(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .start();

    var taintVulnerabilities = listAllTaintVulnerabilities(backend, "configScopeId");

    assertThat(taintVulnerabilities).isEmpty();
  }

  @SonarLintTest
  void it_should_return_no_taint_vulnerabilities_if_the_storage_is_empty(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId")
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start();

    var taintVulnerabilities = listAllTaintVulnerabilities(backend, "configScopeId");

    assertThat(taintVulnerabilities).isEmpty();
  }

  @SonarLintTest
  void it_should_return_the_stored_taint_vulnerabilities(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main"))
      .start();
    var introductionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main",
            branch -> branch.withTaintIssue(aServerTaintIssue("key")
              .withTextRange(new TextRangeWithHash(1, 2, 3, 4, "hash")).withRuleKey("ruleKey")
              .withType(RuleType.VULNERABILITY)
              .withIntroductionDate(introductionDate)))))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(listAllTaintVulnerabilities(backend, "configScopeId"))
      .extracting(TaintVulnerabilityDto::getIntroductionDate)
      .containsOnly(introductionDate));
  }

  @SonarLintTest
  void it_should_return_taint_details(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("javasecurity:S6549", activeRule -> activeRule
        .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
      .withProject("projectKey", project -> project.withBranch("main")
        .withQualityProfile("qpKey"))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var introductionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var fakeTaintBuilder = aServerTaintIssue("key")
      .withTextRange(new TextRangeWithHash(1, 2, 3, 4, "hash")).withRuleKey("javasecurity:S6549")
      .withType(RuleType.VULNERABILITY)
      .withIntroductionDate(introductionDate);

    var backend = harness.newBackend()
      .withExtraEnabledLanguagesInConnectedMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main",
            branch -> branch.withTaintIssue(fakeTaintBuilder))))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    client.waitForSynchronization();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(listAllTaintVulnerabilities(backend, "configScopeId"))
      .extracting(TaintVulnerabilityDto::getIntroductionDate)
      .containsOnly(introductionDate));
    var taintVulnerability = listAllTaintVulnerabilities(backend, "configScopeId").get(0);
    var actualTaintId = taintVulnerability.getId();

    var taintDetails = backend.getIssueService().getEffectiveIssueDetails(new GetEffectiveIssueDetailsParams("configScopeId", actualTaintId)).join();

    assertThat(taintDetails).isNotNull();
    assertThat(taintVulnerability)
      .extracting("resolved", "resolutionStatus")
      .containsExactly(false, null);
  }

  @SonarLintTest
  void it_should_return_resolved_taint_details(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    var server = harness.newFakeSonarQubeServer()
      .withQualityProfile("qpKey", qualityProfile -> qualityProfile.withLanguage("java").withActiveRule("javasecurity:S6549", activeRule -> activeRule
        .withSeverity(org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity.MAJOR)))
      .withProject("projectKey", project -> project.withBranch("main")
        .withQualityProfile("qpKey"))
      .withPlugin(TestPlugin.JAVA)
      .start();
    var introductionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var fakeTaintBuilder = aServerTaintIssue("key")
      .withTextRange(new TextRangeWithHash(1, 2, 3, 4, "hash")).withRuleKey("javasecurity:S6549")
      .withType(RuleType.VULNERABILITY)
      .withIntroductionDate(introductionDate)
      .resolvedWithStatus(IssueStatus.ACCEPT);

    var backend = harness.newBackend()
      .withExtraEnabledLanguagesInConnectedMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", server,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main",
            branch -> branch.withTaintIssue(fakeTaintBuilder))))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    client.waitForSynchronization();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(listAllTaintVulnerabilities(backend, "configScopeId"))
      .extracting(TaintVulnerabilityDto::getIntroductionDate)
      .containsOnly(introductionDate));
    var taintVulnerability = listAllTaintVulnerabilities(backend, "configScopeId").get(0);
    var actualTaintId = taintVulnerability.getId();

    var taintDetails = backend.getIssueService().getEffectiveIssueDetails(new GetEffectiveIssueDetailsParams("configScopeId", actualTaintId)).join();

    assertThat(taintDetails).isNotNull();
    assertThat(taintVulnerability)
      .extracting("resolved", "resolutionStatus")
      .containsExactly(true, ResolutionStatus.ACCEPT);
  }

  @SonarLintTest
  void it_should_refresh_taint_vulnerabilities_when_requested(SonarLintTestHarness harness) {
    var serverWithATaint = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("main", branch -> branch.withTaintIssue("oldIssueKey", "rule:key", "message", "author", "file/path", "OPEN", null,
        Instant.now(), new TextRange(1, 2, 3, 4), RuleType.VULNERABILITY)))
      .start();
    var newestIntroductionDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    var serverWithAnotherTaint = harness.newFakeSonarQubeServer()
      .withProject("projectKey",
        project -> project.withBranch("main", branch -> branch.withTaintIssue("anotherIssueKey", "rule:key", "message", "author", "file/path", "OPEN", null,
          newestIntroductionDate, new TextRange(1, 2, 3, 4), RuleType.VULNERABILITY)))
      .start();
    var backend = harness.newBackend()
      .withEnabledLanguageInStandaloneMode(Language.JAVA)
      .withSonarQubeConnection("connectionId", serverWithATaint,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start();
    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(listAllTaintVulnerabilities(backend, "configScopeId")).isNotEmpty());
    //ACR-393f8732aac846aa9759982adf40b256
    backend.getConnectionService()
      .didUpdateConnections(new DidUpdateConnectionsParams(List.of(new SonarQubeConnectionConfigurationDto("connectionId", serverWithAnotherTaint.baseUrl(), true)), List.of()));

    var taintVulnerabilities = refreshAndListAllTaintVulnerabilities(backend, "configScopeId");

    assertThat(ChronoUnit.MINUTES.between(taintVulnerabilities.get(0).getIntroductionDate(), newestIntroductionDate)).isZero();
  }

  private List<TaintVulnerabilityDto> listAllTaintVulnerabilities(SonarLintTestRpcServer backend, String configScopeId) {
    try {
      return backend.getTaintVulnerabilityTrackingService().listAll(new ListAllParams(configScopeId)).get().getTaintVulnerabilities();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private List<TaintVulnerabilityDto> refreshAndListAllTaintVulnerabilities(SonarLintTestRpcServer backend, String configScopeId) {
    try {
      return backend.getTaintVulnerabilityTrackingService().listAll(new ListAllParams(configScopeId, true)).get().getTaintVulnerabilities();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
