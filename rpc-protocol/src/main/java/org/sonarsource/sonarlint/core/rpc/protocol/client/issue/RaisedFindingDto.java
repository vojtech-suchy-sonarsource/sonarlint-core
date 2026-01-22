/*
ACR-7499db157ebf4602ae830dee6c4b2c24
ACR-9e3cf9394dd143669dab5c29ce1d1372
ACR-bafb62d86e9043f09e009dd7d288b4f1
ACR-40adf415df1e44da87f190776270db77
ACR-7562f3cbfaf34fd6b3e770e75e85242c
ACR-60579ec46a0c44499a4f55596d5ff64e
ACR-37a1932d82fd4ca2ad092ceb909d7b32
ACR-4e5bdc13e70f4a758176262dd38e8f60
ACR-6fbbd3aa3dc04cb39a5325c5e48e474b
ACR-199117c1e2614c9ca721b941d2749317
ACR-be627cc7d60f49c29fa2e49d558c0628
ACR-598a3c8798024783ad71b0909e6898fb
ACR-84de1d1eddcf4b96b4fa820d8d4906c3
ACR-40d8d3756cec4f65aad819eaede6eab6
ACR-90119394590c41d3936cfaa46abab362
ACR-9f75b50dcb5f4ea79e69945dd1125211
ACR-eaf98cd61f18421fa5be7751f1722616
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
