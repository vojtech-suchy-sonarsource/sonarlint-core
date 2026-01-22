/*
ACR-dce09ace83db4e6895b9472dc51fe792
ACR-184d495f358643e9bc6da64a7ec15273
ACR-507f8dfa98f4454a93ccec9c95cd5306
ACR-28d92f9c738e4fe6b5da489eef032522
ACR-6a7f913ce7a642e9b7c47ba021693683
ACR-2130b453c6904eec9b2c36b54e645d0f
ACR-68e31ca1e6bc46d39155378c9a655c07
ACR-4e3dfb57330f4f6f8ca65927695e8648
ACR-2e57c41eaf9d496ebb193ff44eff0754
ACR-db7b5034e95742c4a827972e037c5506
ACR-0f9bab8eb34f4a87855a13fcb3b84578
ACR-730d51ac41bd4a129b08e1e7463f7911
ACR-a0872c9bf7d0438eacd97ef47640804c
ACR-f14d708415084f04abf625a9ad66bd68
ACR-4ce5a57563e7440383d97be5e05b58b1
ACR-bc16067bfe8444838ba07cc85184b695
ACR-bf1d9fa3374640ab82289a3cbea51b14
 */
package mediumtest.sca;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.ListAllParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sca.DidChangeDependencyRisksParams;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SCA_SYNCHRONIZATION;
import static org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk.Severity;
import static org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk.Status;
import static org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk.Type;
import static org.sonarsource.sonarlint.core.test.utils.storage.ServerDependencyRiskFixtures.aServerDependencyRisk;

class DependencyRisksMediumTests {

  private static final String CONFIG_SCOPE_ID = "configScopeId";
  private static final String CONNECTION_ID = "connectionId";
  private static final String PROJECT_KEY = "projectKey";

  @SonarLintTest
  void it_should_return_no_dependency_risks_if_the_scope_does_not_exist(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .start();

    var dependencyRisks = listAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    assertThat(dependencyRisks).isEmpty();
  }

  @SonarLintTest
  void it_should_return_no_dependency_risks_if_the_scope_is_not_bound(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .start();

    var dependencyRisks = listAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    assertThat(dependencyRisks).isEmpty();
  }

  @SonarLintTest
  void it_should_return_no_dependency_risks_if_the_storage_is_empty(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .withSonarQubeConnection(CONNECTION_ID)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var dependencyRisks = listAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    assertThat(dependencyRisks).isEmpty();
  }

