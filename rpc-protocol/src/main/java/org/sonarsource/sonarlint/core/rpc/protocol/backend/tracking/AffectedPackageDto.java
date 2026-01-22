/*
ACR-6a44ea31864548fb8abf06b93ee7cf66
ACR-e1e0783d17744382a888ffaa87174c60
ACR-c6b3de3717864e7381e7dbaa9d77b1b9
ACR-40921d13d153413092cb030585958935
ACR-67e79fde996d4fa8a223113da33f9ef7
ACR-3825802b02c7457daa6f0fd216fb3002
ACR-ae98fbe48ae141a492a64c3a7b6571fd
ACR-c40e6e1450a0444dbee931afe3505167
ACR-191a61dd5ee24feeb41d4cd95c80f30c
ACR-a655e14b0eb94ffd9fc0e29e364db69a
ACR-ff7e6d27064947599d8f18db845257d3
ACR-7bc4f3170b494c1680090e17a0691b14
ACR-634e392b15c34ab58938ab94cc90eb0c
ACR-4ced89f60fca4e6abb37f1c89a37c836
ACR-ca9d9add0df64dd9b446ab0c310f3685
ACR-5ce5cffad85e478eb318db4ab023dd72
ACR-37eb195ec4f4407dad852951b1d83526
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class AffectedPackageDto {
  private final String purl;
  private final String recommendation;
  private final RecommendationDetailsDto recommendationDetails;

  public AffectedPackageDto(String purl, String recommendation, @Nullable RecommendationDetailsDto recommendationDetails) {
    this.purl = purl;
    this.recommendation = recommendation;
    this.recommendationDetails = recommendationDetails;
  }

  public String getPurl() {
    return purl;
  }

  public String getRecommendation() {
    return recommendation;
  }

  @CheckForNull
  public RecommendationDetailsDto getRecommendationDetails() {
    return recommendationDetails;
  }
}
