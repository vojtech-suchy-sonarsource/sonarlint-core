/*
ACR-794a8e09d42b484da6a4e18b8eb40cc9
ACR-ebcb0368788147e99ce73ce6cc241a7a
ACR-3a4e0c20ae2849e8bb685a6c235a9d0e
ACR-b4079446914f478eadc2cab6d8fe67aa
ACR-8cccc9c5e2bc4f53b14422ea80f8afb9
ACR-0c58e537799046eea8c3febcea872351
ACR-fcb26c43939b4dd69f622d27f16bd332
ACR-b1fce31e90fc4c37bcca0f690ea3cb81
ACR-f76ddeb7dd4e4b27bee65c1e7d7d83ab
ACR-259dcd7c98b34765911e7a9b1ed423f0
ACR-10e62790169c4127a667d8105a7662a0
ACR-2549688c085847829f38f2c3faac6fa3
ACR-cc65b02217e34d9b85f562f751b9f6e9
ACR-b0f24fc94dac48c3b2b8bc81613a9bbc
ACR-e13215bf2eb740cca604dd96d027ae9c
ACR-e7afab4c8a874ccda9f82a7d9e5656ee
ACR-c210e53a5c9b4af9b4a5a7bbf8f4bfb6
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
    //ACR-6d78552c54d240b49bb2f12c773f9cbf
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
      //ACR-f319124f19f4436a84db3d49a2b53c3e
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
