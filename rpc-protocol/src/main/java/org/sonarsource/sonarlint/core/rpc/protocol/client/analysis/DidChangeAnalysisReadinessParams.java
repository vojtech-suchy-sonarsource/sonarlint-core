/*
ACR-2d3dfc71cbc041db9e432672f55fb566
ACR-02c054d269844113b598b9ebb069d440
ACR-470c913649ca4ae2b2954135eed8f6bc
ACR-7444906ff6494e1cb654250633709723
ACR-5037b0cc37d242d49c714ab064a8b27c
ACR-76e26ff772674b19913d56abbf30c343
ACR-8efe290a5df74ca888201aa2a41d0229
ACR-5a96f4250beb4c79b76c87b027a99347
ACR-19360b6f46474b789350c0d98312b8c7
ACR-f9c72935c66a4634aa1f9492a3a1f730
ACR-e1babbeb1a0843029410abcaefefbb85
ACR-ae5677a2ba6346cfbedd747458b6607b
ACR-ed05790a286f4266a5be9f949fe5f4cd
ACR-39e24c52aa274ae3874cf3cbe1f2c62c
ACR-e902383ad7f142f7b15df149c9083c4a
ACR-9a355cad28964b84b7ec3ccb59734c88
ACR-84c1e247fcac4aff84f99eff5956c220
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
