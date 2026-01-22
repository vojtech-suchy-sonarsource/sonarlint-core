/*
ACR-dee4cd4ee4f44413b03fefaecb6fea3b
ACR-2e11e47002d146e49e6895b5f2c0346a
ACR-3633a4a712644b97ab720bc4554c4763
ACR-577c9445c5fe4915bd81ec5978a480ab
ACR-31fb84751be24bb7bc7e7f95e63d7bfd
ACR-4926a503bef543c28fd9091cd97be176
ACR-6e51365bd3d147519acf2f5c6edf0a12
ACR-c6725b8f2243491ba82b68d358e82702
ACR-f2117087f26f43f7a62724458b8d9491
ACR-71604d0e6cab487eacd94f499c1436f3
ACR-cc15b24e6f3b4682a7ec6aca03dab49b
ACR-0ab1cfa6b38f4a39aaadd400e12d76fc
ACR-77ce16d6a2ef41ffbc99e881c35712f0
ACR-752656acdf3b468f95b964e08acf39a1
ACR-8ebff990514a4575a39610cea0ebcdfb
ACR-986e5c2022774f5f9215c3d287103273
ACR-b3155fedb1eb41b09e81a1481c710cc7
 */
package testutils;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssueResolution;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class LocalOnlyIssueFixtures {

  public static LocalOnlyIssue aLocalOnlyIssueResolvedWithoutTextAndLineRange() {
    return new LocalOnlyIssue(
      UUID.randomUUID(),
      Path.of("file/path"),
      null,
      null,
      "ruleKey",
      "message",
      new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, Instant.now().truncatedTo(ChronoUnit.MILLIS), "comment")
    );
  }

  public static LocalOnlyIssue aLocalOnlyIssueResolved() {
    return aLocalOnlyIssueResolved(UUID.randomUUID());
  }

  public static LocalOnlyIssue aLocalOnlyIssueResolved(Instant resolutionDate) {
    return aLocalOnlyIssueResolved(UUID.randomUUID(), resolutionDate);
  }

  public static LocalOnlyIssue aLocalOnlyIssueResolved(UUID id) {
    return aLocalOnlyIssueResolved(id, Instant.now());
  }

  public static LocalOnlyIssue aLocalOnlyIssueResolved(UUID id, Instant resolutionDate) {
    return new LocalOnlyIssue(
      id,
      Path.of("file/path"),
      new TextRangeWithHash(1, 2, 3, 4, "ab12"),
      new LineWithHash(1, "linehash"),
      "ruleKey",
      "message",
      new LocalOnlyIssueResolution(IssueStatus.WONT_FIX, resolutionDate.truncatedTo(ChronoUnit.MILLIS), "comment")
    );
  }

}
