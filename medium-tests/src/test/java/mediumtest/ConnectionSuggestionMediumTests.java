/*
ACR-d2d1f2d20ad74d50a98bf901a7d7c5ef
ACR-e46e60471a7b4a31a6afa80e3f3b6a36
ACR-e5352138a8ac475f87bad34320505331
ACR-2c259e9d9912416cacb369200526596a
ACR-c21997b13b77403eb5e1554bb656cda2
ACR-d7efc287268b416e93b60ff886b75382
ACR-fe7cfdbad07d4d128355e0f9359d1a7e
ACR-a0100bccf44248abbb5fe967a3a87aad
ACR-22b9afff37b14206ac21e3d166c88fbc
ACR-7c61c12256b940128e77c993a3f6bcea
ACR-dd1a9b49b9d34b69bead6045870804bb
ACR-96eeb775e6314f28bbdc319c8e986e35
ACR-5652c721bcbd4bd2908928b003c0eac5
ACR-ddad04153eda454ebc9d2fd507b40e8e
ACR-8302c5b1a8cb4387a519aa026e866d3a
ACR-7f847a632d2246eba17bfb9fa93c7370
ACR-309147f400054ce6b3861742e13b434c
 */
package mediumtest;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.DidAddConfigurationScopesParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.GetConnectionSuggestionsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class ConnectionSuggestionMediumTests {

  public static final String CONFIG_SCOPE_ID = "myProject1";
  public static final String SLCORE_PROJECT_KEY = "org.sonarsource.sonarlint:sonarlint-core-parent";
  public static final String ORGANIZATION = "Org";

  @RegisterExtension
  static WireMockExtension sonarqubeMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @SonarLintTest
  void should_suggest_sonarqube_connection_when_initializing_fs(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @ParameterizedTest(name = "Should not suggest connection setup for projectKey: {0}, SQ server URL: {1}, and SC organization key: {2}")
  @ExtendWith(SonarLintTestHarness.class)
  @MethodSource("emptyBindingSuggestionsTestValueProvider")
  void should_not_suggest_connection_for_empty_values(String projectKey, String serverUrl, String organizationKey, SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var projectKeyData = projectKey != null ? "\"projectKey\":\"" + projectKey + "\"," : "";
    var serverData = serverUrl != null ? "\"sonarQubeUri\":\"" + serverUrl + "\"" :
      organizationKey != null ? "\"sonarCloudOrganization\":\"" + organizationKey + "\"" : "";
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    var content = "{" + projectKeyData + serverData + "}";
    Files.writeString(clue, content, StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, List.of(fileDto))
      .build();

    var backend = harness.newBackend().start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    await().pollDelay(Duration.ofMillis(300)).untilAsserted(() -> assertThat(fakeClient.getSuggestionsByConfigScope()).isEmpty());
  }

  @ParameterizedTest(name = "Should not suggest connection setup for projectKey: {0}, SQ server URL: {1}, and SC organization key: {2}")
  @MethodSource("nonEmptyBindingSuggestionsTestValueProvider")
  @ExtendWith(SonarLintTestHarness.class)
  void should_suggest_connection_for_non_empty_values(String projectKey, String serverUrl, String organizationKey, SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var projectKeyData = projectKey != null ? "\"projectKey\":\"" + projectKey + "\"," : "";
    var serverData = serverUrl != null ? "\"sonarQubeUri\":\"" + serverUrl + "\"" :
      organizationKey != null ? "\"sonarCloudOrganization\":\"" + organizationKey + "\"" : "";
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    var content = "{" + projectKeyData + serverData + "}";
    Files.writeString(clue, content, StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend().start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(fakeClient.getSuggestionsByConfigScope()).hasSize(1));
  }

  @SonarLintTest
  void should_suggest_sonarcloud_connection_when_initializing_fs(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarCloudOrganization\": \"" + ORGANIZATION + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getOrganization()).isEqualTo(ORGANIZATION);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_suggest_connection_when_initializing_fs_for_csharp_project(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve("random");
    Files.createDirectory(sonarlintDir);
    sonarlintDir = tmp.resolve("random/path");
    Files.createDirectory(sonarlintDir);
    sonarlintDir = tmp.resolve("random/path/.sonarlint");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve("random/path/.sonarlint/random_name.json");
    Files.writeString(clue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get("random/path/.sonarlint/random_name.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_suggest_connection_when_initializing_fs_with_scanner_file(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var clue = tmp.resolve("sonar-project.properties");
    Files.writeString(clue, "sonar.host.url=" + sonarqubeMock.baseUrl() + "\nsonar.projectKey=" + SLCORE_PROJECT_KEY, StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get("sonar-project.properties"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isFalse();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.PROPERTIES_FILE);
  }

  @SonarLintTest
  void should_suggest_sonarcloud_connection_when_initializing_fs_with_scanner_file(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var clue = tmp.resolve(".sonarcloud.properties");
    Files.writeString(clue, "sonar.organization=" + ORGANIZATION + "\nsonar.projectKey=" + SLCORE_PROJECT_KEY, StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get("sonar-project.properties"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getOrganization()).isEqualTo(ORGANIZATION);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isFalse();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.PROPERTIES_FILE);
  }

  @SonarLintTest
  void should_suggest_connection_when_config_scope_added(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_suggest_connection_with_multiple_bindings_when_config_scope_added(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var sqClue = tmp.resolve(".sonarlint/connectedMode1.json");
    Files.writeString(sqClue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var scClue = tmp.resolve(".sonarlint/connectedMode2.json");
    Files.writeString(scClue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarCloudOrganization\": \"" + ORGANIZATION + "\"}", StandardCharsets.UTF_8);
    var sqFileDto = new ClientFileDto(sqClue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), sqClue, null, null, true);
    var scFileDto = new ClientFileDto(scClue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), scClue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(sqFileDto, scFileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(2);
    for (var suggestion : connectionSuggestion.get(CONFIG_SCOPE_ID)) {
      if (suggestion.getConnectionSuggestion().isLeft()) {
        assertThat(suggestion.getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
        assertThat(suggestion.getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
      } else {
        assertThat(suggestion.getConnectionSuggestion().getRight().getOrganization()).isEqualTo(ORGANIZATION);
        assertThat(suggestion.getConnectionSuggestion().getRight().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
      }
      assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
      assertThat(suggestion.getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
    }
  }

  @SonarLintTest
  void should_suggest_sonarlint_configuration_in_priority(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var sqClue = tmp.resolve(".sonarlint/connectedMode1.json");
    Files.writeString(sqClue, "{\"projectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"sonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var propertyClue = tmp.resolve("sonar-project.properties");
    Files.writeString(propertyClue, "sonar.host.url=https://sonarcloud.io\nsonar.projectKey=", StandardCharsets.UTF_8);
    var sqFileDto = new ClientFileDto(sqClue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), sqClue, null, null, true);
    var scFileDto = new ClientFileDto(propertyClue.toUri(), Paths.get("sonar-project.properties"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), propertyClue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(sqFileDto, scFileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getConfigurationService()
      .didAddConfigurationScopes(
        new DidAddConfigurationScopesParams(List.of(
          new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
            new BindingConfigurationDto(null, null, false)))));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_suggest_sonarqube_connection_when_pascal_case(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"ProjectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"SonarQubeUri\": \"" + sonarqubeMock.baseUrl() + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getServerUrl()).isEqualTo(sonarqubeMock.baseUrl());
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getLeft().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_suggest_sonarcloud_connection_when_pascal_case(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"ProjectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"SonarCloudOrganization\": \"" + ORGANIZATION + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    ArgumentCaptor<Map<String, List<ConnectionSuggestionDto>>> suggestionCaptor = ArgumentCaptor.forClass(Map.class);
    verify(fakeClient, timeout(5000)).suggestConnection(suggestionCaptor.capture());

    var connectionSuggestion = suggestionCaptor.getValue();
    assertThat(connectionSuggestion).containsOnlyKeys(CONFIG_SCOPE_ID);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID)).hasSize(1);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight()).isNotNull();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getOrganization()).isEqualTo(ORGANIZATION);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getConnectionSuggestion().getRight().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.get(CONFIG_SCOPE_ID).get(0).getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_return_list_of_suggestions_when_requested(SonarLintTestHarness harness, @TempDir Path tmp) throws IOException {
    var sonarlintDir = tmp.resolve(".sonarlint/");
    Files.createDirectory(sonarlintDir);
    var clue = tmp.resolve(".sonarlint/connectedMode.json");
    Files.writeString(clue, "{\"ProjectKey\": \"" + SLCORE_PROJECT_KEY + "\",\"SonarCloudOrganization\": \"" + ORGANIZATION + "\"}", StandardCharsets.UTF_8);
    var fileDto = new ClientFileDto(clue.toUri(), Paths.get(".sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, null, StandardCharsets.UTF_8.name(), clue, null, null, true);
    var fakeClient = harness.newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID,
        List.of(fileDto))
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getFileService().didUpdateFileSystem(new DidUpdateFileSystemParams(List.of(fileDto), Collections.emptyList(), Collections.emptyList()));

    var connectionSuggestions = backend.getConnectionService().getConnectionSuggestions(new GetConnectionSuggestionsParams(CONFIG_SCOPE_ID)).join();

    assertThat(connectionSuggestions.getConnectionSuggestions()).hasSize(1);
    var connectionSuggestion = connectionSuggestions.getConnectionSuggestions().get(0);
    assertThat(connectionSuggestion.getConnectionSuggestion().getRight()).isNotNull();
    assertThat(connectionSuggestion.getConnectionSuggestion().getRight().getOrganization()).isEqualTo(ORGANIZATION);
    assertThat(connectionSuggestion.getConnectionSuggestion().getRight().getProjectKey()).isEqualTo(SLCORE_PROJECT_KEY);
    assertThat(connectionSuggestion.isFromSharedConfiguration()).isTrue();
    assertThat(connectionSuggestion.getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  @SonarLintTest
  void should_return_empty_suggestions_list_when_no_clues(SonarLintTestHarness harness) {
    var fakeClient = harness.newFakeClient()
      .build();

    var backend = harness.newBackend()
      .start(fakeClient);

    backend.getConfigurationService().didAddConfigurationScopes(new DidAddConfigurationScopesParams(List.of(
      new ConfigurationScopeDto(CONFIG_SCOPE_ID, null, true, "sonarlint-core",
        null))));

    var connectionSuggestions = backend.getConnectionService().getConnectionSuggestions(new GetConnectionSuggestionsParams(CONFIG_SCOPE_ID)).join();

    assertThat(connectionSuggestions.getConnectionSuggestions()).isEmpty();
  }

  private static Stream<Arguments> emptyBindingSuggestionsTestValueProvider() {
    return Stream.of(
      Arguments.of("", "", ""),
      Arguments.of("", "", null),
      Arguments.of("", "", "foo"),
      Arguments.of("", "", "foo"),
      Arguments.of("", null, ""),
      Arguments.of(null, "", ""),
      Arguments.of("", null, null),
      Arguments.of(null, "", null),
      Arguments.of(null, null, ""),
      Arguments.of(null, "foo", "bar")
    );
  }

  public static Stream<Arguments> nonEmptyBindingSuggestionsTestValueProvider() {
    return Stream.of(
      Arguments.of("", "foo", ""),
      Arguments.of("", "foo", null),
      Arguments.of("", null, "foo"),
      Arguments.of("key", "foo", ""),
      Arguments.of("key", "foo", null),
      Arguments.of("key", null, "foo"),
      Arguments.of("key", "", "foo")
    );
  }

}
