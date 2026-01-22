/*
ACR-c056a859bdc645d6b7c6d303a730f0cf
ACR-c08219c3bde540c3859b9b5ea57e229c
ACR-1b340e512ebb4b1388e8b91f427a692a
ACR-2442ef2fc0f44d4485728fb9a37aa1dc
ACR-d58f3a07b4614f57b65f7f21eb528be4
ACR-490054f265a348498d9310e1f43398db
ACR-951509586e75499f8d8a9863f29ecede
ACR-4b275527d93d486593d87bd7f6b376c5
ACR-27ae5eca5bfa48308df25613e978cac4
ACR-4ce13c8fada548e2811a0f93424cc0d4
ACR-6cbc243c3c5143a98b267a874d24b8fd
ACR-a954a9b2a39c4e2bbb8480a8021295d7
ACR-67065d95dae445c69b7d1e3f946aface
ACR-54ee4f1bfd9d483aab977afceac134b7
ACR-24892b04729c450d89790a18ea653616
ACR-3ca776af1d1f4e72891317204cc2dd89
ACR-5b293bf744ad41beb754532b2f195465
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
