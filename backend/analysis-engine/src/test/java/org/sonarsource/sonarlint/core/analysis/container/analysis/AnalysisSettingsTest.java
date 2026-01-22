/*
ACR-3516220e4f014b8fa4495da9b2c847df
ACR-3229793783c44a51a17fc21d0fb8893f
ACR-a0e621f7c2614a54a9b6f9a9b8228152
ACR-f8e15a0a07664cb88b3b9ced21f04942
ACR-7c96f84317674e49bb486e9a4802c44c
ACR-e25cde6c7e9246a1b5de04bb9b04c5a7
ACR-40fb32442f2941758819df8e3ed890a0
ACR-e6cfd72be471417fa888eecd627def36
ACR-1c09fc7e56824a99b14de4592b47a6a2
ACR-f6883d0aa4e544819b5d8f049acc2315
ACR-be422dcd65b446d5b1f693f0acf43712
ACR-8623ffa16ec744978c6f3cbc9009e69c
ACR-8e5caa690f6840348e189ff32d7f86f4
ACR-4c8bc1ea1b69479a9210994593e89eab
ACR-9ce14701e7dc4f1795a736d14e49316c
ACR-669e9cdc156b4aa388a3f231a71fa823
ACR-5210c4a09d8e4cb8b5a0a21d8d0c6015
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