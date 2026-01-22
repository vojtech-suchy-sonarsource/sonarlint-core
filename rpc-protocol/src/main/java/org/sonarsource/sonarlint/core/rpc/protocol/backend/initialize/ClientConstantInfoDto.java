/*
ACR-0abbddbc5dee4de98c73e0245e2b35ca
ACR-e21767ec5f1d4f999b223e94b70ef612
ACR-ebba1c2c9bfb449dbd85a0b89d7f5840
ACR-f190849e882d4fd1b62de655a39b7636
ACR-ced5600dbfa14ef781c62a7764d6fc00
ACR-c54dc9f6fc724995a4e075a38f5da04c
ACR-02bb2a9ac77d44c5ad63cf8eb912b5d2
ACR-528c7d2e2bd34e9b861bc1eca5b5757f
ACR-13b7ba67df1c402da9084b161a9df42b
ACR-c6f01fabf449436bbc9843344062544b
ACR-eedf31515a8e44498351176770bb3a5e
ACR-1e1fd62c19b541d4860b4515bdc4ff9d
ACR-e91cf4a14b64498194d1134243a17f8f
ACR-7414fe5ef8544f4fb29f8bfffdd452ae
ACR-cd5600ffa584451cac6cb4df62363e86
ACR-6dddb8c2680e4bf384a23c36dced84d0
ACR-fa9d3523e7234913b93af19cec11e4d6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;

/*ACR-65258ae7849d4c0d9a6899bb677f3006
ACR-4de17965815e4ed8b52cb15ea8526dc6
 */
public class ClientConstantInfoDto {
  /*ACR-88660a8137404bb3b7f0bc5fecffe2b8
ACR-f70658b777f74a8da8962d7f097b7f5d
   */
  private final String name;

  /*ACR-fa0ab1b17285452a8b42202ee5b1c1fb
ACR-ad8ab766bde341bc8d3f44ed71dcda6a
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
