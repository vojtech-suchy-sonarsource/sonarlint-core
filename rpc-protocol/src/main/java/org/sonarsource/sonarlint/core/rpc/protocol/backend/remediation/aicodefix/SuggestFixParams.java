/*
ACR-73a779210b7d459bbb4f5fc066484144
ACR-872e2c9dd8f84ed6b357fb1eb56645ea
ACR-ef8e7c6b34ee49a4a5676fd24d202e12
ACR-3b93d130da734168a1d0f9e0efe62fce
ACR-f73144057a6a4dd78baa78705536dd85
ACR-27b0fe711ae64ab7adc5a49371512c37
ACR-f234768aef944337b9eafddb1d5a77a5
ACR-9a8928ef8c36472daec88fe6367fb414
ACR-9a7494c531404eff9c3269a912f7b9ca
ACR-6c23d805f55849309b89ec5190627b78
ACR-078387f512234606a3d8aa50d651ec74
ACR-4e014b5c6e624dc28e97a24fadcf66a8
ACR-fa8cb88bc78f40bc95d917c29e92bd4d
ACR-b6a27d3e6a0f4ad58988d6d70c8164f3
ACR-b9146234885641238ff1822f6b758da9
ACR-0da364f28bed4096b88409fbfdd2f49c
ACR-f5a9fc831fda4bc488181fea93358cfe
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
