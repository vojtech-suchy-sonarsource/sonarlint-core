/*
ACR-f9091007743447e782d9a490fa8f6a56
ACR-5806e1232a4c48bbbe15782c3e45b183
ACR-81cae0af75374f7b8693fd8e606ea960
ACR-afc8eb361741489c98a475e6ab27d948
ACR-8fc895fbd0074dbcabf2ecf67de94b9b
ACR-7fdc3e45060b46a89ecff59d0c75110d
ACR-d58b22c532a44c2fac3fc680466ede9b
ACR-af316f4db76f4d0b80bbac7093bdc4b6
ACR-5c2cc27248dc4ffca8e61cad8ba2415d
ACR-9d4619296bfd4fa9949b68994e895a1e
ACR-51ace01300b748828fd5393c58e969d0
ACR-f3fa7061bec04e989dded4d8732b557f
ACR-f05a94a6a07a422f818f8b1217c86292
ACR-c00a735041f542859023a7537c71ec01
ACR-221bfa5e93ec41869ff103f0295dc42f
ACR-a98080d8b2d74b61a7c0461e6f8ad47f
ACR-ea5652bb641645d6bf97c701285c836c
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
