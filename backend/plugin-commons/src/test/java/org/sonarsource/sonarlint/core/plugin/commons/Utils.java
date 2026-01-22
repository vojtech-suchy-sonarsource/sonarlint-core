/*
ACR-afb6d3f0a2c249e1b96a4f7229b2f87b
ACR-fa2671f963e94598aedd06cc29b1a997
ACR-23283291a81d4232a8d9290fc580af01
ACR-f7edff4828be4dbab3e68d47c5afd2c9
ACR-d1a326bd12cb4bd3a81d83ad135f7fea
ACR-cedcb5e1947c4cf7a0f1f10d33af973d
ACR-6fda883717594ed5b1c201dfa24424ec
ACR-b4501f7fe6504c8fbd41b11317cd22f1
ACR-82df0b2a88b346ecb8416e5027641f4b
ACR-ba9836ed622b49ed8c689a17a3d9a96e
ACR-485e94f3a0254010a6154a01c580044d
ACR-27c9055894094c669a44262b5c0eaea0
ACR-f6a8a77d2961449a81949224b53f92e9
ACR-7aae431dad0b4bae859add48e0e9928c
ACR-ae22667ac5204348a417569d303795e9
ACR-aae364a26e28486d8ad03ccf5975ded4
ACR-d81fb59c628d45fd8951e7252dd4f00b
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import static org.apache.commons.lang3.RandomStringUtils.insecure;

public class Utils {
  private Utils() {
    //ACR-b7b12500cc174c37a31f324ab888ef49
  }

  public static String randomAlphanumeric(int count) {
    return insecure().nextAlphanumeric(count);
  }
}
