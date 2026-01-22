/*
ACR-765d11e82eb04a5fb58bbce8359b906e
ACR-b3ceb24f15984d3bb40b5b1277dab219
ACR-9d97d1e80f1c48f29d1c0aebc3c249be
ACR-1fad3264b30942dcb179a94b0777307a
ACR-aab887f24a1442bea85b6dd1c3620b41
ACR-ea82e04726984ffba8c204331ddc7013
ACR-786680cc23da4babb98fa90c288c6a4d
ACR-0cafe9613f4d420fa50520ee3a742dfa
ACR-73325f92d06d4c91a36d2d6188c0cc65
ACR-e4470d403f28475892bb025cbd053c63
ACR-8de35a78cd57415287d32c57daff11ca
ACR-9189db078a014d6e9f4e0dcb314dc966
ACR-39c9e38eaeea4fae97b6c87e56804c29
ACR-7018ea8e479b43e986e569dcc343e015
ACR-50978d8d65eb4623a81f37b35a384afd
ACR-171c36f3c48a43d2a66eba10dfa10f08
ACR-5bfc699b54674073b856373fe70c492d
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import org.sonarsource.sonarlint.core.ConfigurationService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.ConfigurationRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidRemoveConfigurationScopeParams;

class ConfigurationRpcServiceDelegate extends AbstractRpcServiceDelegate implements ConfigurationRpcService {

  public ConfigurationRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public void didAddConfigurationScopes(DidAddConfigurationScopesParams params) {
    notify(() -> getBean(ConfigurationService.class).didAddConfigurationScopes(params.getAddedScopes()));
  }

  @Override
  public void didRemoveConfigurationScope(DidRemoveConfigurationScopeParams params) {
    notify(() -> getBean(ConfigurationService.class).didRemoveConfigurationScope(params.getRemovedId()));
  }

  @Override
  public void didUpdateBinding(DidUpdateBindingParams params) {
    notify(() -> getBean(ConfigurationService.class).didUpdateBinding(params.getConfigScopeId(), params.getUpdatedBinding()
    ));
  }
}
