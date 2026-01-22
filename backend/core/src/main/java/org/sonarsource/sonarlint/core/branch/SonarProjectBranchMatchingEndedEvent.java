/*
ACR-852c1e099c9144a498f9711795e01757
ACR-581b89fa38264c0e94084c07155d223f
ACR-42c13a1782e54efdaaae9b12ee233b8e
ACR-226b9d2055b140b0842765aece32c34f
ACR-3a9820fb79934cb7bdd0312447b65b33
ACR-a84d74c62e6a4ff78e2e2050d3ab0507
ACR-e9764ed0b76d4400b1f451d6964ff91a
ACR-36d9d2c91ecc4ee18651eb6b56c514f6
ACR-d3a7b0ff96344b0db2b55b9746540d14
ACR-e709742f49074a519932854672fbbe3e
ACR-67908359bc88409a83ebbf8b2a516798
ACR-10e47cd0d36647beaf9b000e8210e86f
ACR-66c9f5702110463b9111bb4db7f60a2d
ACR-60c3e77527c54dff8b9f9ff4482758c4
ACR-51dc8f43bfb94867adf5dc4b3f2513a0
ACR-23f682f9cd324757981a3522293df2ad
ACR-5ee6d83bb74e4c7295413ff231b736c7
 */
package org.sonarsource.sonarlint.core.branch;

public class SonarProjectBranchMatchingEndedEvent {

  private final String configurationScopeId;

  public SonarProjectBranchMatchingEndedEvent(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

}
