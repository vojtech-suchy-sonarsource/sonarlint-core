/*
ACR-0d0ba2d49db64dad92b874d469a1662f
ACR-aca94a261372423eb65e69ba50b9c577
ACR-f309348ecfe843ea83ed5067aaf032f4
ACR-386b4cdd543843e5bcb0d66d9c82fedc
ACR-46ae948e2a504c85949b38d89f733f07
ACR-aba3e05a04714fd1aa659dafd6608024
ACR-2a5321628caa425ea72194106e51fa15
ACR-3fc4c9ad565746f3acd29fb0e68629da
ACR-7f33206f14b247ec9dcca8527e4b8241
ACR-6e0f6a1ea0194e8eba8ce521ac6c908e
ACR-baa3d46058e04e05bb5d4ef4fe8a7e29
ACR-a580fbbe5104408c860f1e2f0aae7ef9
ACR-15e01a5690614aee81f4fa4f4ae43f9d
ACR-242ec6de406b4e56aaaac7c8086f9415
ACR-e28bf4c109894596908eb7e4258734bd
ACR-826128cc895148f1b655fb63bac629c1
ACR-5442ebeba0b54ae48d76eb63ea0cf6f6
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;

public class AnalyzerConfiguration {
  public static final int CURRENT_SCHEMA_VERSION = 1;
  private final Settings settings;
  private final Map<String, RuleSet> ruleSetByLanguageKey;

  private final int schemaVersion;

  public AnalyzerConfiguration(Settings settings, Map<String, RuleSet> ruleSetByLanguageKey, int schemaVersion) {
    this.settings = settings;
    this.ruleSetByLanguageKey = ruleSetByLanguageKey;
    this.schemaVersion = schemaVersion;
  }

  public Settings getSettings() {
    return settings;
  }

  public Map<String, RuleSet> getRuleSetByLanguageKey() {
    return ruleSetByLanguageKey;
  }

  public int getSchemaVersion() {
    return schemaVersion;
  }
}
