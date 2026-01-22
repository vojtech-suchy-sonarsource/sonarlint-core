/*
ACR-c95b8cb5bd5b4aeb92fc31f92419cd92
ACR-c72438dc1a8a404081cee3dff3b7e9b4
ACR-f61e868f96b94a2eab1c6c2ac00e5355
ACR-3d4872ecfd334e2a84fd5b85b913892f
ACR-2ec496493296451ca224861d4d038841
ACR-a0b86c1f02a24abf8e762696c4c416b7
ACR-d497681f3b5d48d7a0a148f46d0efe69
ACR-d2f73e08eea64e3e8ad283aa031ca1c8
ACR-d7d9d45a5f8e407fbe452784f7e5c45f
ACR-cb1a01e0afad401da3eaf87c5c4152f3
ACR-0822fa5c639744638019a3cfbd957d0b
ACR-4d4efb3161dd4339b9185e7b5262906d
ACR-b2d9a1dc092b461b8cc708a42a6e2616
ACR-434584b830bb42678976b721c8c47b03
ACR-ab880764826743669d426aea7f287b1d
ACR-d97860a974494cdab86130126fee0e21
ACR-954e5bdc91854d25826865327221b362
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.util.Map;
import javax.annotation.Nullable;

public class TelemetryClientConstantAttributesDto {

  private final String productKey;
  private final String productName;
  private final String productVersion;
  private final String ideVersion;
  private final Map<String, Object> additionalAttributes;

  public TelemetryClientConstantAttributesDto(String productKey, String productName, String productVersion, String ideVersion,
    @Nullable Map<String, Object> additionalAttributes) {
    this.productKey = productKey;
    this.productName = productName;
    this.productVersion = productVersion;
    this.ideVersion = ideVersion;
    this.additionalAttributes = additionalAttributes;
  }

  public String getProductKey() {
    return productKey;
  }

  public String getProductName() {
    return productName;
  }

  public String getProductVersion() {
    return productVersion;
  }

  public String getIdeVersion() {
    return ideVersion;
  }

  public Map<String, Object> getAdditionalAttributes() {
    return additionalAttributes != null ? additionalAttributes : Map.of();
  }
}
