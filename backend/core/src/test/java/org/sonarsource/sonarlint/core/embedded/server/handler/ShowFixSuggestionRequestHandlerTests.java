/*
ACR-a161ec16e12b4671abcbfa39e829c5a9
ACR-beee85396f9a447f8d2472a979dc5ec6
ACR-6bd32a6ef78c45ff8fb345c36e0d0bd3
ACR-519436feec3f4dac8f441251b1a7ba43
ACR-813d0b1c7109441eb6ff121d557b4344
ACR-50f4b40d08744b2dbf7e99e707c1e381
ACR-4b935009fda04b85804ab539e27053ca
ACR-153108b6f4a94318a18ff334f57fde51
ACR-72a8ba8459ba4352bdad9cd199f4b5af
ACR-2c9fbad9398349b297a88f991070c3b9
ACR-708c576e42dd4d0294be5a490247b7df
ACR-877196415cc04335bb364926fee40caf
ACR-bae4ca034a654554b109ada549f6b1c8
ACR-593f2cea59d94b3e98220199a7bd7453
ACR-bb6f7f23a60c4b3385c597c875bd46f7
ACR-89a59dfb06454bc5ac1aa38d94cf99e0
ACR-433d1ba58d8947339a4d239e973219cc
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
