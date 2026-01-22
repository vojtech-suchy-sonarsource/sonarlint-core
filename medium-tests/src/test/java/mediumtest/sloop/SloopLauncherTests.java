/*
ACR-14d34d6971b84899aaeaed2015c187f7
ACR-36960370fdde461589e11bce132e3df6
ACR-2a52159f58ac442c8594c8fe8583a42e
ACR-1b23f4e599ea43e5adb052086576e9ab
ACR-a74005c7661540f4ac11a8517d3c0ee0
ACR-26f505a9bfb54bb9abd1b0c6c15651e4
ACR-ce404ff270704aedbdc3e4e6fd78bbf3
ACR-9bee7bc5d2474bc98bcb4835b4e0bfb6
ACR-6f46a7d02f444c0aba2159f9db6e55cc
ACR-c3b35e30aa35459983c13bba6f94eee2
ACR-0cb74bd8d42e47a18657d5f5ff3ff8b3
ACR-cd51957a388e42d1a1fda4d22a48c133
ACR-394a209e88204a9daade092c401cd974
ACR-424f8b9303634605afeef8b7e578089b
ACR-513561429c6a425ab4855a242da4dca9
ACR-83264ad24eba435dbaecc9759007fcb0
ACR-bee229c36d464e05a048cfe42d24de6f
 */
package mediumtest.sloop;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.client.ConnectionNotFoundException;
import org.sonarsource.sonarlint.core.rpc.client.Sloop;
import org.sonarsource.sonarlint.core.rpc.client.SloopLauncher;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintCancelChecker;
import org.sonarsource.sonarlint.core.rpc.client.SonarLintRpcClientDelegate;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcServer;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.ClientConstantInfoDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.HttpConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryClientConstantAttributesDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.GetEffectiveRuleDetailsParams;
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
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.GetProxyPasswordAuthenticationResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.ProxyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
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
import utils.PluginLocator;

import static mediumtest.sloop.UnArchiveUtils.unarchiveDistribution;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.sonarsource.sonarlint.core.rpc.protocol.common.Language.PHP;

class SloopLauncherTests {

  @TempDir
  private static Path sonarUserHome;

  @TempDir
  private static Path unarchiveTmpDir;

  private static Sloop sloop;
  private static SonarLintRpcServer server;
  private static Path sloopOutDirPath;
  private Integer exitValue;
  private boolean shutdownRequested;

  @BeforeAll
  static void setup() {
    var sloopDistPath = SystemUtils.IS_OS_WINDOWS ? SloopDistLocator.getWindowsDistPath() : SloopDistLocator.getLinux64DistPath();
    sloopOutDirPath = unarchiveTmpDir.resolve("sloopDistOut");
    unarchiveDistribution(sloopDistPath.toString(), sloopOutDirPath);
  }

  @BeforeEach
  void start() {
    shutdownRequested = false;
    exitValue = null;
    var sloopLauncher = new SloopLauncher(new DummySonarLintRpcClient());
    sloop = sloopLauncher.start(sloopOutDirPath.toAbsolutePath());
    server = sloop.getRpcServer();
  }

  @AfterEach
  void tearDown() {
    if (!shutdownRequested) {
      sloop.shutdown().join();
    }
  }

  @Test
  void test_all_rules_returns() throws Exception {
    var telemetryInitDto = new TelemetryClientConstantAttributesDto("SonarLint ITs", "SonarLint ITs",
      "1.2.3", "4.5.6", Collections.emptyMap());
    var clientInfo = new ClientConstantInfoDto("clientName", "integrationTests");

    server.initialize(new InitializeParams(clientInfo, telemetryInitDto, HttpConfigurationDto.defaultConfig(), null, Set.of(), sonarUserHome.resolve("storage"), sonarUserHome.resolve("workDir"),
      Set.of(PluginLocator.getPhpPluginPath().toAbsolutePath()), Collections.emptyMap(), Set.of(PHP), Collections.emptySet(), Collections.emptySet(), Collections.emptyList(),
      Collections.emptyList(), sonarUserHome.toString(), Map.of(), false, null, false, null)).get();

    var result = server.getRulesService().listAllStandaloneRulesDefinitions().get();
    assertThat(result.getRulesByKey()).hasSize(222);

    server.getConfigurationService()
      .didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(new ConfigurationScopeDto("myConfigScope", null, true, "My Config Scope", null))));

    var result2 = server.getRulesService().getEffectiveRuleDetails(new GetEffectiveRuleDetailsParams("myConfigScope", "php:S100", null)).join();
    assertThat(result2.details().getName()).isEqualTo("Function names should comply with a naming convention");
  }

  @Test
  void it_should_complete_onExit_future_when_process_exits() {
    var telemetryInitDto = new TelemetryClientConstantAttributesDto("SonarLint ITs", "SonarLint ITs",
      "1.2.3", "4.5.6", Collections.emptyMap());
    var clientInfo = new ClientConstantInfoDto("clientName", "integrationTests");
    server.initialize(new InitializeParams(clientInfo, telemetryInitDto, HttpConfigurationDto.defaultConfig(), null, Set.of(), sonarUserHome.resolve("storage"), sonarUserHome.resolve("workDir"),
      Set.of(PluginLocator.getPhpPluginPath().toAbsolutePath()), Collections.emptyMap(), Set.of(PHP), Collections.emptySet(), Collections.emptySet(), Collections.emptyList(),
      Collections.emptyList(), sonarUserHome.toString(), Map.of(), false, null, false, null)).join();
    sloop.onExit().thenAccept(exitValue -> this.exitValue = exitValue);

    shutdownRequested = true;
    sloop.shutdown().join();

    //ACR-4fd117ec1f774a3a8c811173e26420f7
    await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> assertThat(exitValue).isZero());
  }

  static class DummySonarLintRpcClient implements SonarLintRpcClientDelegate {
    final Queue<LogParams> logs = new ConcurrentLinkedQueue<>();

    public Queue<LogParams> getLogs() {
      return logs;
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
      logs.add(params);
      var log = new StringBuilder();
      log.append("[").append(params.getThreadName()).append("] ");
      log.append(params.getLevel()).append(" ").append(params.getMessage());
      if (params.getConfigScopeId() != null) {
        log.append(" [").append(params.getConfigScopeId()).append("]");
      }
      System.out.println(log);
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
      throw new CancellationException();
    }

    @Override
    public AssistBindingResponse assistBinding(AssistBindingParams params, SonarLintCancelChecker cancelChecker) throws CancellationException {
      throw new CancellationException();
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
      return new GetProxyPasswordAuthenticationResponse(null, null);
    }

    @Override
    public boolean checkServerTrusted(List<X509CertificateDto> chain, String authType) {
      return false;
    }

    @Override
    public void didReceiveServerHotspotEvent(DidReceiveServerHotspotEvent params) {

    }

    @Override
    public String matchSonarProjectBranch(String configurationScopeId, String mainBranchName, Set<String> allBranchesNames, SonarLintCancelChecker cancelChecker) {
      return null;
    }

    @Override
    public void didChangeMatchedSonarProjectBranch(String configScopeId, String newMatchedBranchName) {

    }

    @Override
    public TelemetryClientLiveAttributesResponse getTelemetryLiveAttributes() {
      System.err.println("Telemetry should be disabled in tests");
      throw new CancellationException("Telemetry should be disabled in tests");
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
  }
}
