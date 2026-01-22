/*
ACR-b6500fcbf4dc4b7ea3b23f54bb58c647
ACR-81b7fa0c1ad6461daa352ae7c79f1842
ACR-24bf41b00bc545aeb557697ca7639944
ACR-676a523cfc344931a460c9a1e886f149
ACR-b50ce6211ee547c1af1c88f4c8e09e31
ACR-990a0e8e553d457ca759fd3b6b779d37
ACR-e7d87212231f4772ab54846bc584bf80
ACR-f311b11dabb8411ebbf81dac358f37dd
ACR-f957b9b5b8134892bca1d60118f55e64
ACR-fcc5783861ea4346b2e95d28d253c3b7
ACR-20ea9a8a09b84b3fbd95c14e6b5c7d28
ACR-67bac97447f14096b4c1533737995fc4
ACR-4631394cb06c4d3bb3c37390d0c244b6
ACR-eac72d821c7e42e68e2a2772b2cee0d6
ACR-2251b0ca520c4441829915a30d3fda32
ACR-1828fa5d55a54f5082b938b10ce87389
ACR-aed717c42e844154a8fefdb67bccc0ae
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;

public abstract class ServerIssue<G extends ServerIssue<G>> implements ServerFinding {

  private UUID id;
  private String key;
  private boolean resolved;
  private IssueStatus resolutionStatus;
  private String ruleKey;
  private String message;
  private Path filePath;
  private Instant creationDate;
  private IssueSeverity userSeverity;
  private RuleType type;
  private Map<SoftwareQuality, ImpactSeverity> impacts;

  protected ServerIssue(@Nullable UUID id, String key, boolean resolved, @Nullable IssueStatus resolutionStatus, String ruleKey,
    String message, Path filePath, Instant creationDate, @Nullable IssueSeverity userSeverity, RuleType type,
    Map<SoftwareQuality, ImpactSeverity> impacts) {
    this.id = id;
    this.key = key;
    this.resolved = resolved;
    this.resolutionStatus = resolutionStatus;
    this.ruleKey = ruleKey;
    this.message = message;
    this.filePath = filePath;
    this.creationDate = creationDate;
    this.userSeverity = userSeverity;
    this.type = type;
    this.impacts = impacts;
  }

  @CheckForNull
  public UUID getId() {
    return id;
  }

  public String getKey() {
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

  @CheckForNull
  public IssueSeverity getUserSeverity() {
    return userSeverity;
  }

  public RuleType getType() {
    return type;
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public G setId(@Nullable UUID id) {
    this.id = id;
    return (G) this;
  }

  public G setKey(String key) {
    this.key = key;
    return (G) this;
  }

  public G setResolutionStatus(@Nullable IssueStatus resolutionStatus) {
    this.resolutionStatus = resolutionStatus;
    return (G) this;
  }

  public G setRuleKey(String ruleKey) {
    this.ruleKey = ruleKey;
    return (G) this;
  }

  public G setMessage(String message) {
    this.message = message;
    return (G) this;
  }

  public G setFilePath(Path filePath) {
    this.filePath = filePath;
    return (G) this;
  }

  public G setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
    return (G) this;
  }

  public G setUserSeverity(@Nullable IssueSeverity userSeverity) {
    this.userSeverity = userSeverity;
    return (G) this;
  }

  public G setType(RuleType type) {
    this.type = type;
    return (G) this;
  }

  public G setResolved(boolean resolved) {
    this.resolved = resolved;
    return (G) this;
  }

  public G setImpacts(Map<SoftwareQuality, ImpactSeverity> impacts) {
    this.impacts = impacts;
    return (G) this;
  }

}
