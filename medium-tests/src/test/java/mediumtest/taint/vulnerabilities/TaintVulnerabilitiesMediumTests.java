/*
ACR-b7db0dccf176447db64f967d22087db1
ACR-a1bce42caf0743bc910469d798ff5e2f
ACR-b51d0c945b564893a7ac3b9a69f97403
ACR-335798c69b12480cbf3be1554f90ecc2
ACR-dfd95a09d9f24e36b6944793b0aa5873
ACR-6d3a8e495e4d4c6c871c8571f2a1c076
ACR-efb38e0e488e40dc8c4956e59b9e1b4b
ACR-c936b970bf1a45ad90d845d051484dbc
ACR-aea01da170784981a00f9c65f2dc0390
ACR-65391d46be6649a681fe2aa67e2256b9
ACR-75144dec7d514f2fa55d775b3c3841df
ACR-3a680f9ace9544298ce72cc36b42135f
ACR-f293dc5b596e489c9675d21ff9799fc9
ACR-c49d3db489bb427a83ec9a44ec3f368b
ACR-eef3904a584b436b987288750f83894b
ACR-806cb9a414074e56bada30b5e27a8401
ACR-2ccf9dab7bbc425da68fcebc5d9467c4
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
    //ACR-77ec7cde94b5443c8b46add6547524f4
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
