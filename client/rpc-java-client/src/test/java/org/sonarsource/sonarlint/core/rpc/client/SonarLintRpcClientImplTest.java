/*
ACR-723a8eee3695428db64cc260de02d329
ACR-2805d842cf804b20a9429829cc93b70b
ACR-fb6435015aea4f749ddae00075f474b2
ACR-1355398198ff4084b167a3fae8114e1d
ACR-b4d942a75a194f2e94b10c15f91761c6
ACR-2a569519b0234a6da9ce039480f1d489
ACR-fd64724af715493d8800bde2c493187b
ACR-4d44f448dc4e4db2ae35e2fc349f5945
ACR-b842847c7ae246969c98bc73e532f918
ACR-4e70455efd934a5f9dd45d2491b1f057
ACR-39059efa1ff74ecca050ba8d2d296e66
ACR-f5d780d8f35440f1a41037c917052aa9
ACR-059fd03bf98142db8ff21ff5d852a976
ACR-920816ad201549edae244d5aaa741f1d
ACR-cf9247b0f54a44de87417e3740453585
ACR-52b648bb0c11464eadf8abaab9ab214b
ACR-fb338a22d98b4dfb9a5d31541442a3d1
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SonarLintRpcClientImplTest {
  @Test
  void it_should_print_notification_handling_errors_to_the_client_logs() {
    var fakeClientDelegate = mock(SonarLintRpcClientDelegate.class);
    var argumentCaptor = ArgumentCaptor.forClass(LogParams.class);

    var rpcClient = new SonarLintRpcClientImpl(fakeClientDelegate, Runnable::run, Runnable::run);

    rpcClient.notify(() -> {
      throw new IllegalStateException("Kaboom");
    });

    verify(fakeClientDelegate).log(argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues())
      .anySatisfy(logParam -> {
        assertThat(logParam.getMessage()).contains("Error when handling a notification");
        assertThat(logParam.getStackTrace()).contains("java.lang.IllegalStateException: Kaboom");
      });
  }

  @Test
  void it_should_match_project_branch() throws ExecutionException, InterruptedException {
    var fakeClientDelegate = mock(SonarLintRpcClientDelegate.class);
    var rpcClient = new SonarLintRpcClientImpl(fakeClientDelegate, Runnable::run, Runnable::run);
    var params = new MatchProjectBranchParams("configScopeId", "branch");

    var response = rpcClient.matchProjectBranch(params);

    assertThat(params.getConfigurationScopeId()).isEqualTo("configScopeId");
    assertThat(params.getServerBranchToMatch()).isEqualTo("branch");
    assertThat(response.get().isBranchMatched()).isTrue();
  }
}
