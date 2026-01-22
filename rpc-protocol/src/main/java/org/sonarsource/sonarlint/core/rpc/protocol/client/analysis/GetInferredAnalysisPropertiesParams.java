/*
ACR-8163ca27678544a08b93870b450e824b
ACR-ea8834ace1a240c2b1952d3ee869c024
ACR-09981c15d33e45d3b8ac9b3793815b92
ACR-a15c1f34cd4f4cfdb429bcee5e016ba0
ACR-8ef41fd7b5d443f8ba97d5573a2906cc
ACR-2e9811e2d27f42b7b8fa7baf56762ed6
ACR-af7fa42094844d86b65af37f29d24b04
ACR-721f3a5bda534237ba20608db839bc6b
ACR-f17689603df54b1296699e4955082570
ACR-fe06e1bd05af41389c7c16be577701bc
ACR-2869e8e0afa34b179703a9297b1ffe90
ACR-f401d56c67da49c5ab70416645b80b85
ACR-1a0db750aa564a4786b325841fa8983f
ACR-1052bf2e5e9a49e6800bfb1e86a1e734
ACR-c51fee242dce493f8acf9780ccb218cf
ACR-91125357b32d4973a975fc3d3edbcf69
ACR-96d9b3728921494e88e8282f08c9e904
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
