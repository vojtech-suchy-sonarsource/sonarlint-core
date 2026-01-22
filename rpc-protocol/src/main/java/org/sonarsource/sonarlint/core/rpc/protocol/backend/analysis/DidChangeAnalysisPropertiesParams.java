/*
ACR-e5001b9cdfed4168bd019f074bf4363a
ACR-b9095f10c55a4529a2d4c190bdfdc4e3
ACR-c17bc5b1438843958e39ef4f07862037
ACR-29fbeaea47554874a8bcae09bbe59232
ACR-80b3cba25c8d4482b7bbc27f352223af
ACR-d662ce1db1e44957bb5f4ee457488475
ACR-9b15976122264ced870e38d2f086f7d4
ACR-c9a01b22bc62429aa7b0eac415444934
ACR-40b8f88add8c4b24a9febe95326ee202
ACR-d4c9173d1bb3430985a26ff7366098c6
ACR-fc7a7b973eae4c00bb226600f2ca9670
ACR-9837143897a9456bad9639d8e691aeb0
ACR-906c11dd2a704d2c80da90f56459109c
ACR-1462140b50614b63ae0a6dc486a367bb
ACR-fb43853e7df24aefa16ef63feab650f0
ACR-dc3398c1545e4a52aca06442e05e9351
ACR-836ac18f8d684503bec5d80bac225dcb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.Map;

public class DidChangeAnalysisPropertiesParams {
  private final String configurationScopeId;
  private final Map<String, String> properties;

  public DidChangeAnalysisPropertiesParams(String configurationScopeId, Map<String, String> extraProperties) {
    this.configurationScopeId = configurationScopeId;
    this.properties = extraProperties;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Map<String, String> getProperties() {
    return properties;
  }
}
