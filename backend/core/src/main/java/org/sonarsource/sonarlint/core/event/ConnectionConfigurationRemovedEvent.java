/*
ACR-90d07c4e00dd412e8a4a302d7041d25d
ACR-b6e4c75afb354f9dad2857ed773cd595
ACR-fb4e12f3416b4a709c5ded082e614c5d
ACR-763c4ffa0fa3448f82f5d0dbd958ec59
ACR-c3f535f946344ddcb65fc23224fbcc32
ACR-9fcf8d9004584df893ba0db29907b04d
ACR-7b2b7b8fa9d44daebcd453c69be042f8
ACR-528bebfcd11447d2af793fad490afdca
ACR-46cad417943c4c5f841eec02594a2413
ACR-27c43d824261439db3ebed4d74a1eaed
ACR-7a6af102c8e04456a9429cd668b1133c
ACR-d01b0a91c6984c689943cff5f7019634
ACR-81b83c71098a49f6b2403ee514588ad7
ACR-a1aa7fa6cc924de8b1194b011eab6a58
ACR-ad3fdfdd931f4b77a339685ea9ba9af7
ACR-3d06d39d7b1645258f2c889c801f6a12
ACR-78524130b10f40f88b9ac498cfab7bef
 */
package org.sonarsource.sonarlint.core.event;

public class ConnectionConfigurationRemovedEvent {

  private final String removedConnectionId;

  public ConnectionConfigurationRemovedEvent(String removedConnectionId) {
    this.removedConnectionId = removedConnectionId;
  }

  public String getRemovedConnectionId() {
    return removedConnectionId;
  }
}
