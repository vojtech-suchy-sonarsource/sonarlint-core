/*
ACR-756da291fd124662b7fab8154006e32d
ACR-1e8832dd283a46d8b43f56b346fe0277
ACR-255e0b7cc6d144f19bbb4d841eeb597d
ACR-403e58f4aa2045c0b706ac7da54c4d34
ACR-06da583d3e794c30972bc058201f688b
ACR-3043fe944d844cf6b53c171ffa1886b1
ACR-5ed11931196d4b738127ea43d6009198
ACR-1f9b68b563e14759b447244e0c295055
ACR-3688a22ea0384a11ab248ea09b580fc4
ACR-72b12ff8cffb41aa900d1461f1e5c260
ACR-4bf883d7037940f8a5944f4b5d1f17c3
ACR-707143505c324b1aa4f640c39bf9a714
ACR-ec39452b69044c349beecd785e2ccd94
ACR-529233f6d7be4b18b2ed6b1d4dd6909a
ACR-4fa50a30e03143bf92cf56a0a694b2d5
ACR-273f9ab155e0435692adc1f0ff8b87cd
ACR-a0ea85ed6cdb4d8393e68c73db364e49
 */
package org.sonarsource.sonarlint.core.serverapi.issue;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonar.scanner.protocol.input.ScannerInput;
import org.sonarsource.sonarlint.core.commons.IssueStatus;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;
import org.sonarsource.sonarlint.core.commons.Transition;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.Component;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues.Issue;
import org.sonarsource.sonarlint.core.serverapi.source.SourceApi;
import org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.http.HttpClient.FORM_URL_ENCODED_CONTENT_TYPE;
import static org.sonarsource.sonarlint.core.http.HttpClient.JSON_CONTENT_TYPE;
import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;
import static org.sonarsource.sonarlint.core.serverapi.util.ProtobufUtil.readMessages;
import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.toSonarQubePath;

public class IssueApi {

  private static final Map<IssueStatus, Transition> transitionByStatus = Map.of(
    IssueStatus.ACCEPT, Transition.ACCEPT,
    IssueStatus.WONT_FIX, Transition.WONT_FIX,
    IssueStatus.FALSE_POSITIVE, Transition.FALSE_POSITIVE
  );

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String ORGANIZATION_PARAM = "&organization=";

  private final ServerApiHelper serverApiHelper;

  public IssueApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  /*ACR-97563bb8e5884b6c837b0a2f91cf12c9
ACR-d61461a1475e4999ba99fb5d3f7c9684
ACR-2e6cb940bc474d5bb3357957c97b0960
ACR-980f556b4f73494f8919fd0ed41c33d2
ACR-d7e799b7cf9841288cc64c0d97941ff4
   */
  public DownloadIssuesResult downloadVulnerabilitiesForRules(String key, Set<String> ruleKeys, @Nullable String branchName, SonarLintCancelMonitor cancelMonitor) {
    var searchUrl = new StringBuilder();
    searchUrl.append(getVulnerabilitiesUrl(key, ruleKeys));
    searchUrl.append(getUrlBranchParameter(branchName));
    serverApiHelper.getOrganizationKey()
      .ifPresent(org -> searchUrl.append(ORGANIZATION_PARAM).append(UrlUtils.urlEncode(org)));
    List<Issue> result = new ArrayList<>();
    Map<String, Path> componentsPathByKey = new HashMap<>();
    serverApiHelper.getPaginated(searchUrl.toString(),
      Issues.SearchWsResponse::parseFrom,
      r -> r.getPaging().getTotal(),
      r -> {
        componentsPathByKey.clear();
        //ACR-6674bb72240547db810d2d94c0d9d299
        componentsPathByKey.putAll(r.getComponentsList().stream().filter(Component::hasPath)
          .collect(Collectors.toMap(Component::getKey, component -> Path.of(component.getPath()))));
        return r.getIssuesList();
      },
      result::add,
      true,
      cancelMonitor);

    return new DownloadIssuesResult(result, componentsPathByKey);
  }

  public static class DownloadIssuesResult {
    private final List<Issue> issues;
    private final Map<String, Path> componentPathsByKey;

