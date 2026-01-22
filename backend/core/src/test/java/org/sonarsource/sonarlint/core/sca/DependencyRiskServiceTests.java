/*
ACR-530e12a9ad54437aa88e80fc0ac82fff
ACR-92b639cc98a94fd19187bf88e0652363
ACR-edeffe385274430f84250b150a0355d3
ACR-2faea65797374cf9950ece61630ac62d
ACR-cd98ddc54f0f46b98827580f546d1b8c
ACR-c34532be4d35499388962caa2d04a553
ACR-b41bd495a48549f98a3f91d937032160
ACR-666bceda2d45458993585b4ac4880090
ACR-c9a57032c2eb4752afb6d087a10bb830
ACR-62c76e105b0140b4be79f8aefaabcdac
ACR-e1e2d2d832384b68a48d6e16230eb1f7
ACR-6020d3362b554def90ce697510ab0264
ACR-841239667bab4cacbec2a1e1644941a7
ACR-68e8137a6c544d049428e0dabdec7ea5
ACR-d479b1abe8b54d7ca50e286899a68cc3
ACR-1a499672b3bc4ae2ba068340702288cf
ACR-3131dd369ab945389e5c7c6954a3d6b6
 */
package org.sonarsource.sonarlint.core.sca;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyRiskServiceTests {

  @Test
  void testBuildSonarQubeServerScaUrl() {
    var dependencyKey = UUID.randomUUID();
    assertThat(DependencyRiskService.buildDependencyRiskBrowseUrl("myProject", "myBranch", dependencyKey, new EndpointParams("http://foo.com", "", false, null)))
      .isEqualTo(String.format("http://foo.com/dependency-risks/%s/what?id=myProject&branch=myBranch", dependencyKey));
  }

}