  @SonarLintTest
  void it_should_return_the_stored_dependency_risks(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY, project -> project.withBranch("main"))
      .start();
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage.withProject(PROJECT_KEY,
          project -> project.withMainBranch("main",
            branch -> branch.withDependencyRisk(aServerDependencyRisk()
              .withKey(dependencyRiskKey)
              .withPackageName("com.example.vulnerable")
              .withPackageVersion("2.1.0")
              .withVulnerabilityId("CVE-1234")
              .withCvssScore("7.5")
              .withType(Type.VULNERABILITY)
              .withSeverity(Severity.HIGH)))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var dependencyRisks = listAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(dependencyRisks)
      .extracting(DependencyRiskDto::getId)
      .containsOnly(dependencyRiskKey));
  }

  @SonarLintTest
  void it_should_return_the_stored_fixed_dependency_risks(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY, project -> project.withBranch("main"))
      .start();
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage.withProject(PROJECT_KEY,
          project -> project.withMainBranch("main",
            branch -> branch.withDependencyRisk(aServerDependencyRisk()
              .withKey(dependencyRiskKey)
              .withStatus(Status.FIXED)))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var dependencyRisks = listAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(dependencyRisks)
      .extracting(DependencyRiskDto::getId, DependencyRiskDto::getStatus)
      .containsOnly(Tuple.tuple(dependencyRiskKey, DependencyRiskDto.Status.FIXED)));
  }

  @SonarLintTest
  void it_should_refresh_dependency_risks_when_requested(SonarLintTestHarness harness) {
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var server = harness.newFakeSonarQubeServer()
      .withProject(PROJECT_KEY,
        project -> project.withBranch("main",
          branch -> branch
            .withDependencyRisk(
              new ServerFixture.AbstractServerBuilder.ServerProjectBuilder.ServerDependencyRisk(dependencyRiskKey.toString(), "PROHIBITED_LICENSE",
                "HIGH", "MAINTAINABILITY", "OPEN", "com.example.vulnerable", "2.1.0",
                null, null, List.of("CONFIRM")))))
      .start();
    var backend = harness.newBackend()
      .withBackendCapability(SCA_SYNCHRONIZATION)
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withProject(PROJECT_KEY, project -> project.withMainBranch("main")))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var refreshedDependencyRisks = refreshAndListAllDependencyRisks(backend, CONFIG_SCOPE_ID);

    assertThat(refreshedDependencyRisks)
      .extracting(DependencyRiskDto::getId, DependencyRiskDto::getType, DependencyRiskDto::getSeverity, DependencyRiskDto::getQuality, DependencyRiskDto::getStatus,
        DependencyRiskDto::getTransitions, DependencyRiskDto::getPackageName, DependencyRiskDto::getPackageVersion)
      .containsExactly(
        tuple(dependencyRiskKey, DependencyRiskDto.Type.PROHIBITED_LICENSE, DependencyRiskDto.Severity.HIGH, DependencyRiskDto.SoftwareQuality.MAINTAINABILITY,
          DependencyRiskDto.Status.OPEN, List.of(DependencyRiskDto.Transition.CONFIRM), "com.example.vulnerable", "2.1.0"));
  }

  @SonarLintTest
  void it_should_notify_client_when_new_dependency_risks_are_added(SonarLintTestHarness harness) {
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var server = harness.newFakeSonarQubeServer()
      .withFeature("sca")
      .withProject(PROJECT_KEY,
        project -> project.withBranch("main",
          branch -> branch
            .withDependencyRisk(
              new ServerFixture.AbstractServerBuilder.ServerProjectBuilder.ServerDependencyRisk(dependencyRiskKey.toString(), "VULNERABILITY", "HIGH",
                "SECURITY", "OPEN", "com.example.vulnerable", "2.1.0",
                "CVE-1234", "7.5", List.of("CONFIRM")))))
      .start();
    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(FULL_SYNCHRONIZATION, SCA_SYNCHRONIZATION)
      .start(client);
    client.waitForSynchronization();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      var changes = client.getDependencyRiskChanges();
      assertThat(changes).hasSize(1);
      var change = changes.get(0);
      assertThat(change.getConfigurationScopeId()).isEqualTo(CONFIG_SCOPE_ID);
      assertThat(change.getClosedDependencyRiskIds()).isEmpty();
      assertThat(change.getAddedDependencyRisks())
        .hasSize(1)
        .first()
        .satisfies(dependencyRisk -> {
          assertThat(dependencyRisk.getId()).isEqualTo(dependencyRiskKey);
          assertThat(dependencyRisk.getPackageName()).isEqualTo("com.example.vulnerable");
          assertThat(dependencyRisk.getPackageVersion()).isEqualTo("2.1.0");
          assertThat(dependencyRisk.getVulnerabilityId()).isEqualTo("CVE-1234");
          assertThat(dependencyRisk.getCvssScore()).isEqualTo("7.5");
          assertThat(dependencyRisk.getType()).isEqualTo(DependencyRiskDto.Type.VULNERABILITY);
          assertThat(dependencyRisk.getSeverity()).isEqualTo(DependencyRiskDto.Severity.HIGH);
          assertThat(dependencyRisk.getQuality()).isEqualTo(DependencyRiskDto.SoftwareQuality.SECURITY);
        });
      assertThat(change.getUpdatedDependencyRisks()).isEmpty();
    });
  }

  @SonarLintTest
  void it_should_notify_client_when_dependency_risks_are_removed(SonarLintTestHarness harness) {
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var server = harness.newFakeSonarQubeServer()
      .withFeature("sca")
      .withProject(PROJECT_KEY, project -> project.withBranch("main"))
      .start();
    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage.withProject(PROJECT_KEY,
          project -> project.withMainBranch("main",
            branch -> branch.withDependencyRisk(aServerDependencyRisk()
              .withKey(dependencyRiskKey)
              .withPackageName("com.example.vulnerable")
              .withPackageVersion("2.1.0")
              .withVulnerabilityId("CVE-1234")
              .withCvssScore("7.5")
              .withType(Type.VULNERABILITY)
              .withSeverity(Severity.HIGH)))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(FULL_SYNCHRONIZATION, SCA_SYNCHRONIZATION)
      .start(client);
    client.waitForSynchronization();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      var changes = client.getDependencyRiskChanges();
      assertThat(changes)
        .extracting(DidChangeDependencyRisksParams::getConfigurationScopeId, DidChangeDependencyRisksParams::getClosedDependencyRiskIds, DidChangeDependencyRisksParams::getAddedDependencyRisks,
          DidChangeDependencyRisksParams::getUpdatedDependencyRisks)
        .containsExactly(tuple(CONFIG_SCOPE_ID, Set.of(dependencyRiskKey), List.of(), List.of()));
    });
  }

  @SonarLintTest
  void it_should_notify_client_when_dependency_risks_are_updated(SonarLintTestHarness harness) {
    var dependencyRiskKey = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var server = harness.newFakeSonarQubeServer()
      .withFeature("sca")
      .withProject(PROJECT_KEY,
        project -> project.withBranch("main",
          branch -> branch
            .withDependencyRisk(
              new ServerFixture.AbstractServerBuilder.ServerProjectBuilder.ServerDependencyRisk(dependencyRiskKey.toString(), "VULNERABILITY", "LOW",
                "RELIABILITY", "ACCEPT", "com.example.vulnerable", "2.1.0", "CVE-1234",
                "7.5", List.of("REOPEN")))))
      .start();
    var client = harness.newFakeClient().build();
    harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage.withProject(PROJECT_KEY,
          project -> project.withMainBranch("main",
            branch -> branch.withDependencyRisk(aServerDependencyRisk()
              .withKey(dependencyRiskKey)
              .withPackageName("com.example.vulnerable2")
              .withPackageVersion("0.1.2")
              .withType(Type.PROHIBITED_LICENSE)
              .withSeverity(Severity.HIGH)
              .withQuality(ServerDependencyRisk.SoftwareQuality.RELIABILITY)
              .withStatus(Status.OPEN)
              .withTransitions(List.of(ServerDependencyRisk.Transition.REOPEN))))))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(FULL_SYNCHRONIZATION, SCA_SYNCHRONIZATION)
      .start(client);
    client.waitForSynchronization();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
      var changes = client.getDependencyRiskChanges();
      assertThat(changes).hasSize(1);
      var change = changes.get(0);
      assertThat(change.getConfigurationScopeId()).isEqualTo(CONFIG_SCOPE_ID);
      assertThat(change.getClosedDependencyRiskIds()).isEmpty();
      assertThat(change.getAddedDependencyRisks()).isEmpty();
      assertThat(change.getUpdatedDependencyRisks())
        .extracting(DependencyRiskDto::getId, DependencyRiskDto::getType, DependencyRiskDto::getSeverity, DependencyRiskDto::getStatus,
          DependencyRiskDto::getTransitions, DependencyRiskDto::getPackageName, DependencyRiskDto::getPackageVersion,
          DependencyRiskDto::getVulnerabilityId, DependencyRiskDto::getCvssScore)
        .containsExactly(
          tuple(dependencyRiskKey, DependencyRiskDto.Type.VULNERABILITY, DependencyRiskDto.Severity.LOW, DependencyRiskDto.Status.ACCEPT, List.of(DependencyRiskDto.Transition.REOPEN), "com.example.vulnerable",
            "2.1.0", "CVE-1234", "7.5"));
    });
  }

  @SonarLintTest
  void it_should_check_for_supported_sca(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withServerVersion("2025.4"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isTrue();
    assertThat(response.getReason()).isNull();
  }

  @SonarLintTest
  void it_should_not_be_supported_if_old_version(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerFeature(Feature.SCA)
          .withServerVersion("2025.3"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isFalse();
    assertThat(response.getReason()).isEqualTo("The connected SonarQube Server version is lower than the minimum supported version 2025.4");
  }

  @SonarLintTest
  void it_should_not_be_supported_if_sca_disabled(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, server,
        storage -> storage
          .withServerVersion("2025.4"))
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .start();

    var response = checkSupported(backend, CONFIG_SCOPE_ID);

    assertThat(response.isSupported()).isFalse();
    assertThat(response.getReason()).isEqualTo("The connected SonarQube Server does not have Advanced Security enabled (requires Enterprise edition or higher)");
  }

  private List<DependencyRiskDto> listAllDependencyRisks(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getDependencyRiskService().listAll(new ListAllParams(configScopeId)).join().getDependencyRisks();
  }

  private List<DependencyRiskDto> refreshAndListAllDependencyRisks(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getDependencyRiskService().listAll(new ListAllParams(configScopeId, true)).join().getDependencyRisks();
  }

  private CheckDependencyRiskSupportedResponse checkSupported(SonarLintTestRpcServer backend, String configScopeId) {
    return backend.getDependencyRiskService().checkSupported(new CheckDependencyRiskSupportedParams(configScopeId)).join();
  }
}
