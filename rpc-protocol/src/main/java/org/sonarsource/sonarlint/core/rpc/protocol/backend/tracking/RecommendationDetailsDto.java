/*
ACR-e5465ede1087447696b13c648a903bda
ACR-5b2f6a371afb4f3c80b8348b27d3efff
ACR-ca31d67b550e49b68c8bfc93a3c4f458
ACR-df19853e5c66410585eb3aaa81f648d3
ACR-7e41ba43dec44737bfd24110a8e4dc1d
ACR-b0a358f91de84ad59344912e854e510c
ACR-0a687791271b443db09e1525e90d2661
ACR-79374a6cfa5449cb814e119a891a08a9
ACR-4797d70c510149f3961ef52bf885339a
ACR-b62573684102401c895d2743b86aa85b
ACR-f1192311d35547889dc0d2302a1a6c10
ACR-99e9c9f578c541f09be0c996f5d62704
ACR-8d4d9bacbc394636ba84df242f58420d
ACR-8f2aa53f80694dcc98dc570dddeb4021
ACR-d3b960ff6b754411b70ad1c7501f470c
ACR-583f3c18f5ae46bc86ee321a47343b9d
ACR-3dbb7cbd80df478a93d380358e1ea7f0
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
