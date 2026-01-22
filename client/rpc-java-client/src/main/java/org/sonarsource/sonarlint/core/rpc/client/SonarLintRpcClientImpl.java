/*
ACR-9b3564ebcaf14f1cabb2c7c2ef146942
ACR-4b662baf0e4140a18a1d70942d69de65
ACR-214308905939402092f505e42e75284e
ACR-302bcef6a94d46c69a205e6bce24e596
ACR-b233c6f18dce436e99846b242aa5cf35
ACR-1afccf975aab4c0fa6b6597163a764d2
ACR-4a4af0f8196c4569a6a864696e3adb29
ACR-ec68bb38a5da4669a53826136aebe1bd
ACR-3376f3acaa9846afa362016d32e66061
ACR-364fe995e8f84c5d9326bed79d73cadf
ACR-7861cc2d807f4372bf427e6c64250ee3
ACR-1c6ea4a151b145da974acef130f05e1b
ACR-6a59d675401a494c802382f7ad05e4df
ACR-dc1a5846c053482da2226f6517bc9e13
ACR-246c7d67203c46d1a7337dc58cf93713
ACR-f3b3d3de2f664f7ca644127901ba184f
ACR-6f83a37de09e4739a7f32756e00656cd
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

/*ACR-fcf73df30d2745859d89a9d97020a087
ACR-c4dc31dad6094a4b8fec160e9fe0cd4f
ACR-b2e438810651471f90d47a15383affea
ACR-1ab17a8e532347c9a75d5466a726a35e
ACR-e424c23f8bb24518a2becd208c4fb8cd
ACR-757931f496c241b888c43a27d6ef3981
ACR-2f16dcf932d04919a59773992c26d0a0
ACR-b0451852f56e44b3911434e37ebac38e
ACR-eb5b7faf068d4713a743b608638dad6f
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
    //ACR-c3140415fbb84672b469858890804f38
    //ACR-ed314940201a44f086cfa5215ce9bd2e
    var sequentialFuture = start.thenApplyAsync(cancelChecker -> {
      //ACR-4db60c77c3ac4f77a1b13e37d7aeab36
      cancelChecker.checkCanceled();
      return cancelChecker;
    }, requestAndNotificationsSequentialExecutor);
    //ACR-1931face06ff4d3d9bdab5f8188f8da6
    var requestFuture = sequentialFuture.thenApplyAsync(cancelChecker -> {
      cancelChecker.checkCanceled();
      return code.apply(cancelChecker);
    }, requestsExecutor);
    start.complete(new CompletableFutures.FutureCancelChecker(requestFuture));
    return requestFuture;
  }

  protected CompletableFuture<Void> runAsync(Consumer<CancelChecker> code) {
    CompletableFuture<CancelChecker> start = new CompletableFuture<>();
    //ACR-b442636977f548f99ba845a92d1cacf1
    //ACR-052915afcc5a4138bc467829832ec0d2
    var sequentialFuture = start.thenApplyAsync(cancelChecker -> {
      //ACR-89b628057854488e83f5e8e1b74fb242
      cancelChecker.checkCanceled();
      return cancelChecker;
    }, requestAndNotificationsSequentialExecutor);
    //ACR-3fa41586f602487c8cc4e525572d2809
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

  /*ACR-c0600756b6a84fc892755fa815a22653
ACR-c4e198426f5b40bfbbae45dc9650bdfd
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
