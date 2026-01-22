/*
ACR-b7b5eec510fd4fe89d82198a01a26ceb
ACR-66e444b3965f493bb188e1612074b4cb
ACR-1082641b2a6042a2a585af2f453be6c9
ACR-2464a7ca688d4b03a1cbfa429e68e41d
ACR-66ed4383205841f19cfc0faf2693e507
ACR-9ce34fe6e2854330a460356e906e0377
ACR-d78285fc2bb045859bdde325dc39fb2f
ACR-526787956912492f9d1714c1b4726408
ACR-e8d073115ea5413a9a60e24bce9343f5
ACR-7c01c95c305146ffa6f45965e1d0f4f9
ACR-bcad6c25ff8d4fd9a10d0d3d6e2ce523
ACR-7f04882bae36457dad423727102a2f58
ACR-4fe3b186fcbe4cb8a63c30d233b14c36
ACR-771f2600777345bcba8c20d5965f6bb7
ACR-84cb24321600456981f64e6ae1341af1
ACR-62e68bf3ecbb4fbf9dba448f69568181
ACR-9af1075b45704531bb2051ce6faab9b1
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
