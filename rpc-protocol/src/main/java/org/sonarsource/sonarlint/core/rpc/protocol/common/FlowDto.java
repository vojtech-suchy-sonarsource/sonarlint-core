/*
ACR-f4c53420540e45db9b2c9a92d0233ec0
ACR-1db2765c6272496daac38a938bd76805
ACR-cdcf094b9ac149488863a8b861b3ed69
ACR-a6c68e65efb84f3db1057bee8127239e
ACR-bbe1d97a034c4ba7afbfbca47c5cec8a
ACR-a6d1a0285cb34c56a3743719810de090
ACR-3bad506efdeb4caf844f2a14915a655d
ACR-4ba49afcb47c4b379cde2a155694dc19
ACR-89b65451a18244af9bf495a4767701ab
ACR-e5481f16e50b4a949cea23b8a0eb8908
ACR-6f5c7ba68d3f41c1aa7e22352bffd0f0
ACR-706d6702e4174586822cdd601d1d8efb
ACR-8cde90089ec5489faee82a9275e4736b
ACR-bfaf0e0d101e45dabd47ec142de4bdc4
ACR-97a37b71b481422984ac3de75f531ef4
ACR-bf19e29e44414240ba07d9908120a014
ACR-0241a9cb372c45e58896e216c8d02809
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.List;

public class FlowDto {
  private final List<LocationDto> locations;

  public FlowDto(List<LocationDto> locations) {
    this.locations = locations;
  }

  public List<LocationDto> getLocations() {
    return locations;
  }
}
