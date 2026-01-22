/*
ACR-6a7e984cc7d2466da3713023d7e3371e
ACR-e9998ab0da844ac9a620375d0dba90f1
ACR-d4eb23fe24db4b988411c4a0d60a1fca
ACR-8dc2a287918940d1b375bb03890382c2
ACR-572821468389419abe9a6ab767105153
ACR-1e8e7a667b5f4bef92ad17f5d9280920
ACR-8b4551a04a4941d5979b74c7442041bc
ACR-3a32ee22189946428141ce781b0842e0
ACR-1d4a140e76b44a69b087cd25f049e6ee
ACR-82bbc758a7aa47dfbb1729766de6170b
ACR-0bc7499a242c435ab5689baadddb7c51
ACR-c26f6aeca0024d40ac193fbab726151b
ACR-ce43d63b2e7d4f8ab0c4bd3261ea88b4
ACR-2034e5a4765643e59106244feaa539bc
ACR-fa3d0fcc733f4810b6e27c99ec489ca8
ACR-e55b95f1220e49dda49587b1d4091310
ACR-ec941c267b7b4509b54d09dc6ab9f1c3
 */
package org.sonarsource.sonarlint.core.tracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverconnection.issues.LineLevelServerIssue;
import org.sonarsource.sonarlint.core.tracking.matching.ServerIssueMatchingAttributesMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerIssueMatchingAttributesMapperTests {

  private final LineLevelServerIssue serverIssue = mock(LineLevelServerIssue.class);
  private final ServerIssueMatchingAttributesMapper underTest = new ServerIssueMatchingAttributesMapper();

  @BeforeEach
  void prepare() {
    when(serverIssue.getLineHash()).thenReturn("blah");
    when(serverIssue.isResolved()).thenReturn(true);
    when(serverIssue.getLine()).thenReturn(22);
  }

  @Test
  void should_delegate_fields_to_server_issue() {
    assertThat(underTest.getMessage(serverIssue)).isEqualTo(serverIssue.getMessage());
    assertThat(underTest.getLineHash(serverIssue)).contains(serverIssue.getLineHash());
    assertThat(underTest.getRuleKey(serverIssue)).isEqualTo(serverIssue.getRuleKey());
    assertThat(underTest.getLine(serverIssue)).contains(serverIssue.getLine());
  }
}
