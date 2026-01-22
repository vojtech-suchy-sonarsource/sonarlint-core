/*
ACR-fac36fd414094a2dbd45953b123f5d2c
ACR-be9dbb37aa264060b85a1944716990f3
ACR-e38a97ee8fd643af9e82677a37c0ca97
ACR-f10d31451aa645f7bcada41d1e998432
ACR-9bf2dda90ef9457fada7224919bc8c7f
ACR-2a59ab69ecd1429585ceea65a5e8139f
ACR-38b90fb0e98c44a28a59a77278b3f049
ACR-51f35b7e94fc40d9b14a48175084a2e6
ACR-c7e0f25ca0ac4af7af0eb35ffedbae22
ACR-2d8513dab38b4e8581d0554e06468e40
ACR-1e7c2deb72ac4b6ab7c5a27797b3737d
ACR-0bbbdeb69c964b9eb3cf6189e1bf3895
ACR-8c9dd97dec214feda3643cdc91a8bc72
ACR-71d56f7218524272898b1f82cbdc5681
ACR-84c9d46c7f6a4bbd8b00e6917ae03b86
ACR-c1abe233d4674560bd2a78ea17f84e0d
ACR-c60a1ef94b71481fa7049d1e00ae7b76
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
