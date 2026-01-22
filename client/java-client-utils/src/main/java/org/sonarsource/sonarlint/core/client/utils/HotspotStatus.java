/*
ACR-c62d07ac70024e08aff722a0de6f8d24
ACR-8f52b38105574cbf984af0aa1899a959
ACR-eeaa1027c06145d4a542b53e4f279548
ACR-a6d7514c49474967adaa47184001e6b0
ACR-94e5ec1f2dec4e2598b91eb05c50ca6f
ACR-7a900415a52a4e63b35a8a48a8d75d9e
ACR-fa8be16136224ef18b94529a0d2fd73b
ACR-2c451c86b85542c48b1997d57b015b03
ACR-32f968118b6e49bb8000015e0353d838
ACR-16260c3dee8a44d6bddaaf3c925d45f4
ACR-acc7486067024104810f0ab2dac9a327
ACR-396e9bac843c4d8182d49a6ba2193f1e
ACR-b5088ba407d644ffbd64144f8ff1a774
ACR-cd89427e3f154762a768d3e72c977680
ACR-19dcfbeb329743c6bf4ebb533851961e
ACR-8f2b8687e9e24288a6ea3d89faab6e21
ACR-25ff5e58fe104af29882fb0ae7063bbc
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum HotspotStatus {
  //ACR-f4c408a27f834404aa10b30ee67a3988
  TO_REVIEW("To Review", "This Security Hotspot needs to be reviewed to assess whether the code poses a risk."),
  ACKNOWLEDGED("Acknowledged", "The code has been reviewed and does pose a risk. A fix is required."),
  FIXED("Fixed", "The code has been modified to follow recommended secure coding practices."),
  SAFE("Safe", "The code has been reviewed and does not pose a risk. It does not need to be modified.");

  private final String title;
  private final String description;

  HotspotStatus(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public static HotspotStatus fromDto(org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus rpcEnum) {
    switch (rpcEnum) {
      case TO_REVIEW:
        return TO_REVIEW;
      case ACKNOWLEDGED:
        return ACKNOWLEDGED;
      case FIXED:
        return FIXED;
      case SAFE:
        return SAFE;
      default:
        throw new IllegalArgumentException("Unknown status: " + rpcEnum);
    }
  }
}
