/*
ACR-3ba3ce42a97f4c24b10a2b6adbe4f6dd
ACR-d4a266ed3d7747d5ad50e2682e516247
ACR-76baed422abf4a038d1b7fd20449096b
ACR-7d72cfba90c446a09bf58a03bb325505
ACR-932eea3479d942aca2a061cc38298fda
ACR-63c6acd3ac824513aa68d52f006426bf
ACR-86edfafeb26d487ba3187fc5ad7cfa64
ACR-0c07433ceabb40869baeecc9588141f2
ACR-c93f265d37ed4fc695ad129d53a86192
ACR-28156c6402514ee39faceb35666dee31
ACR-f6a650376f1f4977ae73f3e19030eba3
ACR-6387e478ff6c48c89d1e12022ac1a8c1
ACR-8190eb532f3e4271b5f2d8d549fc9071
ACR-e6150f837e574dcd85b93174a77de150
ACR-aa9f1d3401944965b0d199aa051e967c
ACR-6b913ea92d9c4168b6b0329c6a5f72ab
ACR-bb2d18b5044d42ffa2e1cb57c13fe9a5
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
