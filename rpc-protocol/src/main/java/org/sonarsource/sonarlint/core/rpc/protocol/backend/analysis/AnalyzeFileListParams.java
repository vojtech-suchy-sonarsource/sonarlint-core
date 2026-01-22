/*
ACR-96c19c15e3f04f478474374532b29f68
ACR-0852d4fbbf774862a4a76df84f2b6886
ACR-32d7b79fa3194d14b6e242f725cc4ff2
ACR-44e903f2e8234b21a84cb6c0c8c3eb64
ACR-70ed6278dbe743578ac6d736a50145ea
ACR-0c32e8105e874051ad37f154eb561ff3
ACR-9405cda03e15416cbf52608586dea2ec
ACR-e6d5f35eab814ae4b34fec9ba8742d8f
ACR-8bbd8b1daa004d6299632a9f6a433085
ACR-54d1d8f991794b688d5ee96977151a2e
ACR-6f853b134b0646769bb746856d708195
ACR-fb66cd6c38d54602bdaaf036ee96195d
ACR-4dad85ef4dc8447098eddabf326d17e5
ACR-3665bf13f7234b588c1c263eb2e2511e
ACR-119894b54e8b4c30b95d5716e4e73d84
ACR-143cf3333da541749420a3cab6229238
ACR-69b7a7e12ee944c3b04f3ed27ee834b3
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
