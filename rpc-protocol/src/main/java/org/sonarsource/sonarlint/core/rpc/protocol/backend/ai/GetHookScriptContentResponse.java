/*
ACR-52d5775bea2b40698573c870581b83e3
ACR-1c03b2ced69842a180e8893230c2e6ba
ACR-fcff6510c502410bbd5de4d104c50a85
ACR-2be93debb3f641e5b9c62372a18ad46c
ACR-917b0bc913294fab87181811a3126eb3
ACR-fc1b5fd9e1184ee583ff9e5909de5527
ACR-c1cfcc41df6a4124bcccc642258435e0
ACR-1838a2c128f14ecb8f06be5a1158ddd8
ACR-32228ea9dd954e55942c16356efcd705
ACR-0e227b9a20574469906c7ba02d3c080f
ACR-03d724bd532b48019a4341373d3a0594
ACR-8cec175025d94989be45320a4227fbaa
ACR-10bf4cdc74de4ed9a02fee20eead83eb
ACR-3d2ae97b60704cb983f11877a697569d
ACR-e408657e906f4ac69a51ae78221ffab7
ACR-d1943d49ad55468ca7bfb0100f418741
ACR-3201bcf2d8e34ab982e6bafb34a3fdae
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetHookScriptContentResponse {
  private final String scriptContent;
  private final String scriptFileName;
  private final String configContent;
  private final String configFileName;

  public GetHookScriptContentResponse(String scriptContent, String scriptFileName, String configContent, String configFileName) {
    this.scriptContent = scriptContent;
    this.scriptFileName = scriptFileName;
    this.configContent = configContent;
    this.configFileName = configFileName;
  }

  public String getScriptContent() {
    return scriptContent;
  }

  public String getScriptFileName() {
    return scriptFileName;
  }

  public String getConfigContent() {
    return configContent;
  }

  public String getConfigFileName() {
    return configFileName;
  }
}

