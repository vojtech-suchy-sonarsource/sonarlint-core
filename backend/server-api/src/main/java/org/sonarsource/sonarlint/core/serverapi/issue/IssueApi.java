/*
ACR-d763a2819a324e87bc388e137b9c1190
ACR-c917ffd6d0cf4ccea1c80e3c40f99096
ACR-31d5c606159c4e4f801c837bc87a4861
ACR-73460b3781b24bc6b248d3532fe3d57d
ACR-897419393813413099424794b833cb8f
ACR-437ccafa62f34867b8e83f88bf69b57d
ACR-2487e8c346c948179c2e49702973026c
ACR-523985e010a140c8b1790308b843ae27
ACR-cadcbe111b1643eaa25054f2f7faddf0
ACR-4bbb814551664986bd0b9d19b14c3e5c
ACR-53f08c5bfda84f9c8205bb36475cd645
ACR-be0fc1159f304148b4455ebfb7a375b5
ACR-ebf569dcd1974d3ba70f560a77e49e1e
ACR-7b557ce885a74585b8abb3f99abd2fae
ACR-afbd281718464d8d8bc141becbcadb43
ACR-68184e23401a40968567b9c72b95225c
ACR-50da67f6e2dc402f8863c27941d46e94
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

  /*ACR-f8f53bd854384e119ec28b8aeac1e377
ACR-67343cba3184463d8a641e5fc22fb70c
ACR-8dceb8f3d8c44ec8bc376ad74e328cc4
ACR-b415efc5af9e4fc7b2add7c2a865d659
ACR-b7bd48c913f042b8aae93f1435b182f8
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
        //ACR-11a2770e986743c096f1775990926fb2
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
      //ACR-f23dea7e2488483e863fd871d7e005c3
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
