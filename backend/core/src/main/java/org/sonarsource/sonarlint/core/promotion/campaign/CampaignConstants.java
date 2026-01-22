/*
ACR-cb91f25cb97648b2849dafd60d22f635
ACR-73d989dc5d9541efa441f9eb8b9b7a7e
ACR-5c61143784d34c7a98aa5807f36e7809
ACR-9c79f41b04a4475a895c0d1784de5b97
ACR-9210325e39b54d2484b73c7d9c8fb85a
ACR-f6258cc7fc7141ce8648530b2dff1f64
ACR-8cf2a48c457c46ca9b96ee814839f7b8
ACR-bf8a9e00a8e04d2796f4656b79adda86
ACR-bb8dfd5f735243c28502def765523f9d
ACR-755ec29c308e4c479abdb3cf38995381
ACR-cce0b490a5b1439e9e5bc18d0735f536
ACR-2c8b3750f2694c058f979494f3b6cdb4
ACR-e3c4e64a87b7436686a54511f005564a
ACR-439ca1de4bc448959bb883630ef23018
ACR-8ce84b776cb447ae9631162b67443a3b
ACR-5e6a384b787f400cb65fd6388eee21e8
ACR-288c98980a6e43848c9b1fb4b0709b85
 */
package org.sonarsource.sonarlint.core.promotion.campaign;

public class CampaignConstants {

  public static final String FEEDBACK_2026_01_CAMPAIGN = "feedback_2026_01";
  private static final String JETBRAINS_MARKETPLACE = "https://plugins.jetbrains.com/plugin/7973-sonarqube-for-ide/reviews";
  private static final String VS_MARKETPLACE = "https://marketplace.visualstudio.com/items?itemName=SonarSource.SonarLintforVisualStudio2022&ssr=false#review-details";
  private static final String VSCODE_MARKETPLACE = "https://marketplace.visualstudio.com/items?itemName=SonarSource.sonarlint-vscode&ssr=false#review-details";
  private static final String OPEN_VSX = "https://open-vsx.org/extension/SonarSource/sonarlint-vscode/reviews";
  private static final String INTELLIJ_GOOGLE_FORM = "https://forms.gle/kDyQ7sDyBfpPEBsy6";
  private static final String VISUAL_STUDIO_GOOGLE_FORM = "https://forms.gle/LjKGKWECDdJw1PmU7";
  private static final String VS_CODE_GOOGLE_FORM = "https://forms.gle/TncKAVK4EWM7z4RV6";

  private CampaignConstants() {
  }

  static String urlToOpen(FeedbackNotificationActionItem response, String productKey) {
    return switch (response) {
      case LOVE_IT -> switch (productKey) {
        case "idea" -> JETBRAINS_MARKETPLACE;
        case "visualstudio" -> VS_MARKETPLACE;
        case "vscode" -> VSCODE_MARKETPLACE;
        case "windsurf", "cursor", "kiro" -> OPEN_VSX;
        default -> null;
      };
      case SHARE_FEEDBACK -> switch (productKey) {
        case "idea" -> INTELLIJ_GOOGLE_FORM;
        case "visualstudio" -> VISUAL_STUDIO_GOOGLE_FORM;
        case "vscode", "windsurf", "cursor", "kiro" -> VS_CODE_GOOGLE_FORM;
        default -> null;
      };
      default -> null;
    };
  }
}
