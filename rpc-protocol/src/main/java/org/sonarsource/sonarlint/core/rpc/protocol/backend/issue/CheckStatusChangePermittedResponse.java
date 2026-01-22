/*
ACR-c87aca99d71142b59844e9ba180c677d
ACR-03c91522e30045b68db34265b5bd8314
ACR-88e27e8dbabb44f2970235db707f8491
ACR-a31f6e31bb9449d3901f0b8324ced7d2
ACR-c4b3549d29c846cf9a3fa4f54eb8c0d2
ACR-70aa4d2663374da796a98933b727a088
ACR-13c21c05b357469aa41dddde97fe78fd
ACR-ce71b9fe033446cdad1c2304000637c3
ACR-ca99ea3357cd42669a0bcd4c46dd669e
ACR-3075009da6ba4d498223fcac690e3b6c
ACR-a2d1bc1176704561a86063a4320b2012
ACR-7e65101d497e4a85a74e486b11f171bf
ACR-78af86369c734bc3a13b3b74a8846043
ACR-27f1bf1cd3aa4ff683c381428cecf51b
ACR-dd01b1a803944b30a9b6a606ac96c1db
ACR-91ce435731c347ebb069e4b6a9b76536
ACR-e220ad9f0644461ba7b0482992dd2c9a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckStatusChangePermittedResponse {
  private final boolean permitted;
  private final String notPermittedReason;
  private final List<ResolutionStatus> allowedStatuses;

  public CheckStatusChangePermittedResponse(boolean permitted, @Nullable String notPermittedReason, List<ResolutionStatus> allowedStatuses) {
    this.permitted = permitted;
    this.notPermittedReason = notPermittedReason;
    this.allowedStatuses = allowedStatuses;
  }

  public boolean isPermitted() {
    return permitted;
  }

  @CheckForNull
  public String getNotPermittedReason() {
    return notPermittedReason;
  }

  public List<ResolutionStatus> getAllowedStatuses() {
    return allowedStatuses;
  }
}
