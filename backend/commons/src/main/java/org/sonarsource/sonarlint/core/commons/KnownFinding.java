/*
ACR-e11f9edddb52413da9428a83ef59da0b
ACR-9d82a51364c64e4c8a48105ec3801380
ACR-28c7fad13bb3477c86d46c392e9880db
ACR-85d9eab745fb4d35bc9f5b43301d1bf9
ACR-45222dac93d0465698999aedcd592a06
ACR-87eca64d77f94726b715813903fe8eb9
ACR-c84ce1b78a2c4f0087a7930e0e5fc9bb
ACR-e46afca0e5b7429f8713b66a753fb8c8
ACR-323cf2642d094fec8b9828e38cb0150b
ACR-3ad6d63f9bf9435980a1d90692bd899f
ACR-d6483707806e4143add7580d9a6b778f
ACR-8b88efd65e3440d5a8c3a0fffef2099c
ACR-cacabdcbe2f34987baf76496a4df3141
ACR-259151aa2a6d41bf8ca7af60a0a75952
ACR-b2ec04a373e441b7aa34eb5fb4ff86d3
ACR-06c6b6c4f3a14997b15d40d8294d2442
ACR-65efe9a48f144c4fa70ab0b9e0f1c92c
 */
package org.sonarsource.sonarlint.core.commons;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

public class KnownFinding {
  private final UUID id;
  private final String serverKey;
  private final TextRangeWithHash textRangeWithHash;
  private final LineWithHash lineWithHash;
  private final String ruleKey;
  private final String message;
  private final Instant introductionDate;

  public KnownFinding(UUID id, @Nullable String serverKey, @Nullable TextRangeWithHash textRangeWithHash, @Nullable LineWithHash lineWithHash, String ruleKey, String message,
    Instant introductionDate) {
    this.id = id;
    this.serverKey = serverKey;
    this.textRangeWithHash = textRangeWithHash;
    this.lineWithHash = lineWithHash;
    this.ruleKey = ruleKey;
    this.message = message;
    this.introductionDate = introductionDate;
  }

  public UUID getId() {
    return id;
  }

  @CheckForNull
  public String getServerKey() {
    return serverKey;
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

  public Instant getIntroductionDate() {
    return introductionDate;
  }
}
