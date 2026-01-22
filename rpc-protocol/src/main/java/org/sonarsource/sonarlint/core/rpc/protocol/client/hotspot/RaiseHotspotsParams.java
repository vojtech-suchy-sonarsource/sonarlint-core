/*
ACR-29445d1bd11f469f9867955e1b7f08fc
ACR-e2a3e2f30db24cf3a6c61f39b3ad5a14
ACR-990561082cb24301b7fb00c26db3cab7
ACR-27ebc24fa6d548ac91fa6abf6a918213
ACR-fc8c762c9e564d5292d6800b9e7b93bd
ACR-fa5765ccd7234c689bcf266182a175f6
ACR-62cd2ef46e5b457188c523fb3e4d89fc
ACR-d2199edf91a9434fb6b18ac63027a28a
ACR-b7cc9fce7a834a4b9bfcfab8ba59b01c
ACR-2523ca94226d4a0f9584e18110838d9d
ACR-fba04c5969f642e8b1710c6dcfcd5c55
ACR-033997e0d00c4579917f613f47505ed0
ACR-2f27a615fd53497a8a0ba3a32b5653eb
ACR-86b22410876e4323b6d4a893e563abc4
ACR-ef0b05007ae24d399079dcd7de76753f
ACR-8e44a8b2c7e44e0f8fba46551baefa94
ACR-f2d177fd7d78493398987059d5fe931c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class RaiseHotspotsParams {
  private final String configurationScopeId;
  private final Map<URI, List<RaisedHotspotDto>> hotspotsByFileUri;
  //ACR-8e6d18933efb463881d4b4f34e9e26ff
  private final boolean isIntermediatePublication;
  @Nullable
  //ACR-22cde99e93374fd0bbc6b8601131c2d7
  private final UUID analysisId;

  public RaiseHotspotsParams(String configurationScopeId, Map<URI, List<RaisedHotspotDto>> hotspotsByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
    this.configurationScopeId = configurationScopeId;
    this.hotspotsByFileUri = hotspotsByFileUri;
    this.isIntermediatePublication = isIntermediatePublication;
    this.analysisId = analysisId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Map<URI, List<RaisedHotspotDto>> getHotspotsByFileUri() {
    return hotspotsByFileUri;
  }

  public boolean isIntermediatePublication() {
    return isIntermediatePublication;
  }

  @CheckForNull
  public UUID getAnalysisId() {
    return analysisId;
  }
}
