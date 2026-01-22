/*
ACR-9b6b471760054ab08dce705e85b09901
ACR-093ae2a2f1bf4c6bb52e24d9e5788fef
ACR-73978f7d15c640988236ae1818ba8575
ACR-488a9fc0bcbe45adb432b8c9cc9d247b
ACR-53301e67ded54e96b947aac488a6eaca
ACR-b30a9c045a814349961cade1e588e0f9
ACR-9f4ac5687f994a9498c7507720a6f171
ACR-e34e61eb51664c3d980b3fc9d37b40aa
ACR-103ece3ee48242fd878aba180920eec6
ACR-d7ee73f7eb574774bf0b71aa7fa9b416
ACR-2ebad11afb9740528a0382c2435b5a93
ACR-d04c2cd833fa4ea69da4d94ee218efb8
ACR-498cff891a7346f2afd0ba22dba2809f
ACR-d1cc67490ca34a5d83263510e2a03d27
ACR-3c52db95999b46d695f4eb6af107b202
ACR-6c848fd9c8d444bd8dd20eeb0c674a59
ACR-0a1cf35259c94445a7dfc8c69b581458
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
