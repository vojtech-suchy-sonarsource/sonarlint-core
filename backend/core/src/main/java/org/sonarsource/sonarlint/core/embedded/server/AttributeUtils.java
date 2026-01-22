/*
ACR-eb432a230d6542f59338965b5c705e1c
ACR-66c431e659f3476991b93754fd87f0a9
ACR-d434fed45cd545d790bd2a957453ae3d
ACR-84e103f9b4134c5283a4957bd790daf2
ACR-c6bcaf06888f437d90a4cd4ce520af0f
ACR-44ae1cccae39433e998fae38ebd75ea5
ACR-aa145aa7b4914716b4508c6fd6d145e5
ACR-e0a9bba3833f44b38e260acbb7fe163f
ACR-7b6d921688484731877734551c122a0c
ACR-d7f70f778839458fa2f38d617c9b4c24
ACR-df7d6f4573024584af1161f0f59067a0
ACR-9b668737300941f0a9c27eafe02a2ad2
ACR-7239915b71f8445cb32c2c02b2171858
ACR-7a44ccff4ba3471f947aeb9f024e13af
ACR-d50acd0dd4804537b67773ade17b3d21
ACR-f85d055659604ee8a3e5aa98253e1edc
ACR-6f955bc8368447178e7331a3bf10e250
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

  /*ACR-5d67e7d0230d4b1fb044f74b5a065488
ACR-0f136a7b060f4c34b23fa90bcfe1a366
ACR-5895d213f7b341c3809242091dfcab8a
   */
  public static Map<String, String> getParams(HttpContext context) {
    return Optional.of(context)
      .map(c -> (Map<String, String>) c.getAttribute(PARAMS_ATTRIBUTE))
      .orElse(Collections.emptyMap());
  }

  /*ACR-0d0ba05078b44814862cacccb497775c
ACR-33dec368ff31414e886fe45ce0766423
ACR-a473dd4f4cbb4565bed02e7a76295347
   */
  public static String getOrigin(HttpContext context) {
    return (String) context.getAttribute(ORIGIN_ATTRIBUTE);
  }

}
