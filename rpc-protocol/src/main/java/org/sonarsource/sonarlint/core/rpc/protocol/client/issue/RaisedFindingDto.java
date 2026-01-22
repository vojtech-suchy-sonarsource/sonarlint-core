/*
ACR-325f5218c43041418338e22d97823d78
ACR-8e2cd61dc9b146c9ac5dccfc0f0c3718
ACR-89585e094efe4108b8bc044030e25797
ACR-48a58e09ea2e4b2da9641f5ae834dc05
ACR-f00a16e4e2df4fd083de9e9bbb956fe0
ACR-43dd465ba87344c495901cae5977ee6a
ACR-d4600c6625f24ff98cfa58be0138f146
ACR-6b5c450a5f434694bcd850ad9da10e88
ACR-f586350886e142339f5061ec323ebaee
ACR-e365d18c8942466c9089248854d6c732
ACR-8e82eddb6b1a47bdb8ae6ae2ee9cea52
ACR-583877ae94b24490a373aa9f9190724d
ACR-f144ac21812b454bae8760b862b1b0f6
ACR-27847888f2ba4e3aa414d5e9e6ef58e5
ACR-9cf6ee81c4844120a3c9f60e509b92b0
ACR-58759052df614f3b97acc75225609270
ACR-5122dcfe95d343a09386d7ad1b2a8557
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public abstract class RaisedFindingDto {

  private final UUID id;
  @Nullable
  private final String serverKey;
  private final String ruleKey;
  private final String primaryMessage;
  private final Either<StandardModeDetails, MQRModeDetails> severityMode;
  private final Instant introductionDate;
  private final boolean isOnNewCode;
  private final boolean resolved;
  @Nullable
  private final TextRangeDto textRange;
  private final List<IssueFlowDto> flows;
  private final List<QuickFixDto> quickFixes;
  @Nullable
  private final String ruleDescriptionContextKey;

  protected RaisedFindingDto(UUID id, @Nullable String serverKey, String ruleKey, String primaryMessage, Either<StandardModeDetails, MQRModeDetails> severityMode,
    Instant introductionDate, boolean isOnNewCode, boolean resolved, @Nullable TextRangeDto textRange, List<IssueFlowDto> flows, List<QuickFixDto> quickFixes,
    @Nullable String ruleDescriptionContextKey) {
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
  }

  public UUID getId() {
    return id;
  }

  @CheckForNull
  public String getServerKey() {
    return serverKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getPrimaryMessage() {
    return primaryMessage;
  }

  public Either<StandardModeDetails, MQRModeDetails> getSeverityMode() {
    return severityMode;
  }

  public Instant getIntroductionDate() {
    return introductionDate;
  }

  public boolean isOnNewCode() {
    return isOnNewCode;
  }

  public boolean isResolved() {
    return resolved;
  }

  @CheckForNull
  public TextRangeDto getTextRange() {
    return textRange;
  }

  public List<IssueFlowDto> getFlows() {
    return flows;
  }

  public List<QuickFixDto> getQuickFixes() {
    return quickFixes;
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }
}
