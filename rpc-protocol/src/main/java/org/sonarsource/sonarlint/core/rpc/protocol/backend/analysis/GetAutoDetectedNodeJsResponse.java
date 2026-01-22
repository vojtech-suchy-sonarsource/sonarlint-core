/*
ACR-776ef99a9774450dbf6aa79c0e710488
ACR-8ae71b065a864394a9b4d2f243403cb6
ACR-cbf8174e0f344b6da757081d1e083f5c
ACR-562684c9107747d2abf97c6db945d008
ACR-933bd6ced1124f44824c0861024320ab
ACR-8eb744465324440eb8d5c7ecfd6308af
ACR-e25a063619f041699b6bc5b5cd90c347
ACR-a123947f46c24a9eb8fbe84a026d3120
ACR-503688b867a449ddbc0965896c2ff55d
ACR-e50a4bb2854f4705b776d435ed2c0c66
ACR-24d77aa24ed3431b9bab0d154736adaa
ACR-ad788c19310c43b0945eea82fbdb6d3f
ACR-164abe4604be48c996494ad57a7607c6
ACR-30d65fee9aa44f998f7107e63f359860
ACR-fa99305ecb2d4774b44b5dcaef1d57cf
ACR-fc4276c8fc034d74a0133b4485fc78a1
ACR-80777bfef56849ca9a46856af4a398d4
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetAutoDetectedNodeJsResponse {
  private final NodeJsDetailsDto details;

  public GetAutoDetectedNodeJsResponse(@Nullable NodeJsDetailsDto details) {
    this.details = details;
  }

  @CheckForNull
  public NodeJsDetailsDto getDetails() {
    return details;
  }
}
