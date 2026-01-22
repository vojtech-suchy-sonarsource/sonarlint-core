/*
ACR-ce2474cf14b84d7aa2e5e11a5530e30b
ACR-5ecda585144c4d2bb037189032fa45a0
ACR-9c81f1895a3a4978a2c687552400222a
ACR-27cc9b0eef694ec897e530d09a23ca65
ACR-683c6fb495924d229ad444b77e180d01
ACR-97138d417c304625bc654dd4d8f758dd
ACR-8294d9e5444443dc98149b6645c43e59
ACR-9eb759754b6c4baab12d72775fc41a66
ACR-28df3c9f4c774871baea4f685eb9f548
ACR-4909312becfc415e86dca05c9775bb98
ACR-f6370a20f6714ab0a95a4d120aad55ab
ACR-8ed073b7ca5e4141b8b094d4017fce86
ACR-b3ba0f9ff5d349719a30e93ff6b2a882
ACR-546cc350647a4834ba846bd7c1bfcc18
ACR-606db030cb1c463da10f7af012422482
ACR-321a946a650f46fb84d4b782f45d91bf
ACR-2397762af5684785b5f43ef461368e19
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.BindingSuggestionProvider;
import org.sonarsource.sonarlint.core.SharedConnectedModeSettingsProvider;
import org.sonarsource.sonarlint.core.commons.SonarLintException;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.BindingRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetBindingSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetSharedConnectedModeConfigFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetSharedConnectedModeConfigFileResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.GetBindingSuggestionsResponse;

class BindingRpcServiceDelegate extends AbstractRpcServiceDelegate implements BindingRpcService {

  public BindingRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetBindingSuggestionsResponse> getBindingSuggestions(GetBindingSuggestionParams params) {
    return requestAsync(
      cancelMonitor -> new GetBindingSuggestionsResponse(
        getBean(BindingSuggestionProvider.class).getBindingSuggestions(params.getConfigScopeId(), params.getConnectionId(), cancelMonitor)),
      params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<GetSharedConnectedModeConfigFileResponse> getSharedConnectedModeConfigFileContents(GetSharedConnectedModeConfigFileParams params) {
    return requestAsync(cancelMonitor -> {
      try {
        return new GetSharedConnectedModeConfigFileResponse(
          getBean(SharedConnectedModeSettingsProvider.class).getSharedConnectedModeConfigFileContents(params.getConfigScopeId()));
      } catch (SonarLintException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_BOUND, e.getMessage(), params.getConfigScopeId());
        throw new ResponseErrorException(error);
      }
    }, params.getConfigScopeId());
  }
}
