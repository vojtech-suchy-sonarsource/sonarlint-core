/*
ACR-3afa0c27d7ab442f85bcfc4fc813a481
ACR-08ce3aa7e1f64b5aa59376ee66fbe0f7
ACR-a3b6f257c967494f8ae3b82a4fca3838
ACR-435929f2426d45e388bff4ec4da389f0
ACR-1aafc9def819447598c2719c8f2bb4d4
ACR-689549e0e2c3423199a6d5a30e395519
ACR-1518f4f42e384a1cae136dff4e1d2459
ACR-796ad8297aa34001a576b5310bce97f2
ACR-492ca1b098eb4bcbac5f2f572bcfe1ce
ACR-c974656a0aa74c0f88b038f83584d51a
ACR-8d2ce51fdf53402788584e4050cf5f05
ACR-61463475496445b8ae8b46e0a79599b4
ACR-db6cf4ccc77e4179838afea363daf7aa
ACR-d430a8f91f7f45878d5c333b5b349c33
ACR-99a0753b7b334ab5ad685aa0dc36e572
ACR-dfb5ac36ae564d49acbf6feeda657193
ACR-1955d6c907424ce689fa9c476f96ec12
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import org.sonar.api.config.Configuration;
import org.sonarsource.sonarlint.core.plugin.commons.sonarapi.ConfigurationBridge;
import org.springframework.context.annotation.Bean;

public class GlobalConfigurationProvider {

  private Configuration globalConfig;

  @Bean("Configuration")
  public Configuration provide(GlobalSettings settings) {
    if (globalConfig == null) {
      this.globalConfig = new ConfigurationBridge(settings);
    }
    return globalConfig;
  }

}
