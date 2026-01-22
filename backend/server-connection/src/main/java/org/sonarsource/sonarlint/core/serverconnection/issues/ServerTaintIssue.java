/*
ACR-0bb0730a2b3e411789b02122f87308c1
ACR-21f9ce8cad424517a56562668fff5f43
ACR-151f8d81baf145fb8cfa6bc80a42f0a9
ACR-4b95816867e84d3786339bab86fdbeb8
ACR-adf34d9411244179bbfe354ab7d1a19c
ACR-887aaa8e7faa485c87a21fb6a4a4396f
ACR-3ca2b7751dee4e828b573dc7ccff1665
ACR-18f9ac8bd3a2488ba1789e4c839ac805
ACR-3bc805db4afc4cb48385b8bf256224e9
ACR-50563ed4f20846b1b62278bb80747c3e
ACR-077e519af3334f42bb9c533fe8463be9
ACR-b16d2992ea94497fba4cb18d024ef4f6
ACR-7355c0cea5504ee682c6b69568031c7b
ACR-9b21bae5e84b4be098b8a242bcc47b50
ACR-4896e58be2c24e87ab9d6bcea57e56da
ACR-78b3e945150543c5ab50c4a7226a7896
ACR-65b1ea6c751642d5995b580458a6c228
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class ServerTaintIssue implements ServerFinding {
  private final UUID id;
  private final String key;
  private boolean resolved;
  @Nullable
  private final IssueStatus resolutionStatus;
  private final String ruleKey;
  private final String message;
  private final Path filePath;
  private final Instant creationDate;
  private IssueSeverity severity;
  private RuleType type;
  private final List<Flow> flows;
  private final TextRangeWithHash textRange;
  private Map<SoftwareQuality, ImpactSeverity> impacts;
  @Nullable
  private final String ruleDescriptionContextKey;
  @Nullable
  private final CleanCodeAttribute cleanCodeAttribute;

  public ServerTaintIssue(UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, IssueSeverity severity, RuleType type,
    @Nullable TextRangeWithHash textRange, @Nullable String ruleDescriptionContextKey, @Nullable CleanCodeAttribute cleanCodeAttribute,
    Map<SoftwareQuality, ImpactSeverity> impacts, List<Flow> flows) {
    this.id = id;
    this.key = key;
    this.resolved = resolved;
    this.resolutionStatus = resolutionStatus;
    this.ruleKey = ruleKey;
    this.message = message;
    this.filePath = filePath;
    this.creationDate = creationDate;
    this.severity = severity;
    this.type = type;
    this.textRange = textRange;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.impacts = impacts;
    this.flows = flows;
  }

  public UUID getId() {
    return id;
  }

  public String getSonarServerKey() {
    return key;
  }

  public boolean isResolved() {
    return resolved;
  }

  @CheckForNull
  public IssueStatus getResolutionStatus() {
    return resolutionStatus;
  }

  @Override
  public String getRuleKey() {
    return ruleKey;
  }

  public String getMessage() {
    return message;
  }

  public Path getFilePath() {
    return filePath;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RuleType getType() {
    return type;
  }

  @CheckForNull
  public TextRangeWithHash getTextRange() {
    return textRange;
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  public List<Flow> getFlows() {
    return flows;
  }

  public Optional<CleanCodeAttribute> getCleanCodeAttribute() {
    return Optional.ofNullable(cleanCodeAttribute);
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public ServerTaintIssue setResolved(boolean resolved) {
    this.resolved = resolved;
    return this;
  }

  public ServerTaintIssue setImpacts(Map<SoftwareQuality, ImpactSeverity> impacts) {
    this.impacts = impacts;
    return this;
  }

  public ServerTaintIssue setSeverity(IssueSeverity severity) {
    this.severity = severity;
    return this;
  }

  public ServerTaintIssue setType(RuleType type) {
    this.type = type;
    return this;
  }

  public record Flow(List<ServerIssueLocation> locations) {
  }

  public record ServerIssueLocation(@Nullable Path filePath, @Nullable TextRangeWithHash textRange, @Nullable String message) {
  }
}
