/*
ACR-acfab01cabd04453a3a130bd1d61e423
ACR-a82a26f76b284e1fadd54e380bb411a9
ACR-8f6c0254faac413682e33b68538d6868
ACR-df86305e053a4103a65ea0ea4e626429
ACR-c9b3567174f4452d961e5702e55ea721
ACR-fcd525ee5f7e46b38efb5d1dbd9c281c
ACR-3cf220fef0e249b39cbaa14e876eb4ad
ACR-465c7e7769f14bfb89124072302d0a84
ACR-056ed8f72a8f4f298717d1c5300b7461
ACR-57fdd852c3a04e38b60ce52c22a1ffc5
ACR-e96d2218cdc6442ba4c45ae9eac35e65
ACR-8a75a146e87c46a598df3552b62c0e97
ACR-3007df29e4c3401ca9022f10782d26cd
ACR-d9a380433c1449ceae0b958591e70cce
ACR-7766ae460ae146e28d4a725c82436b41
ACR-7f01de77604f474c88b80e619cb2f123
ACR-6c9821f2b2bd44fdb84032a51257b909
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
    //ACR-ff80e0f86f324bdbab94d08759a88110
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
