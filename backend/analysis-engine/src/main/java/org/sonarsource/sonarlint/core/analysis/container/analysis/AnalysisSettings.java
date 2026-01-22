/*
ACR-1caec4b12d22474f84746be1de2a403a
ACR-5f743065e3b243a29dde185e12e47ab4
ACR-39ffeb121756467ab398755e30f259d5
ACR-f712eecb7b4c418694a61c147d2a28ca
ACR-c660e15dc0ef480ca67ed18dd95bcd0a
ACR-cd7b92c10c814f88b67abd817875aba9
ACR-41d8bbc5028f4587b0837742acee742d
ACR-1fa3f48eebb14cc7ba7634d07b798f3a
ACR-22923ba25a8b4c65892df230dc03ff43
ACR-5d67c1344da742fd98cd9799dd01a0b8
ACR-a89be87051d740a9b1a26f19cace1744
ACR-b3de305781e54664a1c5fb93af92ee1a
ACR-dd93f920a6fd431e9051646905ff7eb3
ACR-c232fb68862441bd93cfa587386daa5c
ACR-22712cfeab0b46f2b1f41a0a860b72a2
ACR-a9701537eaca4548995474702a711035
ACR-edc25a4138cf4c44bebfe0786bc48d5a
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import java.util.HashMap;
import java.util.Map;
import org.sonar.api.config.PropertyDefinitions;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.container.global.GlobalSettings;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.MapSettings;

public class AnalysisSettings extends MapSettings {

  public AnalysisSettings(GlobalSettings globalSettings, AnalysisConfiguration analysisConfig, PropertyDefinitions propertyDefinitions) {
    super(propertyDefinitions, mergeInOrder(globalSettings, analysisConfig));
  }

  private static Map<String, String> mergeInOrder(GlobalSettings globalSettings, AnalysisConfiguration analysisConfig) {
    Map<String, String> result = new HashMap<>(globalSettings.getProperties());
    result.putAll(analysisConfig.extraProperties());
    return result;
  }

}
