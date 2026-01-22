/*
ACR-caa4243b99894219b5926449dc8f9015
ACR-b80962fc80684c7ab7a765d93195c215
ACR-40074cd4d99743168c2c0a05d6cc80ca
ACR-3bd7bca5e8d9493db8ea20a85e400bf7
ACR-5e72da6b176d439299e4585631eff881
ACR-5844ea93b8b744d78bbae02c9e35c8b9
ACR-b2fe8242555f41c49ae5f0c6dacd0955
ACR-fccf92d295644da9984b10ea28a8af68
ACR-4e026a47f9704cf1bcce3d5775ffd0eb
ACR-9ed310bb043b4c13b1f8b60d32c26239
ACR-f433d2be1ebe4c4ab131714e94730d2b
ACR-55d750ff329945bf888f9348e6d29857
ACR-2338faa3421d4588b162a5e60db59292
ACR-1abfe17c87784ebc8f502c95f9cfea2f
ACR-1dd80c7375274e6e8003aad3510ce279
ACR-df32ef2e0d684a4ea0fd599821dd8362
ACR-f250afd980904ae6bdace143346e9b33
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
