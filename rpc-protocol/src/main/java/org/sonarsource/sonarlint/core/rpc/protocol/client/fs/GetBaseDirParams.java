/*
ACR-075e39b6c13148eb88450fc5355ca3ab
ACR-f7bc2ab042f44fd8ba0273703ec45e64
ACR-0464548b3afd440982d32cb98a310320
ACR-20f9fe449ebb4877b5da59e9758fea2c
ACR-7a6fedb0d1eb478b89d2a7a8677ee8d9
ACR-599e3233334c439c9fa98f0e4da08f3a
ACR-2325c6217673463ba9a63bdbfa9f5988
ACR-97d93525e9a545adb4334b05d84ecf6d
ACR-fe6f05c772f3454c9ee7668efeb9781d
ACR-e24688c9f76941b48813c90d21f0270b
ACR-e514485f1fe64e3ebefd98e757d97e26
ACR-93da6593543241a2983f2f2fb8d24d02
ACR-5d5257f7916d4e8a96ea52144974f510
ACR-8f2a7ad7b7724cc6ae8ac347cb9df6f7
ACR-f76f55a1fc4e40179632e27179adedeb
ACR-8703b08824e04cb6a2bb8a446fbebf3d
ACR-e11d82b820db419f9a9c4d9421b636ac
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

public class GetBaseDirParams {

  private final String configurationScopeId;

  public GetBaseDirParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
