/*
ACR-8373908f21d94881961410b56dce4b47
ACR-bb68eea06b4f4f9d898cd267d7d3d7b5
ACR-fb9a03a99df9453b9ba2641fe72f29de
ACR-ad5cdb0964734c2f83ebaaee7e5b6960
ACR-923f4a36fc074baa8c60e9c49d1834d8
ACR-00b3d421e172400b9a48fef597aca6f0
ACR-4d1f49be33e84cafa99a02cb1105927b
ACR-2173f5352a0641ae9b84356ff52e7637
ACR-7edea1246ee04687b04dffc59957ca3a
ACR-f9fa72f39f434a18b71ac61f15c79d6f
ACR-75b68f98db044c048d902da3f9c34a14
ACR-4284d91948f14473bc44b2088514f78e
ACR-e88224130a6f4a2ca640875d99704ee9
ACR-6051c29758cd40669edd3a75a3246011
ACR-72d8bf855b4942f4a2468b0552ce9b23
ACR-ac9e36ac171e4f318085742474035d59
ACR-40e8a53c49844f5cbfa3902f42cdacff
 */
package mediumtest;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.testutils.GitUtils;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.binding.GetBindingSuggestionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.DidUpdateConnectionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Components;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class BindingSuggestionsMediumTests {

  public static final String MYSONAR = "mysonar";
  public static final String CONFIG_SCOPE_ID = "myProject1";
  public static final String SLCORE_PROJECT_KEY = "org.sonarsource.sonarlint:sonarlint-core-parent";
  public static final String SLCORE_PROJECT_NAME = "SonarLint Core";
  public static final String PROJECT_ID = "123e4567-e89b-12d3-a456-426614174000";
  public static final String REMOTE_URL = "git@github.com:myorg/myproject.git";

  @RegisterExtension
  static WireMockExtension sonarqubeMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @BeforeEach
  void init() {
    sonarqubeMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"10.8\",\"status\": " +
        "\"UP\"}")));
  }

  @SonarLintTest
  void test_connection_added_should_suggest_binding_with_no_matches(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "My Project 1")
      .start(fakeClient);
    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("No connections configured, skipping binding suggestions."));

    backend.getConnectionService()
      .didUpdateConnections(new DidUpdateConnectionsParams(List.of(new SonarQubeConnectionConfigurationDto(MYSONAR, sonarqubeMock.baseUrl(), true)), List.of()));

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());
  }

  @SonarLintTest
  void test_connection_added_should_suggest_binding_with_matches(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "sonarlint-core")
      .start(fakeClient);
    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("No connections configured, skipping binding suggestions."));

    sonarqubeMock.stubFor(get("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.SearchWsResponse.newBuilder()
        .addComponents(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .setPaging(Common.Paging.newBuilder().setTotal(1).build())
        .build()))));

    backend.getConnectionService()
      .didUpdateConnections(new DidUpdateConnectionsParams(List.of(new SonarQubeConnectionConfigurationDto(MYSONAR, sonarqubeMock.baseUrl(), true)), List.of()));

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getValue();
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName,
        BindingSuggestionDto::isFromSharedConfiguration, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, false, BindingSuggestionOrigin.PROJECT_NAME));
  }

  @SonarLintTest
  void test_project_added_should_suggest_binding_with_matches(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sonarqubeMock.baseUrl())
      .start(fakeClient);

    sonarqubeMock.stubFor(get("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.SearchWsResponse.newBuilder()
        .addComponents(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .setPaging(Common.Paging.newBuilder().setTotal(1).build())
        .build()))));

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getValue();
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName,
        BindingSuggestionDto::isFromSharedConfiguration, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, false, BindingSuggestionOrigin.PROJECT_NAME));
  }

  @SonarLintTest
  void test_uses_binding_clues_when_initializing_fs(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var clue = tmp.resolve("sonar-project.properties");
    Files.writeString(clue, "sonar.projectKey=" + SLCORE_PROJECT_KEY + "\nsonar.projectName=" + SLCORE_PROJECT_NAME, StandardCharsets.UTF_8);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(new ClientFileDto(clue.toUri(), Paths.get("sonar-project.properties"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true)))
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sonarqubeMock.baseUrl())
      .withSonarQubeConnection("another")
      .start(fakeClient);

    sonarqubeMock.stubFor(get("/api/components/show.protobuf?component=org.sonarsource.sonarlint%3Asonarlint-core-parent")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.ShowWsResponse.newBuilder()
        .setComponent(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .build()))));

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getValue();
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName, BindingSuggestionDto::isFromSharedConfiguration)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, false));
  }

  @SonarLintTest
  void test_uses_binding_clues_when_updating_fs(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var fakeClient = harness.newFakeClient()
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sonarqubeMock.baseUrl())
      .withSonarQubeConnection("another")
      .start(fakeClient);

    sonarqubeMock.stubFor(get("/api/components/show.protobuf?component=org.sonarsource.sonarlint%3Asonarlint-core-parent")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.ShowWsResponse.newBuilder()
        .setComponent(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .build()))));

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    //ACR-e4d10709db16430cb38cbc7a6541d7f8
    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());

    //ACR-fdb8544f54ab409fa270b6cec0913650
    var clue = tmp.resolve("sonar-project.properties");
    Files.writeString(clue, "sonar.projectKey=" + SLCORE_PROJECT_KEY + "\nsonar.projectName=" + SLCORE_PROJECT_NAME, StandardCharsets.UTF_8);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(
      List.of(new ClientFileDto(clue.toUri(), Paths.get("sonar-project.properties"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name()
        , clue, null, null, true)),
      List.of(),
      List.of()
    ));

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getAllValues().get(0);
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName,
        BindingSuggestionDto::isFromSharedConfiguration, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, false, BindingSuggestionOrigin.PROPERTIES_FILE));
  }

  @SonarLintTest
  void test_binding_suggestion_via_service(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var fakeClient = harness.newFakeClient().build();
    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sonarqubeMock.baseUrl())
      .start(fakeClient);

    sonarqubeMock.stubFor(get("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.SearchWsResponse.newBuilder()
        .addComponents(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .setPaging(Common.Paging.newBuilder().setTotal(1).build())
        .build()))));

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    //ACR-b3535569e4ec4b10bafeda58e9707f37
    verify(fakeClient, timeout(5000)).suggestBinding(any());

    var bindingParamsCompletableFuture = backend.getBindingService().getBindingSuggestions(new GetBindingSuggestionParams(CONFIG_SCOPE_ID, MYSONAR));
    var bindingSuggestions = bindingParamsCompletableFuture.get().getSuggestions();
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName, BindingSuggestionDto::isFromSharedConfiguration)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, false));
  }

  @SonarLintTest
  void test_uses_binding_clues_from_shared_configuration_when_updating_fs(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var fakeClient = harness.newFakeClient()
      .build();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sonarqubeMock.baseUrl())
      .withSonarQubeConnection("another")
      .start(fakeClient);

    sonarqubeMock.stubFor(get("/api/components/show.protobuf?component=org.sonarsource.sonarlint%3Asonarlint-core-parent")
      .willReturn(aResponse().withResponseBody(protobufBody(Components.ShowWsResponse.newBuilder()
        .setComponent(Components.Component.newBuilder()
          .setKey(SLCORE_PROJECT_KEY)
          .setName(SLCORE_PROJECT_NAME)
          .build())
        .build()))));

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    //ACR-b58f2fc8dae84834a05cfdeee5ac6051
    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());

    //ACR-5d0cd56b5a274211a69e4985d924b477
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(
      List.of(new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true)),
      List.of(),
      List.of())
    );

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getAllValues().get(0);
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey, BindingSuggestionDto::getSonarProjectName,
        BindingSuggestionDto::isFromSharedConfiguration, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, true, BindingSuggestionOrigin.SHARED_CONFIGURATION));
  }

  @SonarLintTest
  void should_suggest_binding_by_remote_url_when_no_other_suggestions_found(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    } catch (Exception e) {
      throw new RuntimeException("Failed to setup Git repository", e);
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var scServer = harness.newFakeSonarCloudServer()
      .withOrganization("orgKey", organization ->
        organization.withProject(SLCORE_PROJECT_KEY, project -> project
          .withBranch("main")
          .withName(SLCORE_PROJECT_NAME)
          .withId(UUID.fromString(PROJECT_ID))
          .withBinding(REMOTE_URL)))
      .start();

    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarQubeCloudEuRegionApiUri(scServer.baseUrl())
      .withSonarCloudConnection(MYSONAR, "orgKey")
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .withTelemetryEnabled()
      .start(fakeClient);

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getValue();
    assertThat(backend.telemetryFileContent().getSuggestedRemoteBindingsCount()).isEqualTo(1);
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey,
        BindingSuggestionDto::getSonarProjectName, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, BindingSuggestionOrigin.REMOTE_URL));
  }

  @SonarLintTest
  void should_suggest_binding_by_remote_url_when_no_other_suggestions_found_for_sonarqube_server(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    } catch (Exception e) {
      throw new RuntimeException("Failed to setup Git repository", e);
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var sqServer = harness.newFakeSonarQubeServer()
      .withProject(SLCORE_PROJECT_KEY, project -> project
        .withBranch("main")
        .withProjectName(SLCORE_PROJECT_NAME)
        .withId(UUID.fromString(PROJECT_ID))
        .withBinding(REMOTE_URL))
      .start();

    var backend = harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sqServer.baseUrl())
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .withTelemetryEnabled()
      .start(fakeClient);

    ArgumentCaptor<Map<String, List<BindingSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestBinding(suggestionCaptor.capture());

    var bindingSuggestions = suggestionCaptor.getValue();
    assertThat(backend.telemetryFileContent().getSuggestedRemoteBindingsCount()).isEqualTo(1);
    assertThat(bindingSuggestions).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(bindingSuggestions.get(CONFIG_SCOPE_ID))
      .extracting(BindingSuggestionDto::getConnectionId, BindingSuggestionDto::getSonarProjectKey,
        BindingSuggestionDto::getSonarProjectName, BindingSuggestionDto::getOrigin)
      .containsExactly(tuple(MYSONAR, SLCORE_PROJECT_KEY, SLCORE_PROJECT_NAME, BindingSuggestionOrigin.REMOTE_URL));
  }

  @SonarLintTest
  void should_return_empty_when_sqc_project_bindings_is_null(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException, GitAPIException, URISyntaxException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var scServer = harness.newFakeSonarCloudServer()
      .withOrganization("orgKey", organization ->
        organization.withProject(SLCORE_PROJECT_KEY, project -> project.withBranch("main")))
      .withResponseCodes(codes -> codes.withStatusCode(500))
      .start();

    harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarQubeCloudEuRegionApiUri(scServer.baseUrl())
      .withSonarCloudConnection(MYSONAR, "orgKey")
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .start(fakeClient);

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());
  }

  @SonarLintTest
  void should_return_empty_when_sqs_project_bindings_is_null(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException, GitAPIException, URISyntaxException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var sqServer = harness.newFakeSonarQubeServer()
      .withProject(SLCORE_PROJECT_KEY, project -> project.withBranch("main"))
      .withResponseCodes(codes -> codes.withStatusCode(500))
      .start();

    harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sqServer.baseUrl())
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .start(fakeClient);

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());
  }

  @SonarLintTest
  void should_return_empty_when_sqc_search_response_is_null(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException, GitAPIException, URISyntaxException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var scServer = harness.newFakeSonarCloudServer()
      .withOrganization("orgKey", organization ->
        organization.withProject(SLCORE_PROJECT_KEY, project -> project
          .withBranch("main")
          .withName(SLCORE_PROJECT_NAME)
          .withId(UUID.fromString(PROJECT_ID))
          .withBinding(REMOTE_URL)))
      .withResponseCodes(codes -> codes.withStatusCode(500))
      .start();

    harness.newBackend()
      .withSonarQubeCloudEuRegionUri(scServer.baseUrl())
      .withSonarQubeCloudEuRegionApiUri(scServer.baseUrl())
      .withSonarCloudConnection(MYSONAR, "orgKey")
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .start(fakeClient);

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());
  }

  @SonarLintTest
  void should_return_empty_when_sqs_server_project_is_not_present(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException, GitAPIException, URISyntaxException {
    var gitRepo = tmp.resolve("git-repo");
    Files.createDirectory(gitRepo);

    try (var git = GitUtils.createRepository(gitRepo)) {
      git.remoteAdd()
        .setName("origin")
        .setUri(new URIish(REMOTE_URL))
        .call();
    }

    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, gitRepo, List.of())
      .build();

    var sqServer = harness.newFakeSonarQubeServer()
      .withDopTranslation(dop -> dop
        .withProjectBinding(REMOTE_URL, PROJECT_ID, SLCORE_PROJECT_KEY))
      .start();

    harness.newBackend()
      .withSonarQubeConnection(MYSONAR, sqServer.baseUrl())
      .withUnboundConfigScope(CONFIG_SCOPE_ID, "unmatched-project-name")
      .start(fakeClient);

    await().untilAsserted(() -> assertThat(fakeClient.getLogMessages()).contains("Found 0 suggestions for configuration scope '" + CONFIG_SCOPE_ID + "'"));
    verify(fakeClient, never()).suggestBinding(any());
  }
}
