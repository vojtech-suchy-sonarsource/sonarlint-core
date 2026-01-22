/*
ACR-5079291ec72a4adfa943142f11be8ad2
ACR-bff7afac4b8b4e1fb3055b7deeaa567a
ACR-563ca645dafb4a849ec8e3f4841c9295
ACR-4a70bec5102f4addae77bd5a39a52c18
ACR-67eb3c8b7b72411f9348b8f8a7e2dc9e
ACR-26e86329a5b14cd2b0ecc23c77cf05ba
ACR-aa35d833d7894d24b61fd23f181b4bd9
ACR-4ccfb56392804ce29b31b94708b9dc7f
ACR-1d2e2c9e1e324ffcb97f601d2de90ea5
ACR-f17cc89de58a4c588020a776946f2849
ACR-4f417006d298405e8d04360fba7425f0
ACR-7b90a9dc8bf54430a5e5e3b6bf697fe5
ACR-8020406179c04ca4bf18c17501dc7042
ACR-70660603c2cb4df9b65d141deb36eb5f
ACR-a3968a07646d40ffb61ff342bb0af2cd
ACR-b7573fd4923140e48c50f86bc9cafdac
ACR-0d93d35ee7134183a7ecc31a55a0d1a0
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

    //ACR-a4e77c134ae143dda7e1780569e41306
    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), any());

    //ACR-b536882c62e8460aab40c275ccb65a16
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

    //ACR-cda639d6155a4115ad3575393e772d65
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

    //ACR-2e961a4451ca48518231ad295f4585fc
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

    //ACR-c2aaedb3d77d459d9af7a601d26907dd
    verify(client, timeout(5000)).didChangeMatchedSonarProjectBranch(eq("configScopeId"), eq("main"));
    verify(client, timeout(5000)).didSynchronizeConfigurationScopes(Set.of("configScopeId"));

    //ACR-a4ba9c671a754d508b0ffda7f63dd9ba
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

    //ACR-b147bb7939804018996e40e4124c12ac
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
