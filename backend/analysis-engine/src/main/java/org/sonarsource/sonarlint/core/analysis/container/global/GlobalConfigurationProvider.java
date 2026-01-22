/*
ACR-fd57534db613411f8d1a9d27a75d1035
ACR-2c52c0b3f7154673aed5c31604d0be6f
ACR-136ed850e79644f89af8f035206c00e0
ACR-80b6a6fb8a4d45bbb34680dc68770a57
ACR-5ebbed759dda4fb4b898962d948f80c7
ACR-f841503b2695422480870537d508e6d1
ACR-90d3a0b4b73a47659501860f1b56b9e3
ACR-73b91909a5c840e7af66a2e7d0965bc2
ACR-550c84aa415944808ee5e87b50d756eb
ACR-af56df0264624d24bdb88aafc00e878f
ACR-5deacd29278741048c8b27528298ecdd
ACR-1d683ef1f057411cb7c3921bc7eeee51
ACR-df4b2681d2064fbc9b3c1b0357f2eeb5
ACR-4718fdea371246e595958a6ae218e7f3
ACR-d43d1939fb0748a58ffb4b846bf43faa
ACR-a9be97db69614969a46900dff00f2423
ACR-a85649b509744fdd97aa109a2ad26acc
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
