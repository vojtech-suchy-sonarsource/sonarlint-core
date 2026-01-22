/*
ACR-ad934a7286124e2aa3becc041900f997
ACR-e2ec3a6e25504c419a58b4e2104468ba
ACR-127f23bfa27b4da687d4e1185517d2d3
ACR-979bb79517334230915530a91db9ed7f
ACR-a10cf42d5f0240ca807b7ce3031263ea
ACR-7f3968c2121f4952b859cd678a726e50
ACR-8623bb59aa914515be223afc4d2f6ba1
ACR-a859eaec50114613a23e9c3b3b3f1ad9
ACR-40918aee05d348f0a5f41e1713687e8b
ACR-d2fc252ea2c345f0a50451952ada7c6b
ACR-45926b563e114c3298d4399b30c3849b
ACR-2a281e645dbc4a4490f118d5d55f9490
ACR-77ba1b7cd4634c8faf214371a9a7b8b2
ACR-faa11f83e4d94a359c58bd356c5338c3
ACR-9bb39a43f3e94d759012d37866407012
ACR-0510f06c938e4063b2278a6d55bc8cfd
ACR-da03927858b74aaf93fb4c0dba5137ba
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckDependencyRiskSupportedResponse {

  private final boolean supported;
  private final String reason;

  public CheckDependencyRiskSupportedResponse(boolean supported, @Nullable String reason) {
    this.supported = supported;
    this.reason = reason;
  }

  public boolean isSupported() {
    return supported;
  }

  @CheckForNull
  public String getReason() {
    return reason;
  }

}
