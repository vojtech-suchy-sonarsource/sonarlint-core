/*
ACR-1e2dfdd026bb4ae9a43a1392fe1b1a75
ACR-662120eb69c04b8cab3053f103087c5c
ACR-8d4279efdc8846daaf47572bbe5fbd4c
ACR-d98138c828b94dcfb9d58001a9e6b8bf
ACR-66dcbea8cd1744a6b973855a9b5bfd5b
ACR-4ff439414c234aacbcdf6c2ced5a31e2
ACR-9152fd524f514edf8bc7f6f9f77dc504
ACR-5a14f6e31a484583a0217e784f74157e
ACR-2322c2a4f0bc4180bde0719df26b7f3c
ACR-aaffa237107f4115b6f5b6b6d4f273de
ACR-dbdf62f9ca7144368cc32ec1f8c4a51c
ACR-34753dbf116a433b97dae80acc5c1072
ACR-b207cb97b0ce474c8cb351460e815f9f
ACR-4e42aaf56cff450b809a7b0d64d5019e
ACR-cf171007e5e74808b5f873deff033445
ACR-97f98828f8e14a379b4065d3ce968de3
ACR-cc8210d214ca4689a527d69291f392b8
 */
package org.sonarsource.sonarlint.core.serverapi.hotspot;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class ServerHotspot {
  private final UUID id;
  private final String key;
  private final String ruleKey;
  private final String message;
  private Path filePath;
  private final TextRange textRange;
  private final Instant creationDate;
  private HotspotReviewStatus status;
  private final VulnerabilityProbability vulnerabilityProbability;
  @Nullable
  private String assignee;

  public ServerHotspot(@Nullable UUID id, String key,
    String ruleKey,
    String message,
    Path filePath,
    TextRange textRange,
    Instant creationDate,
    HotspotReviewStatus status,
    VulnerabilityProbability vulnerabilityProbability,
    @Nullable String assignee) {
    this.id = id;
    this.key = key;
    this.ruleKey = ruleKey;
    this.message = message;
    this.filePath = filePath;
    this.textRange = textRange;
    this.creationDate = creationDate;
    this.status = status;
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.assignee = assignee;
  }

  /*ACR-f2a1b2ec068b49a0a44faabaf0502ec9
ACR-94d9ad4d11dc4b408f5aa03b585d7e18
ACR-6d57e74c936248e1a19dbf12bef011a7
   */
  public ServerHotspot(String key,
    String ruleKey,
    String message,
    Path filePath,
    TextRange textRange,
    Instant creationDate,
    HotspotReviewStatus status,
    VulnerabilityProbability vulnerabilityProbability,
    @Nullable String assignee) {
    this(null, key, ruleKey, message, filePath, textRange, creationDate, status, vulnerabilityProbability, assignee);
  }

  @CheckForNull
  public UUID getId() {
    return id;
  }

  public void setFilePath(Path filePath) {
    this.filePath = filePath;
  }

  public String getKey() {
    return key;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getMessage() {
    return message;
  }

  public Path getFilePath() {
    return filePath;
  }

  public TextRange getTextRange() {
    return textRange;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public HotspotReviewStatus getStatus() {
    return status;
  }

  public ServerHotspot withStatus(HotspotReviewStatus newStatus) {
    return new ServerHotspot(key, ruleKey, message, filePath, textRange, creationDate, newStatus, vulnerabilityProbability, assignee);
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setStatus(HotspotReviewStatus status) {
    this.status = status;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }
}
