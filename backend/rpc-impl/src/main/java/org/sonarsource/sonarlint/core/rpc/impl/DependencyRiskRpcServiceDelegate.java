/*
ACR-86f0e57acd2a40639cddeed9ec5b7224
ACR-63f8f40e315241539cad3d5b2222316e
ACR-58cc6481eb1249cc9c8081da4f146b13
ACR-b2f366e9d43f46efaa9b54dbf4d18b0b
ACR-d9b258f82ba34086a6a28257d5c02725
ACR-a73e492efb7145b5916f77ac0eb9b764
ACR-707b3c22651f4ced8064eb3307d4a50b
ACR-3d100315ce2948338f03e29af78a023f
ACR-1a2cf97ce5964cc2978c9bd51552085d
ACR-11d9b48093d6447d89560aaf2811a7bd
ACR-eb0c70d01eb445baadc4883221bf216a
ACR-c8e887445323425faadec9ec3f01ef9c
ACR-d5ea75a2cb844848bcb599347abc72e3
ACR-0aa2a7a1394e4bfb8f18b73b56acb7f1
ACR-3b2c71e54e7a4628902f795d9a84bcbc
ACR-0f0ba8e871fb4832b84f150c5f6e425b
ACR-fc46ca72923146859a7c5941a0a0cfba
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.ChangeDependencyRiskStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.CheckDependencyRiskSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.DependencyRiskRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.ListAllDependencyRisksResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.sca.OpenDependencyRiskInBrowserParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.ListAllParams;
import org.sonarsource.sonarlint.core.sca.DependencyRiskService;

public class DependencyRiskRpcServiceDelegate extends AbstractRpcServiceDelegate implements DependencyRiskRpcService {

  public DependencyRiskRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<ListAllDependencyRisksResponse> listAll(ListAllParams params) {
    return requestAsync(cancelMonitor -> new ListAllDependencyRisksResponse(getBean(DependencyRiskService.class)
      .listAll(params.getConfigurationScopeId(), params.shouldRefresh(), cancelMonitor)));
  }

  @Override
  public CompletableFuture<Void> changeStatus(ChangeDependencyRiskStatusParams params) {
    return runAsync(cancelMonitor -> {
      try {
        getBean(DependencyRiskService.class).changeStatus(
          params.getConfigurationScopeId(),
          params.getDependencyRiskKey(),
          params.getTransition(),
          params.getComment(),
          cancelMonitor);
      } catch (DependencyRiskService.DependencyRiskNotFoundException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.ISSUE_NOT_FOUND,
          "Dependency Risk with key " + e.getKey() + " was not found", e.getKey());
        throw new ResponseErrorException(error);
      } catch (IllegalArgumentException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.INVALID_ARGUMENT, e.getMessage(), null);
        throw new ResponseErrorException(error);
      }
    }, params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<Void> openDependencyRiskInBrowser(OpenDependencyRiskInBrowserParams params) {
    return runAsync(cancelMonitor -> {
      try {
        getBean(DependencyRiskService.class).openDependencyRiskInBrowser(
          params.getConfigScopeId(),
          params.getDependencyRiskKey());
      } catch (IllegalArgumentException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.INVALID_ARGUMENT, e.getMessage(), null);
        throw new ResponseErrorException(error);
      }
    }, params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<CheckDependencyRiskSupportedResponse> checkSupported(CheckDependencyRiskSupportedParams params) {
    return requestAsync(cancelMonitor ->
      getBean(DependencyRiskService.class).checkSupported(params.getConfigurationScopeId()), params.getConfigurationScopeId());
  }

}
