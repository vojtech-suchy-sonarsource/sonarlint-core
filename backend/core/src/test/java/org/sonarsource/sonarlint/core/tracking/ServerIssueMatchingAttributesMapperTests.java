/*
ACR-c19c1cb2c4454721bb3c4567f1d13303
ACR-9db374f384db44f0ad85a46fef9d89fb
ACR-4063834317104c45b0f9255a17526c59
ACR-cc26e79f3d2c4d008829f52c95d86402
ACR-26aa2f5ad5ec43e287c2cf041697d4ff
ACR-911ec9012ae84483a47b1cbeadcc48c5
ACR-84c67beb9dfe4c9db116b23a8378b39d
ACR-ce5ae267a35845318615c828e7a12728
ACR-ffa3c60bba1c4de3b2a9e1ebd2699552
ACR-b276d18c9b4847b094bf04cebf723c08
ACR-3541b6eecccf4c33a968986a1a8d5276
ACR-1f33b1cee2a545768896e0c3676d70ae
ACR-8d56426c41be44a19b0bee27e0457d15
ACR-e26e1e267c35450c9843ee1b87a7bfdd
ACR-847a9def6c8f4d55b9d93591fef687cf
ACR-472b222a052f430697640c04c5a0e0df
ACR-b55c8a1c564741ce9bfdd622273d0050
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
