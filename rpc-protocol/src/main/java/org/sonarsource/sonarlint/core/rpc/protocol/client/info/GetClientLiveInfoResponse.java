/*
ACR-d43537daf01942a88bd7daae63649957
ACR-ac8b7a36fbbf469b8c9ccd4aaf5363c4
ACR-bd1e82e4f50a42d899c56606020704da
ACR-3a2f068c25e6422587acd3b845093672
ACR-35975dcad30b4398ae7387d33ce29141
ACR-5d616877878c41c295e550bfe3f21232
ACR-5a294251768d47249e8865a13a28fe64
ACR-ad08d077c1d84bdabf5562cdfbb8da28
ACR-3d45f0db0c894053ad8723a229db6043
ACR-4f1d4c0285b743b8ae628d9a57117aaf
ACR-1181f2f904394111ad349e0970646ead
ACR-3f3e6902d0e143a695493796f373c86e
ACR-f4d35fb88859454db5e9ef20377789b4
ACR-01e6f73390ce473c9997e32c19e50e09
ACR-053efef2491b4e809aab292fe07333e6
ACR-7b8b4e63eb7e4f34b43bdd98dd60da88
ACR-126d621e4bbf4ea1831d68303eb43e79
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.info;

public class GetClientLiveInfoResponse {

  private final String description;

  /*ACR-c25ea3ed1c944f1f885d1c3d0076ad0e
ACR-aa95b32c2cfc456c808887569919f40c
ACR-fda4748c895b489cad1b675c3ceaf92c
ACR-fbe267ee854246e680548b556121323c
   */
  public GetClientLiveInfoResponse(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
