/*
ACR-d5a12335e92a469ba55aa9b51f762252
ACR-ff3c91460f874505bed3ee41865cb036
ACR-0a6c2469cf9f4025a8c89d30f934e758
ACR-624de10a2a6b43168cafa5311c1b5eb6
ACR-04263bead9db4b15831223baa0df9aec
ACR-7e0c9d83a72d4293abd7444ca32bf0b7
ACR-3dedced102d14a1ebf0b8e88ce6b5ab1
ACR-f12fa2e4795245d1832d1acba5be884d
ACR-85a0b34be2b64899b1c16a7db45e2318
ACR-4a365ee139664ebda1994822d0dc5724
ACR-45f5235410394ad498c1c0a0e15ea622
ACR-86d82277e3b74aaca94aa23465ebd8ac
ACR-56934273c1f245e3bc6dcdcbe4633e82
ACR-dd6b2558d4b34b8ebad3f684523ffbfb
ACR-a64efebb2bdf4160a0cf9aca1a069ea8
ACR-e265f4562d474b97a352849bb4741e5c
ACR-fb9f6130d1af4753bb70a6f18692cabc
 */
package mediumtest.issues;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.DidUpdateBindingParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.binding.AssistBindingResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.AssistCreatingConnectionResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.EMBEDDED_SERVER;

class OpenIssueInIdeMediumTests {

  public static final String PROJECT_KEY = "projectKey";
  public static final String SONAR_PROJECT_NAME = "SonarLint IntelliJ";
  @RegisterExtension
  SonarLintLogTester logTester = new SonarLintLogTester(true);

  private static final String ISSUE_KEY = "myIssueKey";
  private static final String PR_ISSUE_KEY = "PRIssueKey";
  private static final String FILE_LEVEL_ISSUE_KEY = "fileLevelIssueKey";
  private static final String CONNECTION_ID = "connectionId";
  private static final String CONFIG_SCOPE_ID = "configScopeId";
  public static final String RULE_KEY = "ruleKey";
  private static final String BRANCH_NAME = "branchName";
  private static final Instant ISSUE_INTRODUCTION_DATE = LocalDateTime.of(2023, 12, 25, 12, 30, 35).toInstant(ZoneOffset.UTC);

  @SonarLintTest
  void it_should_update_the_telemetry_on_show_issue(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, fakeServerWithIssue)
      .withBoundConfigScope(CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY)
      .withBackendCapability(EMBEDDED_SERVER)
      .withTelemetryEnabled()
      .start(fakeClient);

