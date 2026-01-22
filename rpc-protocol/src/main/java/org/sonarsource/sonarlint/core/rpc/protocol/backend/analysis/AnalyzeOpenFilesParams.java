/*
ACR-3f5717699d9943bab3431e0afdb9458f
ACR-27dc21735b0449c3a4f0b5be2c87a3a5
ACR-4ff5d4e1adf4458f80c2352b58e5b6e1
ACR-4fbc6d11ba0446bf8d03403230ac7203
ACR-bd8c04aed6a94cf6be9984da849c3958
ACR-bc0a0ac4ebeb468e92095e934c59af22
ACR-ba226bfc221641f196d5e7ec95d256b4
ACR-dd8d475c077a43d1bb7ca51b777a6a6c
ACR-2bebefdd45934e86a91c1059a2f9504e
ACR-a99b42ef4c6a49b38864512115e8941e
ACR-1d97b58e1a9842fe81d9bf24e5eb52d9
ACR-52221c13bb484e608afbe51a9cc8ff88
ACR-64fd687240fa4021a8bb7d639f44821e
ACR-924b951978e34ec3b90ecd0f02dff3a6
ACR-2ddd72b2486e4beeb0255e908fcfa7ca
ACR-bf3a3dbd54074a12b705375fd4ed35b2
ACR-c273e83508af45e3b5c775f5cd263db8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class AnalyzeOpenFilesParams {

  private final String configScopeId;

  public AnalyzeOpenFilesParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
