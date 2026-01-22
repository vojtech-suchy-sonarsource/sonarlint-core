/*
ACR-01277c3ce9de4a509485d2b43e5c0074
ACR-657975e68ccc494fa952f264503f0182
ACR-03bc318187fb45118d7d2c9419694343
ACR-e93b406c253e480dbac03c2d9490e1a0
ACR-c275678412ca41278e800420c284d1fa
ACR-c15853cf217e4b469d8a0c8b5b03368d
ACR-00b75297bfdf41e3971627ae8b67de13
ACR-a802b5f265b34315a059d5598f5543aa
ACR-c289e0ed5a5f41ea9d212623a5197e58
ACR-2f246a78a67449bfa79cdfab05b41727
ACR-a6a617d7e0094027ae6da68302c7cea2
ACR-81b92b301e5f4ac2bf34efe7021b76d0
ACR-a1858ec94aaf45aba019ddc8e0978ef7
ACR-1154801217654cb89282f4694abd85b3
ACR-e6f16ed8ed8a4bbda3bc29b649973565
ACR-99fcd3cb4e4a41ec93ff1c4a8c178ae7
ACR-f04b7ff09b9245e3a1a8c2f872f9c293
 */
package org.sonarsource.sonarlint.core.embedded.server;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.apache.hc.core5.http.protocol.HttpContext;

public class AttributeUtils {

  public static final String PARAMS_ATTRIBUTE = "params";
  public static final String ORIGIN_ATTRIBUTE = "origin";

  private AttributeUtils() { }

  /*ACR-315939adfaff4569aa1878189ea1155a
ACR-15cf38e2eeb843eea706a43d23001061
ACR-100db95b32d94c29bd11de096fa25314
   */
  public static Map<String, String> getParams(HttpContext context) {
    return Optional.of(context)
      .map(c -> (Map<String, String>) c.getAttribute(PARAMS_ATTRIBUTE))
      .orElse(Collections.emptyMap());
  }

  /*ACR-6460778dbea54459a9d055f341a8191c
ACR-0355c5fba0214ba58bb53fd5c3891a45
ACR-d5085a4129e24158a3c7e82b1792e51e
   */
  public static String getOrigin(HttpContext context) {
    return (String) context.getAttribute(ORIGIN_ATTRIBUTE);
  }

}
