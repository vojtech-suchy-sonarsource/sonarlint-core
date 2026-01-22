/*
ACR-220235cd02c14ee7be48c502f14fa66a
ACR-26ddab42e8a842d5ade50f7330151f73
ACR-292e1bd8f9514a648b19981719e19f9e
ACR-75590138a24c44c1b5ea814dfb845867
ACR-3c06ed6277f848619fea103115e72f5d
ACR-f11f4ca5eb5b4c38bbd006cf5f4d75e3
ACR-1348c26a12c54bbcb1a2947461860090
ACR-62d8da22727d429698c38e93f62abb67
ACR-109576cbf0e644ebba88ada2b323046e
ACR-79a5114c44b34b81beb8047d1aa20e13
ACR-3991cd05a8134f928ddb125196c6c43b
ACR-2f2e6bd20c584ec5b2975fbc304f7d72
ACR-bf81fe1fc2ed4be6be4f8275f407dad2
ACR-24edfe972917417489fc84bdd82abeb2
ACR-73c0b6eab1f24de895b482ddeda754aa
ACR-84591c21ddc04cf1b0af5e45534a9fe4
ACR-30a0fd807eb64134a71f561c8bae5f4a
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
