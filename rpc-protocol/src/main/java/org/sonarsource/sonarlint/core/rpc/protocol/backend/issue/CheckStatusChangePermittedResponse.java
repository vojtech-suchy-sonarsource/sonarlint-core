/*
ACR-4d1d433d965040f8910eb6ce0364a1b8
ACR-ca27b4006c974622b93b605f01cceb8d
ACR-01f654a8c84f4c799f4b3cb7bd4189f9
ACR-46180dba69e64eabaaa29444ef3295c3
ACR-d1f7de4a7b504c3982f5f3e523a2309c
ACR-fc446aaa398746aa965c605063be4f59
ACR-12ceba16cf684ebd97ea8c223870f66e
ACR-c5acdd502e6f439cac3438204b06b882
ACR-11161f3e51ae410ab9f5c74bf1a20aa9
ACR-2dc0e61bee044f688706973a41c239d6
ACR-9f33baf7d50c4532a563df390140f699
ACR-af02b1263f72466d89af05f93a4368dc
ACR-bb3b813d8eb94d1e96e1d224994d2e84
ACR-90090f9210804745837c2196ed8ed74b
ACR-a46921921d214c4eb1848cc884bc6c85
ACR-a315b3fbffd747d48019baeb209f4820
ACR-c7fa9fb022e74832831f55b122f873ea
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
