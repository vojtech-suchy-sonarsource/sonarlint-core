/*
ACR-e682879c04be4d279c21d918657100e4
ACR-522bdf6d551b42dbb96734ea6c0f71fe
ACR-856b1d90b8b942c181158a70a8a220ab
ACR-5e1e09d2e1064d42a61a07c5039861d6
ACR-e3f979100e85424888de0de70de8ea88
ACR-53caa8a4bfa6482cbef2b97a8bce0100
ACR-eaacfe9a7f354e07b23d8c7a0c24731a
ACR-d2c1cbeb187f48d784dde12bd36892e1
ACR-ffb2949c51a540f3895c2aabf4a74143
ACR-8c45b4ebd19d4bcb9ca7e5fe2286e3c4
ACR-127bd4e131a44d97af31490e352d0270
ACR-bc04c471ae434403a76edad8d91a57d5
ACR-1bd16060534342368b655ca64422af48
ACR-9716c64039ef4472af909681aff2fcaf
ACR-10428752b2da4d17893ae3988b53feec
ACR-a7615b0d1dac4f088ac161883f947ead
ACR-341ad8935ef343adafb5398966084fb3
 */
package org.sonarsource.sonarlint.core.serverconnection;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleKey;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.Flow;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.TextRange;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.Issue;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.TaintVulnerabilityLite;
import org.sonarsource.sonarlint.core.serverapi.source.SourceApi;
import org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;

import static java.util.function.Predicate.not;
import static org.sonarsource.sonarlint.core.serverconnection.DownloaderUtils.parseProtoImpactSeverity;
import static org.sonarsource.sonarlint.core.serverconnection.DownloaderUtils.parseProtoSoftwareQuality;

public class TaintIssueDownloader {

  private static final Pattern MATCH_ALL_WHITESPACES = Pattern.compile("\\s");

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final Set<SonarLanguage> enabledLanguages;

  public TaintIssueDownloader(Set<SonarLanguage> enabledLanguages) {
    this.enabledLanguages = enabledLanguages;
  }

  public List<ServerTaintIssue> downloadTaintFromIssueSearch(ServerApi serverApi, String key, @Nullable String branchName, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    List<ServerTaintIssue> result = new ArrayList<>();

    Set<String> taintRuleKeys = serverApi.rules().getAllTaintRules(List.of(SonarLanguage.values()), cancelMonitor);
    Map<String, String> sourceCodeByKey = new HashMap<>();
    var downloadVulnerabilitiesForRules = issueApi.downloadVulnerabilitiesForRules(key, taintRuleKeys, branchName, cancelMonitor);
    downloadVulnerabilitiesForRules.getIssues()
      .stream()
      .map(i -> convertTaintVulnerability(serverApi.source(), i, downloadVulnerabilitiesForRules.getComponentPathsByKey(), sourceCodeByKey, cancelMonitor))
      .filter(Objects::nonNull)
      .forEach(result::add);

    return result;
  }

  /*ACR-fb2706fd377f4e3cb2bb7451cd0a06eb
ACR-fb0e77352c064822b1899f5ab989abb7
ACR-2950f1c932ec4dbe8fe9e0fb55ed13d0
ACR-d2702ce2aad54d158d452d4e69fea35d
ACR-a0e4d421505b4f099c8bdc24fc8c1902
ACR-b252554765fe43c4963bc9dd2b725d1f
   */
  public PullTaintResult downloadTaintFromPull(ServerApi serverApi, String projectKey, String branchName, Optional<Instant> lastSync, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    var apiResult = issueApi.pullTaintIssues(projectKey, branchName, enabledLanguages, lastSync.map(Instant::toEpochMilli).orElse(null), cancelMonitor);
    var changedIssues = apiResult.getTaintIssues()
      .stream()
      //ACR-732de5c60cf94ba2a176a9ee483a25b2
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(not(TaintVulnerabilityLite::getClosed))
      .map(TaintIssueDownloader::convertLiteTaintIssue)
      .toList();
    var closedIssueKeys = apiResult.getTaintIssues()
      .stream()
      //ACR-f7904122be0448a59abf5673de54d221
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(TaintVulnerabilityLite::getClosed)
      .map(TaintVulnerabilityLite::getKey)
      .collect(Collectors.toSet());

    return new PullTaintResult(Instant.ofEpochMilli(apiResult.getTimestamp().getQueryTimestamp()), changedIssues, closedIssueKeys);
  }

