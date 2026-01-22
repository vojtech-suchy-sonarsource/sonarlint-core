/*
ACR-30b1aaa8556349fbba3c63fc1bd26a9b
ACR-b357801d1d464cae92e129666fc5b4ef
ACR-c34beee61d3148c0818e59fa9477cff4
ACR-23d27d2d6e434f338ba096f8e5617598
ACR-fa73b13c70374c078fb78940185ddb13
ACR-db97f453a0c54b239c3d9b404b0e67b9
ACR-8d02969c2cc544acb8d1702c3ef88db3
ACR-49806fd543564653a3d3c6c397f18a7b
ACR-c7660aecd75b4c158de42bc2cc0146e0
ACR-436d2e9c86aa46edb29c3d2aaf9c36eb
ACR-1926db0589ef4de2bad46da032f34ae7
ACR-ae43f7af56484cf0807c513e7e853ce1
ACR-98623fff15f049dd91c5510f19cbdff1
ACR-5adfc3bf73ff4591bb4eb8e3a7ad0b80
ACR-0db8b13206c44c96928744517c7d3df1
ACR-4847034e32a6413085ebd2b619e10fdf
ACR-df518b93c9f8454fa5dac57d53b3266d
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
