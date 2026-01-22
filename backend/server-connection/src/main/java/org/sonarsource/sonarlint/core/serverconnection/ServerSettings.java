/*
ACR-58f3b564bf5b428e813c29b165cb656b
ACR-6fd2734adf0a466097c342e569df732a
ACR-defffaaa9265401086b9faf0a59e7631
ACR-16027466a89042a4b6891f6b6c9de59d
ACR-6fe25a36cd68475294d5aa2c4cbdbe03
ACR-44061dbc10cc4885a8eb7a0ae9fcc303
ACR-28590f34668a45a3885e2bc37983fa07
ACR-022c036610ec48f7a8a816e8396118d3
ACR-6147646efe874ca08b1397ae70808d94
ACR-43194b8a6c6e416aa17fe79a1e5b4770
ACR-9d17e7285ecd42ffb0ceee716f8b9548
ACR-b8f6defe145340dc8425788e40cd7ef1
ACR-f8c22d871a43472f959635ce0323498d
ACR-ac893e03192042b48f1d9f741d5aff32
ACR-6b07a7e6b2234f7a8c9eeeb578591159
ACR-819b492ed8844855a282ad1e46a468ba
ACR-bf47611bbd484faba38ffce7be9af8c8
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
