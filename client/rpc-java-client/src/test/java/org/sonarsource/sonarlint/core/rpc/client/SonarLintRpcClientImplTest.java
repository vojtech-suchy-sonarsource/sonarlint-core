/*
ACR-23d76c1559254213899c7a413b1cc4a3
ACR-64b49d315cb34de7b665c8cc57038123
ACR-880a0d505f6d4664afee1cebae728d59
ACR-4f28dda7bef34472b4a3de54eaf15a23
ACR-59e373b48e8a41d8ba5fa3105999c651
ACR-443bce48705a40bc99dd34f6364f3e52
ACR-4e6eda5111c44dac9cbfe772929f1ab6
ACR-b33474d28d45404fb234eec70d73b977
ACR-428c527f1c8647bbadcc6b132fad03d9
ACR-e29a5288abcf4b999630f6bda5205b57
ACR-611789bca9254b5d964aa51dd5f101ee
ACR-fd025e9a8aea404183236852c7cfef5c
ACR-67e7f1cbdbef4f0da4a44c63927d710d
ACR-44d2292abc9d442ea7b5518811566aeb
ACR-3e6f7e67c93e495c8a9a9e9605280d23
ACR-60d447798f27446d8524d086bb69da8e
ACR-a67010e0cb4d4fdcb3033e222bab548c
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
