/*
ACR-7dfa427afff44bdb971b706a4d855425
ACR-6284ff53310d4907b5dade29f515e037
ACR-63ab1a8ff11a4d0096fcc9fec9a9e4bd
ACR-47622d3edc504afd85a9c7465b7058bf
ACR-70a0ae065512417995232f29206c0ba0
ACR-b2d294a3492840af93d2231d1bf6bcc4
ACR-dbccade2cab44748acfa36cf78aa201b
ACR-343d2ad1b4db46c5843731117b5c356e
ACR-6cffa42187c54edfac0248a650ea1735
ACR-ed6c35e649cb43bc8337ed96089bb530
ACR-f49fd8a2be40496c9d24c7cb4ef67cd6
ACR-cd39a56cb7764298a66e6650e8015d55
ACR-6d28953616c0489ca8f1d31c775b1518
ACR-78a80debf10a40edad56c71f439871b1
ACR-2077076cc74f45d2b8687a9ade29fbfc
ACR-160d89649386462aac75931ae1030ba5
ACR-4e1273c2d0cc4e538efcc28c96a109f0
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.NoBindingSuggestionFoundParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.embeddedserver.EmbeddedServerStartedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.event.DidReceiveServerHotspotEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FixSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.flightrecorder.FlightRecorderStartedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.HotspotDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.ProxyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageActionItem;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageRequestResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowSoonUnsupportedMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.plugin.DidSkipLoadingPluginParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ReportProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.StartProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification.ShowSmartNotificationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

/*ACR-aafff23e587f43ba852efd12f7b0264d
ACR-51c64516ef2b43528f0d8e8b6efe6983
ACR-0cbd2dd51c2b4b68944bd2dc2c18a068
ACR-a43fe9ffc5bb4b17943d99c5836cdc4e
 */
public interface SonarLintRpcClientDelegate {

  /*ACR-c3aa9698dfb445838d5893940b48d64e
ACR-eb1d4779cd664a0e94fc58f8b9c4b87a
ACR-f0f671779b2142fc98311ed6720291b2
ACR-9fd61c4c614249b286619d54d8a279ad
   */
  void suggestBinding(Map<String, List<BindingSuggestionDto>> suggestionsByConfigScope);

  void suggestConnection(Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScope);

  void openUrlInBrowser(URL url);

  /*ACR-5983170467214cddb911f042849b1574
ACR-fdbfcb6e0dfb4aaf9ae643ec7926fd12
ACR-672f1975d96f44fe964cf5feaa362a98
   */
  void showMessage(MessageType type, String text);

  /*ACR-ff21401be041419f9ae62b15b9298e5f
ACR-808c03b0548c4cf98d84a395ac0b1cc3
ACR-581c6564e427492ab9165765c19970e4
ACR-651734d7a94745d4a72687dc816e9952
ACR-9079ab893f044253bd996ac7f0031f86
ACR-26f4ffda28394c36a8be99f216053881
   */
  default ShowMessageRequestResponse showMessageRequest(MessageType type, String text, List<MessageActionItem> actions) {
    return null;
  }

  void log(LogParams params);

