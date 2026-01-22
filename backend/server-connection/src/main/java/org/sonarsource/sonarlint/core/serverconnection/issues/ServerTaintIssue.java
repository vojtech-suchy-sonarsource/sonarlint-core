/*
ACR-6e5f4272d9e94c46893f24481dfcc5a4
ACR-7817cc1e3f6e4c329ec2beb12379ca23
ACR-971e66ad79334cdfacb02c7c66e9de60
ACR-ac7e49226e2f4733b4215a9433ab4978
ACR-308b0f6337b743bfaa9baeef986fca34
ACR-160eb27ca8fc41c5a80d858e7cf9243f
ACR-cb452070c37847209e68d0d07d390149
ACR-a50d33fb23b340ecab3effeb218e287e
ACR-98d9563bdabd4a9a84bc24b4f6644bcd
ACR-ea019923aa09438a8f72791b136fbf1b
ACR-f03e21d5c9e8410e9deb8faec4fbc55d
ACR-a87a5bdb697f4032b058c54f7f55b5d5
ACR-4a4c9c989f194782b3505ac6e69dd05c
ACR-64772639d4d24936a84d29d20a342dba
ACR-31431e691f644559bd1e23fbaebdd66f
ACR-41397f0b50e54b5e96415d338e20f66e
ACR-26d7fa7d9b974220a75b7d407ace39a2
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
