/*
ACR-751f7ace7d70403e92dc41ad969657d3
ACR-59f3b19724634af2bbf8af43970eb376
ACR-7c3cedce39cc459f87da5b45675a045a
ACR-bdbfc20abfb2497899766523087fcfc2
ACR-04cc7015dbbe493aa03d062e49ba4e3d
ACR-6509ad6145f94a6492f1b8d859d3b108
ACR-3c8f500307d648db8e5c3147bb1beb24
ACR-5fb975e3321b422aaea971805272cf06
ACR-bb22cfec41304affbe52ad9eaa841737
ACR-1357b09495994ec6a4d6df0320cf908d
ACR-47a5c2bfce144c91b0329b3bd27f6f38
ACR-ce19af347c184648aa96161401fda240
ACR-44932122418a443497e4d6fda362cd8c
ACR-767778d300744decb2565ff9eaa22af4
ACR-31bf5751348042f7b6c9d5c6c5b8ff33
ACR-3f9524f32afb484cb78c3a1d40c73679
ACR-34058d8794974e38b5bd8bdf760812bb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-ebd4bc767f5649bfa1ecbafde243904b
ACR-b30e90c370f44c1490563af40c9ecfec
ACR-25e2b66e7815479babe778f5b7976751
 */
@Deprecated(since = "10.2")
public class RawIssueFlowDto {

  private final List<RawIssueLocationDto> locations;

  public RawIssueFlowDto(List<RawIssueLocationDto> locations) {
    this.locations = locations;
  }

  public List<RawIssueLocationDto> getLocations() {
    return locations;
  }
}
