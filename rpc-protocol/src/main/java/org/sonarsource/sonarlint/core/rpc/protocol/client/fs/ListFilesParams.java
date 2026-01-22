/*
ACR-4ffdfb8dabec495398001c5f997c075f
ACR-7cb15df76a4f4aa794b4275224e8ebd7
ACR-d3abe4a0f5c840858cd04d660b8ebbbe
ACR-733820acde2345e7b44289ffa9b98463
ACR-3c38e36076b54b42a9dbdbcc779df1df
ACR-e39c2196a7b64d0cadb8dec057168713
ACR-d5db5062bcb1420099de2d5b1fa5a925
ACR-77d3121dd5724225aa11864208a590fc
ACR-486bb04bec57461c908bbc6626e87049
ACR-db60b9c4170b4bacb3fddcc13fb29cd0
ACR-4c0834582fb44634a8bf80873f894edc
ACR-010013fb488d4863830262e91d87caa5
ACR-775dfb0904394dbea1e828ed33ab936e
ACR-341b202cd08c4b3aac10967411c8e637
ACR-ead5c383bc1c4fbab811e57220ac039d
ACR-d22804e5f1d1453dad2b04744bf3c115
ACR-b6877d813bec44d5b96f357998059c9b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

public class ListFilesParams {
  
  private final String configScopeId;

  public ListFilesParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
