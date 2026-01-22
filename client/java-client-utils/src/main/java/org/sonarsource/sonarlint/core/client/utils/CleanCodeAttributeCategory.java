/*
ACR-557c3cb3de31435b8a2e60b1af6863b6
ACR-61713854903f4f3eaf1b03b10666ffe5
ACR-0920e569e5e24ca7b6438d7f7e2c15c1
ACR-e127ed35914b4cc9aa3efbaea3753029
ACR-5dd24485ad47448785397418423130ff
ACR-00472b2f8a8c44c5913e443d536e2292
ACR-d2c0eb371a9b45aaa3e7a66ebfd2b316
ACR-7e800ce2b4bb4f558a74bbfdd0df1cdd
ACR-d32318ac21304a8290f1b7f171a250c9
ACR-726f90f0d8d54066a9447d625a7b9f11
ACR-8c34bfc9cc0d41e9b929f26790d8dbc8
ACR-9659f5ff8340499385177f11723832ca
ACR-a35aa5a7fb1448bf98383e566c0ce590
ACR-25e4e57c13ce44768c61e4dc275bed0a
ACR-de728a5926a64bee9f51b6b4dbaa0640
ACR-3a4f2de122e94028b137bd4ef6a066bc
ACR-0a74a055fa4a4b80a231d782e208d6cb
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum CleanCodeAttributeCategory {
  ADAPTABLE("Adaptability"),
  CONSISTENT("Consistency"),
  INTENTIONAL("Intentionality"),
  RESPONSIBLE("Responsibility");

  private final String label;

  CleanCodeAttributeCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static CleanCodeAttributeCategory fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttributeCategory rpcEnum) {
    switch (rpcEnum) {
      case ADAPTABLE:
        return ADAPTABLE;
      case CONSISTENT:
        return CONSISTENT;
      case INTENTIONAL:
        return INTENTIONAL;
      case RESPONSIBLE:
        return RESPONSIBLE;
      default:
        throw new IllegalArgumentException("Unknown category: " + rpcEnum);
    }
  }
}