    private DownloadIssuesResult(List<Issue> issues, Map<String, Path> componentPathsByKey) {
      this.issues = issues;
      this.componentPathsByKey = componentPathsByKey;
    }

    public List<Issue> getIssues() {
      return issues;
    }

    public Map<String, Path> getComponentPathsByKey() {
      return componentPathsByKey;
    }

  }

  private static String getVulnerabilitiesUrl(String key, Set<String> ruleKeys) {
    return "/api/issues/search.protobuf?statuses=OPEN,CONFIRMED,REOPENED,RESOLVED&types=VULNERABILITY&componentKeys="
      + urlEncode(key) + "&rules=" + urlEncode(String.join(",", ruleKeys));
  }

  private static String getUrlBranchParameter(@Nullable String branchName) {
    if (branchName != null) {
      return "&branch=" + urlEncode(branchName);
    }
    return "";
  }

  public List<ScannerInput.ServerIssue> downloadAllFromBatchIssues(String key, @Nullable String branchName, SonarLintCancelMonitor cancelMonitor) {
    String batchIssueUrl = getBatchIssuesUrl(key) + getUrlBranchParameter(branchName);
    return ServerApiHelper.processTimed(
      () -> serverApiHelper.rawGet(batchIssueUrl, cancelMonitor),
      response -> {
        if (response.code() == 403 || response.code() == 404) {
          return Collections.emptyList();
        } else if (!response.isSuccessful()) {
          throw ServerApiHelper.handleError(response);
        }
        var input = response.bodyAsStream();
        var parser = ScannerInput.ServerIssue.parser();
        return readMessages(input, parser);
      },
      duration -> LOG.debug("Downloaded issues in {}ms", duration));
  }

  private static String getBatchIssuesUrl(String key) {
    return "/batch/issues?key=" + UrlUtils.urlEncode(key);
  }

  private static String getPullIssuesUrl(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince) {
    var enabledLanguageKeys = enabledLanguages.stream().map(SonarLanguage::getSonarLanguageKey).collect(Collectors.joining(","));
    var url = new StringBuilder()
      .append("/api/issues/pull?projectKey=")
      .append(UrlUtils.urlEncode(projectKey)).append("&branchName=").append(UrlUtils.urlEncode(branchName));
    if (!enabledLanguageKeys.isEmpty()) {
      url.append("&languages=").append(enabledLanguageKeys);
    }
    if (changedSince != null) {
      url.append("&changedSince=").append(changedSince);
    }
    return url.toString();
  }

