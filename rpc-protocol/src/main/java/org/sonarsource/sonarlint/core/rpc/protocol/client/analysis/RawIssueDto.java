/*
ACR-20c6ae5b749449cebcb1ccbc16c87c4b
ACR-479d2ae9a8fd4e70b44fcffc223dd0f1
ACR-3b3903688b8d466db6418461e26c999a
ACR-1f667df9289740c8aa56dc31279bd9da
ACR-5188e28dfd6747188930eabecdd20419
ACR-81aee5170d30490f8e065c4779552ad7
ACR-93bab2e1fbbf4762867faab50d53cd4e
ACR-9670545815404416bbf2431c4741e712
ACR-d0df875d9d4a4be4bed945f96ede82ab
ACR-eb5919d23cac4126b5c55aeaf4d403c7
ACR-18027ab0492f45baaacc6ed281ad67c5
ACR-c1247cf0595940a3af300306765fbd29
ACR-2a4aeebc945b4b769b45f97f9a112f97
ACR-bd63414d882c404d8415903fb14419fb
ACR-4941bcd801044eff81c76831bd0367fc
ACR-02e7add50bc54c668ed409ed7baa3527
ACR-8749862cc9e441bbaa205436589d7d9c
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

/*ACR-8afd9a13976344ff8247298a5d4e5089
ACR-c81b1146f4724144b21abf5d4fc379bd
ACR-d05ecabf61bd49d7bf3dcbbd9fcc3e37
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
