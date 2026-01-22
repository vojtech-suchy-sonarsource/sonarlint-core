/*
ACR-ad73fe26089c4bae99e9c00670fe6969
ACR-6fa480e8db4d42fcb2d85a7fbd0a5441
ACR-a92a6ebcc7b44474876a14837e536c1c
ACR-e3017a05d88d46579054d14ae472f078
ACR-70625c7814ae4276bbf844ae73654522
ACR-b94e938faebb471b9c8107bc799d8bbd
ACR-ce0abdd61ea74875ae515c408c8a670f
ACR-5556507a5b134775b45e606bfd1459eb
ACR-78bd6b1f3aca4b1cacbb208620623f80
ACR-3072e87d640c4199a7b5867cfd761c0c
ACR-f3b8e3743d444057947fe5f16a840a7e
ACR-841e32ed6d36490888e607fd6fb3762d
ACR-ee70145bdbd6413395f9cffbbb4afe93
ACR-4e3286ff9349441eaf6e37fb8089de66
ACR-904e7944793343d195ee01e9d3a88feb
ACR-dc8d1a237ed148508ecb116d800ad37e
ACR-c141c22511074ff8923a11ceeb8bf87f
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
