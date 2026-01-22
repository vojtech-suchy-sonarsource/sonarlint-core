/*
ACR-9376e32033c04b6296fbb4cd39351876
ACR-0d27484353e24e068a698cb5e2c00522
ACR-e34bc341a5364b9fa81716d6950d914f
ACR-ef56f94202e9461d908758cbae2c3770
ACR-67767abc96e34056a1e098191bd7896b
ACR-50f0f90416974ef88927cff1d9d88490
ACR-78c3d60d42764d3abe942026edc6e481
ACR-5de6332e922e49639a72f62ee89c4c36
ACR-cda58eb60d314515bd374ec926d9e0f2
ACR-c515fe6e07a74eb4ad06a1e34eb85a61
ACR-e15b7237196f4f668fc42a91694f6052
ACR-4f65e7625af94920bf59a3a0db7513dd
ACR-435899f84c634cc783515f441da8298d
ACR-5bfdacebb642402c95a92ef8374fcaba
ACR-212f1a16b0d94e719b57b94b055be68b
ACR-938f44f638d1407baea943f6ef24f707
ACR-bd795a82862746bab115e419dc11594a
 */
package org.sonarsource.sonarlint.core.repository.connection;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.SonarCloudRegion;

import static org.assertj.core.api.Assertions.assertThat;

class SonarCloudConnectionConfigurationTest {

  @Test
  void testEqualsAndHashCode() {
    var underTest = new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "id1", "org1", SonarCloudRegion.EU, true);
    assertThat(underTest)
      .isEqualTo(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "id1", "org1", SonarCloudRegion.EU, true))
      .isNotEqualTo(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "id2", "org1", SonarCloudRegion.EU, true))
      .isNotEqualTo(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "id1", "org2", SonarCloudRegion.EU, true))
      .isNotEqualTo(new SonarQubeConnectionConfiguration("id1", "http://server1", true))
      .hasSameHashCodeAs(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "id1", "org1", SonarCloudRegion.EU, true));
  }

}
