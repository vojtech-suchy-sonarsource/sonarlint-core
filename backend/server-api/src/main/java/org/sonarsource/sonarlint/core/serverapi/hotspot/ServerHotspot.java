/*
ACR-e340b19b974a461a82303570a52169ed
ACR-2f1bbd919b6a4cdd948f403a59adc478
ACR-842da954fb144967aa7441620ae04c20
ACR-f3516827def64a46860f7c299fd67aca
ACR-073217a6b3cb4cd6b268c0b23af694a2
ACR-374851e0befd4d89a72b011b8018efa9
ACR-c3829509225f4ed3a1c671f74150e186
ACR-4aeee43abdd54c029bfc182fa486af82
ACR-724bdd50550f46d6a67ad969c5a00c88
ACR-67f89e6a31284a2989539fadf1088eb4
ACR-9ae796151d2348e7a37be6cee39f2acc
ACR-1dd3d5c977cf4bcabab1e8de0fcf9824
ACR-cce8aabc8ec6416cb17dc5b4c3d82baf
ACR-24fc744924b141af8d8b713124b952a6
ACR-f562c60e3f7d4cd88fd5ff541ba5e16d
ACR-5f95cb87cbb146e09ca0e23d25b76319
ACR-fa3ff37ad99949a9a6579e3a2ff7a52e
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

  /*ACR-317e73b6081f479d9f3008f1107ea85c
ACR-cfeae30a06ae415786a187094dc92bc6
ACR-15b72d6800c94feda9be174c1fe34ec0
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
