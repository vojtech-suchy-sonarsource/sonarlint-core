/*
ACR-f97770a560d14d5585fbf127defc25fc
ACR-db3e3178679641e6ba383ce29ee486b6
ACR-53337f331e414f189d92475e6cb01875
ACR-c6537a16b08342a4a026a5743efdd5b8
ACR-302b7dbb7ad14e978b57f9fcca4cd46e
ACR-55be702cc8304f9eb5c8fa2894dd9855
ACR-52a22060df1145a3bb1a51ef3d75064e
ACR-345bda2816f943ae8db0272c4f661e94
ACR-0effa7238cdc439cb0df33cac922ea0f
ACR-cb35da74946541b7b1a04d8c35df31d6
ACR-7bae9600eaac4a27aca86914ad977d72
ACR-9ddc2bb8578a4502bc206b3f139af9de
ACR-c0f3f00361464e47a9c14b41b035f26e
ACR-97199263f1234ccbb7da072dbb16de33
ACR-e49252b1248b46b7b731613fab511f1b
ACR-1efe6460f38946aa9af738e6c0ac3fcf
ACR-ee8b2833a2a04854bf2fef0ccc50a987
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
