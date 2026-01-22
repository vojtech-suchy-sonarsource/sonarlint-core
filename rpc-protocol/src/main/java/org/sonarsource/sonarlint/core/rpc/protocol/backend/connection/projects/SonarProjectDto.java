/*
ACR-59da7df2a0894ea29483424b01a4a6b1
ACR-25313faa4c4440c48d4faa1523e47723
ACR-720eadcc0ff943adbdf0498fe8ca6710
ACR-537b8c1099af48f6bb15ea1e9b13e92a
ACR-85a9008503864d0dbdb777580d36acc0
ACR-cd3dbeff029f47a2813b196dbc87366b
ACR-84ee85029d174f068dc67e724e481b29
ACR-3344a9504a5d4a678de69e8cf5ce31df
ACR-55242aefb1e540e88c6808840ba647b5
ACR-fbc97c1c93bb4d5d9801c6406178e290
ACR-84b68acb4d9942f48be9d33326c303e3
ACR-eef1a61c0c494baf8ee66420182a3ddd
ACR-ed57b31e11d3486d9f0cabd6ce4ad170
ACR-8440c09f37cf486b865cfd1be21a14bd
ACR-84cacdc7718e45e392f22c32aaa386a5
ACR-ae5e07e1b9e1462aa9709536f8f22d18
ACR-08166da1af744d9d88274bbadbcb5154
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import java.util.Objects;

public class SonarProjectDto {
  private final String key;
  private final String name;

  public SonarProjectDto(String key, String name) {
    this.key = key;
    this.name = name;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    SonarProjectDto that = (SonarProjectDto) o;
    return Objects.equals(key, that.key) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, name);
  }

  @Override
  public String toString() {
    return "SonarProjectDto{" +
      "key='" + key + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}
