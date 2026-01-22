/*
ACR-6a3b9f5bba454161be37de41397764cf
ACR-e5c494c259f54b68a404684412b47174
ACR-8ea87e37788541b28cca4eaf8f434e76
ACR-18b1633be3a84b44afe64e382e2448bd
ACR-37af118fb2624d23a37b6c7c77cdf505
ACR-9edacf9ff92844b0a41a2a29a0f7a882
ACR-35342e4b18b24daab3946f73fd3d9bea
ACR-ec15aaa541734da880b9d95a223ae0fd
ACR-36939a328fa0494fb48525659d985125
ACR-218d2b6d8e8a4b55971a976b1f44770a
ACR-ce1b2492518c4d8b80f3b7a59df9817c
ACR-ae2c5e11f3214c93ba9a8ea978289fbb
ACR-b383e2e550da4ff0a3215c3cf22096b4
ACR-b86e1497ff8245a69a43e92c1539c027
ACR-983a057e1fe24881a1d98c2547684707
ACR-790ed58754e54da5a3ff272329959bb6
ACR-61211a3a7d5e42a58ab7e130a9241b0d
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
