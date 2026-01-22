/*
ACR-c48a00ccee754f518b9f94bd81424534
ACR-634947f86bd449208428cbc8a5d1afe2
ACR-a29990944b81442eb988eafb0d52ba0c
ACR-bb38f26e5967472fb0af12b7f5f3a64b
ACR-7df37b82f77e4922b3b2df4f06a23b87
ACR-e35b437eb41540299bac2c8ddfcae051
ACR-68ee5b1c0bd5431b8ce4a9ca7274c3d7
ACR-2565f543b7464d67b7f007e177d01ade
ACR-c9298308a3e942159e1ddb253f672a85
ACR-00dc9723468c4afd9726800afc9fc153
ACR-574780ed151b4dbcb1d5c17a78f7305e
ACR-466f01404b3f4a97b7524cb80ed831ce
ACR-35945fcb8bec4067b240c47856ba7f42
ACR-fb3b5de71d3e461484d32eec0bc08489
ACR-799df7b6fbad48eea5f7277152e1a2ca
ACR-89102da6dba64f83b8baaa00d8cfbc48
ACR-62d85f12fa324b208bea8bc50b2be129
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class OpenHotspotInBrowserParams {
  private final String configScopeId;
  private final String hotspotKey;

  public OpenHotspotInBrowserParams(String configScopeId, String hotspotKey) {
    this.configScopeId = configScopeId;
    this.hotspotKey = hotspotKey;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }
}
