/*
ACR-9aee4e4e55cb459388e704d8d88536fd
ACR-83ba2826c875436f8a51a93586077681
ACR-3a349b06c9964de8984a970128b0ceee
ACR-17dc88aa4d2e4266960522b0789a8b53
ACR-b412fc5bf33446d79caf55c01acbe082
ACR-25466cb21e31415d9ac85cc0d300749e
ACR-810db48f69db4749a85e4d1bc1a40a7f
ACR-0896e8d6bdaa4d50bc2a369a914c26d7
ACR-314fc460abdb441f8af1be48eba1f5be
ACR-437ba2d6c0f3452095c96ccb4474e612
ACR-830f86736b4a4b79a018692abbf97bf8
ACR-44588129bb3f41819f72c70caee1c225
ACR-7db52a73c10d44aaaf8f09174027fdc6
ACR-a5d715c98793407db9ea2c3063908c08
ACR-fdfb1b73f00d4b8c860e6c3aef1f8e0c
ACR-b60514720ece485aa874600feced20e2
ACR-c6beba53ac5f44399c466d6df63abdd6
 */
package org.sonarsource.sonarlint.core.telemetry.gessie.event;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public record GessieMetadata(
  UUID eventId,
  GessieSource source,
  String eventType,
  String eventTimestamp,
  String eventVersion
) {

  public record GessieSource(SonarLintDomain domain) {
  }

  public enum SonarLintDomain {
    @SerializedName("VSCode")
    VS_CODE,
    @SerializedName("VisualStudio")
    VISUAL_STUDIO,
    @SerializedName("Eclipse")
    ECLIPSE,
    @SerializedName("IntelliJ")
    INTELLIJ,
    @SerializedName("SLCore")
    SLCORE;

    public static SonarLintDomain fromProductKey(String productKey) {
      return switch (productKey) {
        case "idea" -> INTELLIJ;
        case "eclipse" -> ECLIPSE;
        case "visualstudio" -> VISUAL_STUDIO;
        case "vscode", "cursor", "windsurf" -> VS_CODE;
        default -> SLCORE;
      };
    }
  }
}
