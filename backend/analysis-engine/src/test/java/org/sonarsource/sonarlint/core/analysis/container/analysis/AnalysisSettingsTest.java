/*
ACR-28f3b37418564336a58947a28c91da36
ACR-0160f5201a024813b5a572400d0f3326
ACR-c4c3fe0eb402494f8231c7b4ab6ee0a3
ACR-8789c0acea444644a10e4ee51d049cbe
ACR-3bbfed1439f54fbe9b98089bbe80e0e8
ACR-97930f520f2b41f482afce5d86eb01ac
ACR-380bfcc9a2c647bc96ff1e2c75973687
ACR-648cda2bb590433d9320b494844f1f16
ACR-53fb24d743bc4a56bb939e35e172d20a
ACR-1ad4444022884567990467a09f53d78c
ACR-db87b5ec06b64ba9a55fc9b43f73d2a3
ACR-58414071dd9e4693895c1086b58c2a90
ACR-ec86844d6327461d97b959ca194694f6
ACR-52ebc460cf80477b9a5f634002a8445b
ACR-aa8fe782c9a34e2d90bd43f04d7b5e4e
ACR-f8f3a727192c440a8c40c4431f4cd40d
ACR-d06b7fb908b34ae4a5f66174c61ef9a8
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.System2;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisConfiguration;
import org.sonarsource.sonarlint.core.analysis.container.global.GlobalSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AnalysisSettingsTest {
  private final PropertyDefinitions propertyDefinitions = new PropertyDefinitions(System2.INSTANCE, Collections.emptyList());
  private final GlobalSettings globalSettings = mock(GlobalSettings.class);

  @Test
  void trimAnalysisPropertyKeys() {
    AnalysisConfiguration analysisConfiguration = AnalysisConfiguration.builder()
      .putAllExtraProperties(Map.of("key1   ", "value1", "key1 ", "value11")).build();

    AnalysisSettings analysisSettings = new AnalysisSettings(globalSettings, analysisConfiguration, propertyDefinitions);

    assertThat(analysisSettings.getProperties().keySet())
      .contains("key1")
      .hasSize(1);
  }

}