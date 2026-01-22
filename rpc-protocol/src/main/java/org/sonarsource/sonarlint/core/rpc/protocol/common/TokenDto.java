/*
ACR-240a4aa73cfa4723bcbcb3e5cbd14a67
ACR-3aab71d92b0e4031b4792cce433eb704
ACR-f08430a18da74e55b52b6390dd4d5322
ACR-493b39ac90714c3ba4eb07715139966f
ACR-824b9cb55fdc4aa790c10fc7b5ecbb53
ACR-48efe62409a64d68a14f2d2ee596e568
ACR-0dfc674abce04bd2b96228a2f74bd9b4
ACR-43f1434a83bc4c28a0131a1649112677
ACR-45703f57729f49d1b04e90b9026e6099
ACR-ec6abe1339314a7a95d041f003f22b64
ACR-47ecf91ae3494e328b7de773a4a201e8
ACR-564b1e6ec4f44bbbba3578995e6c7584
ACR-a35c8b1ae012455c9b4220e93ac3f810
ACR-d63872702bd14ad7a97d194122e6efb5
ACR-49921a9fb56441faa11ee890c5a68b3d
ACR-756cee0abcf44cf985a6065a87215480
ACR-3bb343afdc4f4145b86e2a98c2518d40
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;


import java.util.Objects;

public class TokenDto {

  private final String token;

  public TokenDto(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var tokenDto = (TokenDto) o;
    return Objects.equals(token, tokenDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }
}
