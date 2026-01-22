/*
ACR-cdf78053390b42598b59549b191c983a
ACR-9eeeb9dde4e74904915f2759ec565387
ACR-7bfac5fb838840e5bef91b7cc8af7cf2
ACR-3cec481b22db40018d524d0bfe5f71ee
ACR-ea742f48b6a044ebb418118cac8e1c14
ACR-1a8e6b3a766a4597be2dd154e233dcd5
ACR-52e13313777c47a186609d8b81fbcc99
ACR-d43844d829a249c7ac7c7c551da6c6e8
ACR-7f3e74ba530449fc885347d1a63be887
ACR-344aea8995bc415facf6af054866e56c
ACR-b047d740f33e48c2bf5323f56eea7c6b
ACR-c36408ca54394425b1098ef4eb7a6649
ACR-3f8fc8fd70c8400c810cd0d2e945102e
ACR-60c6287660b545fca05fcdce7fbb7c86
ACR-87e2df8fed4943f8a9ef0122b0aea68d
ACR-e59ccbc5e293488b9966630c42ba7ddb
ACR-c4b66cee9b06495dbf9fb23e85ee32c0
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
  //ACR-faac78843b0f43e7a864de360e90e088
  private final boolean isIntermediatePublication;
  @Nullable
  //ACR-9128da0b4bea4dabbd987e53fd382deb
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
