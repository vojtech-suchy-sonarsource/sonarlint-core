/*
ACR-89eb7b38e0ab4ac39142ebab08eb8f58
ACR-6f1ee76e49ea48e1a82412058a764a08
ACR-45b9d424bcfd4d388ad83745d32edb39
ACR-5afbf40db43d42319698d3ddb399b3b8
ACR-f737681b8fe24346bb5370681871df89
ACR-25ccdd7fd14a45d39f4aef3d8fc12c37
ACR-033aaf050a144bd69761378a4c969bca
ACR-94ff515af7ef4b539fbc5055217eb4c7
ACR-118b97816c9e44c79da1738cdfbea56a
ACR-441586aab2b34d3bb2dff4e94e2fcdaa
ACR-59848a6c7d7744f1a2d96120619bed0e
ACR-9f12b8737bc5482d8ae8fa7772ed2ea5
ACR-6b22808fadb74f83ad27f8383fcb39a8
ACR-3190695d2598472c8dc0a5be5fe60bb2
ACR-e6c63f79e7de4547bfce532d98b094a1
ACR-afdf67a0ca6c48009578e270e974e62d
ACR-f7e7ab1922814f81ab42bd843278c173
 */
package mediumtest.issues;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.file.FilePathTranslation;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.fix.FixSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry.AiSuggestionSource;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.telemetry.TelemetryFixSuggestionReceivedCounter;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.server.ServerFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;
import static utils.AnalysisUtils.createFile;

class OpenFixSuggestionInIdeMediumTests {

  public static final String PROJECT_KEY = "projectKey";
  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester(true);

  private static final String ISSUE_KEY = "myIssueKey";
  private static final String CONNECTION_ID = "connectionId";
  private static final String CONFIG_SCOPE_ID = "configScopeId";
  private static final String BRANCH_NAME = "branchName";
  private static final String ORG_KEY = "orgKey";
  private static final String FIX_PAYLOAD = """
    {
    "fileEdit": {
    "path": "Main.java",
    "changes": [{
    "beforeLineRange": {
    "startLine": 0,
    "endLine": 1
    },
    "before": "",
    "after": "var fix = 1;"
    }]
    },
    "suggestionId": "eb93b2b4-f7b0-4b5c-9460-50893968c264",
    "explanation": "Modifying the variable name is good"
    }
    """;

  @SonarLintTest
  void it_should_update_the_telemetry_on_show_issue(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    assertThat(backend.telemetryFileContent().getFixSuggestionReceivedCounter()).isEmpty();

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY);

