/*
ACR-69b409a08d9247b0a220c2abaaa1ef9f
ACR-b605a6d7b7a142ba89b3c036403dac16
ACR-719cdeea2c49458087f4c8517d357048
ACR-2e96393d51334a6b96b67c56481f5c7d
ACR-a8f956e1b9a543e78b78fe687854d67d
ACR-6bcf30d759ee4e378b6d9a221500c007
ACR-95876cbe3ba7413d8dd0be325b2c702a
ACR-5ea95e00c98b410fb147e84db107d78a
ACR-f4276193aee447f3a38b6f34a1695844
ACR-c25a041b89414b6ba27422197f8c7b65
ACR-61598319af3d4bcd822ce081d9939a8a
ACR-39b39931d4b34926bb043337d419fe7d
ACR-d0433dd4a804478dbaada052818bd625
ACR-6a2920aa9e5946e6ab4cbcb4533e1c82
ACR-55ba31249a6243549b00faada8114b35
ACR-29dcea5767194a98a23bd4afa694dc18
ACR-b74068d625eb41788378dc440961a87e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode;

public class GetNewCodeDefinitionParams {

  String configScopeId;

  public GetNewCodeDefinitionParams(String configurationScopeId) {
    this.configScopeId = configurationScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
