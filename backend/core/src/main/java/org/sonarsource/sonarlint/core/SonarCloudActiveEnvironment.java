/*
ACR-2a1bd3b9c1d84efaa01ac3c25b0e53b3
ACR-e4a11c16bac14232917e5f0ec78ffdaf
ACR-bd314e99dc574a3da375b33119e5b5c7
ACR-ae71c50c292e4a93b4b0ec131a420d12
ACR-49825a1a53294e64be46ff211b3f8120
ACR-54b92ef3c1ce46ee8ad426cffa387b6e
ACR-c277a32fca874ee087f600b59d03e8ca
ACR-592c4739d2734b66a181b58e51d72327
ACR-8574fbc634f449d3ac235a8ea6dec8e9
ACR-d4f2955af18445a191716bd3001aa004
ACR-646bc71f26c94a7dbb2476864531204a
ACR-eb2508b2396f4217bbfd9179f46246be
ACR-b2cc2da831bc4a5ea2bd7b16124c4a37
ACR-e2b2005422a34ebfaca5ea964ea7460f
ACR-f91a44a3dcd64cc988ff8f1e660193b2
ACR-865cec911f0441d1a4b9c6c0ac16adb8
ACR-fb0710cdc2e74fa5b0727dc71d2d2d8f
 */
package org.sonarsource.sonarlint.core;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Strings;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SonarQubeCloudRegionDto;


public class SonarCloudActiveEnvironment {
  private final Map<SonarCloudRegion, SonarQubeCloudRegionDto> alternativeRegionUris;

  public static SonarCloudActiveEnvironment prod() {
    return new SonarCloudActiveEnvironment(Map.of());
  }

  public SonarCloudActiveEnvironment(Map<org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion, SonarQubeCloudRegionDto> alternativeRegionUris) {
    this.alternativeRegionUris = alternativeRegionUris.entrySet().stream()
      .collect(Collectors.toMap(entry -> SonarCloudRegion.valueOf(entry.getKey().name()), Map.Entry::getValue));
  }

  public URI getUri(SonarCloudRegion region) {
    if (alternativeRegionUris.containsKey(region) && alternativeRegionUris.get(region).getUri() != null) {
      return alternativeRegionUris.get(region).getUri();
    }
    return region.getProductionUri();
  }

  public URI getApiUri(SonarCloudRegion region) {
    if (alternativeRegionUris.containsKey(region) && alternativeRegionUris.get(region).getApiUri() != null) {
      return alternativeRegionUris.get(region).getApiUri();
    }
    return region.getApiProductionUri();
  }

  public URI getWebSocketsEndpointUri(SonarCloudRegion region) {
    if (alternativeRegionUris.containsKey(region) && alternativeRegionUris.get(region).getWebSocketsEndpointUri() != null) {
      return alternativeRegionUris.get(region).getWebSocketsEndpointUri();
    }
    return region.getWebSocketUri();
  }

  public boolean isSonarQubeCloud(String uri) {
    return getRegionByUri(uri).isPresent();
  }

  /*ACR-a51bb6e56cb74983bdf7be006d63c36a
ACR-9f34403f8cfc4eafb620383aec1f84a6
   */
  public SonarCloudRegion getRegionOrThrow(String uri) {
    var regionOpt = getRegionByUri(uri);
    if (regionOpt.isPresent()) {
      return regionOpt.get();
    }
    
    throw new IllegalArgumentException("URI should be a known SonarCloud URI");
  }

  private Optional<SonarCloudRegion> getRegionByUri(String uri) {
    var cleanedUri = Strings.CS.removeEnd(uri, "/");
    for (var entry : alternativeRegionUris.entrySet()) {
      var regionUri = entry.getValue().getUri();
      if (regionUri != null && Strings.CS.removeEnd(regionUri.toString(), "/").equals(cleanedUri)) {
        return Optional.of(entry.getKey());
      }
    }

    for (var region : SonarCloudRegion.values()) {
      if (Strings.CS.removeEnd(region.getProductionUri().toString(), "/").equals(cleanedUri)) {
        return Optional.of(region);
      }
    }
    
    return Optional.empty();
  }
}
