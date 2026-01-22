/*
ACR-671d3d197ec440898c46c781a182ff89
ACR-674350afb776485ca8c4d73c3f05d45e
ACR-d9774cdf2895489b99a1e294decb788f
ACR-e0a83834cfd34d1ba3498e4d79f7a45b
ACR-2ffdfc775a814540838e064ec15aa52c
ACR-8a2984c0a1644361930eb61e6fdfee00
ACR-c27fec4476484ac8a912a395fa6651c0
ACR-915524313b2949feb798f841714f5203
ACR-ab789c783e3f40dfb13c71ebae1409b1
ACR-8cf795267a1247abbec7404d43863331
ACR-27978009bc7b4f108a9a0eb77d8d4d4a
ACR-ff24ce88129a4860a936277cf193fd24
ACR-ae3d7791fd0f42439563cfc6da66ef69
ACR-a7314d80007b4d52811aff4223d5cb21
ACR-95b8bd831a9e4d6e8734bccdeac4e39a
ACR-3b9726c87daa4d118106e07735169a4e
ACR-569cd8718a0842b085ff282cf287f2b9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidRemoveConfigurationScopeParams;

/*ACR-88943cc56a654104b0107e1bb3fc8c72
ACR-12637376a2594cb6807d94a2e96ccf95
 */
@JsonSegment("configuration")
public interface ConfigurationRpcService {

  /*ACR-66d56b7096674ba5a237ce6fea3b2dea
ACR-77c013fef70b4d4e82109e20cb47ad36
   */
  @JsonNotification
  void didAddConfigurationScopes(DidAddConfigurationScopesParams params);

  /*ACR-552ed2c4df194f2b9756fec12d1c33d3
ACR-60450817ee834c01b32e39f2c41cf6fc
   */
  @JsonNotification
  void didRemoveConfigurationScope(DidRemoveConfigurationScopeParams params);


  /*ACR-ba4b896ee7ed4b6bb94088525890ffe6
ACR-a21d9fa337b249fe8b191b0aba24b98b
   */
  @JsonNotification
  void didUpdateBinding(DidUpdateBindingParams params);
}
