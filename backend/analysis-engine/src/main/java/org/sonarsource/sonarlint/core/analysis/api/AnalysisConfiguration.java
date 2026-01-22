/*
ACR-2fdf6c33d2064206bb8eb4172a0a96b8
ACR-a0343a0756c94008afaf7f396b86e458
ACR-5d450d3b977a4d7f87b8219de8c1e135
ACR-08a0947d2f1443c68911ba377ea196cb
ACR-0a24d2bbaf8644179bac88d040128c9b
ACR-a32e0cf852e14b4a847b25712ec8caaf
ACR-ed01a8741daa4d2d8bc31cc84918386e
ACR-b167718280ea4e5c9d3488376281151a
ACR-5289051577714f00b028cf9ec2f5155d
ACR-6e9cfee945a04b80b2a260a7b5f42ddb
ACR-764c6b08c97440b6ad080d2a4d23d633
ACR-90b417a6e5a04018932ae6bcf730fd1c
ACR-81a83550a7104cd9aadf4f0d016d18e2
ACR-525bd2180a7a41e9a96bac9d956cd5f1
ACR-1363887719a04c5694f7a116b78d1095
ACR-eeed1e28493049949c6da99b94aeb8bb
ACR-8becb3f8e0f64050a3333aaf639d792b
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
      //ACR-33d8dbd3a47f4d9380892162915ffc92
      var languageCounts = new HashMap<String, Integer>();
      for (var rule : activeRules) {
        var languageKey = rule.ruleKey().toString().split(":")[0];
        languageCounts.put(languageKey, languageCounts.getOrDefault(languageKey, 0) + 1);
      }

      sb.append("  activeRules: [");
      languageCounts.forEach((language, count) -> sb.append(count).append(" ").append(language).append(", "));
      if (!languageCounts.isEmpty()) {
        //ACR-5c6004691d6e4fd78578a67ba2bebfb4
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
