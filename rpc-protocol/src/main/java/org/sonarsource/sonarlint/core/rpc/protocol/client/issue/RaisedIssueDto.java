/*
ACR-bb40ce698b8144a593821b0174564277
ACR-0b3bb122f44b4ca99019cc02c35cee97
ACR-2eb7399356814a4b82fd07a4bb18fe7b
ACR-85b377afc2b14d648d6ddf47f27240c7
ACR-889c3165191e4db78f3821ec95e5640d
ACR-3e02ac0b3b5f4cb7a03774907c6809f2
ACR-272b8dfb6adc4193903e093ec4be46d8
ACR-e8d802a127ac4b2fb0a832c77c1afa00
ACR-eb6e08b12e274e01bed0760a8c7c422a
ACR-07e3c52388304e789f6374455c7b51ef
ACR-50b541bea0ad40029c38642b8816fdf5
ACR-905a6aeed78646af9b9944e7d363dd9a
ACR-8e9cf1dbf8dc4438a49c72fc30e3ed2e
ACR-d93ea2b545d246e8bd4977dcd62e9c78
ACR-862a18b1e1a447029af11a3a46d49def
ACR-403348040139462085b3e9679987ace4
ACR-7f770008d16c4b1ab9ed50b8d43865b8
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
