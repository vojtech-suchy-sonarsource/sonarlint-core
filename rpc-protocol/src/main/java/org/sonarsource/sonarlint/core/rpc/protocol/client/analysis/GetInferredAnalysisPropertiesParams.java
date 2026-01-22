/*
ACR-0d452744fff64575b4a732b8ab97ae05
ACR-78afcfb02b514bf48477c1beb7d0ce5b
ACR-5c6608c9033e414f81979cc69966c4ec
ACR-4876faf1e5974d0dbe32e34d2058cf31
ACR-752db285ad5547caa77a04c4a92ff492
ACR-969177d47efd4594b1a731c77ec96190
ACR-67b272d31fff45a39b6fa7410d524599
ACR-0d3ae4726b8b4b79a90873deec98b2c0
ACR-8771ee184bd14aa6a052da4477d70afe
ACR-443bf98389b545609bd784f5d970a4d0
ACR-c8432407206b41589c480007d7842262
ACR-0c4b2f4ee9e747b0a0f074098e345bc3
ACR-e85cab19d6b9442d895d8d71bf9480bd
ACR-ab5651af6a9b4b60b7a14b3d3eda5059
ACR-b5cb22078fbb4cd09e5d57b035a838dc
ACR-8f806609d0034367a7fda224b73158f8
ACR-2ec3b51ccd714764a446790f903eac49
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import java.util.List;

public class GetInferredAnalysisPropertiesParams {
  private final String configurationScopeId;
  private final List<URI> filesToAnalyze;

  public GetInferredAnalysisPropertiesParams(String configurationScopeId, List<URI> filePathsToAnalyze) {
    this.configurationScopeId = configurationScopeId;
    this.filesToAnalyze = filePathsToAnalyze;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public List<URI> getFilesToAnalyze() {
    return filesToAnalyze;
  }
}
