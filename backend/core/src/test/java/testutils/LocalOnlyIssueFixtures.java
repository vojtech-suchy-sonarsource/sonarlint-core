/*
ACR-6ab58e604326493da7d2782e39ce4db7
ACR-6955a55f0c4f4bf3b6d3029f790f3f07
ACR-c2363fcebda045dc81f53ef193262ec3
ACR-60760ca36a004365ac3a7196908fb9c3
ACR-9705c6c42f65411f9fcfae063bc46146
ACR-943d1c43a52946af975246872273ff57
ACR-ebaea3695b3542deaac9842aefa5c5e3
ACR-9a90659684b846e69746ace162e74a07
ACR-749111f07c724e599f55b03f65632f90
ACR-92581b62213c45fb8446bf21d3dd18b6
ACR-27644337ba2d48cf834ecb33d11ba571
ACR-468a99bef5e540898771333f95d6eee2
ACR-95f18e591eee41b0a4c2bbc6a2ea0196
ACR-134ff5e7fc224cb4b307c9dde27a417a
ACR-8dd0ec78604f4458b5219b7b3c2ba9a0
ACR-d027daf3b228401bb41bea68b0ec224e
ACR-95bbcd2b4a36469eb8338fb6075cd2e3
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
