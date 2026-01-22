/*
ACR-af3db6751eb147d79232e03f6c8cd4ee
ACR-6d53b7609bfb40208ade75466ac3ab19
ACR-dd822c8e574c402ebcc94f87df5542f6
ACR-0ba8bbd0cef7458eac1eef38fc26130d
ACR-12a1f0febcf845b19e7747eeb791878e
ACR-dc2630caf7d54fcebb06161507f7a033
ACR-ec3436c681f4413c97cc79a184f96ca9
ACR-1cec53f7c2434a6bbacce21eef27662f
ACR-b0948847cf674ad788ff51dcc3970c4e
ACR-92589e73e6794825b8399cc47e4ceb3c
ACR-ca5b1b9598cf4e78a16ab0fd7fc8d7d3
ACR-53c62b3126f04b7fbc7ce53a288ba26e
ACR-4ff98b003a184f5a9eebf8c6e8db0fc3
ACR-6b0a4c5930344acbb0642cc4726ff310
ACR-989e6df9888a459a9b1c088885d02a2a
ACR-defadf888c8a46bb83921aeb0677204f
ACR-6b399e0028194f28937256cbafbf3558
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class LocalOnlyIssue {
  private final UUID id;
  private final Path serverRelativePath;
  private final TextRangeWithHash textRangeWithHash;
  private final LineWithHash lineWithHash;
  private final String ruleKey;
  private final String message;
  private LocalOnlyIssueResolution resolution;

  /*ACR-7ba54ca865df4cc99bc598a7c87e26f9
ACR-c7cf3d4456d64d1092e2e1ebc5bde8bb
   */
  public LocalOnlyIssue(UUID id, Path serverRelativePath, @Nullable TextRangeWithHash textRangeWithHash, @Nullable LineWithHash lineWithHash, String ruleKey,
    String message, @Nullable LocalOnlyIssueResolution resolution) {
    this.id = id;
    this.serverRelativePath = serverRelativePath;
    this.textRangeWithHash = textRangeWithHash;
    this.lineWithHash = lineWithHash;
    this.ruleKey = ruleKey;
    this.message = message;
    this.resolution = resolution;
  }

  public UUID getId() {
    return id;
  }

  public Path getServerRelativePath() {
    return serverRelativePath;
  }

  @CheckForNull
  public TextRangeWithHash getTextRangeWithHash() {
    return textRangeWithHash;
  }

  @CheckForNull
  public LineWithHash getLineWithHash() {
    return lineWithHash;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getMessage() {
    return message;
  }

  @CheckForNull
  public LocalOnlyIssueResolution getResolution() {
    return resolution;
  }

  public void resolve(IssueStatus newStatus) {
    resolution = new LocalOnlyIssueResolution(newStatus, Instant.now(), null);
  }

}
