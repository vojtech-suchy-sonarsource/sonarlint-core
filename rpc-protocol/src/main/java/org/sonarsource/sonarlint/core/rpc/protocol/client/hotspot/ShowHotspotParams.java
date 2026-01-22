/*
ACR-056bc321040a4c48a576773210fbe010
ACR-5a08ea0670694561a2cc5aa00aca88f5
ACR-f315712758b541cca47666cf53a95f68
ACR-eb161a128be34d3d808d442cbffd2186
ACR-deb9b29f388f4ffe97c242ab27412f4d
ACR-c464f5a688774c94bc3c1a9e03fdf4c9
ACR-e966fd5428864dd5995eb2a4bb9ff31d
ACR-887cf45829bc43afbea0c2ac9a08ad25
ACR-078449814f774aca8b384295577f48dc
ACR-90fe99eaf2354380aa977adae5d3e6fb
ACR-4ab55cdf8d254ba39ccf606daeec7c77
ACR-af1e7f2919524be7824e8d611abb3983
ACR-304cf16d132d46e3b6756fe21edb935a
ACR-157beca98c014175b7dd9e67a133581a
ACR-5147df2b6f344dedb1486070b8b166d2
ACR-0b65d947be7a401d90e1302999cfbb73
ACR-89980613be9c4e13948b5f1fe238c17d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot;

public class ShowHotspotParams {
  private final String configurationScopeId;
  private final HotspotDetailsDto hotspotDetails;

  public ShowHotspotParams(String configurationScopeId, HotspotDetailsDto hotspotDetails) {
    this.configurationScopeId = configurationScopeId;
    this.hotspotDetails = hotspotDetails;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public HotspotDetailsDto getHotspotDetails() {
    return hotspotDetails;
  }
}
