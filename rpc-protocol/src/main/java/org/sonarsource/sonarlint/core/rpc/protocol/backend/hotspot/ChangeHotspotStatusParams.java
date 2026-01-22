/*
ACR-05e151879631491cb27f4271131e0d0b
ACR-48e12b8679074fbdb4547891f9708e12
ACR-3c326191200f470c905cd233c905d3a9
ACR-52680366870c48009cfbc1b6b1b7e990
ACR-5553d40636024a85b6ced0d0c83c8d71
ACR-1ac5099c1ad14abbb829228c9c81f1d6
ACR-87e18b67cd8a4f7295f3974431b6864e
ACR-e59f68fb6a5d4e88b9359e39485b9c23
ACR-299be38816ac4c36bb3c04dc4ed16b67
ACR-57f266fef0a1422194790c46ae5b8242
ACR-8b4803b207be4a219941840921caa7fc
ACR-5377b00d1b6645958b9b2bd2f37d383f
ACR-07f546799b2a425592cb1e5349ae7e20
ACR-e61586ced0c24059bd3b6d9e147716bb
ACR-51963771098a4e03948160f1302cf722
ACR-7b2d27b7ce1b4b2fa04189ee9ff3a2b9
ACR-636e6b8abc014f0b8443ea5e28654cf8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class ChangeHotspotStatusParams {
  private final String configurationScopeId;
  private final String hotspotKey;
  private final HotspotStatus newStatus;

  public ChangeHotspotStatusParams(String configurationScopeId, String hotspotKey, HotspotStatus newStatus) {
    this.configurationScopeId = configurationScopeId;
    this.hotspotKey = hotspotKey;
    this.newStatus = newStatus;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }

  public HotspotStatus getNewStatus() {
    return newStatus;
  }
}
