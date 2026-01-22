/*
ACR-e2aaf6ee321446c1a1dce007abe907d3
ACR-c5f97a47bdea479e97432b59a04261f5
ACR-e54e8dab858c4623b3ff003a0567ebf3
ACR-e965068916ca49ac8516ccb27a6a4545
ACR-5a4bc766d9e2481aa550dbff9a612355
ACR-a9ee40a33c9e4a65a119bb0119532124
ACR-6aabd433e892467188dab39c971258e0
ACR-c07d3467d3204c28956b0667ce10e186
ACR-b1fb250a0f6d4271ab27bbe819b8bfaa
ACR-b23e199a80cf4dd79aa14f9d289a62ae
ACR-b7f48ce54a0747d4955bc673a48140b3
ACR-c1958f246f574c45b28e6f4b246396b7
ACR-3c5d3fa2d1824a43979ca0f3a7f2b112
ACR-44ebf74d85a14478ac65bc75f884994b
ACR-91d1c2236b454c6cb7718fd67f5bd1ea
ACR-22b3cd8906844168997bb897b3b781b1
ACR-d16b710a65bc4377ba09082dac474fcb
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
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
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchProjectBranchResponse;
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

/*ACR-179b80f328424b788f57aef9582ec89f
ACR-0d51431dbdd244bc9eb4928367214250
 */
public interface SonarLintRpcClient {

  /*ACR-6b7e68eaebfc4126bea372a6bbabcc11
ACR-5b1f405d0b5a4de7ad462b81d1c62c07
ACR-b65031eb0e874e5b9b95e1fcd4e300ab
ACR-d04ad7c2e1a14a55a1d8c152985a89d5
   */
  @JsonNotification
  void suggestBinding(SuggestBindingParams params);

  /*ACR-370558bb52e54b27b666aeaf2709daa3
ACR-d3401e4a178845f1b4155f2d3621cc15
   */
  @JsonNotification
  void suggestConnection(SuggestConnectionParams params);

  @JsonNotification
  void openUrlInBrowser(OpenUrlInBrowserParams params);

  /*ACR-8ba5181ee809451d9ec6348869d2ffa1
ACR-d3682229241644c0884773437e839d26
ACR-b68fcfc2800c4f1b90f6ca4ddc07b369
   */
  @JsonNotification
  void showMessage(ShowMessageParams params);

  /*ACR-5d7b82c06e27485798ed2e0d6af83285
ACR-357a434eebb542969084893bdaabcc10
ACR-d3fe6db30fe34e87accfc2019aa6204f
ACR-3d2aef5f9a7e464090f685c459c5fa72
ACR-1ad8ef9e1e874253a1e4259b660e312c
ACR-cb595eebc9b74cbfa0b66af7ee1d7858
   */
  @JsonRequest
  default CompletableFuture<ShowMessageRequestResponse> showMessageRequest(ShowMessageRequestParams params) {
    return CompletableFuture.completedFuture(new ShowMessageRequestResponse(null));
  }

  @JsonNotification
  void log(LogParams params);

