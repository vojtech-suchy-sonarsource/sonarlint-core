/*
ACR-53143354b04b4951bcc15cb12d143640
ACR-3857f1620e1e410ebbc904780dee48ac
ACR-e9bda85797c44ff4bcc5ef2eda9c0a0b
ACR-99ac9f0b0ed94f1487199e7de0e4e4e0
ACR-2ad5b200b8b94b33bcbfd73f2911d166
ACR-fd6264fa3b764218bae4974b73f82bb3
ACR-e80e6504bd83453a9368f4b441397d8f
ACR-e6850f1930774deaa3352ca55fabf1b5
ACR-a83705feb93e41fd9a0f3682e7bdd9bd
ACR-8b1c4cffb7614b7792774b298195296b
ACR-263bcb0d8a8c4e5ca617d0467f6f5900
ACR-9b8a5eb56e6047388df9bb1d09c4f14a
ACR-a49cbe271a104ff2bb67d70df9d6a198
ACR-7d3568a1987d49f0aa1f60f5d96b88a9
ACR-e759e0af038045ca9e1ad9426f45d6d8
ACR-fb9d889791a147fe9dbde8f026c2d9fc
ACR-b6cc7b8839d64c89928eb5662b6584bf
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.net.URI;
import java.util.List;

public class AnalyzeFileListParams {
  private final String configScopeId;
  private final List<URI> filesToAnalyze;

  public AnalyzeFileListParams(String configScopeId, List<URI> filesToAnalyze) {
    this.configScopeId = configScopeId;
    this.filesToAnalyze = filesToAnalyze;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public List<URI> getFilesToAnalyze() {
    return filesToAnalyze;
  }
}
