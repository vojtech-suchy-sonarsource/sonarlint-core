/*
ACR-c0dcf9919fc04c5bb125e41611bf95b1
ACR-7aa6c22ab0424d8a8ed8a8190c8385d6
ACR-89a41c063a714ad492402dee51826832
ACR-589cc82d781248f3b6b5834cb8618e95
ACR-47fd9140ce574b99a456e439489698f6
ACR-e7c6edb6236148038c44c11ac4760802
ACR-ec5ae78ea7e446c0a744ebaa56c1ca1b
ACR-255898732d40415ba1bcd72310749a42
ACR-0142b41619424d79b26178c4770bc326
ACR-99c5c675f5334355ad29b868e0ea7d05
ACR-e8b0ce2c4ff046928ac49c049f79ed1f
ACR-31161366465846e0824aab3640f70c97
ACR-6fa7035b70ba40e58614b04e44ea59b4
ACR-6fe9fa78573b482bac2acbdef76a19fc
ACR-4996a22e4b6149e9b627d27464671ef7
ACR-8de8b53c1f1d42daafdccc98eeade7a9
ACR-82b8e64d11c345a99843918ab2500c14
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import java.util.Map;


public class TelemetryClientLiveAttributesResponse {
  /*ACR-3c35ee8e42de4fcb9ae75ccbedd2b8ad
ACR-6225358d3a8e4ee7a29b110fee4d9367
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