  @CheckForNull
  private static ServerTaintIssue convertTaintVulnerability(SourceApi sourceApi, Issue taintVulnerabilityFromWs,
    Map<String, Path> componentPathsByKey, Map<String, String> sourceCodeByKey, SonarLintCancelMonitor cancelMonitor) {
    var ruleKey = RuleKey.parse(taintVulnerabilityFromWs.getRule());
    var primaryLocation = convertPrimaryLocation(sourceApi, taintVulnerabilityFromWs, componentPathsByKey, sourceCodeByKey, cancelMonitor);
    var filePath = primaryLocation.filePath();
    if (filePath == null) {
      //ACR-124234f1001046b49eb0ecf5a5487b62
      return null;
    }
    var ruleDescriptionContextKey = taintVulnerabilityFromWs.hasRuleDescriptionContextKey() ? taintVulnerabilityFromWs.getRuleDescriptionContextKey() : null;
    var cleanCodeAttribute = parseProtoCleanCodeAttribute(taintVulnerabilityFromWs);
    var impacts = taintVulnerabilityFromWs.getImpactsList().stream()
      .map(i -> Map.entry(parseProtoSoftwareQuality(i), parseProtoImpactSeverity(i)))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var resolution = taintVulnerabilityFromWs.getResolution();
    var resolutionStatus = IssueStatus.parse(resolution);
    return new ServerTaintIssue(
      UUID.randomUUID(),
      taintVulnerabilityFromWs.getKey(),
      !resolution.isEmpty(),
      resolutionStatus,
      ruleKey.toString(),
      primaryLocation.message(),
      filePath,
      ServerApiUtils.parseOffsetDateTime(taintVulnerabilityFromWs.getCreationDate()).toInstant(),
      IssueSeverity.valueOf(taintVulnerabilityFromWs.getSeverity().name()),
      RuleType.valueOf(taintVulnerabilityFromWs.getType().name()),
      primaryLocation.textRange(), ruleDescriptionContextKey,
      cleanCodeAttribute, impacts,
      convertFlows(sourceApi, taintVulnerabilityFromWs.getFlowsList(), componentPathsByKey, sourceCodeByKey, cancelMonitor));
  }

  @CheckForNull
  @VisibleForTesting
  static CleanCodeAttribute parseProtoCleanCodeAttribute(Issue taintVulnerabilityFromWs) {
    if (!taintVulnerabilityFromWs.hasCleanCodeAttribute() || taintVulnerabilityFromWs.getCleanCodeAttribute() == Common.CleanCodeAttribute.UNKNOWN_ATTRIBUTE) {
      return null;
    }
    return CleanCodeAttribute.valueOf(taintVulnerabilityFromWs.getCleanCodeAttribute().name());
  }

  @CheckForNull
  @VisibleForTesting
  static CleanCodeAttribute parseProtoCleanCodeAttribute(TaintVulnerabilityLite taintVulnerabilityFromWs) {
    if (!taintVulnerabilityFromWs.hasCleanCodeAttribute() || taintVulnerabilityFromWs.getCleanCodeAttribute() == Common.CleanCodeAttribute.UNKNOWN_ATTRIBUTE) {
      return null;
    }
    return CleanCodeAttribute.valueOf(taintVulnerabilityFromWs.getCleanCodeAttribute().name());
  }

