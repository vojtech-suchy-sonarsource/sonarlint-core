/*
ACR-ce3b0ebe3a414b98bbe48b6110254e5c
ACR-fe543524b742491c83d69ab9bc7b85b4
ACR-1516989af8ed407aa74e4b3d2d25505d
ACR-c7c81a3a06e04ba3ac6f9ce168bef37a
ACR-434cf0b2f4f14f3cbe6a52a18860ead0
ACR-a3571aabfdd94ed7963fdecbe3121f0c
ACR-f96d2f94c4ef41c38ce6457005d4867c
ACR-0e239a3dc6dc4556b77cd22cab09085b
ACR-7f560b22fe404838997bae4c37cc7e0a
ACR-e606d203376e40f297f59cfd601f6562
ACR-cacf64d66d874e3ca19f82aec1a8ce70
ACR-1944f01427be43d997c98c811ee2ed84
ACR-43fd5492471c41ba926b34509074c9b3
ACR-8f88ca73c6934f1abc642d1b8f142bf7
ACR-399ab2f968924258866849f1a9b47b4e
ACR-fcc57b48e4184606a999a4221f5967fa
ACR-df1742d6320041bcad6bb7b3b787c65f
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
