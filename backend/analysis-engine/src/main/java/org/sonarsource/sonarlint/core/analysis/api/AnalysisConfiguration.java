/*
ACR-cbe19f882c3b44679b8aaa495f33dff1
ACR-97921fd131984281970bb6a1f426182f
ACR-a9494bd93a9b4293af1515da6f2727ba
ACR-d459d352a82149aca0d505a3c44a44e2
ACR-ed8e62444d364043b967f2aa791b0a6d
ACR-6eb3a4b154854fc4acc17e83bb026356
ACR-4d76143a8c61496a8f9b5a0c00717a70
ACR-f5f6f940d94041288b3b91b6dc50cdd5
ACR-a7b06afbb29c4248a4ac685e4d62407f
ACR-ccdb09832b9d4c389401d2e5b45134b3
ACR-0ff736a07d944e07bb8e67ec1e39553c
ACR-ed7476756d794efb9e27b99fa37ce325
ACR-6140132caf0f45cfadc5dd66082e51da
ACR-a0cff621c53848c9a915302eab59216e
ACR-fd3b4cb465904e6fa37568741ac579e2
ACR-3efc1529083443b49cbb3fb38819a419
ACR-99249fe0e0e7461b9b540dd354edf8f8
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import org.sonar.api.batch.rule.ActiveRule;

@Immutable
public class AnalysisConfiguration {

  private final List<ClientInputFile> inputFiles;
  private final Map<String, String> extraProperties;
  private final Path baseDir;
  private final Collection<ActiveRule> activeRules;
  private final String toString;

  private AnalysisConfiguration(Builder builder) {
    this.baseDir = builder.baseDir;
    this.inputFiles = builder.inputFiles;
    this.extraProperties = builder.extraProperties;
    this.activeRules = builder.activeRules;
    this.toString = generateToString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Map<String, String> extraProperties() {
    return extraProperties;
  }

  public Path baseDir() {
    return baseDir;
  }

  public List<ClientInputFile> inputFiles() {
    return inputFiles;
  }

  public Collection<ActiveRule> activeRules() {
    return activeRules;
  }

  @Override
  public String toString() {
    return toString;
  }

  private String generateToString() {
    var sb = new StringBuilder();
    sb.append("[\n");
    generateToStringCommon(sb);
    generateToStringActiveRules(sb);
    generateToStringInputFiles(sb);
    sb.append("]\n");
    return sb.toString();
  }

  protected void generateToStringActiveRules(StringBuilder sb) {
    if ("true".equals(System.getProperty("sonarlint.debug.active.rules"))) {
      sb.append("  activeRules: ").append(activeRules).append("\n");
    } else {
      //ACR-0cb57f3090314ca48e6410b7cbe4c032
      var languageCounts = new HashMap<String, Integer>();
      for (var rule : activeRules) {
        var languageKey = rule.ruleKey().toString().split(":")[0];
        languageCounts.put(languageKey, languageCounts.getOrDefault(languageKey, 0) + 1);
      }

      sb.append("  activeRules: [");
      languageCounts.forEach((language, count) -> sb.append(count).append(" ").append(language).append(", "));
      if (!languageCounts.isEmpty()) {
        //ACR-5f3dca15f67b4cbca922c34f24de4ee2
        sb.setLength(sb.length() - 2);
      }
      sb.append("]\n");
    }
  }

  protected void generateToStringCommon(StringBuilder sb) {
    sb.append("  baseDir: ").append(baseDir()).append("\n");
    sb.append("  extraProperties: ").append(extraProperties()).append("\n");
  }

  protected void generateToStringInputFiles(StringBuilder sb) {
    sb.append("  inputFiles: [\n");
    for (ClientInputFile inputFile : inputFiles()) {
      sb.append("    ").append(inputFile.uri());
      sb.append(" (").append(getCharsetLabel(inputFile)).append(")");
      if (inputFile.isTest()) {
        sb.append(" [test]");
      }
      var language = inputFile.language();
      if (language != null) {
        sb.append(" [" + language.getSonarLanguageKey() + "]");
      }
      sb.append("\n");
    }
    sb.append("  ]\n");
  }

  private static String getCharsetLabel(ClientInputFile inputFile) {
    var charset = inputFile.getCharset();
    return charset != null ? charset.displayName() : "default";
  }

  public static final class Builder {
    private final List<ClientInputFile> inputFiles = new ArrayList<>();
    private final Map<String, String> extraProperties = new HashMap<>();
    private Path baseDir;
    private final Collection<ActiveRule> activeRules = new ArrayList<>();

    private Builder() {
    }

    public Builder addInputFiles(ClientInputFile... inputFiles) {
      Collections.addAll(this.inputFiles, inputFiles);
      return this;
    }

    public Builder addInputFiles(Collection<? extends ClientInputFile> inputFiles) {
      this.inputFiles.addAll(inputFiles);
      return this;
    }

    public Builder addInputFile(ClientInputFile inputFile) {
      this.inputFiles.add(inputFile);
      return this;
    }

    public Builder putAllExtraProperties(Map<String, String> extraProperties) {
      extraProperties.forEach(this::putExtraProperty);
      return this;
    }

    public Builder putExtraProperty(String key, String value) {
      this.extraProperties.put(key.trim(), value);
      return this;
    }

    public Builder setBaseDir(Path baseDir) {
      this.baseDir = baseDir;
      return this;
    }

    public Builder addActiveRules(ActiveRule... activeRules) {
      Collections.addAll(this.activeRules, activeRules);
      return this;
    }

    public Builder addActiveRules(Collection<? extends ActiveRule> activeRules) {
      this.activeRules.addAll(activeRules);
      return this;
    }

    public Builder addActiveRule(ActiveRule activeRules) {
      this.activeRules.add(activeRules);
      return this;
    }

    public AnalysisConfiguration build() {
      return new AnalysisConfiguration(this);
    }
  }
}
