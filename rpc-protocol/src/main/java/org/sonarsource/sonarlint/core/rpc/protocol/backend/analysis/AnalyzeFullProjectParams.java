/*
ACR-bd78991cdd4446cca2e062b096ba6f65
ACR-b35d701da69f4e4e87b5bd468b9e0e5a
ACR-b505836ec24c47c3afa1c02e650807a1
ACR-735f023b0fa94ef8a775445ac1c2cbae
ACR-4d8e752ba2aa4bedb0cb5d789ce26607
ACR-12756efc2f854f8fa9ef5a274f519736
ACR-cd4fb2b0e39742e78341a0a167756536
ACR-7b735ffff8894ee29c8badc1dfaf2406
ACR-bb173715ff42488ab93ab90fb0069694
ACR-7a9e5cf809844d7a819f2f3b2ea0dc62
ACR-fcf7f39e428649289aaecef58fc70db0
ACR-ef78d48f86364af28d3b30cbd8f1576f
ACR-cbe5a833230b4eeba18e2bd0130f9bdf
ACR-8e062c02616a4728a1a3045c4e89f37d
ACR-54c138d8e23f4e11a603d0520fa5d985
ACR-1312a8609b10484c996fc72aad3a4a5d
ACR-9fd42c934fd4402db3bfcca20b931e50
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class AnalyzeFullProjectParams {

  private final String configScopeId;
  private final boolean hotspotsOnly;

  public AnalyzeFullProjectParams(String configScopeId, boolean hotspotsOnly) {
    this.configScopeId = configScopeId;
    this.hotspotsOnly = hotspotsOnly;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public boolean isHotspotsOnly() {
    return hotspotsOnly;
  }
}
