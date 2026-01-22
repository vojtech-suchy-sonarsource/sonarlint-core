/*
ACR-6931b6e41c6946e081b7c8b9868abb6d
ACR-06e7abdabb03430faa9f2d541a97d10d
ACR-bd688aed2d6d4cbbba528d34047fa7d6
ACR-a4770557f3f34d30860d5800bc647db4
ACR-4c3cecf3b3184a69b42dfc2b75620441
ACR-026d78ec98134addbf948e352c3523ab
ACR-3954a53ed48841b196fea003f098c3a9
ACR-e9a82858287744bfa356004876f686de
ACR-9106247396224286a4a370628d14be1f
ACR-6c8efa535fc349b0ab22a2f02c7c3e94
ACR-59f90b1b7bab4a11869d0063dcbfc97d
ACR-d556a488a0334167abc3a0d195b71559
ACR-d03aaa526be34928a2330a163254beb3
ACR-ac013704b30d4e638d7a4f1f86984474
ACR-af56266e71144c41b507c61deffd1c90
ACR-575e75f1a9e947ccbbbd817b46117452
ACR-0550630e1f524206a672ad736ab41a58
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum HotspotStatus {
  //ACR-fa21d9f952ac4b2da3a35b4829636712
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
