/*
ACR-7a7fe8422d5544d1a6642647a5f8ba97
ACR-1d84ab0bc1094e97bc8bc01c863a2559
ACR-7682663487af40039c0425927589c00d
ACR-1d03ec071f374bf3bfb280f55fe87d0e
ACR-82190e8a2dab4d6a9f28317de0ec3e5b
ACR-e9c17a8b105d4bd98a6464bfaba0a153
ACR-8019ad9f0e2349f2bff0b10a678384f6
ACR-efac5b84f5e548f793197b4f549fc643
ACR-1fb14880f02d4f3e876427310167a27c
ACR-81fb66599b1d4ea2b12dc80e3eb90529
ACR-928521f779f44ef0bc3400d949665da4
ACR-a195c02478ca48e5a5b28494c20c784c
ACR-9ccd2fea7ffd468e8595a904982a8602
ACR-198c2b33187445cf90439f7b14b3357a
ACR-1ec4583ab83f44f39a16d292e6ce2859
ACR-83e4fc5b15ee4e6b944e76257e5d308b
ACR-026f90e8013d40b7bd228f0eeba75649
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;

/*ACR-758ddabf08084b4ca306190617c74cb7
ACR-13c1883652d8481a9781da42c17e18f3
 */
public class ClientConstantInfoDto {
  /*ACR-2ef7b34d7ac845cca4a63863939d3782
ACR-f81a4e93787545e68a9535afc14bf702
   */
  private final String name;

  /*ACR-87faa8317cb24995862c781c7908a8e5
ACR-f66aa2ff674b49e48ad8392b4353225b
   */
  private final String userAgent;

  public ClientConstantInfoDto(String name, String userAgent) {
    this.name = name;
    this.userAgent = userAgent;
  }

  public String getName() {
    return name;
  }

  public String getUserAgent() {
    return userAgent;
  }
}
