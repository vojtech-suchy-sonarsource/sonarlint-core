/*
ACR-816f1621e1c84befae47c2a7d0bd84cc
ACR-e55b4b3bcbb2465082f866368a0219ad
ACR-e01b95f6c97e4658b8a18ebdf071a449
ACR-72c073dd4fa14f899bffc5298c0de461
ACR-f96a577bbd2d4f788d075832e1ac439a
ACR-614ac8b0c7aa4ac3b67e1aa5ab2c29a8
ACR-f1224add83974800a4bffa04152680e4
ACR-888380235bb04abb92c9211ec047f719
ACR-51fcdf13f3604380a2577e72715c5160
ACR-c4b0b5af31c5429a974dd8abb8db647a
ACR-3a68b7d6d129403591cbf60ef01d5108
ACR-2c13d9014d0a48108d87da0e72891959
ACR-a559b042eb0b4c44bc5f66cebe2ec9c6
ACR-813684f6fe7f47759fca2029196eeb6a
ACR-3890698ded8d4878a29faccb8b9fce99
ACR-13debd9f9cc94d4b88ab4f2ee1a17c70
ACR-2344b13e443a4f31b34152a6fa225495
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
