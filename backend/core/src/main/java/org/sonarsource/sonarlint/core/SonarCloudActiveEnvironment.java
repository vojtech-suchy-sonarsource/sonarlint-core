/*
ACR-cc84036f7b414cdcacd719deca6f3f01
ACR-53eab2aa421b4887aee69cf38bb4ed43
ACR-c7cbeaf6a082456eb4130fceb7857f32
ACR-76095da02eb94753b1c104a52cc635a0
ACR-d92d7569401c401c97f15db5945bc35f
ACR-6944d8abaf6c4251b65ba9cfddd433eb
ACR-eb68b647486e445daf59444c619eb773
ACR-ae2971e21c4142858936fa6263e1c07f
ACR-ba9e2129813b427caa24c81790e5a102
ACR-dd7dd65928614fedac0b6cf052fdbb4f
ACR-04a6c4a8506042bbab49277356dc4a51
ACR-ad58d3874a624613be6f90cc93d3827e
ACR-a5acacdedc864d6485e7eb23a2b6da7a
ACR-a66bbba76dab4334ae7ba4344351fdc3
ACR-85960541c48e4de990caa7fa29888424
ACR-7fcb22ab980341f0b9ed87c1cba92f11
ACR-fafaf4b3dcc348d9b86777ee746f7cb5
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

  /*ACR-6e12c43aa6874963a0f028c5e1b9d20e
ACR-cfead7d8ea0a420b822ac0e8ce06e2b2
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
