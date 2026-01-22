/*
ACR-103f3dbbc2a546859cbfdae0eb7bd3a3
ACR-8942ee7427114cbe9559ac2294b4d969
ACR-73dc5e0e4ab9413faa45fe5153f983be
ACR-6e34a66bcae749ab98fdaaac20ffb99a
ACR-9d5256ddab7f43ae9c103aa623d5ca1d
ACR-23bf7593d09d46fe851d8220a3b1288c
ACR-11dd8d3a333c403c81f3b157613ba243
ACR-ddcbdb60caf643b0858e8b0ef957267a
ACR-3ed936e6feb04fa2a7c8f97f2f0c253f
ACR-72419e1b77324df999329a9432072d37
ACR-a7580d435eaa468884a8b71ead41e246
ACR-2590309d616f46a6bba91fa1ccbdfdde
ACR-07cd2638bdf8408caadf3920a068faaa
ACR-f17f0987d1d64a97b6789370f93fe4a4
ACR-b867e4ab802f4198a78c18f81726fc6a
ACR-1f2ed53558da4a2685992beae233494c
ACR-a47db4e6536645339a4de351a94658c1
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