  /*ACR-306f774963dc45de974e93d6a9d4f071
ACR-9cc382ce98e6447dbb3777376dc4e704
ACR-015e20e7f20e41b9b8f9613867c11cc3
ACR-21cf37960a6e46e28c9da9d1ae563d93
ACR-86afe39b9c5b4658afdffdc784fd1670
   */
  @JsonNotification
  void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params);

  @JsonNotification
  void showSmartNotification(ShowSmartNotificationParams params);

  /*ACR-0eec246271064966bc9ceda4006c1430
ACR-da24340150634eeab30fc1de8677b3a7
ACR-3d5aa800972f46cf9e3c661b2f52cc91
ACR-012d5efc954949c2bad4ad97eb6f67be
   */
  @JsonRequest
  CompletableFuture<GetClientLiveInfoResponse> getClientLiveInfo();

  @JsonNotification
  void showHotspot(ShowHotspotParams params);

  /*ACR-6b6d5cb302f2498998aed3fc8ac25bea
ACR-451477ad9fc44147b7144f3516aeadf8
   */
  @JsonNotification
  void showIssue(ShowIssueParams params);

  /*ACR-b59f362838274259a84f4c475c2172dc
ACR-b5415dbe6ae54cbd9e60774894f9b673
   */
  @JsonNotification
  default void showFixSuggestion(ShowFixSuggestionParams params) {

  }

  /*ACR-5b682dce4fdd4a95b21d615e0f0a7f98
ACR-c06be184a5eb4576bc4e9caa0917597a
ACR-cf0a480231f14ab1bce162c332c4709b
ACR-39f2b519ab7440bcb6ae1e1fcb11335e
ACR-fbc1f4e09c914a0faeb8e7856908472c
   */
  @JsonRequest
  CompletableFuture<AssistCreatingConnectionResponse> assistCreatingConnection(AssistCreatingConnectionParams params);

  /*ACR-38afbbcf99544ad4884fda7814f335fb
ACR-e39e9c629ba84d03a0d761d5215b8280
ACR-02bee44044914f5fba93892c1dce4bc3
ACR-e6aff9b2f66342568ce9c4a78c69dbe0
ACR-6253b669af6d4916b8dd16e18248bf87
   */
  @JsonRequest
  CompletableFuture<AssistBindingResponse> assistBinding(AssistBindingParams params);

  /*ACR-c064faab07054f88851f933b78ea936b
ACR-4b04aa430a1b4c399505b692bdea3ee7
ACR-a24967bbad584aaea7817e1df2c9c56a
ACR-026fe521b6e24f2cbe231bab66cf2982
   */
  @JsonRequest
  CompletableFuture<Void> startProgress(StartProgressParams params);

  /*ACR-6844d213fa0b4f928686ebadc983a249
ACR-4b0e62940edb4193b63df703ba5532f8
   */
  @JsonNotification
  void reportProgress(ReportProgressParams params);

  @JsonNotification
  void didSynchronizeConfigurationScopes(DidSynchronizeConfigurationScopeParams params);

  /*ACR-5e8dc016aed04d98a7d6e302e3dafbd9
ACR-8bcddbab69604355bb3e464ace9f9f0b
   */
  @JsonRequest
  CompletableFuture<GetCredentialsResponse> getCredentials(GetCredentialsParams params);

  @JsonRequest
  CompletableFuture<TelemetryClientLiveAttributesResponse> getTelemetryLiveAttributes();

  @JsonRequest
  CompletableFuture<SelectProxiesResponse> selectProxies(SelectProxiesParams params);

  /*ACR-702ad8ac73044ff1999ff2193f639e14
ACR-4f8e6970a95545b8a6f9d0ea10d96c0b
   */
  @JsonRequest
  CompletableFuture<GetProxyPasswordAuthenticationResponse> getProxyPasswordAuthentication(GetProxyPasswordAuthenticationParams params);

  @JsonRequest
  CompletableFuture<CheckServerTrustedResponse> checkServerTrusted(CheckServerTrustedParams params);

  /*ACR-ce56dcec7b17450581c4e89abbbe1c2d
ACR-6763b7e9b3954e489cfb210f5fc24db2
   */
  @Deprecated(since = "10.3")
  @JsonNotification
  void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params);

  @JsonRequest
  CompletableFuture<MatchSonarProjectBranchResponse> matchSonarProjectBranch(MatchSonarProjectBranchParams params);

  /*ACR-7760998851f448fe8f2ccf79b91c53f3
ACR-91ccebd9c4e741699dfd8e797829f0c2
   */
  @Deprecated(since = "10.23", forRemoval = true)
  @JsonRequest
  default CompletableFuture<MatchProjectBranchResponse> matchProjectBranch(MatchProjectBranchParams params) {
    return CompletableFuture.completedFuture(new MatchProjectBranchResponse(true));
  }

  @JsonNotification
  void didChangeMatchedSonarProjectBranch(DidChangeMatchedSonarProjectBranchParams params);

  /*ACR-2dc645210a394d0a89653dbe793ba92f
ACR-7447c7ad62c145ecac8f6f792510230f
ACR-353909e0e6d54a7fa5811e34ee9a5443
   */
  @JsonRequest
  CompletableFuture<GetBaseDirResponse> getBaseDir(GetBaseDirParams params);

  /*ACR-fd5541bcb67b4a14b3c67887a7d12fc7
ACR-7ef6899a17dc4af599c3d4e9d2aea9b7
   */
  @JsonRequest
  CompletableFuture<ListFilesResponse> listFiles(ListFilesParams params);

  /*ACR-e0ad4cbe8f34451f870c710b0da9905f
ACR-cf7e52c28dd5458f812ef8bf65a9707b
ACR-466a09b3d0d046eaaf9c966e1f55fc87
ACR-4f138431bdba491baf39c259cc55bea8
ACR-21c27b9bbde44638ba29cd7993a37839
ACR-41a2b7a3b418460ea8a9c3d61c6cc3e7
ACR-8d5839e72ca945f0a341374648df3ac7
ACR-0b865361c2a24ddc9448037eb387ca00
   */
  @JsonNotification
  void didChangeTaintVulnerabilities(DidChangeTaintVulnerabilitiesParams params);

  /*ACR-3d036b60b131417681e2606b5c600f64
ACR-20c2f164ec0f42939e25975f37ea0b3b
ACR-6b1e0a250abf47f4b7dc8712760ce63b
ACR-65e6ab2773b947dd9409d7898632ffe2
ACR-3877c9694bd341779a2c414f7e1b2933
   */
  @JsonNotification
  void didChangeDependencyRisks(DidChangeDependencyRisksParams params);

  @JsonNotification
  void noBindingSuggestionFound(NoBindingSuggestionFoundParams params);

  /*ACR-809ee7414ede43678abf1ad455b1a253
ACR-de541bc7c42842c28f14baa6faec9bff
ACR-0e4e70ac69f04c52a9c6396fe6101235
   */
  @JsonNotification
  void didChangeAnalysisReadiness(DidChangeAnalysisReadinessParams params);

  /*ACR-ab008ea23b324ab1a931d97162e921cc
ACR-ad1ef741ef8d40c091ad38df91549fab
ACR-e2d2b6e8c3da45ffb0a3cad54c2518fd
ACR-6f3e3e63d99c4bf8bbfe9a504ab6fdf3
ACR-c0eb9863fe80460e8779ae6378bc661d
ACR-4524028a9e234747aca09547a0a1c8f1
ACR-38bcb65de2ec430ebbe1fc9c46d81bd1
ACR-abe18bc866264bdd94d4d83feb34063e
ACR-dd2e87903e7e46d28478b70549cce4c4
ACR-ee6ff39bd3954717aecc6f38db789535
ACR-2ba9fff2751f4ef59526302dc32d4866
ACR-86092cb9398b4805958892a02965be3d
   */
  @JsonNotification
  default void raiseIssues(RaiseIssuesParams params) {
  }

  /*ACR-2e05fe70b2c04355a89b493aec1d0fb5
ACR-dfaf6d6d18ea4ddaa0eace6848f10507
ACR-40ecbba20a49400ca25840ae8279abdf
ACR-0920de3eeb804e70a55df2ed17838f56
ACR-26af8f33b72448159b787d8a458f0240
ACR-fb14eadb4ad14c1e8263306234e130e0
ACR-f5a6d0d587594ec7b518ac6d1b3d34c2
ACR-7e41b2893c9b4488acb1df01b33d5a97
ACR-53e4c14914f8490a82dcab8e901e5c46
ACR-2af818ceaea04a6e85dfcd3ed44a54de
ACR-609a461b246e4250bb9c225491240dc5
ACR-05540d21229a462a85ef7bc5d887323f
   */
  @JsonNotification
  default void raiseHotspots(RaiseHotspotsParams params) {
  }

  /*ACR-983b64cdd6d84f7d966210846be532ce
ACR-1a0dcd95950845c9a24bac3cfb95edd5
ACR-396179b3e8f54b11b1353a6099d46086
ACR-2959c77cea9b4d32b60bde50f4a48001
ACR-c2beffd561fd4dcabbe1f0d6b0a49c8e
ACR-e67801bfb8854d6286e9d29c53a16843
   */
  @JsonNotification
  default void didSkipLoadingPlugin(DidSkipLoadingPluginParams params) {
  }

  /*ACR-75ea7d75389a44779be8dd277621dbae
ACR-6525fd392a8f48cfba05617cb69baa10
ACR-8fd22fe37bbc46448129c5d3c9250b0a
ACR-6eaf7a3bfe4a459db2e8c7004c82f8e9
ACR-038dc983891c47fc902474c5a55be89b
   */
  @JsonNotification
  default void didDetectSecret(DidDetectSecretParams params) {
  }

  /*ACR-cfceed13b8c44e14a44e43438ef39c75
ACR-3cc2c8ad09d14375b75de01b739a2c6a
ACR-2279e6f5f58240eb9011ffd552fe2b72
ACR-c060294f770d4d5dbafe7730280b068a
ACR-aa082decedd84ca49490a7ff6421f042
   */
  @JsonNotification
  default void promoteExtraEnabledLanguagesInConnectedMode(PromoteExtraEnabledLanguagesInConnectedModeParams params) {
  }

  /*ACR-867259c5f4734ffc8ae52e3a4fb33847
ACR-9d87abf60d524829955c1e55d985b5a7
ACR-1477a99a89d445ec9e5baca7511fc46c
ACR-01c03a2cb72c4f28a05cd61bbd01be4b
ACR-8afb4767eaa34449947aad332f7d1d30
   */
  @JsonRequest
  CompletableFuture<GetInferredAnalysisPropertiesResponse> getInferredAnalysisProperties(GetInferredAnalysisPropertiesParams params);

  /*ACR-2f820a30437b4059afe5a9b34b0b6f3c
ACR-8b866fe76b0f4493989a35af4340ad76
ACR-0962590fee4c47d5b0fc3b8b86b2ce69
   */
  @JsonRequest
  CompletableFuture<GetFileExclusionsResponse> getFileExclusions(GetFileExclusionsParams params);

  @JsonNotification
  default void invalidToken(InvalidTokenParams params) {
  }

  @JsonNotification
  default void flightRecorderStarted(FlightRecorderStartedParams params) {
  }

  @JsonNotification
  default void embeddedServerStarted(EmbeddedServerStartedParams params) {
  }
}