  public IssuesPullResult pullIssues(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince,
    SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> serverApiHelper.get(getPullIssuesUrl(projectKey, branchName, enabledLanguages, changedSince), cancelMonitor),
      response -> {
        var input = response.bodyAsStream();
        var timestamp = Issues.IssuesPullQueryTimestamp.parseDelimitedFrom(input);
        return new IssuesPullResult(timestamp, readMessages(input, Issues.IssueLite.parser()));
      },
      duration -> LOG.debug("Pulled issues in {}ms", duration));
  }

  public static class IssuesPullResult {
    private final Issues.IssuesPullQueryTimestamp timestamp;
    private final List<Issues.IssueLite> issues;

    public IssuesPullResult(Issues.IssuesPullQueryTimestamp timestamp, List<Issues.IssueLite> issues) {
      this.timestamp = timestamp;
      this.issues = issues;
    }

    public Issues.IssuesPullQueryTimestamp getTimestamp() {
      return timestamp;
    }

    public List<Issues.IssueLite> getIssues() {
      return issues;
    }
  }

  private static String getPullTaintIssuesUrl(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince) {
    var enabledLanguageKeys = enabledLanguages.stream().map(SonarLanguage::getSonarLanguageKey).collect(Collectors.joining(","));
    var url = new StringBuilder()
      .append("/api/issues/pull_taint?projectKey=")
      .append(UrlUtils.urlEncode(projectKey)).append("&branchName=").append(UrlUtils.urlEncode(branchName));
    if (!enabledLanguageKeys.isEmpty()) {
      url.append("&languages=").append(enabledLanguageKeys);
    }
    if (changedSince != null) {
      url.append("&changedSince=").append(changedSince);
    }
    return url.toString();
  }

  public TaintIssuesPullResult pullTaintIssues(String projectKey, String branchName, Set<SonarLanguage> enabledLanguages, @Nullable Long changedSince,
    SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> serverApiHelper.get(getPullTaintIssuesUrl(projectKey, branchName, enabledLanguages, changedSince), cancelMonitor),
      response -> {
        var input = response.bodyAsStream();
        var timestamp = Issues.TaintVulnerabilityPullQueryTimestamp.parseDelimitedFrom(input);
        return new TaintIssuesPullResult(timestamp, readMessages(input, Issues.TaintVulnerabilityLite.parser()));
      },
      duration -> LOG.debug("Pulled taint issues in {}ms", duration));
  }

  public void changeStatus(String issueKey, Transition transition, SonarLintCancelMonitor cancelMonitor) {
    var body = "issue=" + urlEncode(issueKey) + "&transition=" + urlEncode(transition.getStatus());
    serverApiHelper.post("/api/issues/do_transition", FORM_URL_ENCODED_CONTENT_TYPE, body, cancelMonitor);
  }

  public void addComment(String issueKey, String text, SonarLintCancelMonitor cancelMonitor) {
    var body = "issue=" + urlEncode(issueKey) + "&text=" + urlEncode(text);
    serverApiHelper.post("/api/issues/add_comment", FORM_URL_ENCODED_CONTENT_TYPE, body, cancelMonitor);
  }

  public Issue searchByKey(String issueKey, SonarLintCancelMonitor cancelMonitor) {
    var searchUrl = new StringBuilder();
    searchUrl.append("/api/issues/search.protobuf?issues=").append(urlEncode(issueKey)).append("&additionalFields=transitions");
    serverApiHelper.getOrganizationKey()
      .ifPresent(org -> searchUrl.append(ORGANIZATION_PARAM).append(UrlUtils.urlEncode(org)));
    searchUrl.append("&ps=1&p=1");
    try (var wsResponse = serverApiHelper.get(searchUrl.toString(), cancelMonitor); var body = wsResponse.bodyAsStream()) {
      var pbResponse = Issues.SearchWsResponse.parseFrom(body);
      if (pbResponse.getIssuesList().isEmpty()) {
        throw new UnexpectedBodyException("No issue found with key '" + issueKey + "'");
      }
      return pbResponse.getIssuesList().get(0);
    } catch (IOException e) {
      LOG.error("Error when searching issue + '" + issueKey + "'", e);
      throw new UnexpectedBodyException(e);
    }
  }

  public Optional<ServerIssueDetails> fetchServerIssue(String issueKey, String projectKey, String branch, @Nullable String pullRequest, SonarLintCancelMonitor cancelMonitor) {
    String searchUrl = "/api/issues/search.protobuf?issues=" + urlEncode(issueKey) + "&componentKeys=" + projectKey + "&ps=1&p=1";
    if (pullRequest != null && !pullRequest.isEmpty()) {
      searchUrl = searchUrl.concat("&pullRequest=").concat(urlEncode(pullRequest));
    } else if (!branch.isEmpty()) {
      //ACR-f4cca348d74c41e087bafc4b1e8c686b
      searchUrl = searchUrl.concat("&branch=").concat(urlEncode(branch));
    }

    try (var wsResponse = serverApiHelper.get(searchUrl, cancelMonitor); var is = wsResponse.bodyAsStream()) {
      var response = Issues.SearchWsResponse.parseFrom(is);
      if (response.getIssuesList().isEmpty() || response.getComponentsList().isEmpty()) {
        LOG.warn("No issue found with key '" + issueKey + "'");
        return Optional.empty();
      }
      var issue = response.getIssuesList().get(0);
      var optionalComponentWithPath = response.getComponentsList().stream().filter(component -> component.getKey().equals(issue.getComponent())).findFirst();
      if (optionalComponentWithPath.isEmpty()) {
        LOG.warn("No path found in components for the issue with key '" + issueKey + "'");
        return Optional.empty();
      }

      var fileKey = issue.getComponent();
      var codeSnippet = getCodeSnippet(fileKey, issue.getTextRange(), branch, pullRequest, cancelMonitor);

      return Optional.of(new ServerIssueDetails(issue, Path.of(optionalComponentWithPath.get().getPath()), response.getComponentsList(), codeSnippet.orElse("")));
    } catch (Exception e) {
      LOG.warn("Error while fetching issue", e.getMessage());
      return Optional.empty();
    }
  }

  public Optional<String> getCodeSnippet(String fileKey, Common.TextRange textRange, String branch, @Nullable String pullRequest, SonarLintCancelMonitor cancelMonitor) {
    var source = new SourceApi(serverApiHelper).getRawSourceCodeForBranchAndPullRequest(fileKey, branch, pullRequest, cancelMonitor);
    if (source.isPresent()) {
      try {
        var codeSnippet = ServerApiUtils.extractCodeSnippet(source.get(), textRange);
        return Optional.of(codeSnippet);
      } catch (Exception e) {
        LOG.debug("Unable to compute code snippet of '" + fileKey + "' for text range: " + textRange, e);
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }

  public void anticipatedTransitions(String projectKey, List<LocalOnlyIssue> resolvedLocalOnlyIssues, SonarLintCancelMonitor cancelMonitor) {
    serverApiHelper.post("/api/issues/anticipated_transitions?projectKey=" + projectKey, JSON_CONTENT_TYPE, new Gson().toJson(adapt(resolvedLocalOnlyIssues)), cancelMonitor);
  }

  private static List<IssueAnticipatedTransition> adapt(List<LocalOnlyIssue> resolvedLocalOnlyIssues) {
    return resolvedLocalOnlyIssues.stream().map(IssueApi::adapt).toList();
  }

  private static IssueAnticipatedTransition adapt(LocalOnlyIssue issue) {
    Integer lineNumber = null;
    String lineHash = null;
    var lineWithHash = issue.getLineWithHash();
    if (lineWithHash != null) {
      lineNumber = lineWithHash.getNumber();
      lineHash = lineWithHash.getHash();
    }
    var resolution = requireNonNull(issue.getResolution());
    return new IssueAnticipatedTransition(toSonarQubePath(issue.getServerRelativePath()), lineNumber, lineHash, issue.getRuleKey(), issue.getMessage(),
      transitionByStatus.get(resolution.getStatus()).getStatus(), resolution.getComment());
  }

  public static class TaintIssuesPullResult {
    private final Issues.TaintVulnerabilityPullQueryTimestamp timestamp;
    private final List<Issues.TaintVulnerabilityLite> issues;

    public TaintIssuesPullResult(Issues.TaintVulnerabilityPullQueryTimestamp timestamp, List<Issues.TaintVulnerabilityLite> issues) {
      this.timestamp = timestamp;
      this.issues = issues;
    }

    public Issues.TaintVulnerabilityPullQueryTimestamp getTimestamp() {
      return timestamp;
    }

    public List<Issues.TaintVulnerabilityLite> getTaintIssues() {
      return issues;
    }
  }

  public static class ServerIssueDetails {
    public final String key;
    public final String ruleKey;
    public final String codeSnippet;
    public final String creationDate;
    public final String message;
    public final Path path;
    public final Common.TextRange textRange;
    public final List<Common.Flow> flowList;
    public final List<Component> componentsList;

    public ServerIssueDetails(Issue issue, Path path, List<Component> componentsList, String codeSnippet) {
      this.key = issue.getKey();
      this.ruleKey = issue.getRule();
      this.textRange = issue.getTextRange();
      this.path = path;
      this.flowList = issue.getFlowsList();
      this.message = issue.getMessage();
      this.creationDate = issue.getCreationDate();
      this.componentsList = componentsList;
      this.codeSnippet = codeSnippet;
    }
  }

  private static class IssueAnticipatedTransition {
    public final String filePath;
    public final Integer line;
    public final String hash;
    public final String ruleKey;
    public final String issueMessage;
    public final String transition;
    public final String comment;

    private IssueAnticipatedTransition(String filePath, @Nullable Integer line, @Nullable String hash, String ruleKey, String issueMessage, String transition,
      @Nullable String comment) {
      this.filePath = filePath;
      this.line = line;
      this.hash = hash;
      this.ruleKey = ruleKey;
      this.issueMessage = issueMessage;
      this.transition = transition;
      this.comment = comment;
    }
  }
}
