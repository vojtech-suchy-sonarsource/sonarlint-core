/*
ACR-7436793ee589488c973bb8bb65d45aac
ACR-9a4e3501968444338289ac5f44ba4497
ACR-f49f9f04b28840b4925242aaf3e9cfa2
ACR-91130ef4efa5486ca61d8ce99cc62c20
ACR-95db5a3ca3d34644965e2adf1f2753a1
ACR-d0bdeff0fd3249aaafff94cdf4c19bd7
ACR-b44e211aa64240db97e253e385365894
ACR-a2d3606af5164e448940fdd3fe415f22
ACR-70f4011aa1f6494ba7f57145c35c97bb
ACR-6bfdcecbd215426b88edcd3651a28410
ACR-3fa963018068494d91e582671caa16ee
ACR-ce7230ff7b664d1eac0f23e6bcc0cb7c
ACR-90a54136a1e0482884db478864c8f084
ACR-8ef1b5ad15bf4e7b8f4f661dfc2aac08
ACR-a0cb1558421c410b9dc5dc9b12a4ade1
ACR-9f5712226ff54294a88c64349d76190e
ACR-05c658b0665143c280fbf8efd5e58545
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

/*ACR-725eee118e9b4cca8318b73ae1a25765*/
public class SonarCloudAlternativeEnvironmentDto {
  private final Map<SonarCloudRegion, SonarQubeCloudRegionDto> alternativeRegionUris;

  public SonarCloudAlternativeEnvironmentDto(Map<SonarCloudRegion, SonarQubeCloudRegionDto> alternateRegionUris) {
    this.alternativeRegionUris = alternateRegionUris;
  }

  public Map<SonarCloudRegion, SonarQubeCloudRegionDto> getAlternateRegionUris() {
    return alternativeRegionUris;
  }
}
