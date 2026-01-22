/*
ACR-1d9866007b1548c0bde57d858a505f44
ACR-8a990c01129844d087f53eeace2a4c93
ACR-47e10907eb5d4f8f9d8fbdd09b435d96
ACR-0df4933f2c8c4490888d6dd45f8bd6fd
ACR-d483c4441d344454bb071af69934500c
ACR-b9d8f83e93494b9487fff302bfd096b3
ACR-aff06288388e4a9f9f626470067678e4
ACR-f893d01a3e7c4edd8d3a4d6c9b3bfa5a
ACR-7e10e02c424c484c962fde0716e45379
ACR-a8e0ca0399d945918a90f26f5a5e953e
ACR-4f95e47667c341b1953cb60681552fa0
ACR-fe478b55171442fa8498aa6a2eb3cd03
ACR-f3d937db3162464080c0f91921cce424
ACR-eab6cef5b8254d3fbeb28326fe1c7104
ACR-d897375b3eca41788fa157f4ac86d42e
ACR-e38bd9ab989d493cb0eeba3fb2622c67
ACR-428c7ebd53014165bed1ee7210d2d4df
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class RaisedIssueDto extends RaisedFindingDto {

  private final boolean isAiCodeFixable;
  private final ResolutionStatus resolutionStatus;

  public RaisedIssueDto(UUID id, @Nullable String serverKey, String ruleKey, String primaryMessage, Either<StandardModeDetails, MQRModeDetails> severityMode,
    Instant introductionDate, boolean isOnNewCode, boolean resolved, @Nullable TextRangeDto textRange, List<IssueFlowDto> flows, List<QuickFixDto> quickFixes,
    @Nullable String ruleDescriptionContextKey, boolean isAiCodeFixable, @Nullable ResolutionStatus resolutionStatus) {
    super(id, serverKey, ruleKey, primaryMessage, severityMode, introductionDate, isOnNewCode, resolved, textRange, flows, quickFixes, ruleDescriptionContextKey);
    this.isAiCodeFixable = isAiCodeFixable;
    this.resolutionStatus = resolutionStatus;
  }

  public boolean isAiCodeFixable() {
    return isAiCodeFixable;
  }

  public Builder builder() {
    return Builder.from(this);
  }

  public ResolutionStatus getResolutionStatus() {
    return resolutionStatus;
  }

  public static class Builder {
    private final UUID id;
    private final String serverKey;
    private final String ruleKey;
    private final String primaryMessage;
    private Either<StandardModeDetails, MQRModeDetails> severityMode;
    private final Instant introductionDate;
    private final boolean isOnNewCode;
    private boolean resolved;
    private final TextRangeDto textRange;
    private final List<IssueFlowDto> flows;
    private final List<QuickFixDto> quickFixes;
    private final String ruleDescriptionContextKey;
    private final boolean isAiCodeFixable;
    private final ResolutionStatus resolutionStatus;

    private Builder(UUID id, @Nullable String serverKey, String ruleKey, String primaryMessage, Either<StandardModeDetails, MQRModeDetails> severityMode,
      Instant introductionDate, boolean isOnNewCode, boolean resolved, @Nullable TextRangeDto textRange, List<IssueFlowDto> flows, List<QuickFixDto> quickFixes,
      @Nullable String ruleDescriptionContextKey, boolean isAiCodeFixable, ResolutionStatus resolutionStatus) {
      this.id = id;
      this.serverKey = serverKey;
      this.ruleKey = ruleKey;
      this.primaryMessage = primaryMessage;
      this.severityMode = severityMode;
      this.introductionDate = introductionDate;
      this.isOnNewCode = isOnNewCode;
      this.resolved = resolved;
      this.textRange = textRange;
      this.flows = flows;
      this.quickFixes = quickFixes;
      this.ruleDescriptionContextKey = ruleDescriptionContextKey;
      this.isAiCodeFixable = isAiCodeFixable;
      this.resolutionStatus = resolutionStatus;
    }

    public static Builder from(RaisedIssueDto dto) {
      return new Builder(dto.getId(), dto.getServerKey(), dto.getRuleKey(), dto.getPrimaryMessage(), dto.getSeverityMode(), dto.getIntroductionDate(), dto.isOnNewCode(),
        dto.isResolved(), dto.getTextRange(), dto.getFlows(), dto.getQuickFixes(), dto.getRuleDescriptionContextKey(), dto.isAiCodeFixable(), dto.getResolutionStatus());
    }

    public Builder withResolution(boolean resolved) {
      this.resolved = resolved;
      return this;
    }

    public Builder withStandardModeDetails(IssueSeverity severity, RuleType type) {
      this.severityMode = Either.forLeft(new StandardModeDetails(severity, type));
      return this;
    }

    public Builder withMQRModeDetails(CleanCodeAttribute cleanCodeAttribute, List<ImpactDto> impacts) {
      this.severityMode = Either.forRight(new MQRModeDetails(cleanCodeAttribute, impacts));
      return this;
    }

    public RaisedIssueDto buildIssue() {
      return new RaisedIssueDto(id, serverKey, ruleKey, primaryMessage, severityMode, introductionDate, isOnNewCode, resolved, textRange, flows, quickFixes,
        ruleDescriptionContextKey, isAiCodeFixable, resolutionStatus);
    }
  }
}
