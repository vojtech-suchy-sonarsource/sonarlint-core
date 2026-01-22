/*
ACR-aba176fc63bb4dafb1dc18f4f67ce7c2
ACR-8679421c71314f329714106a0fd26346
ACR-166ca1a6f62145f99fa076be9540be25
ACR-2c417bd1f6df45b7baeb954d7a935509
ACR-d72c85e868e74a19b156a19c3c7f6b53
ACR-8ad899eb34dd4606afda91bebcacb9fa
ACR-9dcb389785b74d0aba069372d8cca8d9
ACR-cef3441db45746a3b2e3680cf678884c
ACR-3a82debb2d8f4142a8a92060cc3c511c
ACR-d1fd471b36b447fe9e0a78aeb832a0df
ACR-245c75db72774eba80529bd039e95eae
ACR-72dc5c16cbab47a893cf4fac987502b4
ACR-6b696a2b1e6143ee9de7bcfc791e2ed5
ACR-a26d0f0f2123484f977f88ebfba46944
ACR-4fae11883b90418cac70a4f41bf476ce
ACR-82d8342765e44268ae9ef7e932de19a6
ACR-f7d0ebccec9e4df096bd31d9c386653c
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.client.OpenUrlInBrowserParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.DidChangeAnalysisReadinessParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.DidDetectSecretParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetFileExclusionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetFileExclusionsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetInferredAnalysisPropertiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.analysis.GetInferredAnalysisPropertiesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.NoBindingSuggestionFoundParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.SuggestBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.DidChangeMatchedSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchSonarProjectBranchResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetCredentialsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SuggestConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.embeddedserver.EmbeddedServerStartedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.event.DidReceiveServerHotspotEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.ShowFixSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.flightrecorder.FlightRecorderStartedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.GetBaseDirParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.GetBaseDirResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.ListFilesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fs.ListFilesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaiseHotspotsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.ShowHotspotParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.CheckServerTrustedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.CheckServerTrustedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.SelectProxiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.SelectProxiesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.info.GetClientLiveInfoResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaiseIssuesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.ShowIssueParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageRequestParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageRequestResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowSoonUnsupportedMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.plugin.DidSkipLoadingPluginParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ReportProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.StartProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.promotion.PromoteExtraEnabledLanguagesInConnectedModeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sca.DidChangeDependencyRisksParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification.ShowSmartNotificationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sync.DidSynchronizeConfigurationScopeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sync.InvalidTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.taint.vulnerability.DidChangeTaintVulnerabilitiesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;

/*ACR-3d1a90c9529545118b477ea8e3a6e96a
ACR-807be9d033ca4e37b8cf95f39115a37c
ACR-37a27a9feae24718b73872bc8a65683c
ACR-8a156bc95ed84bdea762fb9942bde2ff
ACR-f93b1057cc664c54aa6012beefafc67f
ACR-6f042316f4e448878f1b681ecb8ef5bb
ACR-02bf2e881dfb41aeab512e28e0da5a77
ACR-0ae855bfaf5c485fab535229bad66224
ACR-e0c8be8fe9ca4c8290b5c6d3e52c66d5
 */
public class SonarLintRpcClientImpl implements SonarLintRpcClient {

  private final SonarLintRpcClientDelegate delegate;
  private final Executor requestsExecutor;
  private final Executor requestAndNotificationsSequentialExecutor;

  public SonarLintRpcClientImpl(SonarLintRpcClientDelegate delegate, Executor requestsExecutor, Executor requestAndNotificationsSequentialExecutor) {
    this.delegate = delegate;
    this.requestsExecutor = requestsExecutor;
    this.requestAndNotificationsSequentialExecutor = requestAndNotificationsSequentialExecutor;
  }

  protected <R> CompletableFuture<R> requestAsync(Function<CancelChecker, R> code) {
    CompletableFuture<CancelChecker> start = new CompletableFuture<>();
    //ACR-aef64b54a8624516839512ecea99229d
    //ACR-deb6087135e74bf0b36cd5af20e8145a
    var sequentialFuture = start.thenApplyAsync(cancelChecker -> {
      //ACR-eb2a459f15d34ca6bc2561d17eeb18d9
      cancelChecker.checkCanceled();
      return cancelChecker;
    }, requestAndNotificationsSequentialExecutor);
    //ACR-61a3753a54b34a1db53cc265b5232f9f
    var requestFuture = sequentialFuture.thenApplyAsync(cancelChecker -> {
      cancelChecker.checkCanceled();
      return code.apply(cancelChecker);
    }, requestsExecutor);
    start.complete(new CompletableFutures.FutureCancelChecker(requestFuture));
    return requestFuture;
  }

