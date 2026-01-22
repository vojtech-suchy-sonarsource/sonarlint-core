/*
ACR-d1aa39209b064b81898f1f116338f0a6
ACR-25f939895bec466bbdf431f42edcef43
ACR-72ee993b9e8a40c991089d2b8ea81798
ACR-241b8127aec14b0d8f49e720f48afd45
ACR-983b9bc89c7d498293898b337357c1bc
ACR-000585c8f30d467281ab0baf7aec099a
ACR-1808102603af4b59bb447e21c3045126
ACR-969e59e71ad64c30896bad88ee0f3d80
ACR-403ee1ff39f3470c9b2ef24c9750deec
ACR-5360549976924529a3632cf86f54b3bd
ACR-c725ee206bac4a94ac7c31724470a437
ACR-c79cb3c4b7504db689b7f2feb1b22b3a
ACR-755e7a52eaa0415bb4e2551762fad12f
ACR-911dbc1be1164e83b925e3348b7418ea
ACR-f0ec6f31c9484982be898e50982210f1
ACR-9d839b0a6ac542bea2c0fd88f71d4cee
ACR-005dfc400e64475a943dd3b9cec4d056
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import org.sonarsource.sonarlint.core.commons.progress.TaskManager;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.CancelTaskParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.TaskProgressRpcService;

public class TaskProgressRpcServiceDelegate extends AbstractRpcServiceDelegate implements TaskProgressRpcService {

  public TaskProgressRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public void cancelTask(CancelTaskParams params) {
    notify(() -> getBean(TaskManager.class).cancel(params.getTaskId()));
  }

}