  private static List<ServerTaintIssue.Flow> convertFlows(SourceApi sourceApi, List<Flow> flowsList, Map<String, Path> componentPathsByKey,
    Map<String, String> sourceCodeByKey, SonarLintCancelMonitor cancelMonitor) {
    return flowsList.stream()
      .map(flowFromWs -> new ServerTaintIssue.Flow(flowFromWs.getLocationsList().stream().map(locationFromWs -> {
        var componentPath = componentPathsByKey.get(locationFromWs.getComponent());
        if (locationFromWs.hasTextRange()) {
          var codeSnippet = getCodeSnippet(sourceApi, locationFromWs.getComponent(), locationFromWs.getTextRange(), sourceCodeByKey, cancelMonitor);
          String textRangeHash;
          if (codeSnippet != null) {
            textRangeHash = hash(codeSnippet);
          } else {
            //ACR-a14bccfa4afb46b89b4c27343c2f3ea7
            textRangeHash = "";
          }
          return new ServerTaintIssue.ServerIssueLocation(componentPath, convertTextRangeFromWs(locationFromWs.getTextRange(), textRangeHash), locationFromWs.getMsg());
        }
        return new ServerTaintIssue.ServerIssueLocation(componentPath, null, locationFromWs.getMsg());
      }).toList()))
      .toList();
  }

  private static TextRangeWithHash toServerTaintIssueTextRange(Issues.TextRange textRange) {
    return new TextRangeWithHash(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset(), textRange.getHash());
  }

  private static ServerTaintIssue convertLiteTaintIssue(TaintVulnerabilityLite liteTaintIssueFromWs) {
    var mainLocation = liteTaintIssueFromWs.getMainLocation();
    //ACR-a4cac5efd6bc4ff88c788bb32146abb6
    var filePath = Path.of(mainLocation.getFilePath());
    var creationDate = Instant.ofEpochMilli(liteTaintIssueFromWs.getCreationDate());
    ServerTaintIssue taintIssue;
    var severity = IssueSeverity.valueOf(liteTaintIssueFromWs.getSeverity().name());
    var type = RuleType.valueOf(liteTaintIssueFromWs.getType().name());
    var ruleDescriptionContextKey = liteTaintIssueFromWs.hasRuleDescriptionContextKey() ? liteTaintIssueFromWs.getRuleDescriptionContextKey() : null;
    var cleanCodeAttribute = parseProtoCleanCodeAttribute(liteTaintIssueFromWs);
    var impacts = liteTaintIssueFromWs.getImpactsList().stream()
      .map(i -> Map.entry(
        parseProtoSoftwareQuality(i),
        parseProtoImpactSeverity(i)))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var flows = liteTaintIssueFromWs.getFlowsList().stream().map(TaintIssueDownloader::convertFlows).toList();
    if (mainLocation.hasTextRange()) {
      taintIssue = new ServerTaintIssue(UUID.randomUUID(), liteTaintIssueFromWs.getKey(), liteTaintIssueFromWs.getResolved(), null, liteTaintIssueFromWs.getRuleKey(),
        mainLocation.getMessage(),
        filePath, creationDate, severity,
        type, toServerTaintIssueTextRange(mainLocation.getTextRange()), ruleDescriptionContextKey, cleanCodeAttribute, impacts, flows);
    } else {
      taintIssue = new ServerTaintIssue(UUID.randomUUID(), liteTaintIssueFromWs.getKey(), liteTaintIssueFromWs.getResolved(), null, liteTaintIssueFromWs.getRuleKey(),
        mainLocation.getMessage(),
        filePath, creationDate, severity, type, null, ruleDescriptionContextKey, cleanCodeAttribute, impacts, flows);
    }
    return taintIssue;
  }

