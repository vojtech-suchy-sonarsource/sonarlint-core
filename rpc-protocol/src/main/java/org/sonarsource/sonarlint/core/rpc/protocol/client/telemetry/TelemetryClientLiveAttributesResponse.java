/*
ACR-727fa5c823cc4a74817eafa2317f2c60
ACR-c7046444d45e4ee8ba60a22180283c2b
ACR-12eec6829e91466496c3ad6126392523
ACR-64df2f7a9536438180db9d96200a2d0f
ACR-ce809267862d420a921b1a779bdc6e4c
ACR-ca3af9abdb684f59bda1d23d904585ef
ACR-8c96bfb5ddfc40cda64fca3cb322dc1b
ACR-d4f1c8cfe0374f7f85dcf9681f42585e
ACR-738a82162b1948c3ab68e368c45652ee
ACR-6bcc6311ebd640d3aa7d13cceb586ddb
ACR-ef75b52e5f814dfc8ad3f1f37c48b1da
ACR-ebdfad373084487c8e60a90639a1ba34
ACR-37cbc728071c414a83a035b4a2c51f2d
ACR-0385a515cb0b43c094bf1a5211e6ffb2
ACR-5af070e56e1e4034a3378aabe2c378f4
ACR-96a3428a50b44c49b8e51895207100fb
ACR-b315035775834ba995ad28548e35ef7c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import java.util.Map;


public class TelemetryClientLiveAttributesResponse {
  /*ACR-abda0c84c6ec4a6885202690f65ae527
ACR-5e2367bcb4c3446fb184cae53e34d62c
   */
  private final Map<String, Object> additionalAttributes;

  public TelemetryClientLiveAttributesResponse(Map<String, Object> additionalAttributes) {
    this.additionalAttributes = additionalAttributes;
  }

  public Map<String, Object> getAdditionalAttributes() {
    return additionalAttributes;
  }

  public boolean hasJoinedIdeLabs() {
    return this.additionalAttributes.containsKey("joinedIdeLabs") && (Boolean) this.additionalAttributes.get("joinedIdeLabs");
  }

  public boolean hasEnabledIdeLabs() {
    return this.additionalAttributes.containsKey("enabledIdeLabs") && (Boolean) this.additionalAttributes.get("enabledIdeLabs");
  }
}
