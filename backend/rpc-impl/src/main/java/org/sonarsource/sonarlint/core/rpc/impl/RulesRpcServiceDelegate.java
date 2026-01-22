/*
ACR-0e69f4c5a5544e4595f601d1474469a3
ACR-5230e3c1decc4782bdd04163397e6999
ACR-6aa1085e617f4d939eebf284f88d7ac0
ACR-b86699b3f18a42c197e8c561d8a93c64
ACR-cdb36036aaf04314a78de04ef9bf558d
ACR-2af0e56f888d4fa3880fb250c3cbacad
ACR-b2eb4079098b49b39fdd89b16949a26d
ACR-6734a300e3354ac88fe9b8074b3bdbf9
ACR-250c0b2e1a3a44fd91b2981de3d247fc
ACR-b260178771d046a79c4a213cd5024a71
ACR-bc731c9906034cc39ad25515f9a306e6
ACR-34402e789b5b4e31a274292cc011d176
ACR-a2b3ce63433c4a65b4467fb28b041735
ACR-bbc117ee57b048449dc462bf6913c660
ACR-5ff30e7fb9f34b98a56ed66bfa4fb7c5
ACR-ecd95dcd42204d30a13e89b0d414514c
ACR-a2b82c2037a54c70874f3da49e456553
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