    assertThat(statusCode).isEqualTo(200);
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.telemetryFileContent().getFixSuggestionReceivedCounter())
        .isEqualTo(Map.of("eb93b2b4-f7b0-4b5c-9460-50893968c264", new TelemetryFixSuggestionReceivedCounter(AiSuggestionSource.SONARCLOUD, 1, false))));
  }

  @SonarLintTest
  void it_should_open_a_fix_suggestion_in_ide(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarCloudConnection(CONNECTION_ID, ORG_KEY)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY);
    assertThat(statusCode).isEqualTo(200);

    ArgumentCaptor<FixSuggestionDto> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(2000)).showFixSuggestion(eq(CONFIG_SCOPE_ID), eq(ISSUE_KEY), captor.capture());

    var pathTranslation = new FilePathTranslation(Path.of("ide"), Path.of("home"));

    var fixSuggestion = captor.getValue();
    assertThat(fixSuggestion).isNotNull();
    assertThat(fixSuggestion.suggestionId()).isEqualTo("eb93b2b4-f7b0-4b5c-9460-50893968c264");
    assertThat(fixSuggestion.explanation()).isEqualTo("Modifying the variable name is good");
    assertThat(fixSuggestion.fileEdit().idePath().toString()).contains(pathTranslation.serverToIdePath(Paths.get("Main.java")).toString());
    assertThat(fixSuggestion.fileEdit().changes()).hasSize(1);
    var change = fixSuggestion.fileEdit().changes().get(0);
    assertThat(change.before()).isEmpty();
    assertThat(change.after()).isEqualTo("var fix = 1;");
    assertThat(change.beforeLineRange().getStartLine()).isZero();
    assertThat(change.beforeLineRange().getEndLine()).isEqualTo(1);
  }

  @SonarLintTest
  void it_should_assist_creating_the_binding_if_scope_not_bound(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withUnboundConfigScope(CONFIG_SCOPE_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY);
    assertThat(statusCode).isEqualTo(200);

    verify(fakeClient, timeout(2000)).showFixSuggestion(eq(CONFIG_SCOPE_ID), eq(ISSUE_KEY), any());
    verify(fakeClient, never()).showMessage(any(), any());
  }

  @SonarLintTest
  void it_should_not_assist_binding_if_multiple_suggestions(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withUnboundConfigScope("configScopeA", PROJECT_KEY + " 1")
      .withUnboundConfigScope("configScopeB", PROJECT_KEY + " 2")
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY);

    assertThat(statusCode).isEqualTo(200);
    //ACR-10fea3b945534f7986e1a8f9307a4c8e
    verify(fakeClient, timeout(1000)).noBindingSuggestionFound(any());
    verify(fakeClient, never()).showIssue(any(), any());
  }

  @SonarLintTest
  void it_should_assist_binding_if_multiple_suggestions_but_scopes_are_parent_and_child(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs("configScopeParent",
        List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), "configScopeParent", false, null, inputFile, null, null, true)))
      .withInitialFs("configScopeChild", List.of())
      .build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withUnboundConfigScope("configScopeParent", PROJECT_KEY)
      .withUnboundConfigScope("configScopeChild", PROJECT_KEY, "configScopeParent")
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, "configScopeParent", CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY);

    assertThat(statusCode).isEqualTo(200);
    verify(fakeClient, timeout(2000)).showFixSuggestion(any(), any(), any());
    verify(fakeClient, never()).showMessage(any(), any());
  }

  @SonarLintTest
  void it_should_assist_creating_the_connection_when_no_sc_connection(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();

    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withUnboundConfigScope(CONFIG_SCOPE_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenFixSuggestionRequestWithToken(backend, scServer, FIX_PAYLOAD, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, ORG_KEY, "token-name", "token-value");
    assertThat(statusCode).isEqualTo(200);

    verify(fakeClient, timeout(2000)).showFixSuggestion(eq(CONFIG_SCOPE_ID), eq(ISSUE_KEY), any());
    verify(fakeClient, never()).showMessage(any(), any());

    ArgumentCaptor<AssistCreatingConnectionParams> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(1000)).assistCreatingConnection(captor.capture(), any());
    assertThat(captor.getAllValues())
      .extracting(connectionParams -> connectionParams.getConnectionParams().getRight().getOrganizationKey(),
        AssistCreatingConnectionParams::getTokenName,
        AssistCreatingConnectionParams::getTokenValue)
      .containsExactly(tuple(ORG_KEY, "token-name", "token-value"));
  }

  @SonarLintTest
  void it_should_fail_request_when_issue_parameter_missing(SonarLintTestHarness harness) throws Exception {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();
    var scServer = buildSonarCloudServer(harness).start();

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, FIX_PAYLOAD, "", PROJECT_KEY, BRANCH_NAME, ORG_KEY);

    assertThat(statusCode).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_request_when_project_parameter_missing(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();
    var scServer = buildSonarCloudServer(harness).start();

    var statusCode = executeOpenFixSuggestionRequestWithoutToken(backend, scServer, ISSUE_KEY, "", "", "", "");

    assertThat(statusCode).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_when_origin_is_missing(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarCloudConnection(CONNECTION_ID, ORG_KEY)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(
        "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/fix/show?server=" + scServer.baseUrl() + "&issue=" + ISSUE_KEY +
          "&project=" + PROJECT_KEY + "&branch=" + BRANCH_NAME + "&organizationKey=" + ORG_KEY))
      .POST(HttpRequest.BodyPublishers.ofString(FIX_PAYLOAD)).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    var statusCode = response.statusCode();

    assertThat(statusCode).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_when_origin_does_not_match(SonarLintTestHarness harness, @TempDir Path baseDir) throws Exception {
    var inputFile = createFile(baseDir, "Main.java", "");
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(new ClientFileDto(inputFile.toUri(), baseDir.relativize(inputFile), CONFIG_SCOPE_ID, false, null, inputFile, null, null, true)))
      .build();
    var scServer = buildSonarCloudServer(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarCloudConnection(CONNECTION_ID, ORG_KEY)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(
        "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/fix/show?server=" + scServer.baseUrl() + "&issue=" + ISSUE_KEY +
          "&project=" + PROJECT_KEY + "&branch=" + BRANCH_NAME + "&organizationKey=" + ORG_KEY))
      .header("Origin", "malicious")
      .POST(HttpRequest.BodyPublishers.ofString(FIX_PAYLOAD)).build();
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    var statusCode = response.statusCode();

    assertThat(statusCode).isEqualTo(200);

    ArgumentCaptor<LogParams> captor = ArgumentCaptor.captor();
    verify(fakeClient, after(500).atLeastOnce()).log(captor.capture());
    assertThat(captor.getAllValues())
      .extracting(LogParams::getMessage)
      .containsAnyOf("The origin 'malicious' is not trusted, this could be a malicious request");
  }

  @SonarLintTest
  void it_should_fail_request_when_server_points_to_sonarcloud(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start(client);
    HttpRequest request = openFixSuggestionRequest(backend,
      "",
      org.sonarsource.sonarlint.core.SonarCloudRegion.EU.getProductionUri().toString(),
      "someOrigin");
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    verify(client).showMessage(MessageType.ERROR,
      "Invalid request to SonarQube backend. The 'server' parameter should not be SonarQube Cloud URL, use it only to specify URL of a SonarQube Server.");
  }

  @SonarLintTest
  void it_should_fail_request_when_server_points_to_sonarcloud_us(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start(client);
    HttpRequest request = openFixSuggestionRequest(backend,
      "",
      org.sonarsource.sonarlint.core.SonarCloudRegion.US.getProductionUri().toString(),
      "someOrigin");

    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    verify(client).showMessage(MessageType.ERROR,
      "Invalid request to SonarQube backend. The 'server' parameter should not be SonarQube Cloud URL, use it only to specify URL of a SonarQube Server.");
  }

  private Object executeOpenFixSuggestionRequestWithToken(SonarLintTestRpcServer backend, ServerFixture.Server scServer, String payload, String issueKey, String projectKey,
    String branchName, String orgKey,
    String tokenName, String tokenValue) throws IOException, InterruptedException {
    HttpRequest request = openFixSuggestionRequest(backend, scServer, payload, "&issue=" + issueKey, "&project=" + projectKey, "&branch=" + branchName,
      "&organizationKey=" + orgKey, "&tokenName=" + tokenName, "&tokenValue=" + tokenValue);
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  private Object executeOpenFixSuggestionRequestWithoutToken(SonarLintTestRpcServer backend, ServerFixture.Server scServer, String payload, String issueKey, String projectKey,
    String branchName, String orgKey) throws IOException, InterruptedException {
    HttpRequest request = openFixSuggestionRequest(backend, scServer, payload, "&issue=" + issueKey, "&project=" + projectKey, "&branch=" + branchName,
      "&organizationKey=" + orgKey);
    var response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  private HttpRequest openFixSuggestionRequest(SonarLintTestRpcServer backend, ServerFixture.Server scServer, String payload, String... params) {
    return openFixSuggestionRequest(backend, payload, scServer.baseUrl(), scServer.baseUrl(), params);
  }

  private static HttpRequest openFixSuggestionRequest(SonarLintTestRpcServer backend, String payload, String server, String origin, String... params) {
    return HttpRequest.newBuilder()
      .uri(URI.create(
        "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/fix/show?server=" + server + String.join("", params)))
      .POST(HttpRequest.BodyPublishers.ofString(payload))
      .header("Origin", origin)
      .build();
  }

  private void mockAssistBinding(SonarLintTestRpcServer backend, SonarLintBackendFixture.FakeSonarLintRpcClient fakeClient, String configScopeId, String connectionId,
    String sonarProjectKey) {
    doAnswer((Answer<AssistBindingResponse>) invocation -> {
      backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(configScopeId,
        new BindingConfigurationDto(connectionId, sonarProjectKey, false)));
      return new AssistBindingResponse(configScopeId);
    }).when(fakeClient).assistBinding(any(), any());
  }

  private void mockAssistCreatingConnection(SonarLintTestRpcServer backend, SonarLintBackendFixture.FakeSonarLintRpcClient fakeClient, String connectionId) {
    doAnswer((Answer<AssistCreatingConnectionResponse>) invocation -> {
      backend.getConnectionService().didUpdateConnections(
        new DidUpdateConnectionsParams(Collections.emptyList(),
          List.of(new SonarCloudConnectionConfigurationDto(connectionId, ORG_KEY, SonarCloudRegion.EU, true))));
      return new AssistCreatingConnectionResponse(connectionId);
    }).when(fakeClient).assistCreatingConnection(any(), any());
  }

  private static ServerFixture.SonarQubeCloudBuilder buildSonarCloudServer(SonarLintTestHarness harness) {
    return harness.newFakeSonarCloudServer()
      .withOrganization(ORG_KEY, organization -> organization.withProject(PROJECT_KEY, project -> project.withBranch(BRANCH_NAME)));
  }
}
