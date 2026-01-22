/*
ACR-efb3b3513ed64ea2950781db1b792928
ACR-a38a68ec9362427ba80decf680bbe33a
ACR-d4a11dae204b435b865c91340797d268
ACR-1acc51e15318494c86e23037cca53a58
ACR-a996eb17d8ca49a2bbd256cf5082b03a
ACR-405252d0ec76492a9ab3f76ca2d0efa2
ACR-23e78c3042c5485ab1c92c7c712439af
ACR-52a175e28b2947b5a6b564c07c2389df
ACR-e6dbaa59276846fbbc72f0a0dd36353b
ACR-5614b0c6d6164833b3a4ff30ceaa3258
ACR-be5b6bd86cb447ec975822902e083b07
ACR-fd272cc76303493c8898d2fee80370a4
ACR-b0e8d36b35d7492ebf9882ceeb7472fe
ACR-cf0094603de84be990b21333bd8ecbc5
ACR-3d8418170e394173bece0b5f136b2bd1
ACR-613708ddce38410bb5a511b95f211bbc
ACR-1d6d859fd3024d6ca1361b9c6dd1ba83
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot;

import java.nio.file.Path;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class HotspotDetailsDto {
  private final String key;
  @Deprecated(forRemoval = true)
  private final String message;
  private final Path ideFilePath;
  @Deprecated(forRemoval = true)
  private final TextRangeDto textRange;
  @Deprecated(forRemoval = true)
  private final String author;
  @Deprecated(forRemoval = true)
  private final String status;
  @Deprecated(forRemoval = true)
  @Nullable
  private final String resolution;
  @Deprecated(forRemoval = true)
  private final HotspotRule rule;
  @Deprecated(forRemoval = true)
  @Nullable
  private final String codeSnippet;

  public HotspotDetailsDto(String key, String message, Path ideFilePath, TextRangeDto textRange, String author, String status, @Nullable String resolution, HotspotRule rule,
    @Nullable String codeSnippet) {
    this.key = key;
    this.message = message;
    this.ideFilePath = ideFilePath;
    this.textRange = textRange;
    this.author = author;
    this.status = status;
    this.resolution = resolution;
    this.rule = rule;
    this.codeSnippet = codeSnippet;
  }

  public String getKey() {
    return key;
  }

  public String getMessage() {
    return message;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }

  public TextRangeDto getTextRange() {
    return textRange;
  }

  public String getAuthor() {
    return author;
  }

  public String getStatus() {
    return status;
  }

  @Nullable
  public String getResolution() {
    return resolution;
  }

  public HotspotRule getRule() {
    return rule;
  }

  @Nullable
  public String getCodeSnippet() {
    return codeSnippet;
  }

  public static class HotspotRule {
    private final String key;
    private final String name;
    private final String securityCategory;
    private final String vulnerabilityProbability;
    private final String riskDescription;
    private final String vulnerabilityDescription;
    private final String fixRecommendations;

    public HotspotRule(String key, String name, String securityCategory, String vulnerabilityProbability, String riskDescription, String vulnerabilityDescription,
      String fixRecommendations) {
      this.key = key;
      this.name = name;
      this.securityCategory = securityCategory;
      this.vulnerabilityProbability = vulnerabilityProbability;
      this.riskDescription = riskDescription;
      this.vulnerabilityDescription = vulnerabilityDescription;
      this.fixRecommendations = fixRecommendations;
    }

    public String getKey() {
      return key;
    }

    public String getName() {
      return name;
    }

    public String getSecurityCategory() {
      return securityCategory;
    }

    public String getVulnerabilityProbability() {
      return vulnerabilityProbability;
    }

    public String getRiskDescription() {
      return riskDescription;
    }

    public String getVulnerabilityDescription() {
      return vulnerabilityDescription;
    }

    public String getFixRecommendations() {
      return fixRecommendations;
    }
  }
}
