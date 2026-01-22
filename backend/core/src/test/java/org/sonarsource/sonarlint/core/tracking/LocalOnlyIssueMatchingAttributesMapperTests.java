/*
ACR-198db8ca17d340b29606b87e615f86a4
ACR-27d54305d7bd4e8eb2444a4c44d796f7
ACR-ae32fc5e72c24083b2d0126036461900
ACR-0e5c20e76f0a454cbf66c28ce5e3ab52
ACR-0345601a2ef744eca1209c48f1f8da1e
ACR-7e933806f07b42d389553291f9cc7592
ACR-f6e1c44b1806408aacb7a6d00271d223
ACR-37a4dfa279c64cdca4200564624da93f
ACR-34a29a631e1b46809cf9c7443b5b2ecd
ACR-9390bf4f5d96444d81be09cb2a1dc314
ACR-71380a605e1142ae85c3520dc12ec5f8
ACR-f71d313fa6f54cb9a919c9d527964a40
ACR-1ccd3068be204c89a70faf79bf4b28a5
ACR-fcdbf4caa8424f7b9ba947f72e576fe5
ACR-a9b33d14863d413a9a4b51d5442d883e
ACR-92202c04f7714d9db9d6251542ef3793
ACR-e7dba6d1f0874db1a58274ec3ff653de
 */
package org.sonarsource.sonarlint.core.tracking;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssueResolution;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.tracking.matching.LocalOnlyIssueMatchingAttributesMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalOnlyIssueMatchingAttributesMapperTests {

  private final LocalOnlyIssue localOnlyIssue = mock(LocalOnlyIssue.class);
  private final LocalOnlyIssueMatchingAttributesMapper underTest = new LocalOnlyIssueMatchingAttributesMapper();

  @BeforeEach
  void prepare() {
    when(localOnlyIssue.getId()).thenReturn(UUID.randomUUID());
    when(localOnlyIssue.getMessage()).thenReturn("msg");
    when(localOnlyIssue.getResolution()).thenReturn(new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, Instant.now(), null));
    when(localOnlyIssue.getRuleKey()).thenReturn("ruleKey");
    when(localOnlyIssue.getServerRelativePath()).thenReturn(Path.of("file/path"));
    when(localOnlyIssue.getTextRangeWithHash()).thenReturn(new TextRangeWithHash(1, 2, 3, 4, "rangehash"));
    when(localOnlyIssue.getLineWithHash()).thenReturn(new LineWithHash(1, "linehash"));
  }

  @Test
  void should_delegate_fields_to_server_issue() {
    assertThat(underTest.getMessage(localOnlyIssue)).isEqualTo(localOnlyIssue.getMessage());
    assertThat(underTest.getRuleKey(localOnlyIssue)).isEqualTo(localOnlyIssue.getRuleKey());
    assertThat(underTest.getLine(localOnlyIssue)).contains(localOnlyIssue.getLineWithHash().getNumber());
    assertThat(underTest.getLineHash(localOnlyIssue)).contains(localOnlyIssue.getLineWithHash().getHash());
    assertThat(underTest.getTextRangeHash(localOnlyIssue)).contains(localOnlyIssue.getTextRangeWithHash().getHash());
    assertThat(underTest.getServerIssueKey(localOnlyIssue)).isEmpty();
  }

}
