/*
ACR-f7271056c29c43ddb6bdda6f912bf917
ACR-52bdd77fdd4a4b3a828c65bcc0d3730f
ACR-605bbc3fb86b4172aec4f743c0e8a6db
ACR-6bf2de61205c44cb9485d4ba7644006d
ACR-888362bd8e634fcb923b884de039a084
ACR-dcf63f4a86b94307b6ba890b16d33ffc
ACR-b6715322182e4d32bfc63b36b2a5bbbf
ACR-82d6e35469ee484e9ccecb8e20f8449b
ACR-ac6ef24c84ac4c70bb758d6dc60e9f97
ACR-0a103fd060e142ab94c4f56571a9dcbe
ACR-ac91169cdf3f467aa1ec06f935defe17
ACR-8edd4136f8b14f3691614f2e3bcd6d56
ACR-bf259ab3badd4b81aa168fc629eb3e95
ACR-f4fe050c170042fa9dfcddd6dd0e320a
ACR-8d263ac3b59d464ea6eede7763f8c04b
ACR-45dcd1c3c6bb4f93913cb640f8c290a9
ACR-0e8ac510a8d847c6b071d3fecad5c1a0
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
