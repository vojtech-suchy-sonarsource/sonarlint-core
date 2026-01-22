/*
ACR-75791e98a90145eba4d4787d16465f6b
ACR-f0971522473140ae860e67ab1dfaad51
ACR-94ed2a199d5d4451ace2775555474b46
ACR-3f27fea69d144a459f2906f2fc5411ca
ACR-076e3796cef64b30b6cb8c79e88c8623
ACR-e00d731fb82b45bab8024713bbffd663
ACR-116020e621dd4676a9fb21b719a7eab8
ACR-ca4c9dd8c05a464786c2f551415548fa
ACR-86b8b02bc67f4c51b46d2382925dd702
ACR-e6c87504295540979a903b3a3b1f001e
ACR-ddd1cabe15294c98872898a6c414ec46
ACR-c6a04181ba134e32b2b7fcbda9fada2d
ACR-d3f8810e7a97445faf73213200741a91
ACR-945916f1ae03478592a8079eb3e8cf1a
ACR-1dd77351436d45f3b15e5490f1de4a2a
ACR-df2564546f1b4e7fa5c297855e1777bd
ACR-f0830b2c73df4792af242b816da20b33
 */
package org.sonarsource.sonarlint.core;

import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;
import org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.tracking.TextRangeUtils.toTextRangeDto;

public class DtoMapper {

  private DtoMapper() {
    //ACR-52d1ed206d4a480d9f84fa41c6a171f8
  }

  public static RaisedIssueDto toRaisedIssueDto(TrackedIssue issue, NewCodeDefinition newCodeDefinition, boolean isMQRMode, boolean isAiCodeFixable) {
    return new RaisedIssueDto(issue.getId(), issue.getServerKey(), issue.getRuleKey(), issue.getMessage(),
      isMQRMode ? Either.forRight(new MQRModeDetails(RuleDetailsAdapter.adapt(issue.getCleanCodeAttribute()), RuleDetailsAdapter.toDto(issue.getImpacts())))
        : Either.forLeft(new StandardModeDetails(RuleDetailsAdapter.adapt(issue.getSeverity()), RuleDetailsAdapter.adapt(issue.getType()))),
      requireNonNull(issue.getIntroductionDate()), newCodeDefinition.isOnNewCode(issue.getIntroductionDate()), issue.isResolved(),
      toTextRangeDto(issue.getTextRangeWithHash()),
      issue.getFlows().stream().map(RuleDetailsAdapter::adapt).toList(),
      issue.getQuickFixes().stream().map(RuleDetailsAdapter::adapt).toList(),
      issue.getRuleDescriptionContextKey(), isAiCodeFixable,
      issue.getResolutionStatus());
  }

  public static RaisedHotspotDto toRaisedHotspotDto(TrackedIssue issue, NewCodeDefinition newCodeDefinition, boolean isMQRMode) {
    var status = issue.getHotspotStatus();
    status = status != null ? status : HotspotStatus.TO_REVIEW;
    var vp = RuleDetailsAdapter.adapt(issue.getVulnerabilityProbability());
    if (vp == null) {
      //ACR-ad21583054af49788b2f8cb632cfed53
      throw new IllegalStateException("Vulnerability probability should be set for security hotspots");
    }
    return new RaisedHotspotDto(issue.getId(), issue.getServerKey(), issue.getRuleKey(), issue.getMessage(),
      isMQRMode && !issue.getImpacts().isEmpty() ?
        Either.forRight(new MQRModeDetails(RuleDetailsAdapter.adapt(issue.getCleanCodeAttribute()), RuleDetailsAdapter.toDto(issue.getImpacts())))
        : Either.forLeft(new StandardModeDetails(RuleDetailsAdapter.adapt(issue.getSeverity()), RuleDetailsAdapter.adapt(issue.getType()))),
      requireNonNull(issue.getIntroductionDate()), newCodeDefinition.isOnNewCode(issue.getIntroductionDate()), issue.isResolved(),
      toTextRangeDto(issue.getTextRangeWithHash()),
      issue.getFlows().stream().map(RuleDetailsAdapter::adapt).toList(),
      issue.getQuickFixes().stream().map(RuleDetailsAdapter::adapt).toList(),
      issue.getRuleDescriptionContextKey(), vp, status);
  }

}