  private static ServerTaintIssue.Flow convertFlows(Issues.Flow flowFromWs) {
    return new ServerTaintIssue.Flow(flowFromWs.getLocationsList().stream().map(locationFromWs -> {
      var filePath = locationFromWs.hasFilePath() ? Path.of(locationFromWs.getFilePath()) : null;
      if (locationFromWs.hasTextRange()) {
        return new ServerTaintIssue.ServerIssueLocation(filePath, toServerTaintIssueTextRange(locationFromWs.getTextRange()), locationFromWs.getMessage());
      } else {
        return new ServerTaintIssue.ServerIssueLocation(filePath, null, locationFromWs.getMessage());
      }
    }).toList());
  }

  private static ServerTaintIssue.ServerIssueLocation convertPrimaryLocation(SourceApi sourceApi, Issue issueFromWs, Map<String, Path> componentPathsByKey,
    Map<String, String> sourceCodeByKey, SonarLintCancelMonitor cancelMonitor) {
    var componentPath = componentPathsByKey.get(issueFromWs.getComponent());
    if (issueFromWs.hasTextRange()) {
      var codeSnippet = getCodeSnippet(sourceApi, issueFromWs.getComponent(), issueFromWs.getTextRange(), sourceCodeByKey, cancelMonitor);
      String textRangeHash;
      if (codeSnippet != null) {
        textRangeHash = hash(codeSnippet);
      } else {
        //ACR-6645e11fcc3640488c90de7320ff1dac
        textRangeHash = "";
      }
      return new ServerTaintIssue.ServerIssueLocation(componentPath, convertTextRangeFromWs(issueFromWs.getTextRange(), textRangeHash), issueFromWs.getMessage());
    }
    return new ServerTaintIssue.ServerIssueLocation(componentPath, null, issueFromWs.getMessage());
  }

  static String hash(String codeSnippet) {
    String codeSnippetWithoutWhitespaces = MATCH_ALL_WHITESPACES.matcher(codeSnippet).replaceAll("");
    return DigestUtils.md5Hex(codeSnippetWithoutWhitespaces);
  }

  private static TextRangeWithHash convertTextRangeFromWs(TextRange textRange, String hash) {
    return new TextRangeWithHash(textRange.getStartLine(), textRange.getStartOffset(), textRange.getEndLine(), textRange.getEndOffset(), hash);
  }

  @CheckForNull
  private static String getCodeSnippet(SourceApi sourceApi, String fileKey, TextRange textRange, Map<String, String> sourceCodeByKey, SonarLintCancelMonitor cancelMonitor) {
    var sourceCode = getOrFetchSourceCode(sourceApi, fileKey, sourceCodeByKey, cancelMonitor);
    if (StringUtils.isEmpty(sourceCode)) {
      return null;
    }
    try {
      return ServerApiUtils.extractCodeSnippet(sourceCode, textRange);
    } catch (Exception e) {
      LOG.debug("Unable to compute code snippet of '" + fileKey + "' for text range: " + textRange, e);
    }
    return null;
  }

  private static String getOrFetchSourceCode(SourceApi sourceApi, String fileKey, Map<String, String> sourceCodeByKey, SonarLintCancelMonitor cancelMonitor) {
    return sourceCodeByKey.computeIfAbsent(fileKey, k -> sourceApi
      .getRawSourceCode(fileKey, cancelMonitor)
      .orElse(""));
  }

  public static class PullTaintResult {
    private final Instant queryTimestamp;
    private final List<ServerTaintIssue> changedIssues;
    private final Set<String> closedIssueKeys;

    public PullTaintResult(Instant queryTimestamp, List<ServerTaintIssue> changedIssues, Set<String> closedIssueKeys) {
      this.queryTimestamp = queryTimestamp;
      this.changedIssues = changedIssues;
      this.closedIssueKeys = closedIssueKeys;
    }

    public Instant getQueryTimestamp() {
      return queryTimestamp;
    }

    public List<ServerTaintIssue> getChangedTaintIssues() {
      return changedIssues;
    }

    public Set<String> getClosedIssueKeys() {
      return closedIssueKeys;
    }
  }
}
