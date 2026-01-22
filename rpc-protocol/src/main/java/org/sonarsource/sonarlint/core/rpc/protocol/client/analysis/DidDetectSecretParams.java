/*
ACR-04e804f6c6e249b1adf355629e92eaf5
ACR-001ebcd66b2545cc8d4de8a2064fe407
ACR-1613f7810f024d8ab85c3ea2e151c181
ACR-e1beaee3690c43c29854f9ecc8ae5415
ACR-9fd81bfdae9f4f2684203622abe743da
ACR-4bd05020da1e4b2d912846e1ca9b6fc4
ACR-91edb4c282904c038c504c04b7daf146
ACR-f0ff88f795784c28823e3c287a4ce18f
ACR-4a0fab9bcb4a4f14aef06687a6685acf
ACR-be86b7bfa6004f71a9469c7a0dad36b9
ACR-229f6857da464686948b7e100bb376b2
ACR-a1138c178d4747b1ad6ae037eedc9a90
ACR-a380a7c5c6a74c71943d2f09a880cfe5
ACR-551ae05abd1e4883978834d1dcf241db
ACR-65620c357da04ce6ba63b205141790a7
ACR-8918305389044ad58776079d4c2f1392
ACR-9edbafd7b53a4090b39b9d4946046d82
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

public class DidDetectSecretParams {
  private final String configurationScopeId;

  public DidDetectSecretParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
