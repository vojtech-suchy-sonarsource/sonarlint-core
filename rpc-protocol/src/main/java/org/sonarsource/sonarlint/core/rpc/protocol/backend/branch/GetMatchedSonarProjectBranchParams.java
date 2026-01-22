/*
ACR-86a14931d3b7482791b5dc9c653efeaf
ACR-8f1b6f08b9e74975856dcd2a7dfee985
ACR-9b853ff21bca427b9d96d55c4670ad4c
ACR-e47a944f3e0f43e78eb51329a7e7da17
ACR-a912fd12ecab4085a57163841c060edc
ACR-ac069dc408df4c8597f27db2e77045cd
ACR-70dfc36ec96548cba58da45922d17b65
ACR-c844c1e557f54b749b16193cc75ead90
ACR-75dbb860f0684b78a1bed67283377676
ACR-fc59e07593f84cfeb033f53c73c56a5d
ACR-f9fc26e9e8ad408ba5b4485a109152d1
ACR-b46a25d5bd1940cda98a4188e9fbbaff
ACR-9ed4289e80d54f35b9a1cb0c9b6ddfda
ACR-dd43088e9af04be499ad1e28e84b1bd3
ACR-d538f20b25444234b55f1b92bbf62508
ACR-8f50a09171144945b4e7573e7662097d
ACR-536e09df82c949878a6b81e3e9cbc933
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

public class GetMatchedSonarProjectBranchParams {
  private final String configurationScopeId;

  public GetMatchedSonarProjectBranchParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
