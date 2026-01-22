/*
ACR-12d5d627104b42009b0ded57c76306b6
ACR-679e6ef1d28c4984a3fd2a946024e71c
ACR-5d608b994c6445f3ac53f54934eb6e26
ACR-8042616cb32a44f7b3df27c19be3be50
ACR-bb8710bbfa954bbdb42521900e01827e
ACR-fae655acf4264dd4a6f2809d49d0e67d
ACR-53213caa2e4645c7929f1d7354e0acdd
ACR-68f2cf1250a54edea0f3bd3a4a26d57c
ACR-c7c97f401ce34da9b7d92ebff88d3802
ACR-de56eb4fb5414b129f17c53946a6d461
ACR-57db6800fa2e4229ba1f2f8782d5c952
ACR-603eeff5c1564a529bc986729782f8b3
ACR-090d24c0ca7b4796830e81477ee69893
ACR-32f602d14d8a42af8dcfb28b7b30d66a
ACR-35b17fa80baa45ecb57279ae5c5b41ff
ACR-d53ee4d31e454d1f8bd7bb4788432121
ACR-5483b4eb10ce4d489070a90b5bdeff43
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class GetConnectionSuggestionsParams {

  private final String configurationScopeId;

  public GetConnectionSuggestionsParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
