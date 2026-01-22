/*
ACR-b5579c27b325485c9263069d7271fc55
ACR-d7955a4d404f40b286f3d3aed8c0128b
ACR-cdabd08933ae40b79432a56371f4b74b
ACR-0f07b5ea8b8948609eb0bf042678ca79
ACR-2b9e4eb2139d4c7a9649cb4890406938
ACR-9f8b2f731c7a4a379d8f5e7a1b4ef565
ACR-deeafac9d92843bf9cc9435a2b715478
ACR-9dc4baf6a58a4975a48ac514e15ae1b6
ACR-582a4f21371247e5926a4af11ef392c1
ACR-759d3ef872514713a373b6d4906fcb34
ACR-a5e7a1d129f145a09a5478d8d8c46dff
ACR-6d6e102797b141019676376f23c206c9
ACR-974f46ef68b344598c1dd1d5d2b24716
ACR-4362927df409406eb8812708d41d0af3
ACR-bfb2c9f2dd6a47e78d11d34202346cce
ACR-aeccfb03c11e4ccb985dccd8f5bb50d6
ACR-33c03a2a018549d6a42e045bb7c2b313
 */
package its;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import org.jetbrains.annotations.Nullable;
import org.sonarsource.sonarlint.core.rpc.client.ConfigScopeNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.ConnectionNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintCancelChecker;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.NoBindingSuggestionFoundParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.event.DidReceiveServerHotspotEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FixSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.HotspotDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.ProxyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowSoonUnsupportedMessageParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ReportProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.StartProgressParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification.ShowSmartNotificationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.TelemetryClientLiveAttributesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class MockSonarLintRpcClientDelegate implements SonarLintRpcClientDelegate {

  private final Map<String, Map<URI, List<RaisedIssueDto>>> raisedIssues = new HashMap<>();
  private final Map<String, Map<URI, List<RaisedHotspotDto>>> raisedHotspots = new HashMap<>();

  public Map<URI, List<RaisedIssueDto>> getRaisedIssues(String configurationScopeId) {
    var issues = raisedIssues.get(configurationScopeId);
    return issues != null ? issues : Map.of();
  }

  public Map<URI, List<RaisedHotspotDto>> getRaisedHotspots(String configurationScopeId) {
    var hotspots = raisedHotspots.get(configurationScopeId);
    return hotspots != null ? hotspots : Map.of();
  }

  public Map<String, Map<URI, List<RaisedIssueDto>>> getRaisedIssues() {
    return raisedIssues;
  }

  public List<RaisedIssueDto> getRaisedIssuesAsList(String configurationScopeId) {
    return raisedIssues.getOrDefault(configurationScopeId, new HashMap<>()).values().stream().flatMap(List::stream).toList();
  }

  public List<RaisedHotspotDto> getRaisedHotspotsAsList(String configurationScopeId) {
    return raisedHotspots.getOrDefault(configurationScopeId, new HashMap<>()).values().stream().flatMap(List::stream).toList();
  }

  @Override
  public void suggestBinding(Map<String, List<BindingSuggestionDto>> suggestionsByConfigScope) {

  }

  @Override
  public void suggestConnection(Map<String, List<ConnectionSuggestionDto>> suggestionsByConfigScope) {

  }

  @Override
  public void openUrlInBrowser(URL url) {

  }

  @Override
  public void showMessage(MessageType type, String text) {

  }

  @Override
  public void log(LogParams params) {

  }

  @Override
  public void showSoonUnsupportedMessage(ShowSoonUnsupportedMessageParams params) {

  }

  @Override
  public void showSmartNotification(ShowSmartNotificationParams params) {

  }

  @Override
  public String getClientLiveDescription() {
    return "";
  }

  @Override
  public void showHotspot(String configurationScopeId, HotspotDetailsDto hotspotDetails) {

  }

  @Override
  public void showIssue(String configurationScopeId, IssueDetailsDto issueDetails) {

  }

  @Override
  public void showFixSuggestion(String configurationScopeId, String issueKey, FixSuggestionDto fixSuggestion) {

  }

  @Override
  public AssistCreatingConnectionResponse assistCreatingConnection(AssistCreatingConnectionParams params, SonarLintCancelChecker cancelChecker) throws CancellationException {
    throw new CancellationException("Unsupported in ITS");
  }

  @Override
  public AssistBindingResponse assistBinding(AssistBindingParams params, SonarLintCancelChecker cancelChecker) throws CancellationException {
    throw new CancellationException("Unsupported in ITS");
  }

  @Override
  public void startProgress(StartProgressParams params) throws UnsupportedOperationException {

  }

  @Override
  public void reportProgress(ReportProgressParams params) {

  }

  @Override
  public void didSynchronizeConfigurationScopes(Set<String> configurationScopeIds) {

  }

  @Override
  public Either<TokenDto, UsernamePasswordDto> getCredentials(String connectionId) throws ConnectionNotFoundException {
    throw new ConnectionNotFoundException();
  }

  @Override
  public List<ProxyDto> selectProxies(URI uri) {
    return List.of(ProxyDto.NO_PROXY);
  }

  @Override
  public GetProxyPasswordAuthenticationResponse getProxyPasswordAuthentication(String host, int port, String protocol, String prompt, String scheme, URL targetHost) {
    return new GetProxyPasswordAuthenticationResponse("", "");
  }

  @Override
  public boolean checkServerTrusted(List<X509CertificateDto> chain, String authType) {
    return false;
  }

  @Override
  public void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {

  }

  @Override
  public String matchSonarProjectBranch(String configurationScopeId, String mainBranchName, Set<String> allBranchesNames, SonarLintCancelChecker cancelChecker)
    throws ConfigScopeNotFoundException {
    return mainBranchName;
  }

  @Override
  public void didChangeMatchedSonarProjectBranch(String configScopeId, String newMatchedBranchName) {

  }

  @Override
  public TelemetryClientLiveAttributesResponse getTelemetryLiveAttributes() {
    System.err.println("Telemetry should be disabled in ITs");
    throw new CancellationException("Telemetry should be disabled in ITs");
  }

  @Override
  public void didChangeTaintVulnerabilities(String configurationScopeId, Set<UUID> closedTaintVulnerabilityIds, List<TaintVulnerabilityDto> addedTaintVulnerabilities,
    List<TaintVulnerabilityDto> updatedTaintVulnerabilities) {

  }

  @Override
  public List<ClientFileDto> listFiles(String configScopeId) {
    return List.of();
  }

  @Override
  public void noBindingSuggestionFound(NoBindingSuggestionFoundParams params) {
  }

  @Override
  public void didChangeAnalysisReadiness(Set<String> configurationScopeIds, boolean areReadyForAnalysis) {

  }

  @Override
  public void raiseIssues(String configurationScopeId, Map<URI, List<RaisedIssueDto>> issuesByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
    raisedIssues.computeIfAbsent(configurationScopeId, k -> new HashMap<>()).putAll(issuesByFileUri);
  }

  @Override
  public void raiseHotspots(String configurationScopeId, Map<URI, List<RaisedHotspotDto>> hotspotsByFileUri, boolean isIntermediatePublication, @Nullable UUID analysisId) {
    raisedHotspots.computeIfAbsent(configurationScopeId, k -> new HashMap<>()).putAll(hotspotsByFileUri);
  }

  public void clear() {
    raisedIssues.clear();
    raisedHotspots.clear();
  }

}
