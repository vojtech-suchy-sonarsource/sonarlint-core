/*
ACR-2b0d77f314ce441cab7aae285d8647cb
ACR-9c9268f79b1d4cbfbfba3cb86065fe8c
ACR-c346e8e6ede845aab1bf7134d8228c4c
ACR-e6f934985bf14c148dc5e586b71af780
ACR-6a0031a93a014681bbf2c6c1b8f0ab6e
ACR-ef56c6f613d6406089e2d488cd32d2a2
ACR-fa4f16fccbce46319c29417a15f985a8
ACR-ee0bb72c2bac4f4b984f6321f69d17e7
ACR-8d2b035047af4fb19e5a374b16e51f6d
ACR-a6b3bbe270c54f4aa69969a6284451ae
ACR-807189f349674f269ac00b9dde28ca7f
ACR-77032e3ce250499ca7807dae1fc9bd89
ACR-9a2842f6068a40faabd40efb08b32829
ACR-5f67ea4a6c6f4e629c5c3ea57d75a151
ACR-56b417ee72824896977c5fb71e83d57c
ACR-040b45908c884456b7615e817d170035
ACR-43e3e3f84a174f36b0fed300709599b8
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
