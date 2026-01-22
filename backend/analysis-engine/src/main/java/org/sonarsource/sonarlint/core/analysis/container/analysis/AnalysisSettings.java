/*
ACR-8d50a70eb46c43e79efed7f18fcebc90
ACR-226a8009e2a04b1f9827b2be5bd426d0
ACR-15a2026807a544eaa4415fbafe492dad
ACR-cf6174a9ea8748dc9eee629ebffb4945
ACR-fbb0494cf4484169935242466e12e810
ACR-7a70cf164e2c41feb128fda90127e8b1
ACR-c39c4bbaf5144cfcaef86c1a2410062c
ACR-23640b3249ca491c9bb1346bd03075c2
ACR-85a1e3205f3f45fa9dd0fca08c3b6f0e
ACR-04441fbb3e164e85a8d8fa1c85ae3e49
ACR-e6126748a55e4a7084cec3a688a8fbf3
ACR-f5b9156db84b4f30ba30108f0c5bcf6c
ACR-389d79d5d39e4c38849e185936614572
ACR-c48f20df42e14bf0a2a6f8710fcd9961
ACR-29cae31e28ac413f8f89b36bb81f6ac3
ACR-4edccd2533a84e5da06990bb8815e686
ACR-490a184dc2454c4b851d853fd552a483
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
