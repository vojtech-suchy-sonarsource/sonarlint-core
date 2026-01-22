/*
ACR-849040f9e9774256a16f185c1cd429be
ACR-84432067864c498e8f07609be611bc5b
ACR-cd5381840dac477eb228b8ae335c651b
ACR-6fe088d50fda479499a3393a94f80c5d
ACR-854506b05c334996929a8f98f16ae8b0
ACR-e4505792400b43cb8910ae547909609a
ACR-e1f9f5520d5545a1bf01f1e72e9507df
ACR-cccbba870d2447c19d6f5009049b43a3
ACR-9ff8fe273d764b4ba4ac2e5e9e6a0381
ACR-583b0acb8ea24bf9af577e3be6497529
ACR-00ac3c62a0a04af4b721d2ed2a042dcd
ACR-2dc3f68a022347829d67c445c878889d
ACR-a2cfb319520b4b538ecb22c13169e219
ACR-ad3ac2d06ca9446fa20926d30c8e5e4b
ACR-b1f5f63cf3b746bf942f440373c0150f
ACR-727aa9df9cf243e789d9fc0a65e90418
ACR-72bb4d2bab1846ab9e1b8a4f8f94682e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.remediation.aicodefix;

import java.util.UUID;

public class SuggestFixParams {
  private final String configurationScopeId;
  private final UUID issueId;

  public SuggestFixParams(String configurationScopeId, UUID issueId) {
    this.configurationScopeId = configurationScopeId;
    this.issueId = issueId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public UUID getIssueId() {
    return issueId;
  }
}
