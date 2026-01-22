/*
ACR-63af5c93b0304795a4ef1e90584323ab
ACR-54ef862e83a0450295ba94431de28b3a
ACR-a096d0151211495baed13d59f3662ffd
ACR-b278f4688fe74cdaa8d6f6e33c9e8523
ACR-00ac3adc752643468fda4fa294ccf7db
ACR-75e9df7bfeac461aa616257c157d9583
ACR-05ce4c6d112f47fcb5cd9b52e651ee8f
ACR-dcc6b03a0fb74b709ae52c028b725710
ACR-7b1596b4bc4a4edc8bb6af1f41ae6f15
ACR-5de1918228e44d84a059c16b3d42464b
ACR-be788211cac64a93a0df2275e43fc5aa
ACR-75eb628948f84e7e9f533ef48d2e9c9b
ACR-f59c4091f855427494ea15767a6dead1
ACR-d23e80c56e8a46b2b6024e79a09f3f99
ACR-53ee3122720a4ae785de47b965b279a9
ACR-8b0ae38c5ab84932bd2c1fc5d350b72e
ACR-a2632bcda7d540bc8a86dd91ed8f4fc0
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
