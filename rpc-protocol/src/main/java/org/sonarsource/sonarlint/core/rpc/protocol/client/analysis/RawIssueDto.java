/*
ACR-43122082eff2418184c90dfa2cefa567
ACR-5996bb13feec466a9718df956b0635bc
ACR-a67db283ef4b46a6900cf98132e48106
ACR-1334fc7a961b482b841287f2b456fd3a
ACR-02276de49abc43ecb407cd5b6cf0fcb1
ACR-7319a45f09784c1e8d7a1cd879e4da05
ACR-b256b4ca835e4d70b159dee26952368d
ACR-af0ee1450b584ac9a588866ca8aec31d
ACR-6751c83d355740e7be0cde88078b3bfa
ACR-29b465c613d34fc09b9f611814590652
ACR-2a5e85f235464b1a9a970dc55fb705d5
ACR-469950e16b2b47c7b3bc78306acf457a
ACR-2467bcaaf7c449a5aa129f9d6c1a2dbb
ACR-286922ce67c944398af17f211b2d022b
ACR-76088f3d753c4b79a7d3b42013609719
ACR-ff3e24ac4a974f719a148dfa31eed1a0
ACR-111b07cbd4dd4d7c99b5851f0bb9cf7f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.analysis;

import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

/*ACR-e3dcc623938c4e2d8c9a70f0802d098b
ACR-6cf9fe5de48e4885b35cd4a6e932c773
ACR-d9e98e93d35c42c0bbaaa479f7d234da
 */
@Deprecated(since = "10.2")
public class RawIssueDto {
  private final IssueSeverity severity;
  private final RuleType type;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final Map<SoftwareQuality, ImpactSeverity> impacts;
  private final String ruleKey;
  private final String primaryMessage;
  private final URI fileUri;
  private final List<RawIssueFlowDto> flows;
  private final List<QuickFixDto> quickFixes;
  private final TextRangeDto textRange;
  @Nullable
  private final String ruleDescriptionContextKey;
  @Nullable
  private final VulnerabilityProbability vulnerabilityProbability;

  public RawIssueDto(IssueSeverity severity, RuleType type, CleanCodeAttribute cleanCodeAttribute, Map<SoftwareQuality, ImpactSeverity> impacts, String ruleKey,
    String primaryMessage, @Nullable URI fileUri, List<RawIssueFlowDto> flows, List<QuickFixDto> quickFixes, @Nullable TextRangeDto textRange,
    @Nullable String ruleDescriptionContextKey, @Nullable VulnerabilityProbability vulnerabilityProbability) {
    this.severity = severity;
    this.type = type;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.impacts = impacts;
    this.ruleKey = ruleKey;
    this.primaryMessage = primaryMessage;
    this.fileUri = fileUri;
    this.flows = flows;
    this.quickFixes = quickFixes;
    this.textRange = textRange;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
    this.vulnerabilityProbability = vulnerabilityProbability;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RuleType getType() {
    return type;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getPrimaryMessage() {
    return primaryMessage;
  }

  @CheckForNull
  public URI getFileUri() {
    return fileUri;
  }

  public List<RawIssueFlowDto> getFlows() {
    return flows;
  }

  public List<QuickFixDto> getQuickFixes() {
    return quickFixes;
  }

  @CheckForNull
  public TextRangeDto getTextRange() {
    return textRange;
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  @CheckForNull
  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }
}