    await().untilAsserted(() -> assertThat(backend.telemetryFileContent().getShowIssueRequestsCount()).isZero());

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);

    assertThat(statusCode).isEqualTo(200);
    await().atMost(2, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(backend.telemetryFileContent().getShowIssueRequestsCount()).isOne());
  }

  @SonarLintTest
  void it_should_open_an_issue_in_ide(SonarLintTestHarness harness) throws Exception {
    var issueKey = "myIssueKey";
    var projectKey = PROJECT_KEY;
    var connectionId = "connectionId";
    var configScopeId = "configScopeId";

    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, fakeServerWithIssue)
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);
    assertThat(statusCode).isEqualTo(200);

    ArgumentCaptor<IssueDetailsDto> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(2000)).showIssue(eq(configScopeId), captor.capture());

    var issues = captor.getAllValues();
    assertThat(issues).hasSize(1);
    var issueDetails = issues.get(0);
    assertThat(issueDetails.getIssueKey()).isEqualTo(issueKey);
    assertThat(issueDetails.isTaint()).isFalse();
    assertThat(issueDetails.getMessage()).isEqualTo("msg");
    assertThat(issueDetails.getRuleKey()).isEqualTo("ruleKey");
    assertThat(issueDetails.getCreationDate()).isEqualTo("2023-12-25T12:30:35+0000");
    assertThat(issueDetails.getTextRange()).extracting(TextRangeDto::getStartLine, TextRangeDto::getStartLineOffset,
      TextRangeDto::getEndLine, TextRangeDto::getEndLineOffset)
      .contains(1, 0, 3, 4);
    assertThat(issueDetails.getCodeSnippet()).isEqualTo("source\ncode\nfile");
  }

  @SonarLintTest
  void it_should_open_pr_issue_in_ide(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var projectKey = PROJECT_KEY;
    var connectionId = "connectionId";
    var configScopeId = "configScopeId";

    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, fakeServerWithIssue)
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, PR_ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, "1234");
    assertThat(statusCode).isEqualTo(200);

    ArgumentCaptor<IssueDetailsDto> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(2000)).showIssue(eq(configScopeId), captor.capture());

    var issues = captor.getAllValues();
    assertThat(issues).hasSize(1);
    var issueDetails = issues.get(0);
    assertThat(issueDetails.getIssueKey()).isEqualTo(PR_ISSUE_KEY);
    assertThat(issueDetails.isTaint()).isFalse();
    assertThat(issueDetails.getMessage()).isEqualTo("msg");
    assertThat(issueDetails.getRuleKey()).isEqualTo("ruleKey");
    assertThat(issueDetails.getCreationDate()).isEqualTo("2023-12-25T12:30:35+0000");
    assertThat(issueDetails.getTextRange()).extracting(TextRangeDto::getStartLine, TextRangeDto::getStartLineOffset,
      TextRangeDto::getEndLine, TextRangeDto::getEndLineOffset)
      .contains(1, 0, 3, 4);
    assertThat(issueDetails.getCodeSnippet()).isEqualTo("source\ncode\nfile");
  }

  @SonarLintTest
  void it_should_open_a_file_level_issue_in_ide(SonarLintTestHarness harness) throws Exception {
    var issueKey = FILE_LEVEL_ISSUE_KEY;
    var projectKey = PROJECT_KEY;
    var connectionId = "connectionId";
    var configScopeId = "configScopeId";

    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(connectionId, fakeServerWithIssue)
      .withBoundConfigScope(configScopeId, connectionId, projectKey)
      .withBackendCapability(EMBEDDED_SERVER)
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, FILE_LEVEL_ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);
    assertThat(statusCode).isEqualTo(200);

    ArgumentCaptor<IssueDetailsDto> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(2000)).showIssue(eq(configScopeId), captor.capture());

    var issues = captor.getAllValues();
    assertThat(issues).hasSize(1);
    var issueDetails = issues.get(0);
    assertThat(issueDetails.getIssueKey()).isEqualTo(issueKey);
    assertThat(issueDetails.isTaint()).isFalse();
    assertThat(issueDetails.getMessage()).isEqualTo("msg");
    assertThat(issueDetails.getRuleKey()).isEqualTo("ruleKey");
    assertThat(issueDetails.getCreationDate()).isEqualTo("2023-12-25T12:30:35+0000");
    assertThat(issueDetails.getTextRange()).extracting(TextRangeDto::getStartLine, TextRangeDto::getStartLineOffset,
      TextRangeDto::getEndLine, TextRangeDto::getEndLineOffset)
      .contains(0, 0, 0, 0);
    assertThat(issueDetails.getCodeSnippet()).isEqualTo("source\ncode\nfile\nfive\nlines");
  }

  @SonarLintTest
  void it_should_assist_creating_the_binding_if_scope_not_bound(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();

    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, fakeServerWithIssue)
      .withUnboundConfigScope(CONFIG_SCOPE_ID, SONAR_PROJECT_NAME)
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, fakeServerWithIssue, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);
    assertThat(statusCode).isEqualTo(200);

    verify(fakeClient, timeout(2000)).showIssue(eq(CONFIG_SCOPE_ID), any());
    verify(fakeClient, never()).showMessage(any(), any());
  }

  @SonarLintTest
  void it_should_not_assist_binding_if_multiple_suggestions(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, fakeServerWithIssue)
      //ACR-c35d316a41b2473296d48fa61d514c60
      .withUnboundConfigScope("configScopeA", SONAR_PROJECT_NAME + " 1")
      .withUnboundConfigScope("configScopeB", SONAR_PROJECT_NAME + " 2")
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, fakeServerWithIssue, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);

    assertThat(statusCode).isEqualTo(200);
    //ACR-5f315cf7b1c44cb49077dcded2f3b33a
    verify(fakeClient, timeout(1000)).noBindingSuggestionFound(any());
    verify(fakeClient, never()).showIssue(any(), any());
  }

  @SonarLintTest
  void it_should_assist_binding_if_multiple_suggestions_but_scopes_are_parent_and_child(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeConnection(CONNECTION_ID, fakeServerWithIssue)
      //ACR-68171cdf6e544caebbaade9e9219669d
      .withUnboundConfigScope("configScopeParent", SONAR_PROJECT_NAME)
      .withUnboundConfigScope("configScopeChild", SONAR_PROJECT_NAME, "configScopeParent")
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, fakeServerWithIssue, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, "configScopeParent", CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);

    assertThat(statusCode).isEqualTo(200);
    verify(fakeClient, timeout(2000)).showIssue(eq("configScopeParent"), any());
    verify(fakeClient, never()).showMessage(any(), any());
  }

  @SonarLintTest
  void it_should_assist_creating_the_connection_when_server_url_unknown(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID, SONAR_PROJECT_NAME)
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, fakeServerWithIssue, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME);
    assertThat(statusCode).isEqualTo(200);

    verify(fakeClient, timeout(2000)).showIssue(eq(CONFIG_SCOPE_ID), any());
    verify(fakeClient, never()).showMessage(any(), any());

    ArgumentCaptor<AssistCreatingConnectionParams> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(1000)).assistCreatingConnection(captor.capture(), any());
    assertThat(captor.getAllValues())
      .extracting(connectionParams -> connectionParams.getConnectionParams().getLeft().getServerUrl(),
        connectionParams -> connectionParams.getConnectionParams().getLeft() != null,
        AssistCreatingConnectionParams::getTokenName,
        AssistCreatingConnectionParams::getTokenValue)
      .containsExactly(tuple(fakeServerWithIssue.baseUrl(), true, null, null));
  }

  @SonarLintTest
  void it_should_assist_creating_the_connection_when_no_sc_connection(SonarLintTestHarness harness) throws Exception {
    var fakeClient = harness.newFakeClient().build();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri("https://sonar.my")
      .withUnboundConfigScope(CONFIG_SCOPE_ID, SONAR_PROJECT_NAME)
      .withBackendCapability(EMBEDDED_SERVER)
      .beforeInitialize(createdBackend -> {
        mockAssistCreatingConnection(createdBackend, fakeClient, fakeServerWithIssue, CONNECTION_ID);
        mockAssistBinding(createdBackend, fakeClient, CONFIG_SCOPE_ID, CONNECTION_ID, PROJECT_KEY);
      })
      .start(fakeClient);

    var statusCode = executeOpenSCIssueRequest(backend, ISSUE_KEY, PROJECT_KEY, BRANCH_NAME, "orgKey");
    assertThat(statusCode).isEqualTo(200);

    verify(fakeClient, timeout(2000)).showIssue(eq(CONFIG_SCOPE_ID), any());
    verify(fakeClient, never()).showMessage(any(), any());

    ArgumentCaptor<AssistCreatingConnectionParams> captor = ArgumentCaptor.captor();
    verify(fakeClient, timeout(1000)).assistCreatingConnection(captor.capture(), any());
    assertThat(captor.getAllValues())
      .extracting(connectionParams -> connectionParams.getConnectionParams().getRight().getOrganizationKey(),
        AssistCreatingConnectionParams::getTokenName,
        AssistCreatingConnectionParams::getTokenValue)
      .containsExactly(tuple("orgKey", null, null));
  }

  @SonarLintTest
  void it_should_fail_request_when_issue_parameter_missing(SonarLintTestHarness harness) throws Exception {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, "", PROJECT_KEY, BRANCH_NAME);

    assertThat(statusCode).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_request_when_project_parameter_missing(SonarLintTestHarness harness) throws Exception {
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start();
    var fakeServerWithIssue = fakeServerWithIssue(harness).start();

    var statusCode = executeOpenIssueRequest(backend, fakeServerWithIssue, ISSUE_KEY, "", "", "");

    assertThat(statusCode).isEqualTo(400);
  }

  @SonarLintTest
  void it_should_fail_request_when_server_points_to_sonarcloud_from_sqs(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start(client);
    var request = openIssueRequestWithOrigin(backend,
      SonarCloudRegion.EU.getProductionUri().toString(),
      "http://fake.sonar");

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    verify(client).showMessage(MessageType.ERROR,
      "Invalid request to SonarQube backend. The 'server' parameter should not be SonarQube Cloud URL, use it only to specify URL of a SonarQube Server.");
  }

  @SonarLintTest
  void it_should_fail_request_when_server_points_to_sonarcloud_us_from_sqs(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start(client);
    var request = openIssueRequestWithOrigin(backend,
      SonarCloudRegion.US.getProductionUri().toString(),
      "http://fake.sonar");

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    verify(client).showMessage(MessageType.ERROR,
      "Invalid request to SonarQube backend. The 'server' parameter should not be SonarQube Cloud URL, use it only to specify URL of a SonarQube Server.");
  }

  @SonarLintTest
  void it_should_not_fail_request_when_server_points_to_sonarcloud_from_sonarcloud(SonarLintTestHarness harness) throws IOException, InterruptedException {
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withBackendCapability(EMBEDDED_SERVER)
      .start(client);
    var request = openIssueRequestWithOrigin(backend,
      SonarCloudRegion.EU.getProductionUri().toString(),
      SonarCloudRegion.EU.getProductionUri().toString(),
      "&issue=" + ISSUE_KEY,
      "&project=" + PROJECT_KEY,
      "&branch=" + BRANCH_NAME,
      "&organizationKey=orgKey");

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
  }

  private int executeOpenIssueRequest(SonarLintTestRpcServer backend, ServerFixture.Server server, String issueKey, String projectKey, String branch)
    throws IOException, InterruptedException {
    HttpRequest request = openIssueRequest(backend, server.baseUrl(), "&issue=" + issueKey, "&project=" + projectKey, "&branch=" + branch);
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  private int executeOpenSCIssueRequest(SonarLintTestRpcServer backend, String issueKey, String projectKey, String branch, String organizationKey)
    throws IOException, InterruptedException {
    HttpRequest request = this.openIssueRequest(backend, "https://sonar.my", "&issue=" + issueKey, "&project=" + projectKey, "&branch=" + branch,
      "&organizationKey=" + organizationKey);
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  private int executeOpenIssueRequest(SonarLintTestRpcServer backend, ServerFixture.Server server, String issueKey, String projectKey, String branch, String pullRequest)
    throws IOException, InterruptedException {
    HttpRequest request = openIssueRequest(backend, server.baseUrl(), "&issue=" + issueKey, "&project=" + projectKey, "&branch=" + branch, "&pullRequest=" + pullRequest);
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  private HttpRequest openIssueRequest(SonarLintTestRpcServer backend, String baseUrl, String... params) {
    return HttpRequest.newBuilder()
      .uri(URI.create(
        "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/issues/show?server=" + baseUrl + String.join("", params)))
      .header("Origin", baseUrl)
      .GET().build();
  }

  private HttpRequest openIssueRequestWithOrigin(SonarLintTestRpcServer backend, String baseUrl, String origin, String... params) {
    return HttpRequest.newBuilder()
      .uri(URI.create(
        "http://localhost:" + backend.getEmbeddedServerPort() + "/sonarlint/api/issues/show?server=" + baseUrl + String.join("", params)))
      .header("Origin", origin)
      .GET().build();
  }

  private void mockAssistBinding(SonarLintTestRpcServer backend, SonarLintBackendFixture.FakeSonarLintRpcClient fakeClient, String configScopeId, String connectionId,
    String sonarProjectKey) {
    doAnswer((Answer<AssistBindingResponse>) invocation -> {
      backend.getConfigurationService().didUpdateBinding(new DidUpdateBindingParams(configScopeId, new BindingConfigurationDto(connectionId, sonarProjectKey, false)));
      return new AssistBindingResponse(configScopeId);
    }).when(fakeClient).assistBinding(any(), any());
  }

  private void mockAssistCreatingConnection(SonarLintTestRpcServer backend, SonarLintBackendFixture.FakeSonarLintRpcClient fakeClient, ServerFixture.Server server,
    String connectionId) {
    doAnswer((Answer<AssistCreatingConnectionResponse>) invocation -> {
      backend.getConnectionService().didUpdateConnections(
        new DidUpdateConnectionsParams(List.of(new SonarQubeConnectionConfigurationDto(connectionId, server.baseUrl(), true)), Collections.emptyList()));
      return new AssistCreatingConnectionResponse(connectionId);
    }).when(fakeClient).assistCreatingConnection(any(), any());
  }

  private static ServerFixture.AbstractServerBuilder fakeServerWithIssue(SonarLintTestHarness harness) {
    return harness.newFakeSonarQubeServer("10.2")
      .withProject(PROJECT_KEY,
        project -> {
          project.withProjectName(SONAR_PROJECT_NAME).withPullRequest("1234",
            pullRequest -> (ServerFixture.AbstractServerBuilder.ServerProjectBuilder.ServerProjectPullRequestBuilder) pullRequest
              .withIssue(PR_ISSUE_KEY, RULE_KEY, "msg", "author", "file/path", "OPEN", "", ISSUE_INTRODUCTION_DATE,
                new TextRange(1, 0, 3, 4))
              .withSourceFile("projectKey:file/path", sourceFile -> sourceFile.withCode("source\ncode\nfile\nfive\nlines")));
          return project.withBranch("branchName",
            branch -> {
              branch.withIssue(ISSUE_KEY, RULE_KEY, "msg", "author", "file/path", "OPEN", "", ISSUE_INTRODUCTION_DATE,
                new TextRange(1, 0, 3, 4));
              branch.withIssue(FILE_LEVEL_ISSUE_KEY, RULE_KEY, "msg", "author", "file/path", "OPEN", "", ISSUE_INTRODUCTION_DATE,
                new TextRange(0, 0, 0, 0));
              return branch.withSourceFile("projectKey:file/path", sourceFile -> sourceFile.withCode("source\ncode\nfile\nfive\nlines"));
            });
        });
  }
}
