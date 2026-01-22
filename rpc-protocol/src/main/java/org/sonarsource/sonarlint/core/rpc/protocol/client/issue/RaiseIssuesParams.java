/*
ACR-b42c9192f43c40e0a4ba152397bdb68a
ACR-649fd34d6b9f49bd9a40b8eda1720a8a
ACR-764ba9115b0b4f3a9e530a4b2be5ba59
ACR-e0bb8e71205e4770af2b18a5c54b99e8
ACR-cd9a3bb232b74e7d86366e8ca4f57d64
ACR-94200a46feda401db1be95838e36c16a
ACR-2a88e56e1130459db7d86f44f348fdd5
ACR-16fd5b1139c543e1b9b265f1e7ce430c
ACR-d3a1d57e58a140cca1538d8771e1cc1e
ACR-3a0d5516dfa84eb89f68fa1bfb5a7b63
ACR-911382f1e5164034b8238067d6e5f0b7
ACR-b997fbaf6cd24b48b535054df4aed6d3
ACR-aa7eef36d0f84fa7b97691608c05483f
ACR-db3ef59924634b2fa71b092fb38cdb63
ACR-c9b0170d9bde48e59b1d11946646a29c
ACR-5a7fba924c2f44088403706053390db7
ACR-099db2f9d9ee4c099365badc65b7c0e1
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class RaiseIssuesParams {
  private final String configurationScopeId;
  private final Map<URI, List<RaisedIssueDto>> issuesByFileUri;
  //ACR-351c29a806444f3cb22e712ba9259bd8
  private final boolean isIntermediatePublication;
  @Nullable
  //ACR-bce6e32a761e4d2f94ac89bbc4ff342c
  private final UUID analysisId;

  public RaiseIssuesParams(String configurationScopeId, Map<URI, List<RaisedIssueDto>> issuesByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
    this.configurationScopeId = configurationScopeId;
    this.issuesByFileUri = issuesByFileUri;
    this.isIntermediatePublication = isIntermediatePublication;
    this.analysisId = analysisId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Map<URI, List<RaisedIssueDto>> getIssuesByFileUri() {
    return issuesByFileUri;
  }

  public boolean isIntermediatePublication() {
    return isIntermediatePublication;
  }

  @CheckForNull
  public UUID getAnalysisId() {
    return analysisId;
  }
}
