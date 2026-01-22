/*
ACR-ba1862b19162432486bbec99758d8a5c
ACR-e484fa5fbf414228ada6eda3c57c2774
ACR-afdd4e819a9c400f8568f64bf2e46840
ACR-d9afb4118d714f759518db6007db3907
ACR-2e51d0303f91473399921c01e05c64da
ACR-4beb45c8f111404d828e9a1adb9bdb99
ACR-a2a6bc8600b1479a8b1877426712885b
ACR-25cee0cb97b84954b558b2bc01743ae0
ACR-68183f65f53e4fc9a90c310a5de15758
ACR-fee8b1d1bd5b44808fe8a7a5598a96f9
ACR-00901132bede455db1a9bf8158bac3a2
ACR-5fd476e47e424cd0be25d248c1d6679b
ACR-bf7d0d6b83e94c7cbe80dd2822a33c5c
ACR-d5f46f7f9b2842d3bba11771ecbd3a0c
ACR-88f8f2d931844716a4453b25d31574fa
ACR-67c614bd890e4448bc1f773023bbd720
ACR-8902152ef3a248d580b260e3fab62e05
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.binding;

public class GetSharedConnectedModeConfigFileParams {
  private String configScopeId;

  public GetSharedConnectedModeConfigFileParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public void setConfigScopeId(String configScopeId) {
    this.configScopeId = configScopeId;
  }
}
