/*
ACR-5ba71abaacff40d09e0fd63b3a525de4
ACR-bb4be005d3f343fc8a6302671a9e4b07
ACR-c3e783ba502c4bd98dea5038ac788507
ACR-0b7b80aaa47e404d8570e2cabf1c657e
ACR-a0be763070fa472297647a8351b10f44
ACR-d063442e811340e48487cfcebf7f246c
ACR-dc690d549ede48c49547e73150572a0d
ACR-3d236b08d4f54a1988682c7742e65fc4
ACR-33be0e5c0867499a825a5845f46144af
ACR-1b52943849ce4ec987c2ebc5d66dab76
ACR-d9b4958f167249668f39e3f9cde27a30
ACR-bbc47c04bcf1401f94dd5351c10cd88e
ACR-043a8af0d9ce4d8a8800788352b7e286
ACR-e7de7fe4953a4e2887ba1308059eb3fb
ACR-c9e418617d0446d2ab5ef70f68af4cb6
ACR-a484b827c6ee479fa3698d8042ca13bc
ACR-f476ccd182554be2a61e680341a44942
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;

import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

public class SonarCloudConnectionConfigurationDto {

  /*ACR-a3741c4bd3834a5ca7dfb84963c00089
ACR-dd6bea659f654480a6181b3f78fec4a2
   */
  private final String connectionId;
  private final String organization;
  private final SonarCloudRegion region;
  private final boolean disableNotifications;

  public SonarCloudConnectionConfigurationDto(String connectionId, String organization, SonarCloudRegion region, boolean disableNotifications) {
    this.connectionId = connectionId;
    this.organization = organization;
    this.region = region;
    this.disableNotifications = disableNotifications;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getOrganization() {
    return organization;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }

  public boolean isDisableNotifications() {
    return disableNotifications;
  }
}
