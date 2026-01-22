/*
ACR-728310ccb4704d43bb9ed682c2d58d92
ACR-ed205f03965f4b768759751d46d9cd60
ACR-cabeec1589c144cb8c59d49e1efc0952
ACR-ee5b844d252b42c0aaf403094eabddcb
ACR-b3033c9ce3f54246b5845fa3b6322e96
ACR-cefbc0e939c149b9934060e830eabf3a
ACR-c328fb0cee784ebcbccdd2a187773346
ACR-f2ce3471614644ba8eea4c965dab6cad
ACR-5a9b29cc4ea246b88855fe608bff9403
ACR-4645a249fb8d480db08b5a0833b828ae
ACR-02a362e0a2f04faa97d210373f5f96f1
ACR-9a414a60c2064faeab34a02ce8a941b5
ACR-f5b0030588004600bfadf22a7f1e7557
ACR-06666d970d4d48279c7d1975152f2c97
ACR-818230dcb1244ed084ab04f4c870b1c3
ACR-43928023c5694250ad9195e111e56784
ACR-61e874763ac741a1b8b71a3eb03824ad
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
