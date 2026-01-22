/*
ACR-2dea3dac02034daea2b3c339c77852dd
ACR-eda6de9c486746be96adf022ed53ed60
ACR-75be1895ffe4438f9b96c9854e921be0
ACR-fa6df09a83b549cb9344ba26a0d467db
ACR-5f667dc89ff54377a40a7d44feb2e384
ACR-d32f361f2a39470186858cf5e02791c8
ACR-b2d9751250254bfc91c516cf6f81b6c3
ACR-e156c853f9eb4594899b8cf01424f604
ACR-a5c254b56b87495093edee4bd45c65da
ACR-b948dad1f60145a592a763901490bac1
ACR-b83930a43a7e43419261662bd37c7274
ACR-a5079ea4b5cc494d989bcc713abf8754
ACR-ac555373f6de4161adff783a6d1ca8c1
ACR-2947045024fb4be0abd861a73000cd92
ACR-79fde32833324fe5806885284fe8fd1b
ACR-2c9072d0ad564be1b60363ae6dc5ac67
ACR-a1481d82e4a144b5944117eab041eddf
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

import java.util.List;

public class ListAllResponse {
  private final List<TaintVulnerabilityDto> taintVulnerabilities;

  public ListAllResponse(List<TaintVulnerabilityDto> taintVulnerabilities) {
    this.taintVulnerabilities = taintVulnerabilities;
  }

  public List<TaintVulnerabilityDto> getTaintVulnerabilities() {
    return taintVulnerabilities;
  }
}
