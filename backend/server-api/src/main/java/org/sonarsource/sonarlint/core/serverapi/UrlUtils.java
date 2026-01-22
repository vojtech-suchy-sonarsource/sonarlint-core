/*
ACR-d1b987a6f96e4a8d9c771740e0f80089
ACR-6f8704f677ad4ae4a0135bd76066d76f
ACR-0a526a5cd73d46ea95859c6e86bd046a
ACR-3a2e53cb187b46da83da7115d0e92b07
ACR-6aa0cee9aad84df49018842835562798
ACR-364d1b0016bd48618f858d7e503c8c0b
ACR-b9863e9eead54f9b9b3f87bc7467209c
ACR-2f9a555b63574f939c0e9eff2fd96670
ACR-ce1c144503f84f17b0c77a39612c5408
ACR-574ca3468d5b4be0b64411a77189b38b
ACR-9c88da015172455e837a03fa81b8688d
ACR-d8328b3e12c84e5a9e918a65fef98467
ACR-8bfbeb687cb743ff8ac84d85e6f40a8d
ACR-4c9a1e38f1c248bb8b0f351195611ec0
ACR-9823ddada517481bbd7501cd968fc693
ACR-5dc17c56b2274430a5e7642167275d12
ACR-bccfa2f701924876a94e0a59c1032e7e
 */
package org.sonarsource.sonarlint.core.serverapi;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlUtils {

  private UrlUtils() {
  }

  public static String urlEncode(String toEncode) {
    return URLEncoder.encode(toEncode, StandardCharsets.UTF_8);
  }

}
