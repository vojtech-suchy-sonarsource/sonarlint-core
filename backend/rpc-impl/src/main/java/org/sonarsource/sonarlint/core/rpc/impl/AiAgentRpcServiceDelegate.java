/*
ACR-a415b33e4b414562903e447e1934e4ba
ACR-6ca9cf03b00e47faa201f2fefa51357c
ACR-dad08b2597134303b47fee7e70ff4525
ACR-e5b5b580d9ac4237b292d8c1fdfeafbb
ACR-c7cb10e3b2bc426ebf682ed088680e56
ACR-dcbe6cdfd9ec435fa0eb66d79da6be1f
ACR-4ff11fc239f247b2973ae803c85e5603
ACR-2bf92e2b7f6f46c58a40f1ff5c6c93cc
ACR-6b5b345b911948ebabd54412e3282e11
ACR-001799015db342bc84884464a6d8bd7c
ACR-5be52953a6314fe888cb20168e9ca4e1
ACR-7bf3fb8260494edba68a79371aa90155
ACR-f636fe52cab9440fbf6125357ec156a1
ACR-a40a5dd2bbe24c37972e842443785030
ACR-5b1cfa2a3fa14c04a24673112dac4008
ACR-36893e4007be46bfb063148c5e533c82
ACR-2b987934fa3d42508f3a105bf671bccf
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.ai.ide.AiAgentService;
import org.sonarsource.sonarlint.core.ai.ide.AiHookService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.AiAgentRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetHookScriptContentParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetHookScriptContentResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetRuleFileContentParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.ai.GetRuleFileContentResponse;

public class AiAgentRpcServiceDelegate extends AbstractRpcServiceDelegate implements AiAgentRpcService {
  public AiAgentRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public CompletableFuture<GetRuleFileContentResponse> getRuleFileContent(GetRuleFileContentParams params) {
    return requestAsync(cancelMonitor -> getBean(AiAgentService.class).getRuleFileContent(params.getAiAgent()));
  }

  @Override
  public CompletableFuture<GetHookScriptContentResponse> getHookScriptContent(GetHookScriptContentParams params) {
    return requestAsync(cancelMonitor -> getBean(AiHookService.class).getHookScriptContent(params.getAiAgent()));
  }
}
