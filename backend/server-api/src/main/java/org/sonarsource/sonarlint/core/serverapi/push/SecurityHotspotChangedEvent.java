/*
ACR-4ea90d412db6401b8da1cbafc8863bdd
ACR-1a73389aac314ec5875da5338e40d161
ACR-30272d48989f4cc9896494888ec6fc25
ACR-727b1f1d252d48a4b6a498ceea89c8f4
ACR-eb80143d5d3f4b9c956d5d834fa26b8e
ACR-af00e3cb8216491ea240f2d62dc437fa
ACR-5f360a424e1b4a1881e998a4a5a15360
ACR-520d2afbe2444d47abbc3c77cbce4198
ACR-edaaa10c6b1d48d79bc5a06f4f504a3e
ACR-bc784bb122174f0888705c8d237f5df2
ACR-15139b7c1b14470fa633846fb191de0d
ACR-96fabfa0247e436ea1f3a04de6543e86
ACR-3c65c47f8b9e41b2945f041f17e5d914
ACR-739cd8845ff842a8a0dbc8c2f26101d0
ACR-b74f40205c3a4fe3b00bef0004715de9
ACR-7b27531aaeea4aae96c3614b42adbacd
ACR-397ef1ec5b594130a4b2a4fbfcb6ac98
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.nio.file.Path;
import java.time.Instant;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;

public class SecurityHotspotChangedEvent implements ServerHotspotEvent {
  private final String hotspotKey;
  private final String projectKey;
  private final Instant updateDate;
  private final HotspotReviewStatus status;
  private final String assignee;
  private final Path filePath;

  public SecurityHotspotChangedEvent(String hotspotKey, String projectKey, Instant updateDate, HotspotReviewStatus status, String assignee, Path filePath) {
    this.hotspotKey = hotspotKey;
    this.projectKey = projectKey;
    this.updateDate = updateDate;
    this.status = status;
    this.assignee = assignee;
    this.filePath = filePath;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }

  @Override
  public String getProjectKey() {
    return projectKey;
  }

  public Instant getUpdateDate() {
    return updateDate;
  }

  public HotspotReviewStatus getStatus() {
    return status;
  }

  public String getAssignee() {
    return assignee;
  }

  @Override
  public Path getFilePath() {
    return filePath;
  }
}
