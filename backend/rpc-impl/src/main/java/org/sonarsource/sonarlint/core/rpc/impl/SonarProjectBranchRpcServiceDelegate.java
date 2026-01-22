/*
ACR-cd7450a0a3d84f549392d13364d8314b
ACR-1885fb3e641c4b27916f5d59abd78c4e
ACR-baac76abcfb8419e9db3fca0d79a64c0
ACR-8a9a706835f74809a3865643c3f7a356
ACR-62f6fcb6d1c340f3855f9654e5478bab
ACR-38ec58bd31ea45fc9ca008ddd7cb8172
ACR-3f0261ede4ab4ac1b4cccdd68244d108
ACR-4ec05584a8c745d0882b653221db976e
ACR-0e3fbefd103645c9ac1ae47e069f233e
ACR-9e97b28c51be4d67a31fc3af67cebd51
ACR-afc3773ea3164225bb56ab6d1aa1ef60
ACR-bb04565bab1543f39272477a89ec1636
ACR-1bf698cf15724d42a382a120e70dcb77
ACR-9fa8b1af6a824185be46906e5ca9251e
ACR-01eee6b4921d432e9bfb91395a2f9852
ACR-8bad9919f91247c3a27dbcbb5fb9ebc8
ACR-fc58e3aafa774615afb2f4e76552c076
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
