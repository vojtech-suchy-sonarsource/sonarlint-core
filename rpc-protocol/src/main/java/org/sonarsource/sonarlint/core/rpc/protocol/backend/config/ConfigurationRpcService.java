/*
ACR-c94291ff03c24eeb835a5fcad65ad220
ACR-d65c762e61c341ec8ff835fd6f7e2632
ACR-a8cfc93b034747fe8733ec6c8274f8b0
ACR-bdd7d5f234614bdba25259b4cdfad45d
ACR-84bf3e92683b4bf6a4e6f8c47bac957c
ACR-f0fadb74e3bc42cda8b3ccda66f0e752
ACR-42644d66363745959abf26979e1e46cc
ACR-1fa30122a0504327ad8b52b6904b831c
ACR-f57a302c3421442bae4b80a325ded757
ACR-f09a47b9d1c741e79b930358adc7a314
ACR-9ce190446074437b902de447d18a1a65
ACR-75744fa1928b4bd5af11aa941f99eadb
ACR-94e979a84bac455194f317e96eb02f1e
ACR-e3ab8e4606a84f8fa18446a18c0c97b9
ACR-5fe8b1bbc0694ffeae55acba9b8f98ed
ACR-d46b583c736149ffad9841a768ea0e23
ACR-214da8a5fb0f4133a86e0ef8f6fdca9f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidRemoveConfigurationScopeParams;

/*ACR-203180239893423985f97e460793ea85
ACR-a224cacc6006429997a5db5cb51520a5
 */
@JsonSegment("configuration")
public interface ConfigurationRpcService {

  /*ACR-3a4d6dd706d0464aa28d95a3709d0de3
ACR-f11a1e8370394509ab7f5aa1308908af
   */
  @JsonNotification
  void didAddConfigurationScopes(DidAddConfigurationScopesParams params);

  /*ACR-69cf3f1350604191943da9bf47c08954
ACR-f19b0415d95f4d4b941e984ef12ab030
   */
  @JsonNotification
  void didRemoveConfigurationScope(DidRemoveConfigurationScopeParams params);


  /*ACR-92bafab6464b40309cd63b8668ea8170
ACR-30486fbfacd243ca8b80a3730b0e3dfb
   */
  @JsonNotification
  void didUpdateBinding(DidUpdateBindingParams params);
}
