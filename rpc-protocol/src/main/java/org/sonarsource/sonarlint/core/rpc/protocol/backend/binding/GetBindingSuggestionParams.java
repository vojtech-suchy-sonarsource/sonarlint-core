/*
ACR-2c71f05519b645d8af91c8e29082e0ac
ACR-26973941579c464e89a2927d90ac2984
ACR-a4bf165899da49e380f1466345a9fcd3
ACR-335268c706c24936a32e17a348f0b946
ACR-d2b58a4149c24cfd93ff794ea3630b6b
ACR-1adb88701eee4566a1c38927f4120e2f
ACR-f93813d6e9744d97a0dadc77dec3fa3a
ACR-b96c78a5434349f580cd5b9d533fcb83
ACR-bd2c7c7debd74b76bcba84bd3c7e02ab
ACR-a36fc1863f8e469c9a1ab54e43533d00
ACR-62a69e7de54b48fb8a7d42839e0c6bc9
ACR-88f7db80300b4504bb226c283b124af9
ACR-6bc49bd80b264a70995e616d2b61555e
ACR-b56c92de182345e98e953c00666d05e6
ACR-7f3d7b63efed47b7977ea2e7de09782d
ACR-4c47529c932941b4b03567ff13990d0b
ACR-8d29f7e58aca487a89c5c306eda10aa1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.binding;

public class GetBindingSuggestionParams {
  private final String configScopeId;
  private final String connectionId;

  public GetBindingSuggestionParams(String configScopeId, String connectionId) {
    this.configScopeId = configScopeId;
    this.connectionId = connectionId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
