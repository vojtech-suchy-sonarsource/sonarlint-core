/*
ACR-e92aa4f134eb4a9ca86038b6096fddb1
ACR-66470139db6646de9fa796273630f9c6
ACR-39f54cd1529b4420ba42b35575aa1942
ACR-376ee0dbb34d497a919542f9cbad253c
ACR-41cd0beaf29d456b88c810ad4f34dbde
ACR-a278ad2dce70415aa29a939f47263a8d
ACR-ef812f09c9c14b278d6286b04b446d53
ACR-9f8d230dbd184842a057d5cebed60d4c
ACR-5a8ecf1ff96043b584e75ffc4b2a43e0
ACR-564b837dc8c542cd98f364d8b911bffa
ACR-10122116516f4c92b981f30b54184e4d
ACR-cf0a5809d82443108ab86b5471eb5074
ACR-9be84b810c824c6da7cddf98f9b69bcf
ACR-7448d496996e4aea9411f2f7e4855b14
ACR-05637540ed4a4acd8c26ba10fdba0ed1
ACR-42d22b2d495843b089b2376b32d5cec4
ACR-3ce0d6f4aac24f7f99a32053ee9e9c2b
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

/*ACR-33c7585206d54dfb839b5b56ff9ee1e0
ACR-f091a08a4aeb49fa9f0d6ae39f472d7a
 */
public interface SonarLintRpcClient {

  /*ACR-24735b4f31dd4a3f93833c2bcd2b13f6
ACR-4266d9fc2feb45cda5c98d1649278e74
ACR-707e9a42a4a2437f9a8b3bd5096f3378
ACR-7465e16e96654ac7aa0d2a5887831f1f
   */
  @JsonNotification
  void suggestBinding(SuggestBindingParams params);

  /*ACR-3316089f8f3f416ca32c16a190cf9f6e
ACR-584d32e1176e4a8f8bb8133b40454651
   */
  @JsonNotification
  void suggestConnection(SuggestConnectionParams params);

  @JsonNotification
  void openUrlInBrowser(OpenUrlInBrowserParams params);

  /*ACR-e98804549b9748f2ab8785c2e8ff81d4
ACR-57faeb8ec8444b559a8aabb8b45b4e1b
ACR-765f79b9d2634d92bbe1a76732d3ce6d
   */
  @JsonNotification
  void showMessage(ShowMessageParams params);

  /*ACR-15dc373c55354eaaa7a79f0fe97152d4
ACR-833c08432736465a943e531912ba35c1
ACR-583fc4ed2d424e4f9c2992531329c84a
ACR-e179157b27c54115a6cde5fc66d05882
ACR-0ee7f7758f9b4bee8e4c856d3d8ab435
ACR-5b18e8a1d44b4e7eb86d3167a4e03ba6
   */
  @JsonRequest
  default CompletableFuture<ShowMessageRequestResponse> showMessageRequest(ShowMessageRequestParams params) {
    return CompletableFuture.completedFuture(new ShowMessageRequestResponse(null));
  }

  @JsonNotification
  void log(LogParams params);

