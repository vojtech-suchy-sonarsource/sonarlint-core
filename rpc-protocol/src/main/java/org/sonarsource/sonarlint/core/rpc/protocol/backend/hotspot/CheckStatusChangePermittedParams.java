/*
ACR-2edbba71ac944f2b8610b1d3393eb5b1
ACR-42c2cb3b6f1847f9b2645ef7d7ebde31
ACR-1c66ea29eebc48e387acb51136447033
ACR-76d8c2f32d754df7b6c1381b0bd41fa6
ACR-94ef4a84fec342fda4d1ac03ef9f0f33
ACR-500b7b758131472ca1936a86a1e4864e
ACR-fab5d5d7d4b84edf9b4ede4dc1b3d7d5
ACR-154faafb4d2a413285809bb184273bea
ACR-93587222af91477894d0d620b8b067b4
ACR-860cc999456441e090780d627447cc5c
ACR-f5f56be0618447f9964664c797b2d75f
ACR-7e5a7177b6364120b7d033a55a7318f0
ACR-d46e4aa2038140fdafe6c04c3a64f730
ACR-8c085bf5a56245eeaa6a930b108dbc35
ACR-bf2f14552d344b86a3dee6b15e4a6aad
ACR-c006e282642246eda47efcb7c3e880e8
ACR-cd3789b5e6764b188283fd9719746b79
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class CheckStatusChangePermittedParams {
  private final String connectionId;
  private final String hotspotKey;

  public CheckStatusChangePermittedParams(String connectionId, String hotspotKey) {
    this.connectionId = connectionId;
    this.hotspotKey = hotspotKey;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }
}
