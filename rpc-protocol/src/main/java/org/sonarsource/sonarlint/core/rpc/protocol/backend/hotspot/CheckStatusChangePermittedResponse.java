/*
ACR-84e4afeb314c4f3291ebe29a86f5e0f9
ACR-bcb3d2b9c3db46a6a1ec9bd40e67f397
ACR-e3b5e7e0202c4d17abd37ad8d006e58e
ACR-06e453b400b640e68f19b846ff25a7dd
ACR-3bf39f1ecd874019a091ebfe453aeede
ACR-8b292d09e1004745b290f85fbe13c300
ACR-3350d2bd44e84e3cbe77352ed0e9f5fd
ACR-6fca30b6fad6487bab6c4d7f152fa217
ACR-9a8deb8a8fec480ba329768380b8fe09
ACR-592b813207de46059cf3c2d87f7dbc3f
ACR-ea2bbcd82465433a81b6aea6f2ebf0d8
ACR-1953d4dfed644adeb3c481285b2e6131
ACR-9d8846df3f1a4eda8b40e43978d6f980
ACR-756d009db4e94c4390931966ee95bfbe
ACR-153527f1de38419d84cca930c15e81bc
ACR-358f5adb4e1446eb82b509b21bd2953b
ACR-26fb74dd8fe841c486096df4b2020827
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckStatusChangePermittedResponse {
  private final boolean permitted;
  private final String notPermittedReason;
  private final List<HotspotStatus> allowedStatuses;

  public CheckStatusChangePermittedResponse(boolean permitted, @Nullable String notPermittedReason, List<HotspotStatus> allowedStatuses) {
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

  public List<HotspotStatus> getAllowedStatuses() {
    return allowedStatuses;
  }
}
