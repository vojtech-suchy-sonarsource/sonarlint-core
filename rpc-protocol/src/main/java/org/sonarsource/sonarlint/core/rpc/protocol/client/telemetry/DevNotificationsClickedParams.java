/*
ACR-543ea2df3f9946e0ae9da026394903fc
ACR-5e2062c8442541ef8fb6f667917ca094
ACR-d074897fe6ea45f8ab54888299391b7c
ACR-7d04d30d77bb42829a0daed6ebb03f2f
ACR-cdcf7bb77f534b60abd1627172fd281e
ACR-12f21d05a7084b79974ac477f4c1ff8d
ACR-38dbb7afde9b4ef3a8cc9edbcdc1df48
ACR-643a38a6ed7047568b0f7e5494b91cdc
ACR-df8a35379a1b45118af4bd08ea883977
ACR-c5243e2369df40cfa013558115d0cd08
ACR-527ec6c5b4554c229e709d666004a232
ACR-a30918a5ab014c3a8e3392b3353965c2
ACR-68fe70037c384f448443773eb153db9d
ACR-e83c6df20ae14571805dab42114ddce5
ACR-009367e3b1b84f37b8f19546982517a3
ACR-142c90d7d79344b0aa38f001ccd216d4
ACR-f02e85ee709f449185799c9f7a0c01bc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class DevNotificationsClickedParams {
  private final String eventType;

  public DevNotificationsClickedParams(String eventType) {
    this.eventType = eventType;
  }

  public String getEventType() {
    return eventType;
  }
}
