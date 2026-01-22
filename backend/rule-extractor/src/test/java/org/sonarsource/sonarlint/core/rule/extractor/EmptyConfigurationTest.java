/*
ACR-70edc4ac430b442b8457f9bcacad8475
ACR-d9dbbbcce6404d6185eac7638d2279a6
ACR-bd72600f20e345b19664ddb1c62268bc
ACR-d581e09c6d1a4cbc90e925d3f411defe
ACR-14622b53565e4c4ea60a194b60048fc7
ACR-0d209944c62d4ee59dae1fa54f3e57a6
ACR-e2450396cbf04a4a896fdd522ff588e2
ACR-5e9f3140d7d64a72af8b4f20bcea6aa4
ACR-c9b817bbc41948eda3ec104d60371f50
ACR-dea949865a56412abf4b42bdf62466ad
ACR-b6539844e8074a02aef8fb45bccaccb8
ACR-7447552a1dd84022b5124417f8158558
ACR-4ed133ae4eb3406ab60a51fa79771cb6
ACR-b3d18dad09ac43498503f0af2114cd7d
ACR-defefbce5ed8477f9b151c3c410cde8b
ACR-5c3781c1eca44a71971ec18b2bf796a7
ACR-528ff751ef9b4c66a0fd09528f086a04
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyConfigurationTest {
  @Test
  void should_be_empty() {
    var emptyConfiguration = new EmptyConfiguration();

    assertThat(emptyConfiguration.hasKey("")).isFalse();
    assertThat(emptyConfiguration.get("")).isEmpty();
    assertThat(emptyConfiguration.getStringArray("")).isEmpty();
  }
}
