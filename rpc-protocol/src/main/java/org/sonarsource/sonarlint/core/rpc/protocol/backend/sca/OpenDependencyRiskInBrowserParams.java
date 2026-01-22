/*
ACR-02c4fe81bcc84f4993f2f5a5ab1c4ab8
ACR-6862f80be966473ca2e1f90621f3c98f
ACR-5729194779fd4f1f8eed9836de43a7c3
ACR-ff3480d2a87b4062b3dcaa520d27869d
ACR-852de7e83a6b4b72a785c63e33d7646e
ACR-0935ca4a535b476c84d12b91d887754f
ACR-611e27a77df74387bfc1ab94cff2c5bb
ACR-1bb50c26d35245e495deff1b04d54b57
ACR-558a548ffa0143f09b750bd99ae325e7
ACR-e78f5aae2bc04f0d9e48a1e76b949afc
ACR-7cc1407a0b48470eaaec09a03f2da1e1
ACR-dd9de528e90d4198b94ccb8e2e012ed4
ACR-d96386ff9ca747d6b874e718daedfc45
ACR-326ddf4109804752be2a5d34a83ede1a
ACR-407bb79049c7429eaa4949a42b2ba72a
ACR-3e5115e203954b40a52359a915b31324
ACR-46620a07d8854d899deb5d9540940f0d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import java.util.UUID;

public class OpenDependencyRiskInBrowserParams {
  private final String configScopeId;
  private final UUID dependencyRiskKey;

  public OpenDependencyRiskInBrowserParams(String configScopeId, UUID dependencyRiskKey) {
    this.configScopeId = configScopeId;
    this.dependencyRiskKey = dependencyRiskKey;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public UUID getDependencyRiskKey() {
    return dependencyRiskKey;
  }
}
