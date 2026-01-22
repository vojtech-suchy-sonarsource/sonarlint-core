/*
ACR-c3f50b796f1b46a4ab9dd78b06d5473a
ACR-e2cce92fd2614ac6b771394fbe8b85b6
ACR-7eefd6e540ff4e9ab379a7c8ad845afd
ACR-2b43567a963b45cd959c9241d07499d6
ACR-7e2044e428384ed2bd5feec44c670324
ACR-f6c59b60c03c49e9b4c744828b956297
ACR-f6bcdfa388fc4f8893dc1f73a76dec15
ACR-7e1f15085e6c41929bba8604c7891abf
ACR-5824d4d3f4844fca9cd9c3788cb00cf7
ACR-7ddae0b47fbb4958b2c7e56dc944438b
ACR-1bc3d1b0f5f2426cbd1dcadc72de0911
ACR-95a0bba1a850419c88288d42c1dbe96a
ACR-a08c72dd265f425e9a1119a5e174046e
ACR-d860276850414e1291e2fc5b0b34856e
ACR-9a30cd7767a64968818ae13015169cbd
ACR-7452772c9a2d4fc792c6efaca78647c2
ACR-7e60bca3ed0b4ade988a7ff5136baa45
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.sync;

public class InvalidTokenParams {

  String connectionId;

  public InvalidTokenParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
