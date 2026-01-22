/*
ACR-d8e80dfaa2ee44dc9f12e14dbf45ff63
ACR-b2664f8b67464ad4aba41f749d056ceb
ACR-a82054ee332c4771a82997a8e2500468
ACR-73bd9ae2f9a547c7b72985c102313d86
ACR-a26967e9179a44d592c148bf22821b99
ACR-d612dbd59e184d058ac91b298acab2da
ACR-404f009265554d699c8d26a8a9384fb7
ACR-95730e5b542a411bb262ba58c02af095
ACR-380f52eb68fe4107b7d56298db32812e
ACR-8d2877b0d32540e78c65d11ffd6ed7cb
ACR-6f759fba1839435c8d9c41b851494073
ACR-63fca50e8f1b45919039df0be00e5320
ACR-bec8393ee4d14f7fb26b3f2264f5b4ab
ACR-fb25514317d149529c18b3b08e638d15
ACR-a4bcbc7a04dc4a62aace6236fc338ac4
ACR-bec7b10d3ea64d25881e9249eec92257
ACR-5337695100d24480a6733abeae91f2ea
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;
import java.util.Optional;

public record ServerSettings(Map<String, String> globalSettings) {
  public static final String MQR_MODE_SETTING = "sonar.multi-quality-mode.enabled";
  public static final String EARLY_ACCESS_MISRA_ENABLED = "sonar.earlyAccess.misra.enabled";
  public static final String MISRA_COMPLIANCE_ENABLED = "sonar.misracompliance.enabled";

  public Optional<Boolean> getAsBoolean(String settingKey) {
    return Optional.ofNullable(globalSettings.get(settingKey))
      .map(Boolean::valueOf);
  }
}
