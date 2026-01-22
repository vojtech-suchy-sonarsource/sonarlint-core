/*
ACR-fba97d8b69f340ab983f976066c6db7d
ACR-68f8bf9839b34c1b851920464d88c655
ACR-903217606e4240079e984d573922e0d4
ACR-ea9f4fd4e1ee49d38da0018a2d86e9d6
ACR-6ae7e93ebfb449aaa0673892b4916cf5
ACR-d132476f18dd47ceb5f2268b0e90787f
ACR-1dfb90c0767448b9b6880a06713d89fe
ACR-77d06a0a21df4c25b45290321d1b4fa3
ACR-ba8c9d70c4c04fe4929a430883763052
ACR-43e60b7ba1f744918b24b7f1376040f6
ACR-27935fecd5604a63b8d2c0e3df2b88cd
ACR-ef8dfe8482cc4cc9b64907da3fc02a53
ACR-3cb93478054b45f3988982360bcb9223
ACR-80631b26fddd4daa8d5b43a81ed56fcc
ACR-1593d68317154db7ad29357cf470ce65
ACR-7fb45c0808a94686a6b5b1aefbdece6f
ACR-c9a954b569214a8a9330f4ee53224c44
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
