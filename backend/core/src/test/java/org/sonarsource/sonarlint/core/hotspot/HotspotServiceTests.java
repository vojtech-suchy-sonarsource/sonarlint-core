/*
ACR-d8623227514a473daea2ea83747d42f5
ACR-2c86f0dfd1c24b589666c465426ec817
ACR-d1f664ea77f241b3acfe81b95d2818b4
ACR-1055d5edb2f24ec39f4af3644e4e8446
ACR-0dd787d1e02b40d690dcf07387f4d53e
ACR-fe210aed1e004b65b91a715380db5481
ACR-8cc2d32272474d658439ba68df301fc6
ACR-7e16ab896a2a49d3b42d2e060a866730
ACR-3a3bd64ba74440f192786b35a7b2d5f3
ACR-c3bd6eee862b47e08f358a973fe47975
ACR-0a3c1dc3458b47d598eef6a8ff476221
ACR-003239a0bf694b4e9a343c2e15f4be69
ACR-dff6ec68e7c4415ca6545cd3f6729792
ACR-ae590a1047644c7bbc4d0b559d32bc6f
ACR-4bf65470aed54ec581b4fb47237d614a
ACR-7ca5871848b34dc7b5ef4f9163eee1f0
ACR-2faa25a07400408a8c4167ff78b373c9
 */
package org.sonarsource.sonarlint.core.hotspot;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

import static org.assertj.core.api.Assertions.assertThat;

class HotspotServiceTests {

  @Test
  void testBuildSonarQubeHotspotUrl() {
    assertThat(HotspotService.buildHotspotUrl("myProject", "myBranch", "hotspotKey", new EndpointParams("http://foo.com", "", false, null)))
      .isEqualTo("http://foo.com/security_hotspots?id=myProject&branch=myBranch&hotspots=hotspotKey");
  }

  @Test
  void testBuildSonarCloudHotspotUrl() {
    assertThat(HotspotService.buildHotspotUrl("myProject", "myBranch", "hotspotKey", new EndpointParams("https://sonarcloud.io", "", true, "myOrg")))
      .isEqualTo("https://sonarcloud.io/project/security_hotspots?id=myProject&branch=myBranch&hotspots=hotspotKey");
  }
}
