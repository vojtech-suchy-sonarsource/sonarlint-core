/*
ACR-896e928d52ca4600b907d2503a4f1ee4
ACR-6d1f60c167924d7b8084d0a720d4c62a
ACR-175e637c7b67442c83a0dfe7ae7be666
ACR-6da178e3408a4ebda27dad382053a525
ACR-ce6d78a4a04c45119db6381cd67d3280
ACR-b883183589e74cc186f96f04640d5bae
ACR-94b48e879b7b49c4bdfebdd3877c54e6
ACR-dc2afba293fd402a8734b86fa62ff622
ACR-4c5a95d5d97d4cc0a0746192e5a3d64e
ACR-f5b80fc2a2544ec787d4c6263e80e638
ACR-2bcbc5138c56458ba105f86eb49a84a8
ACR-fb020358d367474e8e103dd346ce12b8
ACR-b4dfbc30ef174b0e8415b9840bbb1260
ACR-1a8bbd51338f47cebbb1417ba8b8f00d
ACR-bd43f8e869874111b4cccc5fc62f35d4
ACR-f4e8fbd3d87749fa988e2e9cd51b45e4
ACR-82db34db7c90471b8babb743200a759d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class CheckAnticipatedStatusChangeSupportedParams {
  private final String configScopeId;

  public CheckAnticipatedStatusChangeSupportedParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
