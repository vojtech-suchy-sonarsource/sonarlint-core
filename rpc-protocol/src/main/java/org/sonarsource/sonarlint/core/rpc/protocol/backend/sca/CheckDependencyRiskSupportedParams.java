/*
ACR-4bdef5cdf9744ccea97430a73ad44cfa
ACR-a2a86dd0874f4a9a932b247f828463e8
ACR-69b95f9ef3064ad997197eb82d5395f8
ACR-21b5296364b941cdbbf0170bf684921c
ACR-69557a8fb8e04133aae93d900eaab05b
ACR-626ee8cf716a489397914a07d3f9e13c
ACR-d9a93c039d304634a77d77b70ba1338f
ACR-34053099048f4b3e9e3b710e37b66285
ACR-772ee01dfea547698b4cec929dc63ec5
ACR-3d7a7190e0ae4cabacbfe34975c2c6f3
ACR-a48037062713492ca3a13003b45de807
ACR-6a0b8b25607b402e87bb3f7ac3cf440a
ACR-c08944411835456399c5d6893c5d7433
ACR-fc781687f6494ae186e8474110c374b8
ACR-209ac4b132644847a29200e6be0c5537
ACR-0cc33c01848947bb8a71be8850f1ce13
ACR-020a7be870764c5183bf8a0dac02d910
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

public class CheckDependencyRiskSupportedParams {

  private final String configurationScopeId;

  public CheckDependencyRiskSupportedParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

}
