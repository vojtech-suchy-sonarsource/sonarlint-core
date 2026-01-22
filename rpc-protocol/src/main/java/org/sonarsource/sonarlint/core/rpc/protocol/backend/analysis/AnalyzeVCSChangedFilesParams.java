/*
ACR-c7ae850411524167bbe3a0573eed011a
ACR-7134810fe5a74c54b322f3c32de783a3
ACR-27038120c2734c4d882eb8a72304fb67
ACR-9012a12a4c224ec4bd1a060f7029a388
ACR-c2b465cf3a6248788166bb9f863d8140
ACR-08cec710e27244b8a7932b79d5d72b80
ACR-71124e6f037b41028f7bab2fb9556ed2
ACR-95f6b69410c34530a431bf2a4ee6228f
ACR-e4478d4764084fd7b9490b67d85b6d63
ACR-0ccb548dac40498c892938f1dade9b03
ACR-7aa9533cca094358803b241d77a94f74
ACR-54194692168147a6a7a2478d64351a52
ACR-f6aa182e08c647cf984b8bc2f3c06fff
ACR-4967cab4e08e451fb161b6de00df926f
ACR-77ec9d8ec6e0456bac7497d52757e2f4
ACR-eae681ebbe3d4512b1e01678e7086f64
ACR-048b7a062aa64e1a9b743ff5cdafb054
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class AnalyzeVCSChangedFilesParams {
  private final String configScopeId;

  public AnalyzeVCSChangedFilesParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
