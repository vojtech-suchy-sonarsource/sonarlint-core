/*
ACR-0d54ead6ff4d465c8cd893b4b44df8fd
ACR-f322a851d8434d3e8cfa1f7258afffe5
ACR-de2dabfe125a45c1ae55fd7c0b4760a9
ACR-d4b596c06d0e48f99bd835a566303edc
ACR-8143ae8659ed425caca362ee3c9fd403
ACR-de34a4d562bf412eac8db250381600f9
ACR-147d7218a04044daac013708841cb012
ACR-93eada0da1524979bfac0602b03a719e
ACR-429629607bfa4c1abb56ae18627f1ef7
ACR-d3e3688c8b2449fab1afca945696f74a
ACR-40dd4d2c19e5419c92e01dcb72ed42bc
ACR-22c4b96e7eff46e2b6a6464ddde8c148
ACR-9646c10817e744f7a4a2e53910b21542
ACR-99baf61a8e5640179aa0bdde0dbcc42c
ACR-d64fdcd6276c44eab230de98cc88067f
ACR-d622d363942a4b06bae00cf8341f7b64
ACR-e76751e9faaa474382fd80874545cd79
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
