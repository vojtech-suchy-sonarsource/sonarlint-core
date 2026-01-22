/*
ACR-fc4c095e6c4f4b08aad331e18fa1e440
ACR-4f711222cff449f0844d0f9d8f5041f0
ACR-c5d2c51a5c9c4fb99c74f36a9608e36b
ACR-95803fce676d4f569a25d7d7f28e1d35
ACR-e3521fdf9e884953b98baa9f3e6201f7
ACR-db61f597b6a048aa81b9f110ec81b005
ACR-74e67ff4b4c14af58936cdd40952f34c
ACR-03f27d36270841a78326cf2fcb6c1246
ACR-1462d207bba3492095c30e7c6fb92044
ACR-10f556223dcc432cb6b4c52086ad31a3
ACR-a4640175825d4af5a60a8f7e60e58467
ACR-6d6d6b8e4dab40c5b58d94c750b1f82d
ACR-314afca3a4604805b74aa9c969dc9130
ACR-8fd03fb8a9d34c0d8972ca211bb39b2b
ACR-3efd6354d02d4af8822da673ba021fa1
ACR-e98a09b8fb3544eda918b9233ce3dad6
ACR-5ec6972f989c40efb374306f2a1d9316
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
