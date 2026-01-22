/*
ACR-22c42a1c94424e2295c332572dc7bb3f
ACR-a85c3a8b833d46e79328c1250022bc9a
ACR-b2426fc637954f8491fab3000fb898e7
ACR-bf283b0cc58442328e54206e48b56190
ACR-a24a6531ddef4726b1844e406121efbc
ACR-a8868a536bfc43268918bde166925999
ACR-88967d48904248b7810e9bad03e1570d
ACR-047e63d89ad14fef83928e846c06ad26
ACR-9f46d701d3ef4ffc91e9139553d5636a
ACR-512a58c3eb1b4bca9909af07e009bb73
ACR-2d23018295424d7abf927add544d284d
ACR-ab77378054064941a4902327e825c0c2
ACR-a458cdd57a4e4f1e8341a874c822f0b9
ACR-1037d93cf859471e82def12f774bf2e1
ACR-722a160dcb9641a7ab6336c4f6f2c16c
ACR-5cae3119abf94aedbfd0d048cd32a3d6
ACR-d82f49c16cbb4247b5dead85414a4f60
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.nio.file.Path;
import java.time.Instant;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;

public class SecurityHotspotRaisedEvent implements ServerHotspotEvent {
  private final String hotspotKey;
  private final String projectKey;
  private final VulnerabilityProbability vulnerabilityProbability;
  private final HotspotReviewStatus status;
  private final Instant creationDate;
  private final String branch;
  private final TaintVulnerabilityRaisedEvent.Location mainLocation;
  private final String ruleKey;
  @Nullable
  private final String ruleDescriptionContextKey;
  @Nullable
  private final String assignee;

  public SecurityHotspotRaisedEvent(String hotspotKey, String projectKey, VulnerabilityProbability vulnerabilityProbability,
    HotspotReviewStatus status, Instant creationDate, String branch, TaintVulnerabilityRaisedEvent.Location mainLocation, String ruleKey,
    @Nullable String ruleDescriptionContextKey, @Nullable String assignee) {
    this.hotspotKey = hotspotKey;
    this.projectKey = projectKey;
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.status = status;
    this.creationDate = creationDate;
    this.branch = branch;
    this.mainLocation = mainLocation;
    this.ruleKey = ruleKey;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
    this.assignee = assignee;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }

  @Override
  public String getProjectKey() {
    return projectKey;
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  public HotspotReviewStatus getStatus() {
    return status;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public String getBranch() {
    return branch;
  }

  public TaintVulnerabilityRaisedEvent.Location getMainLocation() {
    return mainLocation;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  @Nullable
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  @Override
  public Path getFilePath() {
    return mainLocation.getFilePath();
  }
}
