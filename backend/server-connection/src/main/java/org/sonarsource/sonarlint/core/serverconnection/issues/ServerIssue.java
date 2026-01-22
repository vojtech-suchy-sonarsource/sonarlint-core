/*
ACR-944b8791f9944d64a4979301469b54df
ACR-8fd0806975a74ac0b3a75829df6bc50e
ACR-767a549a4c1044b8b0767b76256c8c63
ACR-dd606054382647b0839227fceaa4661c
ACR-fcac50e7630a4b65ad9bb2b3b6a9c4ce
ACR-246d8222600247a19edeb156f778572a
ACR-d7d3387b8cac43bcb04b4c64f13a3a23
ACR-9853b2ed75614549b39c633e207cc50c
ACR-b9258a97619b4a03bc09b7ff6641126d
ACR-98cce57ccbc243418d94d44c3b77d44d
ACR-a2b655ea777a45b182c880460dd72169
ACR-c6229e6632a9469083b108d95e122c69
ACR-2614d4540d5549f4945d6284f2bb7f9c
ACR-bdee55daca0440b49a16587b26896b1e
ACR-4fec766f61c7401ca31e3e1ca1cbe620
ACR-85699eebea104752bdc0e00fd5116948
ACR-86df058a91f748c8ae2f80f267f04b08
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
