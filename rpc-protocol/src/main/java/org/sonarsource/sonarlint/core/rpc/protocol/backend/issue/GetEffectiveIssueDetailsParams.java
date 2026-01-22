/*
ACR-5ad0d76870ff431f9a6719b44ab807a0
ACR-114c2587e7b4499c8349a6405eeb1580
ACR-1eb9ce5401ee4029b3dfd8d77cdd236a
ACR-ed0ff41a04d04ca89a15b268a413a8f4
ACR-1d73e95a7d6a4ae9aed9aaad2e9aad89
ACR-4db18fbfe9b54f509ca5ef4aa1cd97d1
ACR-e580c32b230b455fa340e3632a56111b
ACR-63ca7f893d9f47e49796c9c0d4d2435a
ACR-f571b6d7519b4531b98baea408065718
ACR-90a49576aa6b41b1884917f6bd696fb8
ACR-e9678b2ff19846008d105444bce82b7d
ACR-d5834a3f093d4e20ba0031c720e189fe
ACR-8a30e28ccb1148d7bfe5948c6adafaf9
ACR-e132464bd63942799e65e1b6840fdc2a
ACR-c7844d30e15140b88f35c8a2dff9eb3f
ACR-55e0348c08db4412876777100e4d6adc
ACR-9433a085ce2648948d5bf7eddb33ef79
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import java.util.UUID;

public class GetEffectiveIssueDetailsParams {
  private final String configurationScopeId;
  private final UUID issueId;

  public GetEffectiveIssueDetailsParams(String configurationScopeId, UUID issueId) {
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