  protected CompletableFuture<Void> runAsync(Consumer<CancelChecker> code) {
    CompletableFuture<CancelChecker> start = new CompletableFuture<>();
    //ACR-7db680f48ba24e138146412de344f1e5
    //ACR-e6dc13e8d23b439a8a10d015d951ce00
    var sequentialFuture = start.thenApplyAsync(cancelChecker -> {
      //ACR-06f86756c7ce4633b397a64cd5bb047f
      cancelChecker.checkCanceled();
      return cancelChecker;
    }, requestAndNotificationsSequentialExecutor);
    //ACR-dab19c8a8296467a9943c9545599641f
    var requestFuture = sequentialFuture.<Void>thenApplyAsync(cancelChecker -> {
      cancelChecker.checkCanceled();
      code.accept(cancelChecker);
      return null;
    }, requestsExecutor);
    start.complete(new CompletableFutures.FutureCancelChecker(requestFuture));
    return requestFuture;
  }

  protected void notify(Runnable code) {
    requestAndNotificationsSequentialExecutor.execute(() -> {
      try {
        code.run();
      } catch (Throwable throwable) {
        logClientSideError("Error when handling a notification", throwable);
      }
    });
  }

  /*ACR-2f309c4fa3b84fe197a270e769bac39e
ACR-ffb610d138634d0287542fd2b10467b8
   */
  void logClientSideError(String message, Throwable throwable) {
    delegate.log(new LogParams(LogLevel.ERROR, message, null, stackTraceToString(throwable), Instant.now()));
  }

  private static String stackTraceToString(Throwable t) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  @Override
  public void suggestBinding(SuggestBindingParams params) {
    notify(() -> delegate.suggestBinding(params.getSuggestions()));
  }

  @Override
  public void suggestConnection(SuggestConnectionParams params) {
    notify(() -> delegate.suggestConnection(params.getSuggestionsByConfigScopeId()));
  }

  @Override
  public void openUrlInBrowser(OpenUrlInBrowserParams params) {
    notify(() -> {
      try {
        delegate.openUrlInBrowser(new URL(params.getUrl()));
      } catch (MalformedURLException e) {
        throw new ResponseErrorException(new ResponseError(ResponseErrorCode.InvalidParams, "Not a valid URL: " + params.getUrl(), params.getUrl()));
      }
    });
  }

  @Override
  public void showMessage(ShowMessageParams params) {
    notify(() -> delegate.showMessage(params.getType(), params.getText()));
  }

  @Override
  public CompletableFuture<ShowMessageRequestResponse> showMessageRequest(ShowMessageRequestParams params) {
    return requestAsync(cancelChecker -> delegate.showMessageRequest(params.getType(), params.getMessage(), params.getActions()));
  }

  @Override
  public void log(LogParams params) {
    notify(() -> delegate.log(params));
  }

