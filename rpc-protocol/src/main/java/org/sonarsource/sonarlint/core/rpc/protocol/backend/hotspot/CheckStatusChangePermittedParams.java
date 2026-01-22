/*
ACR-4e58e34c66a44f0cbd3602ee3108ec5a
ACR-22630091c9064977b76c9969c43404e4
ACR-22948e0584b6460e9a3c8ff5ac5d17fa
ACR-0055e9694a61463391d80f8791a03f3f
ACR-f47c0d4a8b4e4e93ae98b5cc43731c6c
ACR-02dfd8fd48134d3590b8f90c5d404f56
ACR-93565952a2494f02bfe8e8b024d762f9
ACR-5df2c56ff87b4635b299d0a6d635f523
ACR-7eee73ab78194b8dbc2c1684590e9dec
ACR-f9bab94d678f426e81ad43d30aa01365
ACR-4a9ab78ad8604cd2a702e6a0f4e2ab89
ACR-21d0a46e7da246bf8b2128be4312b974
ACR-babbed1439134f438ea0b98dcf78208e
ACR-5d5ecefb7a034db99ff9c34c0c4a71ad
ACR-056106e41a604723bd99ea3cd4e9a431
ACR-75fbd0876e1f41aabf95fab4a11dae13
ACR-fb6c62a005c34f4fbefb7e094523cdd6
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
