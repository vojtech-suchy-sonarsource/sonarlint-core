/*
ACR-2d9b76d52ffa45858fe92ebd98b0a6c5
ACR-45dc4d4ee4a44e4190d86d24c90cae9e
ACR-a8a17d7ebc6747029f1348c22fc03260
ACR-7a6f494665ce4a059836f7b9e89de24e
ACR-c2914d6293cd44cba19f8c57d43d7048
ACR-baafedc50ef54df7b870634cca7e8c3b
ACR-ca66acbc171744d7b442f05f2cb085bd
ACR-c5bc4e7b00eb4a00842b60413cbb2e0e
ACR-490d8f5dccef4987bf7b43c2a9b72f71
ACR-18689a798adc4ada83b7a9d6075580fb
ACR-613edc23acdc44c6b0dc03160cd4f847
ACR-e06a25d04b8a4024bba37a2b352878d3
ACR-4ee6b33dbd454feabcaab37bee7b0cbc
ACR-2e791b1ec66c40e9a175558aaf578f84
ACR-c8220e82eca24cb9a222b4e1761d3791
ACR-0669dd92d99e4deabb38d73234f503e8
ACR-628bf3f594ac4324b4522db6b1512c3e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.Set;

public class DidChangeAnalysisReadinessParams {
  private final Set<String> configurationScopeIds;
  private final boolean areReadyForAnalysis;

  public DidChangeAnalysisReadinessParams(Set<String> configurationScopeIds, boolean areReadyForAnalysis) {
    this.configurationScopeIds = configurationScopeIds;
    this.areReadyForAnalysis = areReadyForAnalysis;
  }

  public Set<String> getConfigurationScopeIds() {
    return configurationScopeIds;
  }

  public boolean areReadyForAnalysis() {
    return areReadyForAnalysis;
  }
}
