/*
ACR-72b656eff1d5423bac8e3e5030443ebc
ACR-d74521b69b994456bac937044621dfcc
ACR-0a867af37dd74d33bc41461896fd8959
ACR-dacb165b11474ea48538fed7277209f0
ACR-cfcd85911a154a03a02ff5ca405f5208
ACR-39a9d57a8d38453887b4e71fc6766c4d
ACR-6dda570ebbbc4dc884a65823a4b7d440
ACR-b55805f562264cbd80188330a6d77289
ACR-a69fc40678b146da86b86a640efa5bbe
ACR-b43c1fbb2c7a41e3b2a28f5059a9f819
ACR-99dbe95de6a64c35ad3fb92ac0cd6272
ACR-6a49c5f89c124c52aec6184559596614
ACR-63fa01e18f7846169eef232ee0c9e868
ACR-e95b8b9369e74821a1511aff8faf17a1
ACR-eb8780165b7f4fe6a422872f58702ea3
ACR-d0d4fed244bc479688088e7d5837892e
ACR-af8360d0e50f4189b9b512a0ed471865
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnalyzeFilesAndTrackParams {
  private final String configurationScopeId;
  //ACR-87e63929ad09461c91191f11728503b9
  private final UUID analysisId;
  private final List<URI> filesToAnalyze;
  private final Map<String, String> extraProperties;
  private final boolean shouldFetchServerIssues;

  /*ACR-a9ff98f2119a4ef58478070c685a76bf
ACR-fd212399c5794ccfb5e92dac6ca50a8b
ACR-61a6939522424b218087ecff144d8aec
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