  /*ACR-4cd4931247574b29953b13a17bec82d1
ACR-fbcbfdc471c44d34ab21424528592319
ACR-6b4be80722004089869d7cbd7d2f320f
ACR-2e4d51c2764745638d64246dfbf93ec6
ACR-16d76890170e4a33915c28700c7906ec
   */
  void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params);

  void showSmartNotification(ShowSmartNotificationParams params);

  /*ACR-9e3cd78ad6df4deea3ccf481cf4ca37d
ACR-5fa66204d0194be4b83d8d8a03c00a3f
ACR-99640d7c96af4643844349b4b10eec9d
   */
  String getClientLiveDescription();

  void showHotspot(String configurationScopeId, HotspotDetailsDto hotspotDetails);

  /*ACR-aa32778723c5442fa903afd1111a0ad9
ACR-7da4cd5ea29645ff9d6ef1e132404dd2
   */
  void showIssue(String configurationScopeId, IssueDetailsDto issueDetails);

  /*ACR-19306dbdc172421caa7fd330b294cfb6
ACR-d4cb3662942041d98e78b70dd33edadb
ACR-1125c67af74142338167d108b91076ea
   */
  default void showFixSuggestion(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {

  }

  /*ACR-4ad7df90580443c7a48743350af4b05d
ACR-6eb7b878201841309411f67417f6e27a
ACR-acd1d39d8422408bad7a9c1a1de23ef6
ACR-8db42ade88e64dd79bc6d2a21d8d209f
   */
  AssistCreatingConnectionResponse assistCreatingConnection(AssistCreatingConnectionParams params, SonarLintCancelChecker cancelChecker) throws CancellationException;

  /*ACR-075fb977fb1b4e34ab3019dbada82edd
ACR-f39c5ee3de9d420a830d790ea5c089f5
ACR-66b5a88c6471489d8a612699615f695a
ACR-a3be9b9f74874ecf9e0577701c60ed11
   */
  AssistBindingResponse assistBinding(AssistBindingParams params, SonarLintCancelChecker cancelChecker) throws CancellationException;

  /*ACR-8e876e6bb40d46ff9f3cf3ac77959c7c
ACR-68d6fa71528f43d09259fb3919ba2070
ACR-92024be799204730bb4114d44a1c99b4
   */
  void startProgress(StartProgressParams params) throws UnsupportedOperationException;

  /*ACR-ea0df7f873784b6ba955cbbf18cea24c
ACR-212a1d7ddc02465bb978688085d9152f
   */
  void reportProgress(ReportProgressParams params);

  void didSynchronizeConfigurationScopes(Set<String> configurationScopeIds);

  /*ACR-3e5c34adfa7b4b93b123ff6846e14227
ACR-9a9e938d7132474d8a7fbb5a298e4997
ACR-cd07025ca0df4f5aab4efb516c4aa259
   */
  @CheckForNull
  Either<TokenDto, UsernamePasswordDto> getCredentials(String connectionId) throws ConnectionNotFoundException;

  List<ProxyDto> selectProxies(URI uri);

  GetProxyPasswordAuthenticationResponse getProxyPasswordAuthentication(String host, int port, String protocol, String prompt, String scheme, URL targetHost);

  /*ACR-826160e4bf19426cbe753a43a41739bf
ACR-2bd9fdf2bca945e889028fae8dd5289c
ACR-ee3a963595fd4e2497d7f145db54adf8
   */
  boolean checkServerTrusted(List<X509CertificateDto> chain, String authType);

  @Deprecated(since = "10.3")
  default void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {
    //ACR-872370b849ae4a50bdcd99378143d40f
  }

  /*ACR-062d809ceb0d4061932b30685377c59c
ACR-3a0bde583e2749d3a8acc695cb1cb525
   */
  @CheckForNull
  String matchSonarProjectBranch(String configurationScopeId, String mainBranchName, Set<String> allBranchesNames,
    SonarLintCancelChecker cancelChecker) throws ConfigScopeNotFoundException;

  @Deprecated(since = "10.23", forRemoval = true)
  default boolean matchProjectBranch(String configurationScopeId, String branchNameToMatch, SonarLintCancelChecker cancelChecker) {
    return true;
  }

  void didChangeMatchedSonarProjectBranch(String configScopeId, String newMatchedBranchName);

  TelemetryClientLiveAttributesResponse getTelemetryLiveAttributes();

  void didChangeTaintVulnerabilities(String configurationScopeId, Set<UUID> closedTaintVulnerabilityIds, List<TaintVulnerabilityDto> addedTaintVulnerabilities,
    List<TaintVulnerabilityDto> updatedTaintVulnerabilities);

  default void didChangeDependencyRisks(String configurationScopeId, Set<UUID> closedDependencyRiskIds, List<DependencyRiskDto> addedDependencyRisks,
    List<DependencyRiskDto> updatedDependencyRisks) {
  }

  default Path getBaseDir(String configurationScopeId) throws ConfigScopeNotFoundException {
    return null;
  }

  List<ClientFileDto> listFiles(String configScopeId) throws ConfigScopeNotFoundException;

  void noBindingSuggestionFound(NoBindingSuggestionFoundParams params);

  void didChangeAnalysisReadiness(Set<String> configurationScopeIds, boolean areReadyForAnalysis);

  default void raiseIssues(String configurationScopeId, Map<URI, List<RaisedIssueDto>> issuesByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
  }

  default void raiseHotspots(String configurationScopeId, Map<URI, List<RaisedHotspotDto>> hotspotsByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
  }

  default void didSkipLoadingPlugin(String configurationScopeId, Language language, DidSkipLoadingPluginParams.SkipReason reason, String minVersion,
    @Nullable String currentVersion) {
  }

  default void didDetectSecret(String configurationScopeId) {
  }

  default void promoteExtraEnabledLanguagesInConnectedMode(String configurationScopeId, Set<Language> languagesToPromote) {
  }

  default Map<String, String> getInferredAnalysisProperties(String configurationScopeId, List<URI> filesToAnalyze) throws ConfigScopeNotFoundException {
    return Map.of();
  }

  default Set<String> getFileExclusions(String configurationScopeId) throws ConfigScopeNotFoundException {
    return Collections.emptySet();
  }

  default void invalidToken(String connectionId) {
  }

  default void flightRecorderStarted(FlightRecorderStartedParams params) {
  }

  default void embeddedServerStarted(EmbeddedServerStartedParams params) {
  }
}
