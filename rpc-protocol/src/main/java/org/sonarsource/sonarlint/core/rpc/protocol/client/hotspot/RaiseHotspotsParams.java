/*
ACR-e6516814f6264436b3af200c69a97416
ACR-0f31a54641fa45a9ba341717be24c603
ACR-69cd6dc7c8c9425686c97163057dd15d
ACR-f1a7430aaf19422db530f648a47f8a8f
ACR-46f7d96870d24537877e7f2505cf3234
ACR-f516bf3fd55740a99eab3e6d64d9173f
ACR-a093432fed8d486788617b4de38a2fba
ACR-5932452562cf49ec878a43dd49fa19a8
ACR-495d28637e7940b791bb0c2b2580796d
ACR-0e605826376c4127845448eeb09fb853
ACR-0c4e14a0e2c2494c8caea93daf8662e2
ACR-d60ac1ed3bbc4c84bcb0550f663b518e
ACR-9758fa7c99e64584a6a86161f255f009
ACR-9c03cd081ff641fca80638375aab905e
ACR-dda213b826b543c5bbf5f106c1ee2007
ACR-097aaf2e8f1c4eb3ad8cc86bfde9c64d
ACR-7f1c2d1e3b3f47d985e75e2e2d129ebc
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
  //ACR-f8db87f7f1424945a4deea6990d06cfa
  private final boolean isIntermediatePublication;
  @Nullable
  //ACR-a976b9aa42364521af35bd6d19c65420
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
