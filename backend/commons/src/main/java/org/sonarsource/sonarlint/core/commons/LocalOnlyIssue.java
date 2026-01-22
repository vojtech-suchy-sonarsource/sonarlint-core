/*
ACR-e3679cf9a0594e9b8abba413511569a0
ACR-b5210705d60e4bfe9c0fc8f122b7bfc3
ACR-89e03f3cff7447069c9fe55cdf2a0ce2
ACR-62f2ae797d0744759749602b1f4441ff
ACR-c959e38bafe9492fae1a2796d52e4091
ACR-976bb1da72f04e538e5278ac61c193cd
ACR-664903ea2fea4efaa7de8b0997913c5a
ACR-a27ec5fecbfa46b18fe2eb6c671f0b03
ACR-18599241f6cc4806a7e68e83c912961a
ACR-c74d3751c9674f97b227cfac27baef28
ACR-1dd3a7b2f5b844ef95d0491ecc97a3e5
ACR-222658cebe1b44bd8ac12e8992182023
ACR-f86b02cf980941a38d961bda3d60e2b5
ACR-b5434447a0974bfda513fd7afd2180cc
ACR-86a67ca9553242d5bf7963c2bef9dfad
ACR-cc4ef83562cf4192b9187c0dafa107a2
ACR-33d53d7511384aee8092de4c2529b057
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

  /*ACR-5a16fe64f25543a8a0e8f09c9672b8d1
ACR-b35051b6826d410f81254e8e403f7746
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
