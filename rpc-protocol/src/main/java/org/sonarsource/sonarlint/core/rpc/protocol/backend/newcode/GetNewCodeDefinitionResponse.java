/*
ACR-8112fce9c0e14d449fe4e171d217aa87
ACR-18360200490e440c8f91be5ef6a343a6
ACR-30c5985e24c24e3c885a51e6a77dcbfd
ACR-ab0e6dd7b8d444a295640e25e2556600
ACR-80f333147a6a4ed689da8119194190a6
ACR-2f416a585dbd48ddb6242c2795e49da6
ACR-2cd2981e1e96408488d049c7b5759848
ACR-c014906fda07452aa2f55efa309a4938
ACR-951fa982f992440fbf92b53ff7bcf06f
ACR-d8bf896b2457419eaef55e64596a572b
ACR-d415de172c7a4a13aea68834a26f65f7
ACR-b66d406bd1764480ab455a5921319f20
ACR-0b66b0345d3c4558b01557baa088ffcc
ACR-c8aa2f3556834965b8544e69c96a51bb
ACR-185f8bbf454e418d820002395c8c54e5
ACR-16aa66643f5d4edcb20455352bb48e98
ACR-307538f2f30a4796840c51e644628071
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode;

public class GetNewCodeDefinitionResponse {

  private final String description;

  private final boolean isSupported;

  public GetNewCodeDefinitionResponse(String description, boolean isSupported) {
    this.description = description;
    this.isSupported = isSupported;
  }

  public String getDescription() {
    return description;
  }

  public boolean isSupported() {
    return isSupported;
  }
}
