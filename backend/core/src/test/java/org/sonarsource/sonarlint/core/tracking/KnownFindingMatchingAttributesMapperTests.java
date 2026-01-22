/*
ACR-1d63e2ef9072455cb01f8a46061c2cc1
ACR-8964a25ea3f64ee9bf24271abbe94465
ACR-dca23418757146298b0ed2c4a10c5ca4
ACR-eeef051776784fc992d05a04cc7f9219
ACR-5d701502d4f24bd29896b60701825516
ACR-1350a280c88646db976c5c2cc15312ca
ACR-48a509448f3648f382bd60dcdc291dd3
ACR-af89d7d8d44343ed9a60b768b077cf51
ACR-1e2c478606cc495f8a206c99895b183b
ACR-c3e57bb4c7304b719e71913bcd339525
ACR-b79604f51ab344f09bde8d85c5a0a20f
ACR-61ec724aeb30496088fbcdcc9e7de47a
ACR-82373923b2114e26934c51abed0fde83
ACR-c25b6a6243064a0c8404ae1b600faeac
ACR-fc577f73268c462e92488746d1ada65a
ACR-2b887e75fc954260b940ee6b9ca8d35f
ACR-5d69cf0910144bb4988424674b78c79e
 */
package org.sonarsource.sonarlint.core.tracking;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.KnownFinding;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.tracking.matching.KnownIssueMatchingAttributesMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KnownFindingMatchingAttributesMapperTests {

  private final KnownFinding knownFinding = mock(KnownFinding.class);
  private final KnownIssueMatchingAttributesMapper underTest = new KnownIssueMatchingAttributesMapper();

  @BeforeEach
  void prepare() {
    when(knownFinding.getId()).thenReturn(UUID.randomUUID());
    when(knownFinding.getMessage()).thenReturn("msg");
    when(knownFinding.getRuleKey()).thenReturn("ruleKey");
    when(knownFinding.getTextRangeWithHash()).thenReturn(new TextRangeWithHash(1, 2, 3, 4, "rangehash"));
    when(knownFinding.getLineWithHash()).thenReturn(new LineWithHash(1, "linehash"));
  }

  @Test
  void should_delegate_fields_to_server_issue() {
    assertThat(underTest.getMessage(knownFinding)).isEqualTo(knownFinding.getMessage());
    assertThat(underTest.getRuleKey(knownFinding)).isEqualTo(knownFinding.getRuleKey());
    assertThat(underTest.getLine(knownFinding)).contains(knownFinding.getLineWithHash().getNumber());
    assertThat(underTest.getLineHash(knownFinding)).contains(knownFinding.getLineWithHash().getHash());
    assertThat(underTest.getTextRangeHash(knownFinding)).contains(knownFinding.getTextRangeWithHash().getHash());
    assertThat(underTest.getServerIssueKey(knownFinding)).isEmpty();
  }

}
