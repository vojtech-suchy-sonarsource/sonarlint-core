/*
ACR-810486b366e244369709e89fd544c18c
ACR-e2398eb899844499a9fe301f1df815c6
ACR-4003bb2f118243f9a8c68139380ddb38
ACR-1bf830af3f2347098d14ce06c1d08514
ACR-71977945abdb462ea9fcd6b68242c2d7
ACR-b465228c3be1444faf1b47036fab380d
ACR-5a63c42aa8fd4a208e0dc52d43e4762c
ACR-0db8aea540d7452aa4380d34da5031d1
ACR-6eb5d67345d7471b87f86cb28c78dea5
ACR-fa61baa5c5e240f684ace50f73516706
ACR-99202d7908414e089fd93f4402172cf7
ACR-ef5f72f9f42244da93875bc070a90200
ACR-e4c76a26e18645528ee81b5a0db306cc
ACR-aef80908e81c4e1e995e98babaadd868
ACR-a6f4b0f87e6c4c3d95de4cbf19d6b100
ACR-170da2d4255c42cb8384c7758daccc62
ACR-72b98162a1f14f68b9bdb0cbfb4ece1a
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

  /*ACR-5a3b8aff85fa4ada918a83bb556316b4
ACR-8ee0c1eace2a4d068f1608cd298e7a30
ACR-21b0397310c64a3db6ae9a0905233aba
ACR-9632994b847448bf928c3ed35c792482
ACR-a98279008e89415aa91375cf6e737539
ACR-26443c13cc014fb38897fddeff2baeca
   */
  public PullTaintResult downloadTaintFromPull(ServerApi serverApi, String projectKey, String branchName, Optional<Instant> lastSync, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    var apiResult = issueApi.pullTaintIssues(projectKey, branchName, enabledLanguages, lastSync.map(Instant::toEpochMilli).orElse(null), cancelMonitor);
    var changedIssues = apiResult.getTaintIssues()
      .stream()
      //ACR-4d9c1ab3ad3045f19ab2053a22e99576
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(not(TaintVulnerabilityLite::getClosed))
      .map(TaintIssueDownloader::convertLiteTaintIssue)
      .toList();
    var closedIssueKeys = apiResult.getTaintIssues()
      .stream()
      //ACR-099ef118012248edb80c69d0b2e71aa0
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
      //ACR-3f55668c4f4a4d1c8609a33af65de461
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
            //ACR-25d5bef6a1964ae0a7936c2153ad5662
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
    //ACR-e3368137f49f4d91a624e4ea1db3d2d8
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
        //ACR-78ca83a05a564c6695e5f6e2aca29159
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
