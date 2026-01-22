/*
ACR-c41f95bc59824f879db288ccbe798fdb
ACR-f40614ce4aea44299bca307bb5f79b78
ACR-8b9c9d4a0ec54748bfafc3702e878b83
ACR-9222e9f7c46d42adb595d460152b33ff
ACR-a6cb8862e0f244ccb3ad62a7ad3d4fde
ACR-6bffe933501f44d199f96ac5ed755842
ACR-50390d133b814c0a9eed2da81e37038d
ACR-1c494bc328074f96802296a24a6ab058
ACR-57bcf67529764f5196f955cf4eae13a4
ACR-98084564d63747b0bc5a168e53739b52
ACR-6d1ce74796ff4c0382f7cbd3fc187962
ACR-686eb385d5a6439aa35297c0ce4ee7db
ACR-73c998a302744fd6a79eaec740d182c4
ACR-5ff1d813ac0345ef801c262721178ecb
ACR-5f84d0e6d94e434ab89bc8aa7b487906
ACR-317adbfa628b4c04adad3cfbef1ae90b
ACR-50e690ef4e59435ebdff14786c350091
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Strings;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.embedded.server.RequestHandlerBindingAssistant;
import org.sonarsource.sonarlint.core.file.FilePathTranslation;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarCloudConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.ShowIssueParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.FlowDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.LocationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.serverapi.issue.IssueApi;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Issues;
import org.sonarsource.sonarlint.core.serverapi.rules.RulesApi;
import org.sonarsource.sonarlint.core.sync.SonarProjectBranchesSynchronizationService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ShowIssueRequestHandler implements HttpRequestHandler {

  private final SonarLintRpcClient client;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final TelemetryService telemetryService;
  private final RequestHandlerBindingAssistant requestHandlerBindingAssistant;
  private final PathTranslationService pathTranslationService;
  private final SonarCloudActiveEnvironment sonarCloudActiveEnvironment;
  private final SonarProjectBranchesSynchronizationService sonarProjectBranchesSynchronizationService;

  public ShowIssueRequestHandler(SonarLintRpcClient client, SonarQubeClientManager sonarQubeClientManager, TelemetryService telemetryService,
    RequestHandlerBindingAssistant requestHandlerBindingAssistant, PathTranslationService pathTranslationService, SonarCloudActiveEnvironment sonarCloudActiveEnvironment,
    SonarProjectBranchesSynchronizationService sonarProjectBranchesSynchronizationService) {
    this.client = client;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.telemetryService = telemetryService;
    this.requestHandlerBindingAssistant = requestHandlerBindingAssistant;
    this.pathTranslationService = pathTranslationService;
    this.sonarCloudActiveEnvironment = sonarCloudActiveEnvironment;
    this.sonarProjectBranchesSynchronizationService = sonarProjectBranchesSynchronizationService;
  }

  @Override
  public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext context) throws HttpException, IOException {
    var origin = AttributeUtils.getOrigin(context);
    var showIssueQuery = extractQuery(origin, AttributeUtils.getParams(context));
    if (!Method.GET.isSame(request.getMethod()) || !showIssueQuery.isValid()) {
      response.setCode(HttpStatus.SC_BAD_REQUEST);
      return;
    }
    telemetryService.showIssueRequestReceived();

    AssistCreatingConnectionParams serverConnectionParams = createAssistServerConnectionParams(showIssueQuery);

    requestHandlerBindingAssistant.assistConnectionAndBindingIfNeededAsync(
      serverConnectionParams,
      showIssueQuery.projectKey,
      origin,
      (connectionId, boundScopes, configScopeId, cancelMonitor) -> {
        if (configScopeId != null) {
          var branch = showIssueQuery.branch;
          if (branch == null) {
            branch = sonarProjectBranchesSynchronizationService.findMainBranch(connectionId, showIssueQuery.projectKey, cancelMonitor);
          }

          showIssueForScope(connectionId, configScopeId, showIssueQuery.issueKey, showIssueQuery.projectKey, branch,
            showIssueQuery.pullRequest, cancelMonitor);
        }
      });

    response.setCode(HttpStatus.SC_OK);
    response.setEntity(new StringEntity("OK"));
  }

  private static AssistCreatingConnectionParams createAssistServerConnectionParams(ShowIssueQuery query) {
    var tokenName = query.getTokenName();
    var tokenValue = query.getTokenValue();
    return query.isSonarCloud ?
      new AssistCreatingConnectionParams(new SonarCloudConnectionParams(query.getOrganizationKey(), tokenName, tokenValue,
        org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.valueOf(query.getRegion().name())))
      : new AssistCreatingConnectionParams(new SonarQubeConnectionParams(query.getServerUrl(), tokenName, tokenValue));
  }

  private void showIssueForScope(String connectionId, String configScopeId, String issueKey, String projectKey,
    String branch, @Nullable String pullRequest, SonarLintCancelMonitor cancelMonitor) {
    var issueDetailsOpt = tryFetchIssue(connectionId, issueKey, projectKey, branch, pullRequest, cancelMonitor);
    if (issueDetailsOpt.isPresent()) {
      pathTranslationService.getOrComputePathTranslation(configScopeId)
        .ifPresent(translation -> client.showIssue(getShowIssueParams(issueDetailsOpt.get(), connectionId, configScopeId, branch, pullRequest, translation, cancelMonitor)));
    } else {
      client.showMessage(new ShowMessageParams(MessageType.ERROR, "Could not show the issue. See logs for more details"));
    }
  }

  @VisibleForTesting
  ShowIssueParams getShowIssueParams(IssueApi.ServerIssueDetails issueDetails, String connectionId,
    String configScopeId, String branch, @Nullable String pullRequest, FilePathTranslation translation, SonarLintCancelMonitor cancelMonitor) {
    var flowLocations = issueDetails.flowList.stream().map(flow -> {
      var locations = flow.getLocationsList().stream().map(location -> {
        var locationComponent = issueDetails.componentsList.stream().filter(component -> component.getKey().equals(location.getComponent())).findFirst();
        var filePath = locationComponent.map(Issues.Component::getPath).orElse("");
        var locationTextRange = location.getTextRange();
        var codeSnippet = tryFetchCodeSnippet(connectionId, locationComponent.map(Issues.Component::getKey).orElse(""), locationTextRange, branch, pullRequest, cancelMonitor);
        var locationTextRangeDto = new TextRangeDto(locationTextRange.getStartLine(), locationTextRange.getStartOffset(),
          locationTextRange.getEndLine(), locationTextRange.getEndOffset());
        return new LocationDto(locationTextRangeDto, location.getMsg(), translation.serverToIdePath(Paths.get(filePath)), codeSnippet.orElse(""));
      }).toList();
      return new FlowDto(locations);
    }).toList();

    var textRange = issueDetails.textRange;
    var textRangeDto = new TextRangeDto(textRange.getStartLine(), textRange.getStartOffset(), textRange.getEndLine(),
      textRange.getEndOffset());

    var isTaint = isIssueTaint(issueDetails.ruleKey);

    return new ShowIssueParams(configScopeId, new IssueDetailsDto(textRangeDto, issueDetails.ruleKey, issueDetails.key, translation.serverToIdePath(issueDetails.path),
      issueDetails.message, issueDetails.creationDate, issueDetails.codeSnippet, isTaint, flowLocations));
  }

  static boolean isIssueTaint(String ruleKey) {
    return RulesApi.TAINT_REPOS.stream().anyMatch(ruleKey::startsWith);
  }

  private Optional<IssueApi.ServerIssueDetails> tryFetchIssue(String connectionId, String issueKey, String projectKey, String branch, @Nullable String pullRequest,
    SonarLintCancelMonitor cancelMonitor) {
    return sonarQubeClientManager.withActiveClientFlatMapOptionalAndReturn(connectionId,
      serverApi -> serverApi.issue().fetchServerIssue(issueKey, projectKey, branch, pullRequest, cancelMonitor));
  }

  private Optional<String> tryFetchCodeSnippet(String connectionId, String fileKey, Common.TextRange textRange, String branch, @Nullable String pullRequest,
    SonarLintCancelMonitor cancelMonitor) {
    return sonarQubeClientManager.withActiveClientFlatMapOptionalAndReturn(connectionId,
      api -> api.issue().getCodeSnippet(fileKey, textRange, branch, pullRequest, cancelMonitor));
  }

  @VisibleForTesting
  ShowIssueQuery extractQuery(String origin, Map<String, String> params) {
    boolean isSonarCloud = sonarCloudActiveEnvironment.isSonarQubeCloud(origin);
    String serverUrl;
    SonarCloudRegion region = null;
    if (isSonarCloud) {
      serverUrl = Strings.CS.removeEnd(origin, "/");
      region = sonarCloudActiveEnvironment.getRegionOrThrow(serverUrl);
    } else {
      serverUrl = params.get("server");
    }
    return new ShowIssueQuery(serverUrl, params.get("project"), params.get("issue"), params.get("branch"),
      params.get("pullRequest"), params.get("tokenName"), params.get("tokenValue"), params.get("organizationKey"), region, isSonarCloud);
  }

  @VisibleForTesting
  public static class ShowIssueQuery {

    private final String serverUrl;
    private final String projectKey;
    private final String issueKey;
    @Nullable
    private final String branch;
    @Nullable
    private final String pullRequest;
    @Nullable
    private final String tokenName;
    @Nullable
    private final String tokenValue;
    @Nullable
    private final String organizationKey;
    @Nullable
    private final SonarCloudRegion region;
    private final boolean isSonarCloud;

    public ShowIssueQuery(@Nullable String serverUrl, String projectKey, String issueKey, @Nullable String branch, @Nullable String pullRequest,
      @Nullable String tokenName, @Nullable String tokenValue, @Nullable String organizationKey, @Nullable SonarCloudRegion region, boolean isSonarCloud) {
      this.serverUrl = serverUrl;
      this.projectKey = projectKey;
      this.issueKey = issueKey;
      this.branch = branch;
      this.pullRequest = pullRequest;
      this.tokenName = tokenName;
      this.tokenValue = tokenValue;
      this.organizationKey = organizationKey;
      this.region = region != null ? region : SonarCloudRegion.EU;
      this.isSonarCloud = isSonarCloud;
    }

    public boolean isValid() {
      return isNotBlank(projectKey) && isNotBlank(issueKey)
        && (isSonarCloud || isNotBlank(serverUrl))
        && (!isSonarCloud || isNotBlank(organizationKey))
        && isPullRequestParamValid() && isTokenValid();
    }

    public boolean isPullRequestParamValid() {
      if (pullRequest != null) {
        return isNotEmpty(pullRequest);
      }
      return true;
    }

    /*ACR-6759a60e47e34eabbc8ccf0c3f589d7a
ACR-54230fb4d066413f88fe8c19b901fe01
     */
    public boolean isTokenValid() {
      if (tokenName != null && tokenValue != null) {
        return isNotEmpty(tokenName) && isNotEmpty(tokenValue);
      }

      return tokenName == null && tokenValue == null;
    }

    public String getServerUrl() {
      return serverUrl;
    }

    public String getProjectKey() {
      return projectKey;
    }

    @Nullable
    public String getOrganizationKey() {
      return organizationKey;
    }

    public String getIssueKey() {
      return issueKey;
    }

    @Nullable
    public String getBranch() {
      return branch;
    }

    @Nullable
    public String getPullRequest() {
      return pullRequest;
    }

    @Nullable
    public String getTokenName() {
      return tokenName;
    }

    @Nullable
    public String getTokenValue() {
      return tokenValue;
    }

    @Nullable
    public SonarCloudRegion getRegion() {
      return region;
    }
  }

}
