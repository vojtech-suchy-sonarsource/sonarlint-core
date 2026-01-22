/*
ACR-87df2a949cd4481fb743bdfc38e63d7c
ACR-8e34d429cf6540b19a7b65c2fe9a909f
ACR-a1655e9279184382976cc7fa60f6fe4d
ACR-525722c587fb4d64941a6e75dd271faa
ACR-a2aa307a3ece418d8c66c8df7c1ace37
ACR-da35ee7c14384b119f093a0c962280b5
ACR-d1e23b9d904d4a8cb1dab61fe3309c5d
ACR-df2a423144174dad8912c8523f59fcdc
ACR-c122127bd68144bca7c6d7db7ab80e41
ACR-66d2674ac7cb4388ae6ff89482e236a0
ACR-dc441edf2b4043ef87f74c0f69cd083c
ACR-813a16c122cc476989ececff8de3535b
ACR-deb2a867cbcc4f2797aac5241c911032
ACR-d155da1d768940ee94bb3dc66c3ee86e
ACR-52af1cf425544c6f9de8f84035fe386c
ACR-c4b3a82fee0b432082219299303e9d09
ACR-e9a4147610e1462c8f54c7cc5d93327d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CheckLocalDetectionSupportedResponse {
  private final boolean supported;
  private final String reason;

  public CheckLocalDetectionSupportedResponse(boolean supported, @Nullable String reason) {
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
