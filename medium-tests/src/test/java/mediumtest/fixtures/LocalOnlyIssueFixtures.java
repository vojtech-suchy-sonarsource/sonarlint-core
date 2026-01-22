/*
ACR-79258ba1569f4050b1085e3ab8adcca1
ACR-ba8f48780a644b3aa3327641d4b1b4a6
ACR-d50def3462564a1d98afff267157a93e
ACR-e6e465758b7045fa82a34ff55b1838ff
ACR-3d7d441b0d764b3fb252debf50012530
ACR-7b3793ec06a847bbae4a92fc0e71b1c2
ACR-c2f5d6ae7ae946e597b1a15a18841cdb
ACR-87cb79f6855d40a9b7e36f9bfd0c8416
ACR-c642dcb240424be1a63f8067580dd96d
ACR-bb55ce30ccad4e70870a1cf83eb0872e
ACR-fd3a4d8b6ad046139e9a551e72e1d26f
ACR-39be4440baea49cb9cf3f1ca05ca59dc
ACR-a38d66cb9f084548a879c9a60ee106d2
ACR-290473fc0698434db7cddcc332b16839
ACR-0b61ef694f084fe39c346b088fd927c3
ACR-dcf0849dae5448659529e4801a963fb4
ACR-c4dc9da0fb8948faac45043b92923ae7
 */
package mediumtest.fixtures;

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
