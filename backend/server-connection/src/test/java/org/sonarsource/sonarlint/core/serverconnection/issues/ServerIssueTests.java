/*
ACR-c595677da03543a58f2a5367869131b3
ACR-0593a8759ff948e29707942e912ce614
ACR-f519640763db4d9591cd6db57beff5f5
ACR-2d96120b724e453e922b725a0ace7093
ACR-63a21ee5130f467aa8c21427978c94da
ACR-82fffb6de3394d1b90b003afbfaf333b
ACR-ba4f50c9e7dc4909bea2bcec46e6c1f5
ACR-d45c8cfff84c43209e0eadcc29fc59eb
ACR-56a0dbdd60bf4533af64dd1ee4643d38
ACR-fce86709accc42f69742af5e9f5cd95b
ACR-ad3e287527674a268aad54c264895afb
ACR-3c7618f865154c3e9381f336aa6e2e99
ACR-dcf20a58a68f44c882f328b398b156e5
ACR-e6abb3634b124697aee6c148389a5cd9
ACR-0cccd2c22ad54011925a1692b4d057be
ACR-282b9ca08a844caa8182a4bec898a7a9
ACR-9d4c11e8b7dd4d86b5edb08fe8be97c0
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
