/*
ACR-56f241d6e2b3451998612516d1d0e372
ACR-984f10f151334fd69a2db47be2cc484a
ACR-3233fcdc4a2b4e38ba137ebd04d17d56
ACR-8ec16855c1804bb0be99586c75f6bcc1
ACR-5cb81985821c40a3b54de0e846e50c24
ACR-acc66b93035d47a9af1da748914550a0
ACR-755b06d89119408ca4766b65e4fc8eef
ACR-79688e4281de41a4b16c913b369bebf4
ACR-9608eb270f2e43a6913b3bb6dc58b975
ACR-dc5e96c7aa674d039a4b8d06c5ee961d
ACR-3ce386f804664350bda97bacf6ebae7b
ACR-3e5359f370034f2dbe3c8c2c4d3094ab
ACR-7cc92e0e945e4d2abd790e2d0f67eb08
ACR-074e7d8eb16846b9a30c8dfcd123f431
ACR-df4e708c91f147588be30bdb800d1883
ACR-12d37ebd1f194638a07efac94ae4fa7a
ACR-e558ac65b4264f04b3970dc62ccdd478
 */
package org.sonarsource.sonarlint.core.tracking;

import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.tracking.matching.ServerHotspotMatchingAttributesMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ServerHotspotMatchingAttributesMapperTests {

  @Test
  void should_delegate_fields_to_server_issue() {
    var creationDate = Instant.now();
    var textRange = new TextRangeWithHash(1, 2, 3, 4, "realHash");
    var serverHotspot = new ServerHotspot("key", "ruleKey", "message", Path.of("filePath"), textRange, creationDate, HotspotReviewStatus.SAFE, VulnerabilityProbability.LOW, null);

    var underTest = new ServerHotspotMatchingAttributesMapper();

    assertThat(underTest.getServerIssueKey(serverHotspot)).contains("key");
    assertThat(underTest.getMessage(serverHotspot)).isEqualTo("message");
    assertThat(underTest.getLineHash(serverHotspot)).isEmpty();
    assertThat(underTest.getRuleKey(serverHotspot)).isEqualTo("ruleKey");
    assertThat(underTest.getLine(serverHotspot)).contains(1);
    assertThat(underTest.getTextRangeHash(serverHotspot)).contains(textRange.getHash());
  }
}
