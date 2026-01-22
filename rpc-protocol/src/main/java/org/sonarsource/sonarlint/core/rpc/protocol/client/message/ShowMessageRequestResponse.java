/*
ACR-e837174ac3124cba80cb791602ffaddc
ACR-33291f4f6ce14244b643400f12ece32f
ACR-7305b865798a4775a4b3da54cfa52423
ACR-eb11a449e804433dae14426277ab8cb9
ACR-9ef66ef5f63047e7a9bd78dda6867f88
ACR-062fa4bca3d846a8854be28e31eacd60
ACR-d52890420e51472391085fe73e3379d3
ACR-9fbbc89e8fab4849a123083438503f60
ACR-d5f2ba63aa744aa1a005f43f1df9e287
ACR-4d3c27f41ee44cab88669fdbc87bcc81
ACR-562d2cc0a024450a87e1421349613aa2
ACR-d361b5069c854382b67a7e1cc2d79f68
ACR-de90308bd48c4e99ac7ec919504b54b3
ACR-0f89cef3dde943469c3ae7d794da8852
ACR-1fbf91258eb241e299eb6fc604965509
ACR-682d3c2d55a1467e9b674b32c0b28b26
ACR-90f6b2337cac4f90a0a8655b7bc3ba58
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ShowMessageRequestResponse {

  //ACR-4a5ebe2adf794f18b4ff5768c5b6e45b
  @Nullable
  private final String selectedKey;

  public ShowMessageRequestResponse(@Nullable String selectedKey) {
    this.selectedKey = selectedKey;
  }

  @CheckForNull
  public String getSelectedKey() {
    return selectedKey;
  }
}
