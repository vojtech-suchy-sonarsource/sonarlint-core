/*
ACR-a2fc7c08f21a44f49310c94a7c453c29
ACR-0cdfd2a1cf9e4f63b6236b1bbf33820c
ACR-702c3e6d21b4403cad5fadd1ce906ace
ACR-624224e7041c4d4ab447db7ed02d2f01
ACR-8c127103907e4dc99e0a9558a1dfd4c4
ACR-47251528f82a4cb49305919cea78a735
ACR-172a679952824c3ba35f55bc7991b19c
ACR-29727b2d58f449c0a42885fd33f38861
ACR-0868daa39ae24e49b8a436637344ae98
ACR-5f053935e47b4280b032a6044c29ef5b
ACR-dc5d2299d2044ba987e1d0c33dcb4a59
ACR-0b47a6f98c534ba79fa24340a0b80c3a
ACR-73430d7b36574dfa9b7c9214628c3ad3
ACR-ddb68e356bbb4fe0b67df6fa33f83d12
ACR-dd6450703172488d839fc46f40fecb15
ACR-189e563eba5e4c8591cefd8c83b0ac21
ACR-ae64f4b048e045a093b1ccf2a16c777f
 */
package org.sonarsource.sonarlint.core.embedded.server.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.BindingCandidatesFinder;
import org.sonarsource.sonarlint.core.BindingSuggestionProvider;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.embedded.server.AttributeUtils;
import org.sonarsource.sonarlint.core.embedded.server.RequestHandlerBindingAssistant;
import org.sonarsource.sonarlint.core.event.FixSuggestionReceivedEvent;
import org.sonarsource.sonarlint.core.file.FilePathTranslation;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.serverconnection.ProjectBranches;
import org.sonarsource.sonarlint.core.sync.SonarProjectBranchesSynchronizationService;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowFixSuggestionRequestHandlerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);

  private ShowFixSuggestionRequestHandler showFixSuggestionRequestHandler;

  private ConnectionConfigurationRepository connectionConfigurationRepository;
  private ConfigurationRepository configurationRepository;
  private SonarLintRpcClient sonarLintRpcClient;
  private ApplicationEventPublisher eventPublisher;
  private ClientFile clientFile;
  private FilePathTranslation filePathTranslation;

  @BeforeEach
  void setup() {
    connectionConfigurationRepository = mock(ConnectionConfigurationRepository.class);
    configurationRepository = mock(ConfigurationRepository.class);
    var bindingSuggestionProvider = mock(BindingSuggestionProvider.class);
    var bindingCandidatesFinder = mock(BindingCandidatesFinder.class);
    sonarLintRpcClient = mock(SonarLintRpcClient.class);
    filePathTranslation = mock(FilePathTranslation.class);
    var pathTranslationService = mock(PathTranslationService.class);
    when(pathTranslationService.getOrComputePathTranslation(any())).thenReturn(Optional.of(filePathTranslation));
    var sonarCloudActiveEnvironment = SonarCloudActiveEnvironment.prod();
    eventPublisher = mock(ApplicationEventPublisher.class);
    var sonarProjectBranchesSynchronizationService = mock(SonarProjectBranchesSynchronizationService.class);
    when(sonarProjectBranchesSynchronizationService.getProjectBranches(any(), any(), any())).thenReturn(new ProjectBranches(Set.of(), "main"));
    clientFile = mock(ClientFile.class);
    var clientFs = mock(ClientFileSystemService.class);
    when(clientFs.getFiles(any())).thenReturn(List.of(clientFile));
    var connectionConfiguration = mock(ConnectionConfigurationRepository.class);
    when(connectionConfiguration.hasConnectionWithOrigin(SonarCloudRegion.EU.getProductionUri().toString())).thenReturn(true);

    showFixSuggestionRequestHandler = new ShowFixSuggestionRequestHandler(sonarLintRpcClient, eventPublisher,
      new RequestHandlerBindingAssistant(bindingSuggestionProvider, bindingCandidatesFinder, sonarLintRpcClient, connectionConfigurationRepository, configurationRepository,
        sonarCloudActiveEnvironment, connectionConfiguration),
      pathTranslationService, sonarCloudActiveEnvironment, clientFs);
  }

  @Test
  void should_trigger_telemetry() throws URISyntaxException, HttpException, IOException {
    var request = mock(ClassicHttpRequest.class);
    when(request.getUri()).thenReturn(URI.create("/sonarlint/api/fix/show" +
      "?project=org.sonarsource.sonarlint.core%3Asonarlint-core-parent" +
      "&issue=AX2VL6pgAvx3iwyNtLyr&branch=branch" +
      "&organizationKey=sample-organization"));
    when(request.getMethod()).thenReturn(Method.POST.name());
    when(request.getEntity()).thenReturn(new StringEntity("""
      {
        "fileEdit": {
          "path": "src/main/java/Main.java",
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
      """));
    var response = mock(ClassicHttpResponse.class);
    var context = mock(HttpContext.class);
    when(context.getAttribute(AttributeUtils.PARAMS_ATTRIBUTE))
      .thenReturn(Map.of(
        "project", "org.sonarsource.sonarlint.core:sonarlint-core-parent",
        "issue", "AX2VL6pgAvx3iwyNtLyr",
        "branch", "branch",
        "organizationKey", "sample-organization"
      ));
    when(context.getAttribute(AttributeUtils.ORIGIN_ATTRIBUTE))
      .thenReturn(SonarCloudRegion.EU.getProductionUri().toString());

    showFixSuggestionRequestHandler.handle(request, response, context);

    verify(eventPublisher, times(1)).publishEvent(any(FixSuggestionReceivedEvent.class));
  }

  @Test
  void should_extract_query_from_sc_request_without_token() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest("POST", "/sonarlint/api/fix/show" +
      "?project=org.sonarsource.sonarlint.core%3Asonarlint-core-parent" +
      "&issue=AX2VL6pgAvx3iwyNtLyr&branch=branch" +
      "&organizationKey=sample-organization");
    request.setEntity(new StringEntity("""
      {
        "fileEdit": {
          "path": "src/main/java/Main.java",
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
      """));
    var params = Map.of(
      "project", "org.sonarsource.sonarlint.core:sonarlint-core-parent",
      "issue", "AX2VL6pgAvx3iwyNtLyr",
      "branch", "branch",
      "organizationKey", "sample-organization"
    );
    
    var showFixSuggestionQuery = showFixSuggestionRequestHandler.extractQuery(request, SonarCloudRegion.EU.getProductionUri().toString(), params);
    
    assertThat(showFixSuggestionQuery.getServerUrl()).isEqualTo("https://sonarcloud.io");
    assertThat(showFixSuggestionQuery.getProjectKey()).isEqualTo("org.sonarsource.sonarlint.core:sonarlint-core-parent");
    assertThat(showFixSuggestionQuery.getIssueKey()).isEqualTo("AX2VL6pgAvx3iwyNtLyr");
    assertThat(showFixSuggestionQuery.getOrganizationKey()).isEqualTo("sample-organization");
    assertThat(showFixSuggestionQuery.getBranch()).isEqualTo("branch");
    assertThat(showFixSuggestionQuery.getTokenName()).isNull();
    assertThat(showFixSuggestionQuery.getTokenValue()).isNull();
    assertThat(showFixSuggestionQuery.getFixSuggestion().suggestionId()).isEqualTo("eb93b2b4-f7b0-4b5c-9460-50893968c264");
    assertThat(showFixSuggestionQuery.getFixSuggestion().explanation()).isEqualTo("Modifying the variable name is good");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().path()).isEqualTo("src/main/java/Main.java");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).before()).isEmpty();
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).after()).isEqualTo("var fix = 1;");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).beforeLineRange().startLine()).isZero();
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).beforeLineRange().endLine()).isEqualTo(1);
  }

  @Test
  void should_extract_query_from_sc_request_with_token() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest("POST", "/sonarlint/api/fix/show" +
      "?project=org.sonarsource.sonarlint.core%3Asonarlint-core-parent" +
      "&issue=AX2VL6pgAvx3iwyNtLyr&tokenName=abc" +
      "&organizationKey=sample-organization" +
      "&tokenValue=123");
    request.setEntity(new StringEntity("""
      {
        "fileEdit": {
          "path": "src/main/java/Main.java",
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
      """));
    var params = Map.of(
      "project", "org.sonarsource.sonarlint.core:sonarlint-core-parent",
      "issue", "AX2VL6pgAvx3iwyNtLyr",
      "tokenName", "abc",
      "organizationKey", "sample-organization",
      "tokenValue", "123");

    var showFixSuggestionQuery = showFixSuggestionRequestHandler.extractQuery(request, SonarCloudRegion.EU.getProductionUri().toString(), params);
    
    assertThat(showFixSuggestionQuery.getServerUrl()).isEqualTo("https://sonarcloud.io");
    assertThat(showFixSuggestionQuery.getProjectKey()).isEqualTo("org.sonarsource.sonarlint.core:sonarlint-core-parent");
    assertThat(showFixSuggestionQuery.getIssueKey()).isEqualTo("AX2VL6pgAvx3iwyNtLyr");
    assertThat(showFixSuggestionQuery.getTokenName()).isEqualTo("abc");
    assertThat(showFixSuggestionQuery.getOrganizationKey()).isEqualTo("sample-organization");
    assertThat(showFixSuggestionQuery.getTokenValue()).isEqualTo("123");
    assertThat(showFixSuggestionQuery.getFixSuggestion().suggestionId()).isEqualTo("eb93b2b4-f7b0-4b5c-9460-50893968c264");
    assertThat(showFixSuggestionQuery.getFixSuggestion().explanation()).isEqualTo("Modifying the variable name is good");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().path()).isEqualTo("src/main/java/Main.java");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).before()).isEmpty();
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).after()).isEqualTo("var fix = 1;");
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).beforeLineRange().startLine()).isZero();
    assertThat(showFixSuggestionQuery.getFixSuggestion().fileEdit().changes().get(0).beforeLineRange().endLine()).isEqualTo(1);
  }

  @Test
  void should_validate_fix_suggestion_query_for_sc() {
    assertThat(new ShowFixSuggestionRequestHandler.ShowFixSuggestionQuery(null, "project", "issue", "branch", "name", "value",
      "organizationKey", true, generateFixSuggestionPayload()).isValid()).isTrue();
    assertThat(
      new ShowFixSuggestionRequestHandler.ShowFixSuggestionQuery(null, "project", "issue", "branch", "name", "value", null, true, generateFixSuggestionPayload()).isValid())
        .isFalse();
  }

  @Test
  void should_show_fix_suggestion() throws HttpException, IOException {
    var request = new BasicClassicHttpRequest("POST", "/sonarlint/api/fix/show" +
      "?project=org.sonarsource.sonarlint.core%3Asonarlint-core-parent" +
      "&issue=AX2VL6pgAvx3iwyNtLyr" +
      "&organizationKey=sample-organization");
    request.setEntity(new StringEntity("""
      {
        "fileEdit": {
          "path": "src/main/java/Main.java",
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
      """));
    var response = mock(ClassicHttpResponse.class);
    var context = mock(HttpContext.class);
    when(context.getAttribute(AttributeUtils.PARAMS_ATTRIBUTE))
      .thenReturn(Map.of(
        "project", "org.sonarsource.sonarlint.core:sonarlint-core-parent",
        "issue", "AX2VL6pgAvx3iwyNtLyr",
        "branch", "branch",
        "organizationKey", "sample-organization"
      ));
    when(context.getAttribute(AttributeUtils.ORIGIN_ATTRIBUTE))
      .thenReturn(SonarCloudRegion.EU.getProductionUri().toString());

    when(clientFile.getUri()).thenReturn(URI.create("file:///src/main/java/Main.java"));
    when(filePathTranslation.serverToIdePath(any())).thenReturn(Path.of("src/main/java/Main.java"));
    when(connectionConfigurationRepository.findByOrganization(any())).thenReturn(List.of(
      new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), "name", "organizationKey", SonarCloudRegion.EU,
        false)));
    when(configurationRepository.getBoundScopesToConnectionAndSonarProject(any(), any())).thenReturn(List.of(new BoundScope("configScope", "connectionId", "projectKey")));

    showFixSuggestionRequestHandler.handle(request, response, context);

    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> verify(sonarLintRpcClient).showFixSuggestion(any()));
  }

  private static ShowFixSuggestionRequestHandler.FixSuggestionPayload generateFixSuggestionPayload() {
    return new ShowFixSuggestionRequestHandler.FixSuggestionPayload(
      new ShowFixSuggestionRequestHandler.FileEditPayload(
        List.of(new ShowFixSuggestionRequestHandler.ChangesPayload(
          new ShowFixSuggestionRequestHandler.TextRangePayload(0, 1),
          "before",
          "after")),
        "path"),
      "suggestionId",
      "explanation");
  }

}
