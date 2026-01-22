/*
ACR-098ad7aa70394ae8b0ecc768bf1d9fc8
ACR-09ae76e30b434f9d9e02de615bc1e9a3
ACR-1315800422064b41869979cd4ed6f3b7
ACR-80558c5213d24f4da01dbd9bbc1db76f
ACR-9d4fa8a67abf438e9203206ee3afb59e
ACR-1f283bcc0bcb43f7b88f3156514fb656
ACR-2ce9d6295a98470480dca40884cdd9a9
ACR-43358f3bedb6430ebcee795ede82939d
ACR-a4ef3e39ea324e9482a62a88078cdca0
ACR-64e746f9dbcf48dfb969302fef5b23fb
ACR-4e47a5c8439348828dfabf4eb04ab723
ACR-8788aeca8d1a48389d34d858c44a0cf2
ACR-4d342fc4d6d94f12a0b78ae301b80459
ACR-967421382fa84842927d1930b4ed99cb
ACR-3c680c321a264ee5a399e7fd17617fac
ACR-b762c387c774400fbb17711d57900a32
ACR-340f50e116bf443eb56e8429aeed4083
 */
package org.sonarsource.sonarlint.core.serverapi.hotspot;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

import static org.assertj.core.api.Assertions.assertThat;

class ServerHotspotDetailsTests {
  @Test
  void it_should_populate_fields_with_constructor_parameters() {
    var hotspot = new ServerHotspotDetails("message",
      Path.of("path"),
      new TextRange(0, 1, 2, 3),
      "author",
      ServerHotspotDetails.Status.TO_REVIEW,
      ServerHotspotDetails.Resolution.FIXED, new ServerHotspotDetails.Rule(
        "key",
        "name",
        "category",
        VulnerabilityProbability.HIGH,
        "risk",
        "vulnerability",
        "fix"),
      "some code \n content", true);

    assertThat(hotspot.message).isEqualTo("message");
    assertThat(hotspot.filePath).isEqualTo(Path.of("path"));
    assertThat(hotspot.textRange.getStartLine()).isZero();
    assertThat(hotspot.textRange.getStartLineOffset()).isEqualTo(1);
    assertThat(hotspot.textRange.getEndLine()).isEqualTo(2);
    assertThat(hotspot.textRange.getEndLineOffset()).isEqualTo(3);
    assertThat(hotspot.author).isEqualTo("author");
    assertThat(hotspot.status).isEqualTo(ServerHotspotDetails.Status.TO_REVIEW);
    assertThat(hotspot.resolution).isEqualTo(ServerHotspotDetails.Resolution.FIXED);
    assertThat(hotspot.rule.key).isEqualTo("key");
    assertThat(hotspot.rule.name).isEqualTo("name");
    assertThat(hotspot.rule.securityCategory).isEqualTo("category");
    assertThat(hotspot.rule.vulnerabilityProbability).isEqualTo(VulnerabilityProbability.HIGH);
    assertThat(hotspot.rule.riskDescription).isEqualTo("risk");
    assertThat(hotspot.rule.vulnerabilityDescription).isEqualTo("vulnerability");
    assertThat(hotspot.rule.fixRecommendations).isEqualTo("fix");
    assertThat(hotspot.codeSnippet).isEqualTo("some code \n content");
    assertThat(hotspot.canChangeStatus).isTrue();
  }

}
