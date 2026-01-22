/*
ACR-e5ed1beda68848969d0cd4d0a3bc4859
ACR-0d7447c2546e40ce8221a679036646fe
ACR-1d12e8101ce048a08ff7d70fde2d3c58
ACR-54f6a5563008470ca7e126d3de8e5ddf
ACR-9e26c23fde864911babe357a69ca0cbd
ACR-4f9969082ac34e45a3c6d3fd20d60a7a
ACR-64b10d32b568459f9b21d4a0e0d4dec1
ACR-d4026426e4274f5cbd843c7487416435
ACR-1b7dcc2c7c8c4a129fb3db7c97a0cd82
ACR-fab8f7317bef4761a9647651581d1ee4
ACR-af28cfddf40248d88dbb5e4f92ebc1a6
ACR-428c536f95b342978e17486093f4744e
ACR-12b6ba39d5e64890a1c9aacfb0ac754f
ACR-4b475b352d214edf8dbc1acc1f9b391a
ACR-2ffe86aad9c241389e4585f7fb6568f6
ACR-8db9b6dda8a941608776252e013a0bdd
ACR-23ec81e4a18649b2a515e3ac554127b5
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
