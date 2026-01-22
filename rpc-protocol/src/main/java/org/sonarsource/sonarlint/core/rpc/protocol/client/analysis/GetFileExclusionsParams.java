/*
ACR-13fccbca23294043870166179b194b9e
ACR-dcba687d137f4b59bdcdd6882d611b71
ACR-0e26186ed00e4041bdaa888d0aff35ad
ACR-750dc0b710484b51b32d492e456121cb
ACR-1074b37ad1ef4ec388e5278a706fb010
ACR-426367257d89460bb1d8cd45de0a065e
ACR-9f23aed7e49e401b83b04826c3d50e02
ACR-1304ccd1bf0547b1a692da6b803dea84
ACR-58ea8b526d3c45f9986ae59062748bba
ACR-b0bf7662e3534878a94e9c8fbe1b8652
ACR-b2ce81ff1854498f871e0ff30acb6105
ACR-48166ef560e24e4cb79b714d974e2840
ACR-bfda08c6f9904913a4691cbdbbb57a48
ACR-7b35b3e87cdb4268979cc35ee986abfe
ACR-e896961ef0c546be9fb1098ecdd19cdf
ACR-5a0c2efe43c94e7382370625d8932e9a
ACR-db62648f0eee4cba8cf3875ec7ba8d29
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

public class GetFileExclusionsParams {
  private final String configurationScopeId;

  public GetFileExclusionsParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
