/*
ACR-c447834ddd0f43e6a5659bffd732ba46
ACR-cc347f5988e145ddae5792ed60cb9415
ACR-87d7fa175d7f48ea995eab5836758133
ACR-9bbd7fa0dc9f4676bc71d24086665e77
ACR-a850b89ed8044526bb4b017b97fdf37d
ACR-037c54ea18514396902648dade215fe0
ACR-40d52661a28e4ff1b711b623e4cf9947
ACR-f8eac1992def48d3b43a77514ac5504b
ACR-898cf6aac8724f5dbbd79b10297277b3
ACR-3a5a58a926b84122b67e955a203c645d
ACR-fb7fc4efbc7d41ba8fc0b1c05b2aaf77
ACR-9641adfefd6b4822a8d69bcae2b45abe
ACR-96b7ddd94c1a4d05913096607476fd44
ACR-b27ecb29bfcd405e923592c1f7b1dec8
ACR-5985ae332e3a48b3abdd36ad11b97648
ACR-816d281b885d4c5aace4c8779caa541b
ACR-01ddc3565da04a1986386b1cc69a728d
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.DidVcsRepositoryChangeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.GetMatchedSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.GetMatchedSonarProjectBranchResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.branch.SonarProjectBranchRpcService;

class SonarProjectBranchRpcServiceDelegate extends AbstractRpcServiceDelegate implements SonarProjectBranchRpcService {

  public SonarProjectBranchRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public void didVcsRepositoryChange(DidVcsRepositoryChangeParams params) {
    notify(() -> getBean(SonarProjectBranchTrackingService.class).didVcsRepositoryChange(params.getConfigurationScopeId()));
  }

  @Override
  public CompletableFuture<GetMatchedSonarProjectBranchResponse> getMatchedSonarProjectBranch(GetMatchedSonarProjectBranchParams params) {
    return requestAsync(
      cancelMonitor -> new GetMatchedSonarProjectBranchResponse(
        getBean(SonarProjectBranchTrackingService.class).awaitEffectiveSonarProjectBranch(params.getConfigurationScopeId()).orElse(null)));
  }
}
