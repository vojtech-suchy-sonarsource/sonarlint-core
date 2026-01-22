/*
ACR-398c085e871846c88222b7c5b6f53aaa
ACR-659de559b0bf4d21a8713baf66d81517
ACR-3ed7a70ecd174d15a8ed6b9d87155eb1
ACR-37064793649d41f1aa6b2d0d8c5273ce
ACR-1ebbe11d0396468eb35741b593ecf446
ACR-03c2322a0a394e89a714dd5026ce5bd6
ACR-1cd13ffa0b0f4e159f79fe52b4a63458
ACR-b2e66d6f67a14c1eb8e18aee82dc45b3
ACR-631e67d5cb194241a72668de34cf8559
ACR-ca3aac0dd0874ca7b4c2c303baaa54cf
ACR-29df116c4bfc40e88a6823aea49c67af
ACR-9497a9f8a57b426cadad1906c59f63dd
ACR-55c48a49ea894a21b01cccab4a908c39
ACR-ad2c1899ca4b4a968ed19aa835a56934
ACR-b161498051db4628860f5f54aca2d69c
ACR-fb096543cef548889f0d6b13f9cf8bd0
ACR-c17e08c9f65d49af8a8e2de1ec51df41
 */
package org.sonarsource.sonarlint.core.tracking;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.api.Flow;
import org.sonarsource.sonarlint.core.analysis.api.QuickFix;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.LineWithHash;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;

public class TrackedIssue {
  private final UUID id;
  private final String message;
  private final String ruleKey;
  private final TextRangeWithHash textRangeWithHash;
  private final LineWithHash lineWithHash;
  private final String serverKey;
  private final Instant introductionDate;
  private final boolean resolved;
  private final IssueSeverity severity;
  private final RuleType type;
  private final Map<SoftwareQuality, ImpactSeverity> impacts;
  private final List<Flow> flows;
  private final List<QuickFix> quickFixes;
  private final VulnerabilityProbability vulnerabilityProbability;
  private final HotspotStatus hotspotStatus;
  private final ResolutionStatus resolutionStatus;
  private final String ruleDescriptionContextKey;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final URI fileUri;

  public TrackedIssue(UUID id, String message, @Nullable Instant introductionDate, boolean resolved, IssueSeverity overriddenSeverity, RuleType type, String ruleKey,
    @Nullable TextRangeWithHash textRangeWithHash, @Nullable LineWithHash lineWithHash, @Nullable String serverKey, Map<SoftwareQuality, ImpactSeverity> impacts, List<Flow> flows,
    List<QuickFix> quickFixes, @Nullable VulnerabilityProbability vulnerabilityProbability, @Nullable HotspotStatus hotspotStatus, @Nullable ResolutionStatus resolutionStatus, 
    @Nullable String ruleDescriptionContextKey, CleanCodeAttribute cleanCodeAttribute, @Nullable URI fileUri) {
    this.id = id;
    this.message = message;
    this.ruleKey = ruleKey;
    this.textRangeWithHash = textRangeWithHash;
    this.lineWithHash = lineWithHash;
    this.serverKey = serverKey;
    this.introductionDate = introductionDate;
    this.resolved = resolved;
    this.severity = overriddenSeverity;
    this.type = type;
    this.impacts = impacts;
    this.flows = flows;
    this.quickFixes = quickFixes;
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.hotspotStatus = hotspotStatus;
    this.resolutionStatus = resolutionStatus;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.fileUri = fileUri;
  }

  public UUID getId() {
    return id;
  }

  @CheckForNull
  public String getServerKey() {
    return serverKey;
  }

  public Instant getIntroductionDate() {
    return introductionDate;
  }

  public boolean isResolved() {
    return resolved;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RuleType getType() {
    return type;
  }

  public boolean isSecurityHotspot() {
    return getType() == RuleType.SECURITY_HOTSPOT;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  @CheckForNull
  public TextRangeWithHash getTextRangeWithHash() {
    return textRangeWithHash;
  }

  public String getMessage() {
    return message;
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public List<Flow> getFlows() {
    return flows;
  }

  public List<QuickFix> getQuickFixes() {
    return quickFixes;
  }

  @CheckForNull
  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  @CheckForNull
  public LineWithHash getLineWithHash() {
    return lineWithHash;
  }

  @CheckForNull
  public URI getFileUri() {
    return fileUri;
  }

  @CheckForNull
  public HotspotStatus getHotspotStatus() {
    return hotspotStatus;
  }

  public ResolutionStatus getResolutionStatus() {
    return resolutionStatus;
  }
}
