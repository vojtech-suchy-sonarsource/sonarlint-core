/*
ACR-e8b40eb1f61740bf9d730e27e4bb7815
ACR-518571d69ac74a418f7a1bf4628f2787
ACR-d1cbc748fa90449e849fd38243dd0464
ACR-c5772b237c844716b4bff48790f97a0e
ACR-748941ed18ad4b3e8d4a8995bfbef37b
ACR-e57b57b5f56a4a46b848c47bda872349
ACR-2a1d65166680435998397372e2ec3414
ACR-711d25eade42428382f1192e6f7e16cf
ACR-f7e26bb7f4a44c5b99fec1bc66251744
ACR-73efc9230f20446687b9ad42467ad0c9
ACR-df895a5067f14dc58bbddafdcf55676a
ACR-85fd2c90090b4f6d99fbcaf72337d81a
ACR-3095ee23d82142fc83e23e2529a75031
ACR-b5b626a5cfc44ada8555898b7e7ade00
ACR-1e0ff559069f47b4b5c1104446c7c1a1
ACR-8a6abfd2852d4309b05de1f0752852bc
ACR-a3d56a1732f644a080f263ce2ed55dc7
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

public class DidDetectSecretParams {
  private final String configurationScopeId;

  public DidDetectSecretParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
