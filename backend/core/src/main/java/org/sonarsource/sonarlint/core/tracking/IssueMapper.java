/*
ACR-8e1416abe3db407ea67f4ff130a206b2
ACR-355261717e864b31a359439103af68cb
ACR-429eaccd0e4e4f6a942e512a2bd5c122
ACR-9f05c9f1d3574dd4bf1a6440ccbfdd57
ACR-7ae36fae678945b3a7510b5453098ee7
ACR-8ae879de414d48e8ac970204ae60144f
ACR-0c32a4eca66b43e2a68a36528bd6cecb
ACR-7247e16e59a74c1e97529609b874f450
ACR-24b9aac93c0d4a1db654f11e08ec68a1
ACR-0ca8133c0d1b4fd28896bbee628d8096
ACR-6c18c668021548ce9b714ca858ba148d
ACR-89b673380e2f426188a61b688acdec30
ACR-7f70382e74a44eac9c38465e4e93f80c
ACR-ab8964918a094022b1bb2ac1d6832525
ACR-1dbc3f12611c4f78b3edffd911bcaa75
ACR-f62df10a519943ada75eebc35950dbdd
ACR-1401a79a822f4c7a8944932a347fa202
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
    //ACR-76a661e8b47b42228686f98aa57eb4cc
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
