/*
ACR-5becb91b69854de19ff31624b20748fc
ACR-9136827a2605459e9e51af82a3f328d5
ACR-56f6e420f40a4d988ca40cf967b8700f
ACR-8863c06ff9e44ba3aa6048ca364d391d
ACR-1da5f830169f40f98d5ddf730f84a082
ACR-a7bd31396d524d039fb6b2315db66d83
ACR-b037e61dd83a46cbba9d1700b9607687
ACR-6b620c19981742bca5b290a258e73303
ACR-08c4a2c8534b49fea408d46e86e083d3
ACR-0d01f04bd8344035b32518ce73463626
ACR-d3229c06103240cf93d7b2a0cd574c64
ACR-c49812b72ede4b9c85413372d85696a6
ACR-32abc8096d734aedaf2330d83c71fcaa
ACR-d7a93cf4943d4c6aa1b38ba2ce0d4538
ACR-a80119ab94a943a49beffc4f8871f476
ACR-1f253e67070c4137898bf41b85eaf922
ACR-45a2058117c44a04b40c445b1901dd36
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
