/*
ACR-e9aa65558d7d45368ef8329e18c5d3bd
ACR-b6d9130a44734138b2a98cee1aa1c2b3
ACR-7cbf02a648464548813e2e23b2cbac53
ACR-5ea514c51d724041b01969b4a2b8d2d6
ACR-3445a12066934d9bb5fe831a494caf75
ACR-0ba40a2dd1894894add139e8c96434a9
ACR-940e02433a3844a8bf77a5257cc7ceec
ACR-ed9840415bc84f3e953a66ab61820d07
ACR-4699332a288d4ac69d34c146fbb0b7bd
ACR-085818d6925c43e6bf6a36fa293c0453
ACR-cda621c06c3a413cb8455045883dfda2
ACR-268e2f41c4f74dbe9ed65fd45cb22725
ACR-ed9a5576761247d0bdb4499ae74755e2
ACR-2588f483c59347f5bec77f4db40f2954
ACR-628ed4f9e25d44d39a9378ed0887cacf
ACR-d2ddbc07246449838232e65fe87d5b4e
ACR-f94beaab7669447ba8f78fdecf5e7cbe
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
