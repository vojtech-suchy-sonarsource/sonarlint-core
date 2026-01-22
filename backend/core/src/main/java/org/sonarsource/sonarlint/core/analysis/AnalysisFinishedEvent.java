/*
ACR-ef6780dc64cb499b94334d19c92c0441
ACR-fa48a894ac664fab91056e17688a0b91
ACR-d641cef2873744f4a163d59d0e076580
ACR-18f9161a62b44b1eba1009dbf6373f33
ACR-cd9bc673e3bc41f2ae95c318d81181d6
ACR-fbce6a7f501e473ab8783ebea1ee6635
ACR-2c10f0d476b54558b57b2f0f8c44d866
ACR-ea7387a9441d4f69bec18016a8179673
ACR-0b4429550068453982855fffb89fe900
ACR-727e352ce26f47e5b810e65e3e6764ac
ACR-b474561c7d414369931df0a88cea3254
ACR-064694419cfd4559a2c1658c23be1b72
ACR-b7f63fe231044855a416b739b9da07d2
ACR-179688a5bdfe49be8ac92547dae456a2
ACR-ae9e16e2ea6540f78bb01495e32f3c41
ACR-cb19c54cd82742e1835b4536235f84c2
ACR-dc99dd215abc475bbbdee4adb7833fc4
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
