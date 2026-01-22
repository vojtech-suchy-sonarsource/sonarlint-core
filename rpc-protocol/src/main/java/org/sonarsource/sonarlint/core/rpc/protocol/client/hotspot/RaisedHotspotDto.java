/*
ACR-46eda4d2e6454b2b9ebcb0e88f121c5d
ACR-2cb65af17a8445f7b8d8d24a2cf3e29e
ACR-8c82b010e232483f937959461a6ba08e
ACR-6371bebb4c884fb5bd50f16ec9f6b340
ACR-dd6794e4850c42468a3c80b3d6126102
ACR-5f1e89c76fff4342bf26eafd752b9b44
ACR-c7777bd98abf41cd8d2ea105b35f8f10
ACR-10965fb8fff44521ba0646bf729d7e3a
ACR-596d47f9cb8f44ea996bd815b9b88172
ACR-972bfa72cee84be6988157366a0e7549
ACR-386ca66a8b1b4f5883ec48f6f57b2904
ACR-7830d6af22474afa86e80d072b44cb29
ACR-11d923306c554e449a3ed77e830aa8c4
ACR-e2a5ffb882e144ffaa2d38ba992b130a
ACR-66adb5da1b404d98a3732d1e6073414a
ACR-77992b54295c4aa4bfddc4cc3fe14ebc
ACR-dc575d3964e547efad03a1ec3945d81b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueFlowDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.QuickFixDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class RaisedHotspotDto extends RaisedFindingDto {

  private final HotspotStatus status;
  private final VulnerabilityProbability vulnerabilityProbability;

  public RaisedHotspotDto(UUID id, @Nullable String serverKey, String ruleKey, String primaryMessage, Either<StandardModeDetails, MQRModeDetails> severityMode,
    Instant introductionDate, boolean isOnNewCode, boolean resolved, @Nullable TextRangeDto textRange, List<IssueFlowDto> flows, List<QuickFixDto> quickFixes,
    @Nullable String ruleDescriptionContextKey, VulnerabilityProbability vulnerabilityProbability, HotspotStatus status) {
    super(id, serverKey, ruleKey, primaryMessage, severityMode, introductionDate, isOnNewCode, resolved, textRange, flows, quickFixes, ruleDescriptionContextKey);
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.status = status;
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  public HotspotStatus getStatus() {
    return status;
  }

  public RaisedHotspotDto withHotspotStatusAndResolution(HotspotStatus hotspotStatus, boolean resolved) {
    return new RaisedHotspotDto(getId(), getServerKey(), getRuleKey(), getPrimaryMessage(), getSeverityMode(), getIntroductionDate(), isOnNewCode(), resolved, getTextRange(),
      getFlows(), getQuickFixes(), getRuleDescriptionContextKey(), getVulnerabilityProbability(), hotspotStatus);
  }
}
