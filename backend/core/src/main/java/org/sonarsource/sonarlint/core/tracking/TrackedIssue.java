/*
ACR-f73e0d636b874d109c01f315e2facd2c
ACR-9adc715323cc4b619eb1cfeb6a5e759a
ACR-96c3361d0f864068946d984746a80c4d
ACR-37fa5010d2a7455dbf2e435f5c7de859
ACR-fec2f77f5f5f41b5abc0fc04f9a5136d
ACR-5d4847d858324b8ea67ad2628de85ceb
ACR-44b2b107ea8f4a40a2e3ff9b9e658862
ACR-9f2f533005d24e9ca0fffc036e2aadf7
ACR-90b7186ede9143488775faa2a3f1fd8c
ACR-f114e63af70b4191ae103b5bda6d8335
ACR-1d26456b5dd942218ca9ba98e9758d9f
ACR-d2381c8c16a04770bfed31787e7bf68a
ACR-8881f30aae104c0ba8a42d01ab4b989f
ACR-6418e680926746d3a421fdc1a630ca80
ACR-0c189432eb7640f6aa1f0a60421493d9
ACR-8a18f4e6d9554456a6a9bda4d2009e2c
ACR-3ed63da6988b4652955cddace21ec8f1
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
