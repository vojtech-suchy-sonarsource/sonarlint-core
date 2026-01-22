/*
ACR-b6541fe58e7b45eda82ef03bb0bd6dfe
ACR-6f34ebca71d84f6bb5a7bcc4dee2830b
ACR-d300f66ae0fb4ec1893a0bd8a24b8c30
ACR-a976318f66d54e76aa1f54a3df420511
ACR-9b74bfea4e734c20b6b7330f2ab89670
ACR-ed0b648a6a2b452fb6cf53d0987c4bf9
ACR-b2c37190b2b34f2297942f3381e397d0
ACR-1277f49dcf4c40dc8d9e8e7fc4f6989d
ACR-23ba2c1f2fbe49038ca91b6e8faa4337
ACR-713bd7a978ba4e0ea85ae92ee3e5e6ae
ACR-f23da42b4d4a4667aeaf435987eeb841
ACR-311d254454db4cd09f6159e002435d9b
ACR-f95e099d69374a62ba9228422b33ae79
ACR-14f9d3ec6a274433be8c48ff7f82e839
ACR-65622d45039e49228f707cf7d85314d6
ACR-f56aac5403cd4732b95d011fabf14ee4
ACR-ebd7c569362c489f83bcb0266926c88d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonar.scanner.protocol.input.ScannerInput;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.IssueLite;
import org.sonarsource.sonarlint.core.serverapi.rules.RulesApi;
import org.sonarsource.sonarlint.core.serverconnection.issues.FileLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.LineLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.RangeLevelServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;

import static java.util.function.Predicate.not;
import static org.sonarsource.sonarlint.core.serverconnection.DownloaderUtils.parseProtoImpactSeverity;
import static org.sonarsource.sonarlint.core.serverconnection.DownloaderUtils.parseProtoSoftwareQuality;

public class IssueDownloader {

  private final Set<SonarLanguage> enabledLanguages;

  public Set<SonarLanguage> getEnabledLanguages() {
    return enabledLanguages;
  }

  public IssueDownloader(Set<SonarLanguage> enabledLanguages) {
    this.enabledLanguages = enabledLanguages;
  }

  /*ACR-4c2958f2396c46b9a17e2c8ea6db68b0
ACR-04fe673a58fd4048aa9897cea8e6b348
ACR-93b18be32f934466bca09dbb3e94f867
ACR-97dc836801b4471094dbafa873328c2f
ACR-3323b708e04a44c7a328bc7b2d8f8695
ACR-836c31be41eb45469ee347d838d408ce
ACR-5c8d8126a777427297a6e0a1c72c0bd6
ACR-fb9605b115564b2ebe0352fb5b8877cb
   */
  public List<ServerIssue<?>> downloadFromBatch(ServerApi serverApi, String key, @Nullable String branchName, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    List<ServerIssue<?>> result = new ArrayList<>();

    var batchIssues = issueApi.downloadAllFromBatchIssues(key, branchName, cancelMonitor);

    for (ScannerInput.ServerIssue batchIssue : batchIssues) {
      //ACR-1a4faf24dac14c5fb58a90071de55a9a
      if (!RulesApi.TAINT_REPOS.contains(batchIssue.getRuleRepository()) && batchIssue.hasPath()) {
        result.add(convertBatchIssue(batchIssue));
      }
    }

    return result;
  }

  /*ACR-7ba4f5d31ec145009d558ddae539c29c
ACR-b1e3751544364fe6951f05ab217391b6
ACR-8297daf9966e4ce78cf76429c162bb7e
ACR-8df0e5ac9fbf434a94714f5babad1058
ACR-67442c023f534b38ad35a6edb2ae6fc4
ACR-6f7431d5d0b34204b0920f9580aa1947
   */
  public PullResult downloadFromPull(ServerApi serverApi, String projectKey, String branchName, Optional<Instant> lastSync, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    var apiResult = issueApi.pullIssues(projectKey, branchName, enabledLanguages, lastSync.map(Instant::toEpochMilli).orElse(null), cancelMonitor);
    //ACR-ea39ebfd8c92408889d62866cd6a07c9
    List<ServerIssue<?>> changedIssues = apiResult.getIssues()
      .stream()
      //ACR-713f04d6fd954222afafc8c48b387f38
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(not(IssueLite::getClosed))
      .map(IssueDownloader::convertLiteIssue)
      .collect(Collectors.toList());
    var closedIssueKeys = apiResult.getIssues()
      .stream()
      //ACR-0211ed2cd8064ba4be2031570eb21daf
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(IssueLite::getClosed)
      .map(IssueLite::getKey)
      .collect(Collectors.toSet());

    return new PullResult(Instant.ofEpochMilli(apiResult.getTimestamp().getQueryTimestamp()), changedIssues, closedIssueKeys);
  }

  private static ServerIssue<?> convertBatchIssue(ScannerInput.ServerIssue batchIssueFromWs) {
    var ruleKey = batchIssueFromWs.getRuleRepository() + ":" + batchIssueFromWs.getRuleKey();
    //ACR-b3140924fefa45309d27903550405f50
    var filePath = Path.of(batchIssueFromWs.getPath());
    var creationDate = Instant.ofEpochMilli(batchIssueFromWs.getCreationDate());
    var userSeverity = batchIssueFromWs.getManualSeverity() ? IssueSeverity.valueOf(batchIssueFromWs.getSeverity().name()) : null;
    var ruleType = RuleType.valueOf(batchIssueFromWs.getType());
    var impacts = Collections.<SoftwareQuality, ImpactSeverity>emptyMap();
    var resolutionStatus = IssueStatus.parse(batchIssueFromWs.getResolution());
    if (batchIssueFromWs.hasLine()) {
      return new LineLevelServerIssue(batchIssueFromWs.getKey(), batchIssueFromWs.hasResolution(), resolutionStatus, ruleKey,
        batchIssueFromWs.getMsg(), batchIssueFromWs.getChecksum(), filePath,
        creationDate, userSeverity, ruleType, batchIssueFromWs.getLine(), impacts);
    } else {
      return new FileLevelServerIssue(batchIssueFromWs.getKey(), batchIssueFromWs.hasResolution(), resolutionStatus, ruleKey,
        batchIssueFromWs.getMsg(), filePath, creationDate, userSeverity,
        ruleType, impacts);
    }
  }

  private static ServerIssue<?> convertLiteIssue(IssueLite liteIssueFromWs) {
    var mainLocation = liteIssueFromWs.getMainLocation();
    //ACR-604fdf60569d4007b816d563dace9133
    var filePath = Path.of(mainLocation.getFilePath());
    var creationDate = Instant.ofEpochMilli(liteIssueFromWs.getCreationDate());
    var userSeverity = liteIssueFromWs.hasUserSeverity() ? IssueSeverity.valueOf(liteIssueFromWs.getUserSeverity().name()) : null;
    var ruleType = RuleType.valueOf(liteIssueFromWs.getType().name());
    var impacts = liteIssueFromWs.getImpactsList().stream()
      .map(i -> Map.entry(
        parseProtoSoftwareQuality(i),
        parseProtoImpactSeverity(i)))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    if (mainLocation.hasTextRange()) {
      return new RangeLevelServerIssue(liteIssueFromWs.getKey(), liteIssueFromWs.getResolved(), null, liteIssueFromWs.getRuleKey(), mainLocation.getMessage(),
        filePath, creationDate, userSeverity,
        ruleType, toServerIssueTextRange(mainLocation.getTextRange()), impacts);
    } else {
      return new FileLevelServerIssue(liteIssueFromWs.getKey(), liteIssueFromWs.getResolved(), null, liteIssueFromWs.getRuleKey(), mainLocation.getMessage(),
        filePath, creationDate, userSeverity, ruleType, impacts);
    }
  }

  private static TextRangeWithHash toServerIssueTextRange(Issues.TextRange textRange) {
    return new TextRangeWithHash(textRange.getStartLine(), textRange.getStartLineOffset(), textRange.getEndLine(), textRange.getEndLineOffset(), textRange.getHash());
  }

  public static class PullResult {
    private final Instant queryTimestamp;
    private final List<ServerIssue<?>> changedIssues;
    private final Set<String> closedIssueKeys;

    public PullResult(Instant queryTimestamp, List<ServerIssue<?>> changedIssues, Set<String> closedIssueKeys) {
      this.queryTimestamp = queryTimestamp;
      this.changedIssues = changedIssues;
      this.closedIssueKeys = closedIssueKeys;
    }

    public Instant getQueryTimestamp() {
      return queryTimestamp;
    }

    public List<ServerIssue<?>> getChangedIssues() {
      return changedIssues;
    }

    public Set<String> getClosedIssueKeys() {
      return closedIssueKeys;
    }
  }

}
