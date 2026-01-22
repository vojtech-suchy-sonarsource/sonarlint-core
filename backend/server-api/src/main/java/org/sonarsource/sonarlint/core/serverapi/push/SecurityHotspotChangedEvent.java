/*
ACR-959fcb6ed9064ff0853825ff57644db7
ACR-98022a0bc0be43d9be09daa6c7441d1a
ACR-02b03932416549c6a52b55bf1ca18c58
ACR-7b167d808173440ab7b0cd525eeb81bc
ACR-18196d3ffae54cd883a6a00ef6236a34
ACR-7c7718cb8c1a40f2b149e6ba00e581e9
ACR-01eb8d9b1992494da19b9e40c9863aa4
ACR-3cdb52165dd04cbe914b468391b0bb90
ACR-c9b962995bb64a5890aba54081bf22c8
ACR-6cd36029ecca4a10b45292e237af7004
ACR-ca56fe6846844864b0a84147eae483a1
ACR-c548de55f0b14e82a13962b604125c75
ACR-64953d0a43514f169065e2371101b9b2
ACR-cf7ccb8c35a647bba2c1ca4fec2fe78a
ACR-21b5d91b9e114f0fb0bff4492d7a1363
ACR-f0ed2d7573ba49889ad296065c003aae
ACR-9385869a37fb464f92bd6af206f3b84d
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
