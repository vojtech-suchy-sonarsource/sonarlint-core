/*
ACR-446337ccf2d84f40bdc3a805c8c76c55
ACR-76577d5030d24a1bab14bae428fa0b90
ACR-f0a638eb55b442cfa05ca8f3dca36a9c
ACR-b49e7f295ee34cca94e56dd33f8bd434
ACR-376d470a65de471b940f7b3f12d983ff
ACR-b8027514619e4dc7902d8f6444a55c3f
ACR-3faed9a214634dd8bd4d436fcc8354a2
ACR-0c10a6cc25b544aeba6688e552a613fd
ACR-3f29b6937ad84b9d8df369886c7cf23c
ACR-c49c3e6489724851a15a2a752d60e3a5
ACR-ebc641a39ccb4024972738ef0c0980ad
ACR-19a154c551944180b13225db18654848
ACR-4579fc0d365f437089f2443464477c85
ACR-b5d8695f9f6d4e0a99a304b63b448a7d
ACR-54bc43fe10b5406dba7d3755126ef482
ACR-9751817a15644d8f9c3b8da96fd7f50c
ACR-f0b0f8b76e4940de949863571848c28b
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ServerIssueFixtures.aServerIssue;

class ServerIssueTests {
  @Test
  void testRoundTrips() {
    var issue = aServerIssue();
    var i1 = Instant.ofEpochMilli(100_000_000);
    assertThat(issue.setCreationDate(i1).getCreationDate()).isEqualTo(i1);
    assertThat(issue.setFilePath(Path.of("path1")).getFilePath()).isEqualTo(Path.of("path1"));
    assertThat(issue.setKey("key1").getKey()).isEqualTo("key1");
    assertThat(issue.setUserSeverity(IssueSeverity.MAJOR).getUserSeverity()).isEqualTo(IssueSeverity.MAJOR);
    assertThat(issue.setRuleKey("rule1").getRuleKey()).isEqualTo("rule1");
    assertThat(issue.isResolved()).isTrue();
    assertThat(issue.setMessage("msg1").getMessage()).isEqualTo("msg1");
    assertThat(issue.setType(RuleType.BUG).getType()).isEqualTo(RuleType.BUG);
  }

}
