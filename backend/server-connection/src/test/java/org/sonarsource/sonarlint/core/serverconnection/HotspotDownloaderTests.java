/*
ACR-dbd4734117f64a6991a9adf282329f43
ACR-cc6e21492f174d528cb3e045360f03bf
ACR-ecab9c80f3c8496fa54eba2047fd790a
ACR-2277273233fa4fde8f225e83ae4b7465
ACR-a34c7d39f9904b558e1523cd3b073fa5
ACR-517aa2ca429a4002bf989ae56c743b9e
ACR-f538f19185b34cb49b2331ae6ee52760
ACR-d90ab655a9574f4f8c05167317364081
ACR-fb8bceabac5c46b088c317185c193ce1
ACR-ba8ddc754d284a8e859e0d6e06824192
ACR-14eeab89ac424fcca0a3f9ee91355775
ACR-4ea0e99201d14a67968b7d585a5e8522
ACR-713c26993a8b4b14be2e72e3d338f736
ACR-75b430cfff2f4f4cb568d6e8d39d7914
ACR-1755fda7459b4d3ca8c14e07d3799edc
ACR-256c666757284387abd7cba83b3a4a85
ACR-0f7b36baa8904bc0b2623bedba3da639
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Hotspots;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;

class HotspotDownloaderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String DUMMY_KEY = "dummyKey";

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();
  private ServerApi serverApi;

  private HotspotDownloader underTest;

  @BeforeEach
  void prepare() {
    underTest = new HotspotDownloader(Set.of(SonarLanguage.JAVA));
    serverApi = new ServerApi(mockServer.serverApiHelper());
  }

  @Test
  void test_download_one_hotspot_pull_ws() {
    var timestamp = Hotspots.HotspotPullQueryTimestamp.newBuilder().setQueryTimestamp(123L).build();
    var hotspot1 = Hotspots.HotspotLite.newBuilder()
      .setKey("someHotspotKey")
      .setFilePath("foo/bar/Hello.java")
      .setVulnerabilityProbability(VulnerabilityProbability.LOW.toString())
      .setStatus("TO_REVIEW")
      .setMessage("This is security sensitive")
      .setCreationDate(123456789L)
      .setTextRange(Hotspots.TextRange.newBuilder()
        .setStartLine(1)
        .setStartLineOffset(2)
        .setEndLine(3)
        .setEndLineOffset(4)
        .setHash("clearly not a hash")
        .build())
      .setRuleKey("java:S123")
      .setClosed(false)
      .build();
    var hotspot2 = Hotspots.HotspotLite.newBuilder()
      .setKey("otherHotspotKey")
      .setFilePath("foo/bar/Hello.java")
      .setVulnerabilityProbability(VulnerabilityProbability.LOW.toString())
      .setStatus("REVIEWED")
      .setResolution("SAFE")
      .setMessage("This is security sensitive")
      .setCreationDate(123456789L)
      .setTextRange(Hotspots.TextRange.newBuilder()
        .setStartLine(5)
        .setStartLineOffset(6)
        .setEndLine(7)
        .setEndLineOffset(8)
        .setHash("not a hash either")
        .build())
      .setRuleKey("java:S123")
      .setClosed(false)
      .build();

    mockServer.addProtobufResponseDelimited("/api/hotspots/pull?projectKey=" + DUMMY_KEY + "&branchName=myBranch&languages=java", timestamp, hotspot1, hotspot2);

    var result = underTest.downloadFromPull(serverApi.hotspot(), DUMMY_KEY, "myBranch", Optional.empty(), new SonarLintCancelMonitor());
    assertThat(result.getChangedHotspots()).hasSize(2);
    assertThat(result.getClosedHotspotKeys()).isEmpty();

    var serverHotspot1 = result.getChangedHotspots().get(0);
    assertThat(serverHotspot1.getKey()).isEqualTo("someHotspotKey");
    assertThat(serverHotspot1.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
    assertThat(serverHotspot1.getVulnerabilityProbability()).isEqualTo(VulnerabilityProbability.LOW);
    assertThat(serverHotspot1.getStatus()).isEqualTo(HotspotReviewStatus.TO_REVIEW);
    assertThat(serverHotspot1.getMessage()).isEqualTo("This is security sensitive");
    assertThat(serverHotspot1.getCreationDate()).isAfter(Instant.EPOCH);
    assertThat(serverHotspot1.getTextRange().getStartLine()).isEqualTo(1);
    assertThat(serverHotspot1.getTextRange().getStartLineOffset()).isEqualTo(2);
    assertThat(serverHotspot1.getTextRange().getEndLine()).isEqualTo(3);
    assertThat(serverHotspot1.getTextRange().getEndLineOffset()).isEqualTo(4);
    assertThat(((TextRangeWithHash) serverHotspot1.getTextRange()).getHash()).isEqualTo("clearly not a hash");
    assertThat(serverHotspot1.getRuleKey()).isEqualTo("java:S123");

    var serverHotspot2 = result.getChangedHotspots().get(1);
    assertThat(serverHotspot2.getKey()).isEqualTo("otherHotspotKey");
    assertThat(serverHotspot2.getFilePath()).isEqualTo(Path.of("foo/bar/Hello.java"));
    assertThat(serverHotspot2.getVulnerabilityProbability()).isEqualTo(VulnerabilityProbability.LOW);
    assertThat(serverHotspot2.getStatus()).isEqualTo(HotspotReviewStatus.SAFE);
    assertThat(serverHotspot2.getMessage()).isEqualTo("This is security sensitive");
    assertThat(serverHotspot2.getCreationDate()).isAfter(Instant.EPOCH);
    assertThat(serverHotspot2.getTextRange().getStartLine()).isEqualTo(5);
    assertThat(serverHotspot2.getTextRange().getStartLineOffset()).isEqualTo(6);
    assertThat(serverHotspot2.getTextRange().getEndLine()).isEqualTo(7);
    assertThat(serverHotspot2.getTextRange().getEndLineOffset()).isEqualTo(8);
    assertThat(((TextRangeWithHash) serverHotspot2.getTextRange()).getHash()).isEqualTo("not a hash either");
    assertThat(serverHotspot2.getRuleKey()).isEqualTo("java:S123");
  }
}