  @Override
  public void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params) {
    notify(() -> delegate.showSoonUnsupportedMessage(params));
  }

  @Override
  public void showSmartNotification(ShowSmartNotificationParams params) {
    notify(() -> delegate.showSmartNotification(params));
  }

  @Override
  public CompletableFuture<GetClientLiveInfoResponse> getClientLiveInfo() {
    return requestAsync(cancelChecker -> new GetClientLiveInfoResponse(delegate.getClientLiveDescription()));
  }

  @Override
  public void showHotspot(ShowHotspotParams params) {
    notify(() -> delegate.showHotspot(params.getConfigurationScopeId(), params.getHotspotDetails()));
  }

  @Override
  public void showIssue(ShowIssueParams params) {
    notify(() -> delegate.showIssue(params.getConfigurationScopeId(), params.getIssueDetails()));
  }

  @Override
  public void showFixSuggestion(ShowFixSuggestionParams params) {
    notify(() -> delegate.showFixSuggestion(params.getConfigurationScopeId(), params.getIssueKey(), params.getFixSuggestion()));
  }

  @Override
  public CompletableFuture<AssistCreatingConnectionResponse> assistCreatingConnection(AssistCreatingConnectionParams params) {
    return requestAsync(cancelChecker -> delegate.assistCreatingConnection(params, new SonarLintCancelChecker(cancelChecker)));
  }

  @Override
  public CompletableFuture<AssistBindingResponse> assistBinding(AssistBindingParams params) {
    return requestAsync(cancelChecker -> delegate.assistBinding(params, new SonarLintCancelChecker(cancelChecker)));
  }

  @Override
  public CompletableFuture<Void> startProgress(StartProgressParams params) {
    return runAsync(cancelChecker -> {
      try {
        delegate.startProgress(params);
      } catch (UnsupportedOperationException e) {
        throw new ResponseErrorException(new ResponseError(SonarLintRpcErrorCode.PROGRESS_CREATION_FAILED, e.getMessage(), null));
      }
    });
  }

  @Override
  public void reportProgress(ReportProgressParams params) {
    notify(() -> delegate.reportProgress(params));
  }

  @Override
  public void didSynchronizeConfigurationScopes(DidSynchronizeConfigurationScopeParams params) {
    notify(() -> delegate.didSynchronizeConfigurationScopes(params.getConfigurationScopeIds()));
  }

  @Override
  public CompletableFuture<GetCredentialsResponse> getCredentials(GetCredentialsParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new GetCredentialsResponse(delegate.getCredentials(params.getConnectionId()));
      } catch (ConnectionNotFoundException e) {
        throw new ResponseErrorException(
          new ResponseError(SonarLintRpcErrorCode.CONNECTION_NOT_FOUND, "Unknown connection: " + params.getConnectionId(), params.getConnectionId()));
      }
    });
  }

  @Override
  public CompletableFuture<TelemetryClientLiveAttributesResponse> getTelemetryLiveAttributes() {
    return requestAsync(cancelChecker -> delegate.getTelemetryLiveAttributes());
  }

  @Override
  public CompletableFuture<SelectProxiesResponse> selectProxies(SelectProxiesParams params) {
    return requestAsync(cancelChecker -> new SelectProxiesResponse(delegate.selectProxies(params.getUri())));
  }

  @Override
  public CompletableFuture<GetProxyPasswordAuthenticationResponse> getProxyPasswordAuthentication(GetProxyPasswordAuthenticationParams params) {
    return requestAsync(cancelChecker -> delegate.getProxyPasswordAuthentication(params.getHost(), params.getPort(), params.getProtocol(), params.getPrompt(), params.getScheme(),
      params.getTargetHost()));
  }

  @Override
  public CompletableFuture<CheckServerTrustedResponse> checkServerTrusted(CheckServerTrustedParams params) {
    return requestAsync(cancelChecker -> new CheckServerTrustedResponse(delegate.checkServerTrusted(params.getChain(), params.getAuthType())));
  }

  @Override
  public void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {
    notify(() -> delegate.didReceiveServerHotspotEvent(params));
  }

  @Override
  public CompletableFuture<MatchSonarProjectBranchResponse> matchSonarProjectBranch(MatchSonarProjectBranchParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new MatchSonarProjectBranchResponse(
          delegate.matchSonarProjectBranch(params.getConfigurationScopeId(), params.getMainSonarBranchName(),
            params.getAllSonarBranchesNames(), new SonarLintCancelChecker(cancelChecker)));
      } catch (ConfigScopeNotFoundException e) {
        throw configScopeNotFoundError(params.getConfigurationScopeId());
      }
    });
  }

  @Override
  public void didChangeMatchedSonarProjectBranch(DidChangeMatchedSonarProjectBranchParams params) {
    notify(() -> delegate.didChangeMatchedSonarProjectBranch(params.getConfigScopeId(), params.getNewMatchedBranchName()));
  }

  @Override
  public CompletableFuture<GetBaseDirResponse> getBaseDir(GetBaseDirParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new GetBaseDirResponse(delegate.getBaseDir(params.getConfigurationScopeId()));
      } catch (ConfigScopeNotFoundException e) {
        throw configScopeNotFoundError(params.getConfigurationScopeId());
      }
    });
  }

  @Override
  public CompletableFuture<ListFilesResponse> listFiles(ListFilesParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new ListFilesResponse(delegate.listFiles(params.getConfigScopeId()));
      } catch (ConfigScopeNotFoundException e) {
        throw configScopeNotFoundError(params.getConfigScopeId());
      }
    });
  }

  private static ResponseErrorException configScopeNotFoundError(String configScopeId) {
    return new ResponseErrorException(
      new ResponseError(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_FOUND, "Unknown config scope: " + configScopeId, configScopeId));
  }

  @Override
  public void didChangeTaintVulnerabilities(DidChangeTaintVulnerabilitiesParams params) {
    notify(() -> delegate.didChangeTaintVulnerabilities(params.getConfigurationScopeId(), params.getClosedTaintVulnerabilityIds(), params.getAddedTaintVulnerabilities(),
      params.getUpdatedTaintVulnerabilities()));
  }

  @Override
  public void didChangeDependencyRisks(DidChangeDependencyRisksParams params) {
    notify(() -> delegate.didChangeDependencyRisks(params.getConfigurationScopeId(), params.getClosedDependencyRiskIds(), params.getAddedDependencyRisks(),
      params.getUpdatedDependencyRisks()));
  }

  @Override
  public void noBindingSuggestionFound(NoBindingSuggestionFoundParams params) {
    notify(() -> delegate.noBindingSuggestionFound(params));
  }

  public void didChangeAnalysisReadiness(DidChangeAnalysisReadinessParams params) {
    notify(() -> delegate.didChangeAnalysisReadiness(params.getConfigurationScopeIds(), params.areReadyForAnalysis()));
  }

  @Override
  public void raiseIssues(RaiseIssuesParams params) {
    notify(() -> delegate.raiseIssues(params.getConfigurationScopeId(), params.getIssuesByFileUri(), params.isIntermediatePublication(), params.getAnalysisId()));
  }

  @Override
  public void raiseHotspots(RaiseHotspotsParams params) {
    notify(() -> delegate.raiseHotspots(params.getConfigurationScopeId(), params.getHotspotsByFileUri(), params.isIntermediatePublication(), params.getAnalysisId()));
  }

  @Override
  public void didSkipLoadingPlugin(DidSkipLoadingPluginParams params) {
    notify(() -> delegate.didSkipLoadingPlugin(params.getConfigurationScopeId(), params.getLanguage(), params.getReason(), params.getMinVersion(), params.getCurrentVersion()));
  }

  @Override
  public void didDetectSecret(DidDetectSecretParams params) {
    notify(() -> delegate.didDetectSecret(params.getConfigurationScopeId()));
  }

  @Override
  public void promoteExtraEnabledLanguagesInConnectedMode(PromoteExtraEnabledLanguagesInConnectedModeParams params) {
    notify(() -> delegate.promoteExtraEnabledLanguagesInConnectedMode(params.getConfigurationScopeId(), params.getLanguagesToPromote()));
  }

  @Override
  public CompletableFuture<GetInferredAnalysisPropertiesResponse> getInferredAnalysisProperties(GetInferredAnalysisPropertiesParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new GetInferredAnalysisPropertiesResponse(delegate.getInferredAnalysisProperties(params.getConfigurationScopeId(), params.getFilesToAnalyze()));
      } catch (ConfigScopeNotFoundException e) {
        throw configScopeNotFoundError(params.getConfigurationScopeId());
      }
    });
  }

  @Override
  public CompletableFuture<GetFileExclusionsResponse> getFileExclusions(GetFileExclusionsParams params) {
    return requestAsync(cancelChecker -> {
      try {
        return new GetFileExclusionsResponse(delegate.getFileExclusions(params.getConfigurationScopeId()));
      } catch (ConfigScopeNotFoundException e) {
        throw configScopeNotFoundError(params.getConfigurationScopeId());
      }
    });
  }

  @Override
  public void invalidToken(InvalidTokenParams params) {
    notify(() -> delegate.invalidToken(params.getConnectionId()));
  }

  @Override
  public void flightRecorderStarted(FlightRecorderStartedParams params) {
    notify(() -> delegate.flightRecorderStarted(params));
  }

  @Override
  public void embeddedServerStarted(EmbeddedServerStartedParams params) {
    notify(() -> delegate.embeddedServerStarted(params));
  }
}
