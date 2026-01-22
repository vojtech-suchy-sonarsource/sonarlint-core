/*
ACR-e0f1a1f836354142bf366f08d4a7e60b
ACR-c9c8e6ed62a5491ab4a0fd6422222478
ACR-64027bdfc1ad47abb82b499937dbec21
ACR-5048798d72fe49e6bc07dadbef5a10bd
ACR-a70c3ca380a84737957804af42c1d0d3
ACR-296cb864e65b4e8990c7ecd003809c40
ACR-24cfad7d79214541bc0321e1b4944824
ACR-1e1b69047a2a4025b8b107cc84b9a2ed
ACR-06f17f85f4eb4b64b2c5e59b7cd249f6
ACR-1357ceb227e34842ac9a5caf66adb177
ACR-b37c7e0d36694f39a875c1067add1f67
ACR-7791852a9b044cd384372a4f01d6dbe6
ACR-c0a9d99a282c4d8a8b46168182018255
ACR-41938f2380324533b04e81083cba4f46
ACR-6c8b61c201dd4c839d2056d37ecab268
ACR-c29da033a1f3499ca7b97b3943d4df36
ACR-29709e23cfdc478d8a9751686dd3ac08
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import static org.sonarsource.sonarlint.core.commons.ConnectionKind.SONARCLOUD;
import static org.sonarsource.sonarlint.core.commons.ConnectionKind.SONARQUBE;

public enum HotspotReviewStatus {
  TO_REVIEW(Set.of(SONARQUBE, SONARCLOUD)),
  SAFE(Set.of(SONARQUBE, SONARCLOUD)),
  FIXED(Set.of(SONARQUBE, SONARCLOUD)),
  ACKNOWLEDGED(Set.of(SONARQUBE));
  private final Set<ConnectionKind> allowedConnectionKinds;

  HotspotReviewStatus(Set<ConnectionKind> allowedConnectionKinds) {
    this.allowedConnectionKinds = allowedConnectionKinds;
  }

  public boolean isReviewed() {
    return !equals(TO_REVIEW);
  }

  public boolean isResolved() {
    //ACR-ad16cf30360344618914302bf60d1d94
    return equals(SAFE) || equals(FIXED);
  }

  public static HotspotReviewStatus fromStatusAndResolution(String status, @Nullable String resolution) {
    if ("REVIEWED".equals(status)) {
      if (resolution == null) {
        return HotspotReviewStatus.SAFE;
      }
      return switch (resolution) {
        case "SAFE" -> HotspotReviewStatus.SAFE;
        case "FIXED" -> HotspotReviewStatus.FIXED;
        case "ACKNOWLEDGED" -> HotspotReviewStatus.ACKNOWLEDGED;
        default -> HotspotReviewStatus.TO_REVIEW;
      };
    }
    return HotspotReviewStatus.TO_REVIEW;
  }

  private boolean isAllowedOn(ConnectionKind kind) {
    return allowedConnectionKinds.contains(kind);
  }

  public static List<HotspotReviewStatus> allowedStatusesOn(ConnectionKind kind) {
    return Arrays.stream(HotspotReviewStatus.values()).filter(status -> status.isAllowedOn(kind))
      .toList();
  }
}
