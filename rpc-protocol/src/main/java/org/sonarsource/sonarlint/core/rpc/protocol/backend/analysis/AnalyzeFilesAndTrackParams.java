/*
ACR-c4c018d5cb4b4e7ba02753df0938b5f8
ACR-1d19895188c24becb6e64a10bd706bb9
ACR-3d7a8498d423476cb4766b2da21f0731
ACR-e4621fe27d2b4fee835b254564ed0672
ACR-16aaf075594d4473b4640035f39d9646
ACR-fff501ec4ee64fe4be0cb07d188b0370
ACR-06624633d8de40ae905ecda853c8d997
ACR-6c273fa29d614744a54c5f73db85189a
ACR-343864eed70a4a3db435be64503b3380
ACR-09626362690e4e6dbfdf5ddbeee6d1c2
ACR-4714b3a5750b422b9f8b9d9b549e4855
ACR-611ca6354ee14917b1b143ebf6bc508a
ACR-506f23b16af242acbf2936848a260428
ACR-715618608dba4e4b90f1738aa3b57d3e
ACR-c9edb5d5426345479714987973f2fc10
ACR-6995b525acb14bfbb38d8be88a84c7b8
ACR-28cfc1fd683d41d9987c2ead4173ba2a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnalyzeFilesAndTrackParams {
  private final String configurationScopeId;
  //ACR-f204f779a79a44c88f80527975f653ea
  private final UUID analysisId;
  private final List<URI> filesToAnalyze;
  private final Map<String, String> extraProperties;
  private final boolean shouldFetchServerIssues;

  /*ACR-f03d336503a94f5481b0e8e2c2630d0a
ACR-cfc970b5ddab464396117b77decae6b0
ACR-ff25bd095c164504a4918d6f1d615f72
   */
  @Deprecated(since = "10.18")
  public AnalyzeFilesAndTrackParams(String configurationScopeId, UUID analysisId, List<URI> filesToAnalyze, Map<String, String> extraProperties, boolean shouldFetchServerIssues,
    long startTime) {
    this(configurationScopeId, analysisId, filesToAnalyze, extraProperties, shouldFetchServerIssues);
  }

  public AnalyzeFilesAndTrackParams(String configurationScopeId, UUID analysisId, List<URI> filesToAnalyze, Map<String, String> extraProperties,
    boolean shouldFetchServerIssues) {
    this.configurationScopeId = configurationScopeId;
    this.analysisId = analysisId;
    this.filesToAnalyze = filesToAnalyze;
    this.extraProperties = extraProperties;
    this.shouldFetchServerIssues = shouldFetchServerIssues;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public UUID getAnalysisId() {
    return analysisId;
  }

  public List<URI> getFilesToAnalyze() {
    return filesToAnalyze;
  }

  public Map<String, String> getExtraProperties() {
    return extraProperties;
  }

  public boolean isShouldFetchServerIssues() {
    return shouldFetchServerIssues;
  }
}
