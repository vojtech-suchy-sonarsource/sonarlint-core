/*
ACR-1baa28d3384740119f9af41a671ab3aa
ACR-fbcada29ce3345d4b11503ff74ee2e28
ACR-e4b5ba28cdd94febaa3a556af37922e3
ACR-d27258999f68464e951a788668de4679
ACR-56df27deeae6455d95d4fb1f637fe123
ACR-2f6fb416257f46de8d0ef77a399bfd8e
ACR-81c55de88ec140e1833eda5d778bb30e
ACR-326881c72016474fbc7f2541871a1ec7
ACR-c391ee46dd8a40bd95d6d010296974f6
ACR-09e4b369cf2643578ffd982a989958ae
ACR-ca8e506ca82343c7b8abcc5b1a6fdb10
ACR-1fe340313b1b482ba524e6c31fd98052
ACR-81339278ea5344398b3a7a623b9633e6
ACR-20e9b43e098944efac67d76f32c6d2ac
ACR-0b3b0f74fd1a4fa3997dafdb24305c8f
ACR-3869d71b1f084bcebd5d92d1015aed49
ACR-5094b4b5b3ee4ecebdb48b460dae64a8
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
