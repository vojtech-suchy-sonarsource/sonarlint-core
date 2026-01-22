/*
ACR-b9ab32a9966047ef858e9d59534a8a6f
ACR-4bd6498d76ef44908772ada86a76f193
ACR-cdd73a3d30e6472399e586b971a2e8d9
ACR-de92e25ac5e14f10819f19907b1f8c6c
ACR-7e8edfb78945480f9f5468d2bbda7de5
ACR-fa46c78524c14604a1444057d594b085
ACR-6dcdaaa9746e49c2914616ad0e524f7c
ACR-4735866ef44c4e8fbc608bb424ddb183
ACR-f30a2947db3b489895017326829774e2
ACR-f845483f205f4ce2ae19595694c3e33a
ACR-cb137d9d279c474e8e71339bfd3b51a2
ACR-24897496f0994e63ac7c596952944072
ACR-17aba2f2fa1141389a060511b6dd20cd
ACR-86cb701f9ec34a79ae3f5d6f7f05b379
ACR-4d0c45340e04471aa65f1c74e9c00bfb
ACR-cad28842cd044f878387bd6c16f69336
ACR-dfe7752c62c246b6b3821e7bae391fa0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

public class DidChangeMatchedSonarProjectBranchParams {
  private final String configScopeId;
  private final String newMatchedBranchName;

  public DidChangeMatchedSonarProjectBranchParams(String configScopeId, String newMatchedBranchName) {
    this.configScopeId = configScopeId;
    this.newMatchedBranchName = newMatchedBranchName;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public String getNewMatchedBranchName() {
    return newMatchedBranchName;
  }
}
