/*
ACR-4a4697e213e4482988c8e37c4972a803
ACR-65bc727487a3478b88effec1ea8f7182
ACR-5c3f1dc158e240b7934deeb0b45ed33e
ACR-48f7b6c050a442f6aba00dff2d1adf4f
ACR-06e430b558614c55bddd0999779f1ea8
ACR-3a9e49489d31463f8c228c0493a3b9f1
ACR-d8b5a1ad5f0e4e37bac2e03ab4199de0
ACR-1966d7ae8000401e9d311efbd37479c5
ACR-00cf41ac4f794255972b12ed43792062
ACR-bd76d487340f46fc81940a7aefb170b6
ACR-d2a1b8520e844324bcf8284bb2ad4e02
ACR-9be8c0bab00d4dc6a8b6156df91c2e7b
ACR-724b5d852f464b7e9107f10b3e4904bd
ACR-13590dea58654e9aa916d6909ec8ddc2
ACR-1bcdd1d7804044d9aadc757ca946b06f
ACR-a6156c1af5734e43a43216122bceb345
ACR-60c00c1bafb048f1b80b4cd1a3c67437
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.active.rules.ActiveRulesService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetStandaloneRuleDescriptionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetStandaloneRuleDescriptionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ListAllStandaloneRulesDefinitionsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RulesRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.UpdateStandaloneRulesConfigurationParams;
import org.sonarsource.sonarlint.core.rules.RuleNotFoundException;
import org.sonarsource.sonarlint.core.rules.RulesService;

class RulesRpcServiceDelegate extends AbstractRpcServiceDelegate implements RulesRpcService {

  public RulesRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetEffectiveRuleDetailsResponse> getEffectiveRuleDetails(GetEffectiveRuleDetailsParams params) {
    return requestAsync(cancelMonitor -> {
      try {
        return new GetEffectiveRuleDetailsResponse(
          getBean(ActiveRulesService.class).getEffectiveRuleDetails(params.getConfigurationScopeId(), params.getRuleKey(), params.getContextKey(), cancelMonitor));
      } catch (RuleNotFoundException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, e.getMessage(), e.getRuleKey());
        throw new ResponseErrorException(error);
      }
    }, params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<ListAllStandaloneRulesDefinitionsResponse> listAllStandaloneRulesDefinitions() {
    return requestAsync(cancelMonitor -> new ListAllStandaloneRulesDefinitionsResponse(getBean(RulesService.class).listAllStandaloneRulesDefinitions()));
  }

  @Override
  public CompletableFuture<GetStandaloneRuleDescriptionResponse> getStandaloneRuleDetails(GetStandaloneRuleDescriptionParams params) {
    return requestAsync(cancelMonitor -> getBean(ActiveRulesService.class).getStandaloneRuleDescription(params.getRuleKey()));
  }

  @Override
  public void updateStandaloneRulesConfiguration(UpdateStandaloneRulesConfigurationParams params) {
    notify(() -> getBean(ActiveRulesService.class).updateStandaloneRulesConfiguration(params.getRuleConfigByKey()));
  }
}
