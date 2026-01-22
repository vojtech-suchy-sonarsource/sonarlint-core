/*
ACR-b0dc41d5110a4e4f8925bd9f317cdad0
ACR-acb0c0a342834661b068cbb52072d405
ACR-3a2ea9987b234cbe8ebb90e222828834
ACR-55952ed2808445d0a66b8e8be742a098
ACR-689b87c342884acc809b3985ceae6c12
ACR-f2fd4b323c0542fa8363423d82763322
ACR-00e824c23c5d458b91a6d6ead7bd7a4d
ACR-0da96160c70d4c61b0ba435569ea6cec
ACR-376d495d19e5417cba11c7a4aaf41cb2
ACR-f93ef12458cb43ba863f341b5f6648f1
ACR-4191690a38ae491da74f144a154486a8
ACR-bdffffc6fd00405f803e78a919c14e40
ACR-4ecef0be59b1448ca5af495f74150a07
ACR-3e90289a297647febed8e1366bde7142
ACR-bb38ed895ba04d3f9fed7491a3897d5d
ACR-42515c668cae424095b94799e8c8ccc6
ACR-a719a7b6967c4f0798792b85ee150663
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
