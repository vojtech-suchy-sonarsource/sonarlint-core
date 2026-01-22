/*
ACR-a17ef4b82c5140288f2d42010d264b8d
ACR-e93546b4653846f1a0545a734b2a66c6
ACR-c3abe2c3655a41f7b4c62f69000c57ca
ACR-7d5a2efec7c148788397feb8047152b8
ACR-97c4cd4197cc42efa3fe8d165bfcc858
ACR-01ec96024df941299107cbaae57f7c2d
ACR-4894f3233af945fc89d49f393c40dac5
ACR-a0f7baf7478445b5b4b94c30264edbc1
ACR-4cd3cdfe9949406bb61b9eb4c6a11c63
ACR-62abec668a764876bdef62765802ad4b
ACR-9ede569f303c4a5d9ca3805822d51ede
ACR-336a19566e234e078506ce60425378dd
ACR-295d13374bee4884b2c72fb0e4176da1
ACR-a127754342fc4e95b0556decad60de07
ACR-11a808f8b9694a80bc034fa347a6ab40
ACR-cfc8b7acbbdd4696bedcf4a7bca7d310
ACR-2845997088774da1a2df3a0a72da61f8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class IdeLabsExternalLinkClickedParams {

  private final String linkId;

  public IdeLabsExternalLinkClickedParams(String linkId) {
    this.linkId = linkId;
  }

  public String getLinkId() {
    return linkId;
  }
}
