/*
ACR-23c8a270c57b47ae84bd61d36f32fcfb
ACR-547d07cdea99433b880a37d4830a4feb
ACR-36c5ac7887324e228e1f2abbdd6b4f8f
ACR-4c78c076bbb24cada79b99c6538078c5
ACR-478e7f6e14d040759dfa24ff56e889ce
ACR-7feddd20dadd41a3a1104d47b6bf5a40
ACR-3bf4d4dc0425473497124816bd1e8a30
ACR-a2cd976d73484451be751bc03c90821d
ACR-3ccc5e0c6e134b30b92a5703155589b3
ACR-94de4992a6484a9a99636524593a0f94
ACR-caa42ea7b9e54a8cb9a09242905eaee9
ACR-87e8d8fff8354dfe87b524fcb78dc8e8
ACR-17d7b051f9a2461d8e481d6a1e04e192
ACR-70675c39a6de45c2b6591ba868db23fd
ACR-b683d4cee672428d8a760472b00a0424
ACR-ece333d8b6c54082b4bd98888212f177
ACR-38a4628037d24fe78489d5d74c1ceb10
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
