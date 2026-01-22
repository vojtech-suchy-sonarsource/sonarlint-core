/*
ACR-860bbd636d084756b404b8603741b705
ACR-a0002af53d1844208b27f3cb92bc9ea7
ACR-eda933282b4e4147aa8fcd2a30d203ce
ACR-85f8976d917e4fa6a2779d532b0cb313
ACR-c91da4d48b62458cae404fe20c92ef0d
ACR-4b944ad2cbeb40c1a7ea383656b2720d
ACR-f1ee6ee13ea5485b9ac05030796e01aa
ACR-507519ebffda40bbbd61446f38bc06ca
ACR-c28ec3cb85c6480ebfb8d0f0fb463e3e
ACR-c1f118f4b13741bbb58105dffc63f748
ACR-6749b86758214705b6f436cc144407e2
ACR-f54dc50c3bf2493987977088377f59e4
ACR-56adc9e942434854953438fd3d59ff5b
ACR-f443e0638dbf4c60a470c89c5f28797f
ACR-823443a2b1a84d9eb04172659879e17a
ACR-3fbc7fe87dcb4392ba8bd8c267324309
ACR-3859cd4b1e4240cd8bd33d39fce644f4
 */
package org.sonarsource.sonarlint.core.serverapi.hotspot;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

public class ServerHotspotDetails {

  @Deprecated(forRemoval = true)
  public final String message;
  public final Path filePath;
  @Deprecated(forRemoval = true)
  public final TextRange textRange;
  @Deprecated(forRemoval = true)
  public final String author;
  @Deprecated(forRemoval = true)
  public final Status status;
  @Deprecated(forRemoval = true)
  @CheckForNull
  public final Resolution resolution;
  @Deprecated(forRemoval = true)
  public final Rule rule;
  @Deprecated(forRemoval = true)
  @CheckForNull
  public final String codeSnippet;
  public final boolean canChangeStatus;

  public ServerHotspotDetails(String message,
    Path filePath,
    TextRange textRange,
    String author,
    Status status,
    @Nullable Resolution resolution,
    Rule rule,
    @Nullable String codeSnippet,
    boolean canChangeStatus) {
    this.message = message;
    this.filePath = filePath;
    this.textRange = textRange;
    this.author = author;
    this.status = status;
    this.resolution = resolution;
    this.rule = rule;
    this.codeSnippet = codeSnippet;
    this.canChangeStatus = canChangeStatus;
  }

  @Deprecated(forRemoval = true)
  public static class Rule {

    public final String key;
    public final String name;
    public final String securityCategory;
    public final VulnerabilityProbability vulnerabilityProbability;
    public final String riskDescription;
    public final String vulnerabilityDescription;
    public final String fixRecommendations;

    public Rule(String key,
      String name,
      String securityCategory,
      VulnerabilityProbability vulnerabilityProbability,
      String riskDescription,
      String vulnerabilityDescription,
      String fixRecommendations) {

      this.key = key;
      this.name = name;
      this.securityCategory = securityCategory;
      this.vulnerabilityProbability = vulnerabilityProbability;
      this.riskDescription = riskDescription;
      this.vulnerabilityDescription = vulnerabilityDescription;
      this.fixRecommendations = fixRecommendations;
    }

  }

  @Deprecated(forRemoval = true)
  public enum Status {
    TO_REVIEW("To review"), REVIEWED("Reviewed");

    Status(String description) {
      this.description = description;
    }

    public final String description;
  }

  @Deprecated(forRemoval = true)
  public enum Resolution {
    FIXED("fixed"), SAFE("safe"), ACKNOWLEDGED("acknowledged");

    Resolution(String description) {
      this.description = description;
    }

    public final String description;
  }
}
