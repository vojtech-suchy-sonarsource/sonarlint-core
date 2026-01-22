/*
ACR-7ac6a50c313542e788bc631370a90754
ACR-b54430f300e443b899eed68bd9de4517
ACR-a1b83a0caa3943f5999a25167e6f8a81
ACR-d745703f7baa4ce1b8323178e33a1755
ACR-69507c4c957148e3a897f501337309a9
ACR-0feaf1c07d0245638cc837f289b4e70a
ACR-52e898d0eac840759bf4b4914a01024e
ACR-8375d18818654f55aaae6a6e159a39d7
ACR-ee8bb0a6fcdf4ccca455b1497800ed63
ACR-ba1220ade62a47d4af95434fd8584226
ACR-17e283a4452d461aae27e080a42d1f04
ACR-7c5d393ffeee4d3896c7532a18fed8ea
ACR-825f4a9ef48e4f1893bbb08b511afc3d
ACR-b1fc717511ba47cbba9dc35d6c1ad052
ACR-43659f1639074c9ca030d8c41495d0da
ACR-92a45eb911af4eab88e168df9011c280
ACR-d6f97de8c2024adaa8d9d73781a983b3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class OpenHotspotInBrowserParams {
  private final String configScopeId;
  private final String hotspotKey;

  public OpenHotspotInBrowserParams(String configScopeId, String hotspotKey) {
    this.configScopeId = configScopeId;
    this.hotspotKey = hotspotKey;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }
}
