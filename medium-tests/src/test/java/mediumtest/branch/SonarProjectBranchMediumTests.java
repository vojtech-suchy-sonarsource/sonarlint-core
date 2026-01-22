/*
ACR-b07cd088c5ba4d4c8249b79e939165e6
ACR-26bc224c7c424f10b8aab867aae84116
ACR-c356b8c8096040c9ae487a19965c58fa
ACR-82f1b2a3759c4bc794300d495b9d26c5
ACR-22a6bc0aaba9428da7bb8e22f91d7f4d
ACR-6de68497cf1e43719a497de35ae33e2d
ACR-9188ec471d864a6fb4da82f5cd2342e2
ACR-ed08a02a2a9447b3bcc8e641ebf2b423
ACR-8e36b34335cb4c3ca18a4d1cd2092225
ACR-37273cb80d9d4c0da8c5232635f06e26
ACR-376a77c9245e46099f9f71f7a24e0c8d
ACR-9b5fe6fcb3b542ea91e3696bc9d85a8b
ACR-20a42d61135240afbbaa08c35a15368f
ACR-dc2cb4b064e44db98e4fbdfcfe4ed153
ACR-8e8b651d44be403ea3a93ca5b9542123
ACR-18d0d23baace4d86ae9e7c3192eb4316
ACR-552033b6e61e45f18f554a7bfbd30a2d
 */
package mediumtest.branch;

import java.time.Duration;
import java.util.Set;
import org.sonarsource.sonarlint.core.rpc.client.ConfigScopeNotFoundException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.DidVcsRepositoryChangeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.GetMatchedSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.GetMatchedSonarProjectBranchResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.PROJECT_SYNCHRONIZATION;

class SonarProjectBranchMediumTests {

  @SonarLintTest
  void it_should_not_request_client_to_match_branch_when_vcs_repo_change_occurs_on_unbound_project(SonarLintTestHarness harness) throws InterruptedException {
    var client = harness.newFakeClient().build();

    var backend = harness.newBackend()
      .withUnboundConfigScope("configScopeId")
      .start(client);

    notifyVcsRepositoryChanged(backend, "configScopeId");

    Thread.sleep(200);
    verify(client, never()).matchSonarProjectBranch(any(), any(), any(), any());
    verify(client, never()).didChangeMatchedSonarProjectBranch(any(), any());
  }

  @SonarLintTest
  void it_should_request_client_to_match_branch_when_vcs_repo_change_occurs_on_bound_project(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    when(client.matchSonarProjectBranch(eq("configScopeId"), eq("main"), eq(Set.of("main", "myBranch")), any())).thenReturn("myBranch");

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId",
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    notifyVcsRepositoryChanged(backend, "configScopeId");

    verify(client, timeout(2000)).didChangeMatchedSonarProjectBranch("configScopeId", "myBranch");
  }

