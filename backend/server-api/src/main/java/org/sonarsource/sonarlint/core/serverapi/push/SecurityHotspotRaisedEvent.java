/*
ACR-2f043a54453f42dca963d681fbe9e7f1
ACR-eb23acff90294f61baca1703b237c133
ACR-52928faa3e884bcf99d88a2a499063e5
ACR-09dd1afc46b84d34a50542b95c2bdd28
ACR-9784ee60ffd74e6bbc2c795dd99f1691
ACR-3debd9b761c6425f9228e515d611cef0
ACR-b18e56a1e0bc4cc8b30ed0cff68b5a05
ACR-ac939beae8b341c4aadeba1bd84eae8a
ACR-4086f32485ff4479bc73cc5eeb57a4d4
ACR-53b9e81d314f4079bb668a764dd89df2
ACR-19f3bbe5025f4bb2a460acb59818673e
ACR-dbcc753c2e0d47e68fa827620a10bc11
ACR-27009cb2529c4baeb226a08c3a0574de
ACR-54991da251f5400ca0f507073e1e9a9d
ACR-23f68cce20964425ab605a6a7932efb6
ACR-2e2be49a04e84327a5830ba083f69799
ACR-2bda8c4cfb3941648bd0d1798b110a0c
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
