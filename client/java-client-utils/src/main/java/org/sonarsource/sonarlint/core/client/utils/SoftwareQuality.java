/*
ACR-b39a88320764481e810303c8a4d98634
ACR-30149b4e564847338b6238d5c565583f
ACR-a843f3de873344c18494da356973bbd3
ACR-288bfc5f1158459893ff67bc99ae91b5
ACR-8123c284b9444d60a44e5702bb8e5ac7
ACR-ced568775d0f482d8235baea46e87d4e
ACR-7fc6cdb600c14da68e5f6bd83d1ee352
ACR-8c7518a7ced04cc8a51c98a6f2e94b51
ACR-2338e8863283421cb228c7457047d1ef
ACR-ad67fbedd0584173ad659be89deb2321
ACR-a5e8331d650345debfe58ce17624a7e7
ACR-7c5271c41c62441b877a8f407b97eb79
ACR-6cd2b856853b409f9baa073a0a62e5c6
ACR-18f3c15589724a7a8cf5da2442057c8b
ACR-50fce38f60894b358e94ec46b841c022
ACR-119dc3a52d1f49dd805599818c2e7ab6
ACR-7d7a4709453c44959ccf607fc6b402bd
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum SoftwareQuality {
  MAINTAINABILITY("Maintainability"),
  RELIABILITY("Reliability"),
  SECURITY("Security");

  private final String label;

  SoftwareQuality(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static SoftwareQuality fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality rpcEnum) {
    switch (rpcEnum) {
      case MAINTAINABILITY:
        return MAINTAINABILITY;
      case RELIABILITY:
        return RELIABILITY;
      case SECURITY:
        return SECURITY;
      default:
        throw new IllegalArgumentException("Unknown quality: " + rpcEnum);
    }
  }
}
