/*
ACR-099e6a1ac300433faf374f36da562ebf
ACR-24c822da321e4b52b6d50e746bc3540f
ACR-54f47d7b8af84d1aaba88b65e7751810
ACR-105f33969bac41fa99137b04cb090fbf
ACR-aa018a8fd9b64fb992f24873c305e61f
ACR-98833620e6e9411a9909eede8a32f9b6
ACR-6837466e5031415e8f8dbe39270e89b5
ACR-8df42f765d5540ea870230a2eabd8d4c
ACR-a589b902c6ee4b5999fbc12ea905d982
ACR-3d70b5dd3fdf4665a0e6a06b35ec7f3e
ACR-2f1216ca5ad94427a324205f1cac5883
ACR-eebc369fc6a44244867d2b0689501457
ACR-b715b790b7a94cd1a2c4bcd2f3c2b79a
ACR-e3f567cbad6641488e2a36c8d046613f
ACR-9b15af03ab48431abdde27bd21769b5e
ACR-9380d33e780a4e2e99590962b96e3d6a
ACR-cade7498d6fa4547854e875c0071cc78
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

/*ACR-248f158cb56c4153975875fc5b5e870b*/
public class SonarCloudAlternativeEnvironmentDto {
  private final Map<SonarCloudRegion, SonarQubeCloudRegionDto> alternativeRegionUris;

  public SonarCloudAlternativeEnvironmentDto(Map<SonarCloudRegion, SonarQubeCloudRegionDto> alternateRegionUris) {
    this.alternativeRegionUris = alternateRegionUris;
  }

  public Map<SonarCloudRegion, SonarQubeCloudRegionDto> getAlternateRegionUris() {
    return alternativeRegionUris;
  }
}
