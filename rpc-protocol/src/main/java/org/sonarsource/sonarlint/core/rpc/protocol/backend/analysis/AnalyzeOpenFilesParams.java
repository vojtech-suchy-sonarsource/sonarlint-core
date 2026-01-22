/*
ACR-b895780fc1724bc99c326ab9491598fb
ACR-964fe196a2ee47b5b1b6806c30b04a36
ACR-8453308dd4bb427eab3bc3d4a347d40c
ACR-2bdda3c6426d41c5974e08f589f7ecd6
ACR-27db4f9a43b44c16a7798b378cbf2db8
ACR-23dc45a0b10f4420a4f1c707862d93c9
ACR-ba8eb857439e4cfea752da400a4b7035
ACR-748ec25edadf4f4f9a6e447e7d929fca
ACR-234c8e29f09246c8ae86e40ff2a0f348
ACR-d167c05da5fd427eacb22aea1eafd5c7
ACR-52e43631cfde4fc092681a75a7b58636
ACR-17a6b199f3a04110ab86f57262e6d75f
ACR-4841d3d1ddfe4a89bf49e97c2800faf3
ACR-aef316c2503e40ddb743421815e41cfe
ACR-c2be1f12697c4cac977864020c66211f
ACR-90f36a08e8e8479d8a30d20272a2bfd4
ACR-c9775ec28bd94f0b80f6aa39be2b105c
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
