/*
ACR-037483a4e61440b49a4c06b22f9f9978
ACR-9c82cac652c049a4b48139745b2e02e4
ACR-431da74e22164700b396a7e0ec3b2f5b
ACR-f9c3d2c82a854451827e1dd6e11f077e
ACR-42dac17c44b44244a7cec425e138ee4c
ACR-898092cd63134831a64b49218130acd8
ACR-97e732dae5cb45779321715c8a291318
ACR-2012501180b84b4b8909cae03895d37d
ACR-c60507b96a1547919cb7df9a49d5dd2d
ACR-9f7085b908204b7facb97088e9d08832
ACR-03569a47a06844be8f4be63a896cd78a
ACR-759893efdb6747569e3208d07f3f27dc
ACR-c72502247fcf4780812bdcd07b9bd4de
ACR-89b5231e136249b982e3eb5f589d1bd3
ACR-8ea5eac668bd42f3accf5cc9a8f7d01a
ACR-443e149b5beb4c9fbbda2a06d86f09b7
ACR-720d62058a7540b7a247f1ae3a6a90ac
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.remediation.aicodefix.AiCodeFixService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.AiCodeFixRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix.SuggestFixResponse;

public class AiCodeFixRpcServiceDelegate extends AbstractRpcServiceDelegate implements AiCodeFixRpcService {

  public AiCodeFixRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public CompletableFuture<SuggestFixResponse> suggestFix(SuggestFixParams params) {
    return requestAsync(cancelMonitor -> getBean(AiCodeFixService.class).suggestFix(params.getConfigurationScopeId(), params.getIssueId(), cancelMonitor));
  }
}
