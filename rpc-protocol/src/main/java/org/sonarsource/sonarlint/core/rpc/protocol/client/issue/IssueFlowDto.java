/*
ACR-740bb0da83cf4580819e9918075472e6
ACR-74b4b3af7cda4655ab8aa75754d3d3c7
ACR-2ea2cb9eb1c64a3dbfc4be18d80147e8
ACR-78d6970a7a83440dba8a29a917f63ab3
ACR-49ac7d52817047259e022166415c8052
ACR-be142bc5d5004471886bf5e67d95bd2b
ACR-3b82a093934d4ca6bae667d52c0a5997
ACR-4df290ca75bd4421aaca213eb6e7ba54
ACR-8ffc6450f0c84b18931f9e45aefc73c2
ACR-0ac0bbbbbb4145beb13de4d3fed688d4
ACR-3a54160580b4466f8343dba86dcf70de
ACR-5ebb620d2677484e832655aa77bcabd0
ACR-90456612c0c740779572a627bd744078
ACR-8fa5f89a8e894ad9acb6438fdf3f5f05
ACR-f0025e7abca947fc92447647f6fa3db7
ACR-ba003478e13242e1b839fab2787a4119
ACR-6c19b15c34f440d296ea956f01d81e9b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.util.List;

public class IssueFlowDto {

  private final List<IssueLocationDto> locations;

  public IssueFlowDto(List<IssueLocationDto> locations) {
    this.locations = locations;
  }

  public List<IssueLocationDto> getLocations() {
    return locations;
  }
}
