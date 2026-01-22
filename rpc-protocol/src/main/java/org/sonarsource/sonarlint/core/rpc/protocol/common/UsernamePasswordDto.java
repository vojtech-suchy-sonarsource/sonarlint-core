/*
ACR-28636f9ef1f8416487c058f5d80fddb4
ACR-6b1db583c69943fbb8f4cb0ca00e711a
ACR-48e44879e83044369ae7b9bd54ab14d6
ACR-e1d711aeade84884ab9d9abc90549682
ACR-5df8f9e53c6d4a759d3b23ba24fe87fe
ACR-c65611fbd33349b19989ec9d12695726
ACR-e56f8cc8319e479b85fb066d430dbebd
ACR-9860781683f74fc9bf9027793a8d0587
ACR-44d201f39d79411b80935095e41d03a3
ACR-59f1fb0a89894a34a762296080cd776b
ACR-1ff6a05a4c384008bc3a7dee45f2158a
ACR-0c7cc7585cbe457f9069709c7c9602be
ACR-f5e76b4de62248f7a3743641eaeb4fe7
ACR-7ce3dd544eb34166a2682f6a051c5408
ACR-828c9809e6544c9599e23185897dcb0b
ACR-6e8b7da14b2947d5884b1294636efea0
ACR-d6070b79bb0f43ba86b0d810d3f76a7b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.Objects;

public class UsernamePasswordDto {
  
  private final String username;
  private final String password;

  public UsernamePasswordDto(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (UsernamePasswordDto) o;
    return Objects.equals(username, that.username) && Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }
}
