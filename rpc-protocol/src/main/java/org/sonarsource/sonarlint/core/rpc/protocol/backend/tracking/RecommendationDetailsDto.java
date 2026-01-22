/*
ACR-7d367e48daac4de48c91e29df448042c
ACR-a7beddd4afa7487aa955a1d602616226
ACR-2e0308648786437499ced3852f45610f
ACR-c9160d64a83748048528aaa6f5ec4234
ACR-decd9970c6cc4ee2a9e5c7c84698d8e4
ACR-1ffa2ebd49ff47159e3ca7eb3caafdf4
ACR-2856bbef143241689e830229bd5b40eb
ACR-13fab9ce9f7c4aa4b721bde7de2bf506
ACR-d18c8a2367a345a2be843761e98f42b3
ACR-8a0b585b99e14adb81f4b1741286bc09
ACR-95ea76678b794a1ab3a44917990d24ee
ACR-7df8d0e258554fd9a6b20e0562560531
ACR-f64d442879ed406da9adc71bfc818c05
ACR-207f33d8cfde48fc91525c437fe80aee
ACR-d9bc55a53db74062bb83a4ea4676f1fc
ACR-fa9019bc7a7e4101a8a17082cb5aebfc
ACR-d0dfd682c4814c669800b4693d38256e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class RecommendationDetailsDto {
  private final int impactScore;
  private final String impactDescription;
  private final boolean realIssue;
  private final String falsePositiveReason;
  private final boolean includesDev;
  private final boolean specificMethodsAffected;
  private final String specificMethodsDescription;
  private final boolean otherConditions;
  private final String otherConditionsDescription;
  private final boolean workaroundAvailable;
  private final String workaroundDescription;
  private final String visibility;

  private RecommendationDetailsDto(Builder builder) {
    this.impactScore = builder.impactScore;
    this.impactDescription = builder.impactDescription;
    this.realIssue = builder.realIssue;
    this.falsePositiveReason = builder.falsePositiveReason;
    this.includesDev = builder.includesDev;
    this.specificMethodsAffected = builder.specificMethodsAffected;
    this.specificMethodsDescription = builder.specificMethodsDescription;
    this.otherConditions = builder.otherConditions;
    this.otherConditionsDescription = builder.otherConditionsDescription;
    this.workaroundAvailable = builder.workaroundAvailable;
    this.workaroundDescription = builder.workaroundDescription;
    this.visibility = builder.visibility;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getImpactScore() {
    return impactScore;
  }

  public String getImpactDescription() {
    return impactDescription;
  }

  public boolean isRealIssue() {
    return realIssue;
  }

  public String getFalsePositiveReason() {
    return falsePositiveReason;
  }

  public boolean isIncludesDev() {
    return includesDev;
  }

  public boolean isSpecificMethodsAffected() {
    return specificMethodsAffected;
  }

  public String getSpecificMethodsDescription() {
    return specificMethodsDescription;
  }

  public boolean isOtherConditions() {
    return otherConditions;
  }

  public String getOtherConditionsDescription() {
    return otherConditionsDescription;
  }

  public boolean isWorkaroundAvailable() {
    return workaroundAvailable;
  }

  public String getWorkaroundDescription() {
    return workaroundDescription;
  }

  public String getVisibility() {
    return visibility;
  }

  public static class Builder {
    private int impactScore;
    private String impactDescription;
    private boolean realIssue;
    private String falsePositiveReason;
    private boolean includesDev;
    private boolean specificMethodsAffected;
    private String specificMethodsDescription;
    private boolean otherConditions;
    private String otherConditionsDescription;
    private boolean workaroundAvailable;
    private String workaroundDescription;
    private String visibility;

    public Builder impactScore(int impactScore) {
      this.impactScore = impactScore;
      return this;
    }

    public Builder impactDescription(String impactDescription) {
      this.impactDescription = impactDescription;
      return this;
    }

    public Builder realIssue(boolean realIssue) {
      this.realIssue = realIssue;
      return this;
    }

    public Builder falsePositiveReason(String falsePositiveReason) {
      this.falsePositiveReason = falsePositiveReason;
      return this;
    }

    public Builder includesDev(boolean includesDev) {
      this.includesDev = includesDev;
      return this;
    }

    public Builder specificMethodsAffected(boolean specificMethodsAffected) {
      this.specificMethodsAffected = specificMethodsAffected;
      return this;
    }

    public Builder specificMethodsDescription(String specificMethodsDescription) {
      this.specificMethodsDescription = specificMethodsDescription;
      return this;
    }

    public Builder otherConditions(boolean otherConditions) {
      this.otherConditions = otherConditions;
      return this;
    }

    public Builder otherConditionsDescription(String otherConditionsDescription) {
      this.otherConditionsDescription = otherConditionsDescription;
      return this;
    }

    public Builder workaroundAvailable(boolean workaroundAvailable) {
      this.workaroundAvailable = workaroundAvailable;
      return this;
    }

    public Builder workaroundDescription(String workaroundDescription) {
      this.workaroundDescription = workaroundDescription;
      return this;
    }

    public Builder visibility(String visibility) {
      this.visibility = visibility;
      return this;
    }

    public RecommendationDetailsDto build() {
      return new RecommendationDetailsDto(this);
    }
  }
}
