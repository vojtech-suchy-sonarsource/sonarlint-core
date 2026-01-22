/*
ACR-991e47d4163547098ca1a8b83342a2d1
ACR-19afdc8b5d9741fd97bbb78b5eba13a3
ACR-b3c6207dcb1c46dab2a63b91544c8afd
ACR-3b2bb805862443259f5f50e5f31f10f1
ACR-557b586c07a8428babd1174495c12d81
ACR-396f6b801c0643f5bfd46453674f8a63
ACR-b4e22167f8c849c393405cf82f71e372
ACR-c2f16f215c694f659d1494ea77d0f076
ACR-d5de65d62a4f4ffea62b68a52eaa640e
ACR-bb2f04884f3e4c3a91bd91e62335d1ff
ACR-372e264ebb8b45d58bd112f9e9b5ab90
ACR-2d7c97d2a88d455cbcdf4eb2855ef67e
ACR-7980ed9c040e4ca69ba5fa23ba917332
ACR-cb205f3bfeac4fe4acb44ab1b9034c00
ACR-c5d70abbad70496e935d21751845b4f7
ACR-dd1054c834b54330aad3f3f3fd4ce338
ACR-c867edb1bcd940c889212f5fb320e5dd
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

/*ACR-25600a41a84d4bf1808a9169bc14a8dd
ACR-685b957001304800ad5a587346f516d6
ACR-a212b705b7d8467c90357e6d8df5bd9e
ACR-9df08f3984514b60834de9186eb06865
 */
public interface SonarLintRpcClientDelegate {

  /*ACR-76cc60fc22464618a22e420f4fcc681a
ACR-e73a651c652e40a890dbfac767913505
ACR-112752cb4c5747ecaa6d8ac23c8e3c2c
ACR-a674aa50cd4944d39282c2b6788f7969
   */
  void suggestBinding(Map<String, List<BindingSuggestionDto>> suggestionsByConfigScope);

  void suggestConnection(Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScope);

  void openUrlInBrowser(URL url);

  /*ACR-0ecd48d9df484867a764f31735bdb6d3
ACR-48c17adbdec746e8aa39cd9071e34ca5
ACR-52a313a0cfb2472386314836471f4a75
   */
  void showMessage(MessageType type, String text);

  /*ACR-88435ce7ab2c45aa82eedd1bdf612173
ACR-5202274e4f3640fa8c296851fb9530c7
ACR-6dcb0495eb284fad9ce9e5b3d8bc943c
ACR-7c1080030e9c4f28822b14a1349ca921
ACR-4ad02016cc0343b683dffdd1fff1ff6f
ACR-9e2d331096b2438bb04d7017e50a0ffe
   */
  default ShowMessageRequestResponse showMessageRequest(MessageType type, String text, List<MessageActionItem> actions) {
    return null;
  }

  void log(LogParams params);

  /*ACR-0cfd892d72574809af87ff0de9e8e893
ACR-ba161dabee7b4caba847e62d50659460
ACR-1c1d64ea51d94ccf9817c8a609de6645
ACR-d2f64d593cc6462abbd964d2e2cc390d
ACR-a9556f524ea24ef3861ae178c7cb1f84
   */
  void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params);

  void showSmartNotification(ShowSmartNotificationParams params);

  /*ACR-9b5c848d2906458db0563995cb2312fa
ACR-066bbca437b84a36a4c64733ae286304
ACR-ed18456f55e04daf93da3a42f166db99
   */
  String getClientLiveDescription();

  void showHotspot(String configurationScopeId, HotspotDetailsDto hotspotDetails);

  /*ACR-1dd29efd926a4da193f121975dcbd65a
ACR-f475febf4ff945818ee68f1660dfc4bc
   */
  void showIssue(String configurationScopeId, IssueDetailsDto issueDetails);

  /*ACR-f233cb2fdb0b49f38606be9812c9a313
ACR-400c207af20a4047a54d245137c04b38
ACR-61575d87b9c14d3da77266e1e4d4d52d
   */
  default void showFixSuggestion(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {

  }

  /*ACR-59353f9eebef4ff49141c8901853a8bf
ACR-10c528786a6a4e9890a9cf253ed7ed76
ACR-eaf39c5df94a44a99ab8b2654b26deba
ACR-cefaa00511254da7a4bf03de53e249a6
   */
  AssistCreatingConnectionResponse assistCreatingConnection(AssistCreatingConnectionParams params, SonarLintCancelChecker cancelChecker) throws CancellationException;

  /*ACR-6c4b8b363a0e42a09b659ccda162c5ab
ACR-4f31a6e5983747efb1668d2f93850c5c
ACR-f7eb56822d9a49d29d24c5d5e1d5c5d3
ACR-b0b2965f5cd14c17a2e32cee3f17b6bd
   */
  AssistBindingResponse assistBinding(AssistBindingParams params, SonarLintCancelChecker cancelChecker) throws CancellationException;

  /*ACR-53ce59d36abd4ccf8388e9f858b3576e
ACR-a968c8a127634004965b7649f219b40f
ACR-c075e14d6eac405b933b595de56c7a1d
   */
  void startProgress(StartProgressParams params) throws UnsupportedOperationException;

  /*ACR-0ae6bc4031fc4f1dae2d636a4bb2e9d0
ACR-0a4539032b21499b8c21e0ff70e9bc89
   */
  void reportProgress(ReportProgressParams params);

  void didSynchronizeConfigurationScopes(Set<String> configurationScopeIds);

  /*ACR-51504b31b4dc4214844351af1dfb8a1b
ACR-3fe2901262744b769dfa60c46faf6680
ACR-98a25db179a54de6b1127473bc2b4b64
   */
  @CheckForNull
  Either<TokenDto, UsernamePasswordDto> getCredentials(String connectionId) throws ConnectionNotFoundException;

  List<ProxyDto> selectProxies(URI uri);

  GetProxyPasswordAuthenticationResponse getProxyPasswordAuthentication(String host, int port, String protocol, String prompt, String scheme, URL targetHost);

  /*ACR-21bc6ebf4c4b495382c7d17a9324045c
ACR-76198ef0cf7e400eb8a1799cc4f70935
ACR-ee7af56b00e04565bcd306f17ba9b4c4
   */
  boolean checkServerTrusted(List<X509CertificateDto> chain, String authType);

  @Deprecated(since = "10.3")
  default void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {
    //ACR-4c148518c6ef41d8afe3ce3463f9b5a8
  }

  /*ACR-11c512e16123459b90199e11c31f0d07
ACR-1aea110c61d849fc9f333dad88c909e7
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
