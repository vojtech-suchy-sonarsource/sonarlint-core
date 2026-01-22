/*
ACR-aff7b9dfa4e54e4ebcb8e48b6ce26973
ACR-e25271cecdb7400884f455c09ff95465
ACR-ba5893eb298e4d0daad95aa5b11c9cd7
ACR-d8926ac97de64a49865dd2656e73a960
ACR-b55d387f318340d4a88b4a9d19ad60d9
ACR-486c7aaf3c4f4d5a9923274aa399cda8
ACR-46f376fe610c47ca8c113551677d205d
ACR-483ba55ae8c54744afd94bcc6cb1ef2f
ACR-4d1ae6beaf624a22a711820755700a78
ACR-d10ec34c169047a5bd412d4b675f87f9
ACR-6904cc6d1fde4f259e75809c3062e2ee
ACR-2b8ce83f35f946d98f068e667d9c0b9f
ACR-08a2ed9563de43ea832f18e46114e2bd
ACR-15d1e13ca7ad4d459be061fb96131f7e
ACR-5766514f4e9548e6ac5c862122e9c8e2
ACR-8e2e627355d34c3fac26e75ed8c6a2b2
ACR-c191736f8a24492bb80af06b78c9ff79
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.ConfigurationBridge;
import org.springframework.context.annotation.Bean;

public class AnalysisConfigurationProvider {

  private Configuration analysisConfig;

  @Bean("Configuration")
  public Configuration provide(AnalysisSettings settings) {
    if (analysisConfig == null) {
      this.analysisConfig = new ConfigurationBridge(settings);
    }
    return analysisConfig;
  }

}
