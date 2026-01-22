/*
ACR-67605092bcbe480da0aa8d4ec0d6ee08
ACR-a950373269c54c8cab55b03c26f7fcdb
ACR-710dc207833a429fbb2b6f5b6b701c49
ACR-82c5bf82bc2e4c7a8df560ed571b0904
ACR-8319faead37d4e0ba70794f130ae2a7a
ACR-a206339b896549ec92ea07fe0843a0c9
ACR-0a7a4f894cf541e39e5ca5d8bcf216b3
ACR-36ac85ae45c54dfead5673c344659992
ACR-5c1d75c129fe48e49036e1d8c8b5c798
ACR-bc9846a314284f37b376df2289f41115
ACR-9507f885586640ee9c33103a9c74a15c
ACR-e620a69fa0a4422a99e1fc8b2f8a3955
ACR-e7c269d269364d038834132794925230
ACR-32fff0893e3c4d5fb872235be775b6e9
ACR-c4dac62806f045c58ac32e3f13124136
ACR-4f7ef2f0cf1c46f2bb38c1930815ef25
ACR-d0934be3e6534343925cf9246d530419
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
