/*
ACR-5fe34502d8d8473180ed79136a2afb3a
ACR-597948f0ffb94bbc8f89392b4a6f40c3
ACR-6c9c8a71e818426cab9c2e3bf8418269
ACR-25087262d2a74840bfedb2e5095b6d0a
ACR-89e3410840a8414a9989d366630d95c6
ACR-8335e440f443431d8f33f69f69cb9044
ACR-8355a308ea4e4d71aa9b59e436e7e651
ACR-880d051085fc416aaa14a41172678209
ACR-071e0d7c6d0740ad82f315b325f79c43
ACR-63594fd40a82427eb68d0419ee5fbcc0
ACR-9850a88762ca47898621d1f220a497f3
ACR-91691bd47527480391cf9eb1cadf9669
ACR-1df6cba3e7684aea869163b0aca877d5
ACR-866a472abf394603a6997438e4d90add
ACR-bcb5c5de12944d7faec42ccaa5567055
ACR-624b11240e2d4e5ba54f4db762955dbe
ACR-928b633fec2249b19cd25961e4086741
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;

/*ACR-f624c6a8225a47d59266dc611b011d7e
ACR-8b0c6eefee9a40b8950eaec584619590
ACR-9b3d2c82d037484a8ef54dfc42a66a64
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
