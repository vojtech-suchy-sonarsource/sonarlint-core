/*
ACR-da9d502f7e844bf9aa3f4717ad8eb2fe
ACR-cfe71eaa535945f9a226e01cca93e67f
ACR-bacaea56830d42bf843a635adb097c41
ACR-18277b00b9cc4bacbc11f88282ace631
ACR-5c6ee3dc29f04f9184e821d62c24fd6c
ACR-6232fb83b9b54c0fa6b619a906a68793
ACR-c291e05c9b624387886513923988f91c
ACR-6f8ef0065ad2424ea3062dfd4d055caa
ACR-ea6f249a1a8a49809421a6e2146450e1
ACR-fff6aba6f506443f82b8abf300b72890
ACR-127dfce642c846d1a1efe0cfbee4e86e
ACR-c3faa0b952634f8eafa0b1882422ba45
ACR-7144daabd6c44262a5a9d539ccc9a724
ACR-70ad59d05f2748619ecc58b302c75fea
ACR-60935d79b48649b4b54309dd7be0e27c
ACR-e8f078034f954a4e8e6dac9c8ea10e02
ACR-b0be439fafb1453480d57d1e814beb82
 */
package mediumtest.synchronization;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.GetMatchedSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.GetNewCodeDefinitionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture.FakeSonarLintRpcClient.ProgressReport;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture.FakeSonarLintRpcClient.ProgressStep;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.waitAtMost;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.FULL_SYNCHRONIZATION;

class BranchSpecificSynchronizationMediumTests {

  @SonarLintTest
  void it_should_automatically_synchronize_bound_projects_that_have_an_active_branch(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("9.9")
      .withProject("projectKey",
        project -> project.withBranch("main",
          branch -> branch.withIssue("key", "ruleKey", "msg", "author", "file/path", "REVIEWED", "SAFE", Instant.now(), new TextRange(1, 0, 3, 4))
            .withSourceFile("projectKey:file/path", sourceFile -> sourceFile.withCode("source\ncode\nfile"))))
      .start();

    var client = harness.newFakeClient().withMatchedBranch("configScopeId", "branchName").build();

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    waitAtMost(3, SECONDS)
      .untilAsserted(() -> assertThat(backend.getNewCodeService().getNewCodeDefinition(new GetNewCodeDefinitionParams("configScopeId"))).succeedsWithin(1, MINUTES));
  }

  @SonarLintTest
  void it_should_honor_binding_inheritance(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer("9.9")
      .withProject("projectKey",
        project -> project
          .withBranch("branchNameParent",
            branch -> branch.withIssue("keyParent", "ruleKey", "msg", "author", "file/path", "REVIEWED", "SAFE", Instant.now(), new TextRange(1, 0, 3, 4))
              .withSourceFile("projectKey:file/path", sourceFile -> sourceFile.withCode("source\ncode\nfile")))
          .withBranch("branchNameChild",
            branch -> branch.withIssue("keyChild", "ruleKey", "msg", "author", "file/path", "REVIEWED", "SAFE", Instant.now(), new TextRange(1, 0, 3, 4))
              .withSourceFile("projectKey:file/path", sourceFile -> sourceFile.withCode("source\ncode\nfile"))))
      .start();

    var client = harness.newFakeClient()
      .withMatchedBranch("parentScope", "branchNameParent")
      .withMatchedBranch("childScope", "branchNameChild")
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(client);

    backend.getConfigurationService().didAddConfigurationScopes(
      new DidAddConfigurationScopesParams(List.of(
        new ConfigurationScopeDto("parentScope", null, true, "Parent", new BindingConfigurationDto("connectionId", "projectKey", true)),
        new ConfigurationScopeDto("childScope", "parentScope", true, "Child", new BindingConfigurationDto(null, null, true)))));

    waitAtMost(3, SECONDS).untilAsserted(() -> {
      assertThat(client.getLogs()).extracting(LogParams::getMessage).contains(
        "Matching Sonar project branch",
        "Matched Sonar project branch for configuration scope 'parentScope' changed from 'null' to 'branchNameParent'",
        "Matching Sonar project branch",
        "Matched Sonar project branch for configuration scope 'childScope' changed from 'null' to 'branchNameChild'",
        "[SYNC] Synchronizing issues for project 'projectKey' on branch 'branchNameParent'",
        "[SYNC] Synchronizing issues for project 'projectKey' on branch 'branchNameChild'");
    });

    assertThat(backend.getSonarProjectBranchService().getMatchedSonarProjectBranch(new GetMatchedSonarProjectBranchParams("parentScope")))
      .succeedsWithin(1, MINUTES)
      .matches(response -> "branchNameParent".equals(response.getMatchedSonarProjectBranch()));
    assertThat(backend.getSonarProjectBranchService().getMatchedSonarProjectBranch(new GetMatchedSonarProjectBranchParams("childScope")))
      .succeedsWithin(1, MINUTES)
      .matches(response -> "branchNameChild".equals(response.getMatchedSonarProjectBranch()));
  }

  @SonarLintTest
  void it_should_report_progress_to_the_client_when_synchronizing(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();
    var server = harness.newFakeSonarQubeServer("9.9")
      .withProject("projectKey")
      .withProject("projectKey2")
      .start();
    harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBoundConfigScope("configScopeId2", "connectionId", "projectKey2")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(fakeClient);

    fakeClient.waitForSynchronization();

    waitAtMost(3, SECONDS).untilAsserted(() -> assertThat(fakeClient.getProgressReportsByTaskId())
      .hasKeySatisfying(isUUID())
      .containsValue(new ProgressReport(null, "Synchronizing projects...", null, false, false,
        List.of(
          new ProgressStep("Synchronizing with 'connectionId'...", 0),
          new ProgressStep("Synchronizing project 'projectKey'...", 0),
          new ProgressStep("Synchronizing project 'projectKey2'...", 50)),
        true)));
  }

  @SonarLintTest
  void it_should_not_report_progress_to_the_client_when_synchronizing_if_client_rejects_progress(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();
    doThrow(new UnsupportedOperationException("Failed to start progress"))
      .when(fakeClient)
      .startProgress(any());

    var server = harness.newFakeSonarQubeServer("9.9")
      .withProject("projectKey")
      .withProject("projectKey2")
      .start();
    harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBoundConfigScope("configScopeId2", "connectionId", "projectKey2")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(fakeClient);
    fakeClient.waitForSynchronization();

    waitAtMost(3, SECONDS).untilAsserted(() -> {
      assertThat(fakeClient.getSynchronizedConfigScopeIds()).contains("configScopeId");
      assertThat(fakeClient.getProgressReportsByTaskId()).isEmpty();
    });
  }

  @SonarLintTest
  void it_should_skip_second_consecutive_synchronization_for_the_same_server_project(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();
    var server = harness.newFakeSonarQubeServer("9.9")
      .withProject("projectKey")
      .withProject("projectKey2")
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server)
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(FULL_SYNCHRONIZATION)
      .start(fakeClient);
    fakeClient.waitForSynchronization();
    reset(fakeClient);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto("configScope2", null, true, "Child1", new BindingConfigurationDto("connectionId", "projectKey", true)))));

    verify(fakeClient, after(2000).times(0)).didSynchronizeConfigurationScopes(any());
  }

  private static Condition<String> isUUID() {
    return new Condition<>() {
      public boolean matches(String value) {
        try {
          UUID.fromString(value);
          return true;
        } catch (Exception e) {
          return false;
        }
      }
    };
  }
}
