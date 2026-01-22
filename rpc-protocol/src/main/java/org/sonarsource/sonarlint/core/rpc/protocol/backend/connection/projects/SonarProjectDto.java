/*
ACR-b67f8ba8cd584d619a2251a8ff076246
ACR-06f976e71ae94a7da8529fcbcc84837c
ACR-eba04a4508be46ab923523ba2c1e4db3
ACR-0112162978ae436bbcde020d22e49db1
ACR-519d39e0327a48b585598624a7c296ef
ACR-1be28868502a4b6fa49f02c05379ced3
ACR-46d51c2669324f7ba44109ea98825a50
ACR-899c599d637640dbafd9fc8f1bab2be7
ACR-7f46d6cda539442bb1f23ef6277333c9
ACR-d2c78268d2004115b1e11047e51f5286
ACR-734619c54efb429eb8bfd93b48e6add1
ACR-d865daf9c8904eba8cb322e0231fbba2
ACR-78f5a713e1e4402fac51875f1b34e49c
ACR-1cc481345e29484ba894bee9b5afc0b6
ACR-08e10d85e0d74e0e8711ca4a8b056c48
ACR-1651cc3c36a14bd89b73751f39b8943a
ACR-f828d4695a3d421da00df611d3b17232
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
