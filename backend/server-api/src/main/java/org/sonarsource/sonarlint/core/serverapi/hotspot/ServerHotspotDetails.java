/*
ACR-efc664cea58b40029fe76b5a78538bd7
ACR-594b572049a4439dadc9f964c1121020
ACR-91c3e6c26c0c4d10930fd1a1ba5b0c1c
ACR-b257e004c6584505857fd588f723f1f6
ACR-8fb28b3abb8d4cee8ef6e90c9ab4ce9f
ACR-cb44fa0c77e14e249d8f6b6776446c6f
ACR-fdb9603e2e70438fb22c71eca620e2e4
ACR-3a5692ed1ded45719a94829d153213f8
ACR-7812d53cf9c54d858395122e921dbae7
ACR-8e68ffe8a7a74ba9ac0a2e6dd2247a46
ACR-167aef5444bd493989598c9c919430e1
ACR-3e550cc99ed24ccabfd27a7a0c7c87c2
ACR-e022be4fe66249c09366e06bc0416249
ACR-66dad95f8f0948eeb7a4c33f75821f8e
ACR-0735041b066b4437ab9fcf8c65792605
ACR-152a13ab6719439dbc5240f9504ea8f8
ACR-c0c061b6075a4a29ad74c39911baf487
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
