/*
ACR-a1f710d4e49f40dabab993e30d5b9983
ACR-f83411e0715e4b42877d6bccc0218931
ACR-41389edda01c4b8e86b9845ce3d7e3c1
ACR-2d2930700c6340f3bc0b92fc26ea16fc
ACR-0e74d088596e44cc8ee85a732c780cb0
ACR-276e3c24a9c14497b35a4579a4672439
ACR-63836d1ea04e4cadbc805a0cf5bd30fc
ACR-2593519b0af64e36b95cdd674ff7ab99
ACR-29a9533a9ad24c9b8f56d443520fa190
ACR-554122d2995d422585124469afbaf883
ACR-c33e32fcc04643529d798245ea518303
ACR-0aa6419f7e3046fb85642db2294f2d75
ACR-6634e6cf54384e428279fbe46a651d7e
ACR-2523f67c65084315a9432696fbae5cdc
ACR-2f96d8d8b95d4caa8a943093f26d5b4d
ACR-d44e515bf69c452f848efe88dace40c5
ACR-cbb06a34312044ec8137acc999d148e5
 */
package org.sonarsource.sonarlint.core.analysis;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

import static java.util.function.Predicate.not;

public class AnalysisFinishedEvent {
  private final UUID analysisId;
  private final String configurationScopeId;
  private final Duration analysisDuration;
  private final Map<URI, SonarLanguage> languagePerFile;
  private final boolean succeededForAllFiles;
  private final List<RawIssue> issues;
  private final Set<String> reportedRuleKeys;
  private final Set<SonarLanguage> detectedLanguages;
  private final boolean shouldFetchServerIssues;

  public AnalysisFinishedEvent(UUID analysisId, String configurationScopeId, Duration analysisDuration, Map<URI, SonarLanguage> languagePerFile, boolean succeededForAllFiles,
    List<RawIssue> issues, boolean shouldFetchServerIssues) {
    this.analysisId = analysisId;
    this.configurationScopeId = configurationScopeId;
    this.analysisDuration = analysisDuration;
    this.languagePerFile = languagePerFile;
    this.succeededForAllFiles = succeededForAllFiles;
    this.issues = issues;
    this.reportedRuleKeys = issues.stream().map(RawIssue::getRuleKey).collect(Collectors.toSet());
    this.detectedLanguages = languagePerFile.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
    this.shouldFetchServerIssues = shouldFetchServerIssues;
  }

  public UUID getAnalysisId() {
    return analysisId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Duration getAnalysisDuration() {
    return analysisDuration;
  }

  public Map<URI, SonarLanguage> getLanguagePerFile() {
    return languagePerFile;
  }

  public boolean succeededForAllFiles() {
    return succeededForAllFiles;
  }

  public Set<String> getReportedRuleKeys() {
    return reportedRuleKeys;
  }

  public Set<SonarLanguage> getDetectedLanguages() {
    return detectedLanguages;
  }

  public List<RawIssue> getIssues() {
    return issues.stream().filter(not(RawIssue::isSecurityHotspot)).toList();
  }

  public List<RawIssue> getHotspots() {
    return issues.stream().filter(RawIssue::isSecurityHotspot).toList();
  }

  public boolean shouldFetchServerIssues() {
    return shouldFetchServerIssues;
  }
}