  /*ACR-8e0684b07ca2464c9424b0b2cddb907d
ACR-3df6c6e196d040ab8a0fbae8e3bd6a35
ACR-5071df02bd1640fea1b6abe7a68b8f8f
ACR-b63b86d0a65f4d39a2fbb639ad534b64
ACR-2492fda0f7464bdc85916d1a900ee6b6
   */
  @JsonNotification
  void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params);

  @JsonNotification
  void showSmartNotification(ShowSmartNotificationParams params);

  /*ACR-e138158c81264c28860316052f05ed8e
ACR-75f4a755e1064fe1be8af84ff3a2ccc0
ACR-8aa55cbf4e1f4b2dbe05a05519fd146e
ACR-859364844f724df5ae17a72cb151f3f2
   */
  @JsonRequest
  CompletableFuture<GetClientLiveInfoResponse> getClientLiveInfo();

  @JsonNotification
  void showHotspot(ShowHotspotParams params);

  /*ACR-c675b83ed90f445ca1256f172a888c0d
ACR-055116f92dd142a0af1c0b8a2a9860ce
   */
  @JsonNotification
  void showIssue(ShowIssueParams params);

  /*ACR-f0ea8cac96c64de788015af433df609d
ACR-431f65853a8d427da341ab7349903adc
   */
  @JsonNotification
  default void showFixSuggestion(ShowFixSuggestionParams params) {

  }

  /*ACR-14da07dff3db47e8b3d2f0f9f2f07684
ACR-ccf0b57e6e4b4de6bbfdec83dd986cd8
ACR-6a21161c148a46ca8f8b6fe0ea16abff
ACR-320df244b4df4f339e100209bc8a719d
ACR-fe510901609542cd9acecaed81b7083b
   */
  @JsonRequest
  CompletableFuture<AssistCreatingConnectionResponse> assistCreatingConnection(AssistCreatingConnectionParams params);

  /*ACR-22f8ffdb6fe04f9b9d17efa71b0a96ba
ACR-7cfa55de0a0b4e2fa18a663a0a4d2a65
ACR-c1e71becbf8248feb24e5d47dfd6fd72
ACR-9cf9aceb65c5419e8b71f2beedd1b33d
ACR-785d43c133284dc68915e00d2fe620ea
   */
  @JsonRequest
  CompletableFuture<AssistBindingResponse> assistBinding(AssistBindingParams params);

  /*ACR-a9ee85237c3d484e96215c4ba36c179a
ACR-54973cf901604e869b8197279f2bea8b
ACR-5792f2318f9e449a9cd77b172892ef43
ACR-c03196bf0801439c9f14de64ef97de83
   */
  @JsonRequest
  CompletableFuture<Void> startProgress(StartProgressParams params);

  /*ACR-4caa17d85b774fdb97ddd404c7b7ba68
ACR-5a1e29f5990446b993e5bfc9d35e5843
   */
  @JsonNotification
  void reportProgress(ReportProgressParams params);

  @JsonNotification
  void didSynchronizeConfigurationScopes(DidSynchronizeConfigurationScopeParams params);

  /*ACR-367545f6d2b246bcb5c5d62793ae88ea
ACR-3b829c2726404e32a50c9e5d3e516093
   */
  @JsonRequest
  CompletableFuture<GetCredentialsResponse> getCredentials(GetCredentialsParams params);

  @JsonRequest
  CompletableFuture<TelemetryClientLiveAttributesResponse> getTelemetryLiveAttributes();

  @JsonRequest
  CompletableFuture<SelectProxiesResponse> selectProxies(SelectProxiesParams params);

  /*ACR-8fc50790be334bee8ed7006e3c8ff6a5
ACR-8a3560cca37942dd8b3d1ff46b03e31c
   */
  @JsonRequest
  CompletableFuture<GetProxyPasswordAuthenticationResponse> getProxyPasswordAuthentication(GetProxyPasswordAuthenticationParams params);

  @JsonRequest
  CompletableFuture<CheckServerTrustedResponse> checkServerTrusted(CheckServerTrustedParams params);

  /*ACR-56088e89c53e4c6dbeefd57b242ff5b8
ACR-50d405cc15494f85ad63ce6367e622f7
   */
  @Deprecated(since = "10.3")
  @JsonNotification
  void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params);

  @JsonRequest
  CompletableFuture<MatchSonarProjectBranchResponse> matchSonarProjectBranch(MatchSonarProjectBranchParams params);

  /*ACR-4e10a4e3fbf443fcae5fba1475c0e913
ACR-e45e14fc0be8494d9d8c77534e090861
   */
  @Deprecated(since = "10.23", forRemoval = true)
  @JsonRequest
  default CompletableFuture<MatchProjectBranchResponse> matchProjectBranch(MatchProjectBranchParams params) {
    return CompletableFuture.completedFuture(new MatchProjectBranchResponse(true));
  }

  @JsonNotification
  void didChangeMatchedSonarProjectBranch(DidChangeMatchedSonarProjectBranchParams params);

  /*ACR-4d58bb7c06fd422ebbfa611e615f5d3d
ACR-446a2ba486e24a11b60b11463c3df9a2
ACR-c3a1e19aa23d4a2a960b29016324cc5a
   */
  @JsonRequest
  CompletableFuture<GetBaseDirResponse> getBaseDir(GetBaseDirParams params);

  /*ACR-92445692930a4b7cbb0f0a76443ba297
ACR-d2f825f58a1d4b3386293578360a16cf
   */
  @JsonRequest
  CompletableFuture<ListFilesResponse> listFiles(ListFilesParams params);

  /*ACR-4cbf232ee06047e4bae1e937b1917037
ACR-6f87af0bbf8945609988d43de6ec5a2a
ACR-7ac4da5955f7496bade77ea5b897a01c
ACR-433f1fbcc37b4c1a93e6535a645ca4cf
ACR-4669f0006df24ed1b54ad18e5ec36b7a
ACR-1af08de5237548f1bc0fd3fb95ee21f9
ACR-6cbd4b79406b4bf0b1588df516d3bda1
ACR-120926f491d14cda8f1907ae3bcca6b5
   */
  @JsonNotification
  void didChangeTaintVulnerabilities(DidChangeTaintVulnerabilitiesParams params);

  /*ACR-7566d960a3f1458995cb2f27955e20aa
ACR-eee93dbc8f4a410db5abb22f50d60997
ACR-3c076feeb50842c0a52998dfd8a55a8a
ACR-a43ece056d8b4b519fcc63815fad8a7c
ACR-d1cbd80ce4784264ad52399f0ae73209
   */
  @JsonNotification
  void didChangeDependencyRisks(DidChangeDependencyRisksParams params);

  @JsonNotification
  void noBindingSuggestionFound(NoBindingSuggestionFoundParams params);

  /*ACR-ca7f65f23c734aab91f0d450ac846c33
ACR-036eb912d6914b2296d2d3a6c22cff11
ACR-31c9ce2f212640068f852e5c3ca14e7c
   */
  @JsonNotification
  void didChangeAnalysisReadiness(DidChangeAnalysisReadinessParams params);

  /*ACR-0f1e920738ea42b580efa9da4380454b
ACR-4260af348ec7447894b0da0452f54213
ACR-cf600cc1232b4fddbfc503d3aaab67b9
ACR-e2db611e390c4ccd81983973db9abb7e
ACR-5d53d4f69bda4636856fe59492db01e1
ACR-525d691958f8452f8dac530832eacbdb
ACR-87901084f2a2424ab350182f3f16ee2f
ACR-4a04aebe79e041e483eff6f497ebdd91
ACR-9b3700197a264065ae9d3b3993b494cb
ACR-8e74d0c667414da682d076933a453fd7
ACR-c1e8c6b956ec43b0886f62bb82d4a037
ACR-effb43ffbce44e17814869c76413e895
   */
  @JsonNotification
  default void raiseIssues(RaiseIssuesParams params) {
  }

  /*ACR-625b38fc3464499d9775ad9461d3f636
ACR-e9cfbec11c33428f98a396301fdb774f
ACR-fabfb9d1f1794722a07d7dcff379a7f2
ACR-25009dcf09194221a84df08089768201
ACR-19cfa8dcbcef44aa94c63d06ac1ac9b1
ACR-a1514c0db15d4fb3957c6924622173fe
ACR-e74d922d166e4989ab4a2090710f1a47
ACR-7e8014eb382544038472305a454d2917
ACR-afe71c96872e4945961aa6120d4fec3b
ACR-f71ba896cf0a41309b2c20a8d30a4c83
ACR-8b91625586dd460ebfe87a47036d060c
ACR-2d1c4f838ce3411fb539aa6f19de8418
   */
  @JsonNotification
  default void raiseHotspots(RaiseHotspotsParams params) {
  }

  /*ACR-36cd6ad81f4941dca21df3482acc3b74
ACR-82cc3b9ae63c43398115c977fa258be3
ACR-e12e64bd10bf40f8b491eae99c8905d4
ACR-5eb087f8926c4131b109422c042a14c8
ACR-58353f2af0b14a15b7b368826ab8755e
ACR-8e9dd8e871f84286b81877cbacff2a40
   */
  @JsonNotification
  default void didSkipLoadingPlugin(DidSkipLoadingPluginParams params) {
  }

  /*ACR-13785db53d184e95b7a0ef1e0dad3c4d
ACR-c57a1942f0694f9c87d99f08f253b602
ACR-12ef0df2c3664b48a6f9dfaecbcfe4db
ACR-d5d6324fc6f6419184860863fef93e39
ACR-eb535d86f7d44e538d8d9bbc1535dbf7
   */
  @JsonNotification
  default void didDetectSecret(DidDetectSecretParams params) {
  }

  /*ACR-08d98e4b1a9a43848b8132e43f853f5c
ACR-c5eca4dbb2184cf28042301a0939223e
ACR-f7cd65b82ea249db8b8d6a617417ae75
ACR-76458e2933e54f4c972172800f23c76c
ACR-a91de287077f48928308b9fb365590df
   */
  @JsonNotification
  default void promoteExtraEnabledLanguagesInConnectedMode(PromoteExtraEnabledLanguagesInConnectedModeParams params) {
  }

  /*ACR-a1d94369368d4591be063c90ba18c793
ACR-9b30e3f4539540628d5d5df46994fd1f
ACR-2e8cde94c27d44738c7e544e35a7f2a2
ACR-fc8e97388f374b679873af9516a2ffce
ACR-3ae250b5066e4942b079916a69a56796
   */
  @JsonRequest
  CompletableFuture<GetInferredAnalysisPropertiesResponse> getInferredAnalysisProperties(GetInferredAnalysisPropertiesParams params);

  /*ACR-466747b90f1c41e0a1fc6d951a29b847
ACR-0704939bacbc4908a925e62ac18141c0
ACR-2e37766a73944041b1142f33535e2e94
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
