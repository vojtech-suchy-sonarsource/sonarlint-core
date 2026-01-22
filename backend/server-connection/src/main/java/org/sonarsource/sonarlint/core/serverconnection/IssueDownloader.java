/*
ACR-404b9100d0ad456a9c5e53b09f3820fa
ACR-13b416429e82403088d0cf75359f8fdc
ACR-4ffcb95914f24547a212f519765b40c7
ACR-73d8eb0841ee44729498b3810c437d21
ACR-1820af81e84c4a57a735cf883756ab05
ACR-6a9bbc4463f14b65ba79f731b57383da
ACR-fd638a6fc652405ab56bc03a5330509c
ACR-9ece30117eac46a1ae966899d98f9512
ACR-70ef6574aa5c4e74ba409adb573de3c2
ACR-1fc73447faf14aecbab4d524de6082d2
ACR-f00ee511d77c411ebbb6126084c34906
ACR-5386998999554870af84cf2947764238
ACR-4041bce1e5fe4c7e891d48ca79c6c360
ACR-4ea9207c7dd74d10baa57fc77ddb71c9
ACR-02149380264c468dbfecbf7d0bf38346
ACR-06dd7f1fd30c439ab0793d3b32022987
ACR-5b0d1bdf208d465aa2cb28151bcec624
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

  /*ACR-681574ebbd48441b9bcdfc05818a2889
ACR-46262a9dc63141df9104efb7feba6192
ACR-7c89679c4cef4ff9b0a9e7e268e660b2
ACR-e6418716ab5a4c45a34d555ad9a2d998
ACR-2170d6506ebf49949df7de89a5597ff3
ACR-f909f6c1c5ff45b5beb5452c6564e1cd
ACR-a964b472d85343dfa5f64f3532be0207
ACR-5c38e34a42be4a8abeb84456ec1f039d
   */
  public List<ServerIssue<?>> downloadFromBatch(ServerApi serverApi, String key, @Nullable String branchName, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    List<ServerIssue<?>> result = new ArrayList<>();

    var batchIssues = issueApi.downloadAllFromBatchIssues(key, branchName, cancelMonitor);

    for (ScannerInput.ServerIssue batchIssue : batchIssues) {
      //ACR-d02fef000c7947d58aa76c325321d59c
      if (!RulesApi.TAINT_REPOS.contains(batchIssue.getRuleRepository()) && batchIssue.hasPath()) {
        result.add(convertBatchIssue(batchIssue));
      }
    }

    return result;
  }

  /*ACR-d7c43811c7204fc2885ef1cdfc06c3c2
ACR-1b37a3edd618473aa195d2d028b5b37a
ACR-8df569483c5449f28b89b5c5b77fe20b
ACR-91939aadef014bf2852959b1d98fed6a
ACR-8ada4859e7ed40c3851254e8badc84bf
ACR-f2b80cac8ab04d14bc566bad598a0478
   */
  public PullResult downloadFromPull(ServerApi serverApi, String projectKey, String branchName, Optional<Instant> lastSync, SonarLintCancelMonitor cancelMonitor) {
    var issueApi = serverApi.issue();

    var apiResult = issueApi.pullIssues(projectKey, branchName, enabledLanguages, lastSync.map(Instant::toEpochMilli).orElse(null), cancelMonitor);
    //ACR-fb9fcc673c0347d99c2dea16a576ecc6
    List<ServerIssue<?>> changedIssues = apiResult.getIssues()
      .stream()
      //ACR-39269e1d52794056b33d448a452b3471
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(not(IssueLite::getClosed))
      .map(IssueDownloader::convertLiteIssue)
      .collect(Collectors.toList());
    var closedIssueKeys = apiResult.getIssues()
      .stream()
      //ACR-08dd5363ecd84aecbb490710ff1a7f2b
      .filter(i -> i.getMainLocation().hasFilePath())
      .filter(IssueLite::getClosed)
      .map(IssueLite::getKey)
      .collect(Collectors.toSet());

    return new PullResult(Instant.ofEpochMilli(apiResult.getTimestamp().getQueryTimestamp()), changedIssues, closedIssueKeys);
  }

  private static ServerIssue<?> convertBatchIssue(ScannerInput.ServerIssue batchIssueFromWs) {
    var ruleKey = batchIssueFromWs.getRuleRepository() + ":" + batchIssueFromWs.getRuleKey();
    //ACR-7ddd1087a58042d5b4d8b067523fe666
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
    //ACR-877e3726097440f880a5c322de880aa4
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
