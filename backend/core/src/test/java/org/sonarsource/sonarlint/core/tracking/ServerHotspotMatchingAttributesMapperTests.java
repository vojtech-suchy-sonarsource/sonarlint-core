/*
ACR-356ad2aeae2949fdba7012e975a2bf29
ACR-b7580f4566d941dabfe6960aeae8733a
ACR-6dd6731398ef47dfb0eedddd8c18ba9b
ACR-fb7967b77d614651b886436da9908bed
ACR-816673d10332439e9b2b05220b5b8800
ACR-432cb9a863f044d281e1fd9233f946f4
ACR-9c0994caba584d819d173671b0b2d667
ACR-ee369a5f1d5641fcb9cc280b78176ac4
ACR-8a4c194c845344bfba9306035abbed94
ACR-d91fe6f3c1914f90bcfb8fa6f87ba722
ACR-34475952152f4461a1a5d64aae6b3551
ACR-8980ff498c6a4dca883059a22d0110b9
ACR-8867b761c9a440c1a2987c76c3f3bea1
ACR-22f1faa130d54bf6b7bddc3cd4ee5c0f
ACR-2dfb06789444484aa8fc485188ce78d2
ACR-fd2627134939401b812bf1ba31a36ad5
ACR-71b16813d76244ce9f717c0eb296f8d6
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