  @SonarLintTest
  void it_should_not_notify_client_if_matched_branch_did_not_change(SonarLintTestHarness harness) throws InterruptedException {
    var client = harness.newFakeClient()
      .build();
    when(client.matchSonarProjectBranch(eq("configScopeId"), eq("main"), eq(Set.of("main", "myBranch")), any())).thenReturn("myBranch");

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId",
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    //ACR-14dd7d4cb0b24f47a84403d54c470b06
    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), any());

    //ACR-4df424b28f1448cf8a186d02f43375b5
    notifyVcsRepositoryChanged(backend, "configScopeId");

    verify(client, timeout(1000).times(2)).matchSonarProjectBranch(eq("configScopeId"), eq("main"), eq(Set.of("main", "myBranch")), any());
    Thread.sleep(200);
    verify(client, times(1)).didChangeMatchedSonarProjectBranch(any(), any());
  }

  @SonarLintTest
  void it_should_default_to_the_main_branch_if_client_unable_to_match_branch(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    when(client.matchSonarProjectBranch(any(), any(), any(), any())).thenReturn(null);

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId",
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    notifyVcsRepositoryChanged(backend, "configScopeId");

    verify(client, timeout(2000)).didChangeMatchedSonarProjectBranch("configScopeId", "main");
  }

  @SonarLintTest
  void it_should_not_match_any_branch_if_there_is_none_in_the_storage(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId")
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    notifyVcsRepositoryChanged(backend, "configScopeId");

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Cannot match Sonar branch, storage is empty"));
    verify(client, never()).matchSonarProjectBranch(any(), any(), any(), any());
    verify(client, never()).didChangeMatchedSonarProjectBranch(any(), any());
  }

  @SonarLintTest
  void it_should_not_notify_client_when_error_occurs_during_client_branch_matching_and_default_to_main_branch(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    when(client.matchSonarProjectBranch(any(), any(), any(), any())).thenThrow(new ConfigScopeNotFoundException());
    harness.newBackend()
      .withSonarQubeConnection("connectionId",
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    verify(client, timeout(1000)).didChangeMatchedSonarProjectBranch("configScopeId", "main");
  }

  @SonarLintTest
  void verify_that_multiple_quick_branch_notifications_are_not_running_in_race_conditions(SonarLintTestHarness harness) {
    var client = harness.newFakeClient()
      .build();
    doReturn("branchA", "branchB").when(client).matchSonarProjectBranch(any(), any(), any(), any());
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId",
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    //ACR-41f58cafd0fe40eaad3e7b92bd657630
    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch("configScopeId", "branchA");

    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams("configScopeId"));
    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams("configScopeId"));
    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams("configScopeId"));
    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams("configScopeId"));

    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch("configScopeId", "branchB");
  }

  @SonarLintTest
  void it_should_return_matched_branch_after_matching(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();
    doReturn("main", "myBranch")
      .when(client).matchSonarProjectBranch(eq("configScopeId"), eq("main"), eq(Set.of("main", "myBranch")), any());

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", harness.newFakeSonarQubeServer().start(),
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    //ACR-2719bce367824cb1be8707695cea138f
    verify(client, timeout(1000)).didChangeMatchedSonarProjectBranch("configScopeId", "main");

    notifyVcsRepositoryChanged(backend, "configScopeId");

    verify(client, timeout(1000)).didChangeMatchedSonarProjectBranch("configScopeId", "myBranch");

    assertThat(backend.getSonarProjectBranchService().getMatchedSonarProjectBranch(new GetMatchedSonarProjectBranchParams("configScopeId")))
      .succeedsWithin(Duration.ofSeconds(1))
      .extracting(GetMatchedSonarProjectBranchResponse::getMatchedSonarProjectBranch)
      .isEqualTo("myBranch");
  }

  @SonarLintTest
  void it_should_trigger_branch_specific_synchronization_if_the_branch_changed(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("myBranch", branch -> branch.withIssue("issueKey")))
      .start();
    var client = harness.newFakeClient()
      .build();
    doReturn("main", "myBranch")
      .when(client)
      .matchSonarProjectBranch(eq("configScopeId"), eq("main"), eq(Set.of("main", "myBranch")), any());

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .withBackendCapability(PROJECT_SYNCHRONIZATION)
      .start(client);

    //ACR-984fbff7a2ca4ec9989df7dd0ca8f2f0
    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), eq("main"));
    verify(client, timeout(5000)).didSynchronizeConfigurationScopes(Set.of("configScopeId"));

    //ACR-f9ee2b0c20294a3696e2ea023e118c18
    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams("configScopeId"));

    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), eq("myBranch"));
    verify(client, timeout(5000).times(2)).didSynchronizeConfigurationScopes(Set.of("configScopeId"));
  }

  @SonarLintTest
  void it_should_clear_the_matched_branch_when_the_binding_changes(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey", project -> project.withBranch("myBranch", branch -> branch.withIssue("issueKey")))
      .start();
    var client = harness.newFakeClient().build();
    doReturn("myBranch")
      .when(client)
      .matchSonarProjectBranch(eq("configScopeId"), any(), any(), any());

    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server,
        storage -> storage.withProject("projectKey",
          project -> project.withMainBranch("main").withNonMainBranch("myBranch")))
      .withBoundConfigScope("configScopeId", "connectionId", "projectKey")
      .start(client);

    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), eq("myBranch"));

    //ACR-75f523f0b6a74a2dab3fa0ad0af5fdae
    bind(backend, "configScopeId", "connectionId", "projectKey2");

    assertThat(backend.getSonarProjectBranchService().getMatchedSonarProjectBranch(new GetMatchedSonarProjectBranchParams("configScopeId")))
      .succeedsWithin(Duration.ofSeconds(5))
      .extracting(GetMatchedSonarProjectBranchResponse::getMatchedSonarProjectBranch)
      .isNull();
  }

  @SonarLintTest
  void it_should_match_project_branch(SonarLintTestHarness harness) {
    var client = harness.newFakeClient().build();

    client.matchProjectBranch(any(), any(), any());

    verify(client).matchProjectBranch(any(), any(), any());
  }

  private void bind(SonarLintTestRpcServer backend, String configScopeId, String connectionId, String projectKey) {
    backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(configScopeId, new BindingConfigurationDto(connectionId, projectKey, true)));
  }

  private void notifyVcsRepositoryChanged(SonarLintTestRpcServer backend, String configScopeId) {
    backend.getSonarProjectBranchService().didVcsRepositoryChange(new DidVcsRepositoryChangeParams(configScopeId));
  }
}
