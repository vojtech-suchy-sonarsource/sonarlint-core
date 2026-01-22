/*
ACR-03e2aafe2dad46d38d9e187d838c01eb
ACR-eb3497f23a1e44c6859e7618299dab2c
ACR-786cb66aaf6a47d39f3bcbce964d0bbd
ACR-d4d54cec10974b92a5c4bfed9753f73f
ACR-333953b9dd2247ef911ed85330a95569
ACR-8a6cb41bd6f24746af79dee4d973be6c
ACR-2bdc3d4589124493ba54cafdd0d52d80
ACR-8aa523a54c71489abf9b287d5acaf3f3
ACR-3a34da6b70c34838b9f335c9c2a64a6b
ACR-8a195cb1152d483ba6d5d1b33236d137
ACR-05c43cac8cf94cc8bbab51cfe17dd60a
ACR-1cdc4f15f7174a70bbf3ca41d501887f
ACR-20b6b88f8d974780a54eb29db81a2d22
ACR-c2409a078d4e47f3a3c4d3dfffda77c6
ACR-24f21a675a1b4919a328ba1a69de772e
ACR-f18d329d83054affa1d11af223985ddd
ACR-51b2e23f0c3c4a9ca78256fbdfb6c825
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
