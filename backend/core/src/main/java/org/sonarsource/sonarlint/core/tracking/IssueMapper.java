/*
ACR-b3cc99a0b93145baa0b3609f61886ef6
ACR-0872716a3361468da1dff1fe9ee34437
ACR-26daf4cbbd2b458181873e82a9dc703d
ACR-a3748f45032a4f8fb03327b17649c5a8
ACR-382926daa5e646fa81102541994c8052
ACR-766c08903347433d94f6c6e2edc5cfaf
ACR-7323695329eb4e5d8db42f566677e462
ACR-376367dd9b92413ab313340e00f309f2
ACR-96245273415d4d22b394657feca52df7
ACR-325794c2c78644e78bfd8aceda60a88c
ACR-4570cce441b148bd90139099691457f7
ACR-6931305020bf4f8c862876ae36de5536
ACR-1a746e6640fa4be2b9d065c826b2b5a2
ACR-70775ec942f94bcd905aa81572e65fa9
ACR-a42546f895f443f1ae5396754fa3bdfe
ACR-a289fe12134e44c0b7e4652bb422475b
ACR-9c56b0417fd84923b7e647fb1f433535
 */
package org.sonarsource.sonarlint.core.tracking;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.RawIssue;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;

import static org.sonarsource.sonarlint.core.tracking.TextRangeUtils.getLineWithHash;
import static org.sonarsource.sonarlint.core.tracking.TextRangeUtils.getTextRangeWithHash;

public class IssueMapper {

  private static final Map<IssueStatus, ResolutionStatus> STATUS_MAPPING = statusMapping();

  private IssueMapper() {
    //ACR-fa2e6fa4dae343c7a1dd75bd9ab5d83b
  }

  public static TrackedIssue toTrackedIssue(RawIssue issue, Instant introductionDate) {
    return new TrackedIssue(UUID.randomUUID(), issue.getMessage(), introductionDate, false, issue.getSeverity(),
      issue.getRuleType(), issue.getRuleKey(), getTextRangeWithHash(issue.getTextRange(),
      issue.getClientInputFile()), getLineWithHash(issue.getTextRange(),
      issue.getClientInputFile()), null, issue.getImpacts(), issue.getFlows(), issue.getQuickFixes(),
      issue.getVulnerabilityProbability(), null, null, issue.getRuleDescriptionContextKey(), issue.getCleanCodeAttribute(), issue.getFileUri());
  }

  public static ResolutionStatus mapStatus(@Nullable IssueStatus status) {
    return STATUS_MAPPING.get(status);
  }

  private static EnumMap<IssueStatus, ResolutionStatus> statusMapping() {
    return new EnumMap<>(Map.of(
      IssueStatus.ACCEPT, ResolutionStatus.ACCEPT,
      IssueStatus.FALSE_POSITIVE, ResolutionStatus.FALSE_POSITIVE,
      IssueStatus.WONT_FIX, ResolutionStatus.WONT_FIX
    ));
  }
}
