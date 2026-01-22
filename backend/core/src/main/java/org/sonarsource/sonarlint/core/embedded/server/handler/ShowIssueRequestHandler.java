/*
ACR-b8789f61082a4dd18d157ffd4a41c051
ACR-72b2cb09d11d4346b3c2277140cb52f5
ACR-3a288ce7178142a691af6e037e8add7f
ACR-ccc7ce4b595c4ab6902613019e2b528e
ACR-b052f25b93a04ac283743c2bd6229abf
ACR-ddf36299b6fc497fb3784cb6ab0d45fa
ACR-a27be92abeb548f7b4baf66b69fe1c2d
ACR-dbc53a37211141509b1e2746978dcc88
ACR-40211f3a6cf8469b9795fb3889bd7a06
ACR-f15164745f9d4822beb625beabdc9828
ACR-15b2541e31f348caa4474e26808b2255
ACR-d5e35a664d4941f2b4fccc945ab1c915
ACR-54ddd443d63e400c93e8617326e2230d
ACR-7f36c84f8b064258a9756e9ceedb29bb
ACR-1b830fd8fa6a4c238d8e944cf0a143dc
ACR-ce5c1897a0b94ccb936d8ffbc70b0238
ACR-06e49bec04b44eb28436662c46ccf89f
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

    /*ACR-406b32acf42d436f9b060de11e20f570
ACR-e94914f0e3e94073abcad02376dd5b6e
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
