/*
ACR-d90dd078780042be886498965438d6f6
ACR-44d604cfc9164fb8b2a9e5d2135853c3
ACR-d6e510412e59432eb5eea1a970f5c05c
ACR-f54ded8e4bbf49dca7954a57b28075d9
ACR-7ee82a64027a4db5a33f318b1991093f
ACR-c4170a1b0a854398bbbe2d2dfb0930ea
ACR-a68d298be7234f7dbcf32cbbd2375ea4
ACR-64db911d2ba84839aec248ae17d4d8f3
ACR-7f2d690f159149308ae57ecebf05f47b
ACR-37bfd38197ad46bf8ab7879d10d415f6
ACR-5f3ba716dc2c4188af9a320cd7113084
ACR-bc16104ea95f4f4582ad00c3de2ee1f8
ACR-e6dd436d436f4fe9b8e6483a70695ac5
ACR-7c9689a52d3c4683bba8eacb87d7d98c
ACR-69f05d9ca39c40948eb14723711d7c3a
ACR-80aef86b1c944cdca0b456dca33d9559
ACR-c5b803ca9d074cb7bea9f8a35554b1a4
 */
package org.sonarsource.sonarlint.core;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BindingClueProviderTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  public static final String SQ_CONNECTION_ID_1 = "sq1";
  public static final String SQ_CONNECTION_ID_2 = "sq2";
  public static final String SC_CONNECTION_ID_1 = "sc1";
  public static final String SC_CONNECTION_ID_2 = "sc2";
  private static final String PROJECT_KEY_1 = "myproject1";
  public static final String MY_ORG_1 = "myOrg1";
  public static final String MY_ORG_2 = "myOrg2";

  public static final String CONFIG_SCOPE_ID = "configScopeId";
  private final ConnectionConfigurationRepository connectionRepository = mock(ConnectionConfigurationRepository.class);
  private final ClientFileSystemService clientFs = mock(ClientFileSystemService.class);
  BindingClueProvider underTest = new BindingClueProvider(connectionRepository, clientFs, SonarCloudActiveEnvironment.prod());

  @Test
  void should_detect_sonar_scanner_for_sonarqube() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=http://mysonarqube.org\n")));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarQubeBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isNull();
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SQ_CONNECTION_ID_1);
  }

  @Test
  void should_detect_sonar_scanner_for_sonarqube_with_project_key() {
    mockFindFileByNamesInScope(
      List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=http://mysonarqube.org\nsonar.projectKey=" + PROJECT_KEY_1)));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
  }

  @Test
  void should_match_multiple_connections() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=http://mysonarqube.org\n")));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));
    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_2)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_2, "http://Mysonarqube.org/", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1, SQ_CONNECTION_ID_2), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SQ_CONNECTION_ID_1, SQ_CONNECTION_ID_2);
  }

  @Test
  void should_detect_sonar_scanner_for_sonarcloud_based_on_url() {
    mockFindFileByNamesInScope(
      List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=https://sonarcloud.io\nsonar.projectKey=" + PROJECT_KEY_1)));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_2)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_2, MY_ORG_2, SonarCloudRegion.EU, true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SC_CONNECTION_ID_2), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarCloudBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_1, SC_CONNECTION_ID_2);
  }

  @Test
  void should_detect_sonar_scanner_for_sonarcloud_based_on_url_and_region() {
    mockFindFileByNamesInScope(
      List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=https://sonarcloud.io\nsonar.projectKey=" + PROJECT_KEY_1 + "\nsonar.region=US")));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.US.getProductionUri(), SonarCloudRegion.US.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.US, true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarCloudBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_1);
    assertThat(bindingClueWithConnections1.getBindingClue().getClass()).isEqualTo(BindingClueProvider.SonarCloudBindingClue.class);
    var sonarCloudBindingClue = (BindingClueProvider.SonarCloudBindingClue) bindingClueWithConnections1.getBindingClue();
    assertThat(sonarCloudBindingClue.getRegion().name()).isEqualTo(SonarCloudRegion.US.name());
  }

  @Test
  void should_detect_sonar_scanner_for_sonarcloud_based_on_organization() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.organization=" + MY_ORG_2)));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_2)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_2, MY_ORG_2, SonarCloudRegion.EU, true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SC_CONNECTION_ID_2), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarCloudBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isNull();
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_2);
  }

  @Test
  void should_detect_autoscan_for_sonarcloud() {
    mockFindFileByNamesInScope(List.of(buildClientFile(".sonarcloud.properties", "path/to/.sonarcloud.properties", "sonar.projectKey=" + PROJECT_KEY_1)));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarCloudBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_1);
  }

  @Test
  void should_detect_autoscan_for_sonarcloud_and_region() {
    mockFindFileByNamesInScope(List.of(buildClientFile(".sonarcloud.properties", "path/to/.sonarcloud.properties", "sonar.projectKey=" + PROJECT_KEY_1 + "\nsonar.region=US")));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.US.getProductionUri(), SonarCloudRegion.US.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.US, true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.SonarCloudBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_1);
    assertThat(bindingClueWithConnections1.getBindingClue().getClass()).isEqualTo(BindingClueProvider.SonarCloudBindingClue.class);
    var sonarCloudBindingClue = (BindingClueProvider.SonarCloudBindingClue) bindingClueWithConnections1.getBindingClue();
    assertThat(sonarCloudBindingClue.getRegion().name()).isEqualTo(SonarCloudRegion.US.name());
  }

  @Test
  void should_detect_unknown_with_project_key() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.projectKey=" + PROJECT_KEY_1)));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).hasSize(1);
    var bindingClueWithConnections1 = bindingClueWithConnections.get(0);
    assertThat(bindingClueWithConnections1.getBindingClue()).isInstanceOf(BindingClueProvider.UnknownBindingClue.class);
    assertThat(bindingClueWithConnections1.getBindingClue().getSonarProjectKey()).isEqualTo(PROJECT_KEY_1);
    assertThat(bindingClueWithConnections1.getConnectionIds()).containsOnly(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1);
  }

  @Test
  void ignore_scanner_file_without_clue() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.sources=src")));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).isEmpty();
  }

  @Test
  void ignore_scanner_file_invalid_content() {
    mockFindFileByNamesInScope(List.of(buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "\\usonar.projectKey=" + PROJECT_KEY_1)));

    when(connectionRepository.getConnectionById(SC_CONNECTION_ID_1)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID_1, MY_ORG_1, SonarCloudRegion.EU, true));
    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SC_CONNECTION_ID_1, SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).isEmpty();
    assertThat(logTester.logs(LogOutput.Level.ERROR)).contains("Unable to parse content of file 'file://path/to/sonar-project.properties'");
  }

  @Test
  void should_not_detect_sonarlint_configuration_file_if_wrong_content() {
    mockFindSonarlintConfigurationFilesByScope(List.of(buildClientFile("connectedMode.json", "/path/to/.sonarlint/connectedMode.json", "{\"sonarCloudOrganization\": \"org\",\"sonarQubeUri\": \"http://mysonarqube.org\"}")));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).isEmpty();
  }

  @Test
  void should_not_detect_sonarlint_configuration_file_if_not_in_right_folder() {
    mockFindSonarlintConfigurationFilesByScope(List.of(buildClientFile("connectedMode.json", "/path/to/connections/connectedMode.json", "{\"projectKey\": \"pKey\",\"sonarQubeUri\": \"http://mysonarqube.org\"}")));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var bindingClueWithConnections = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(bindingClueWithConnections).isEmpty();
  }

  @Test
  void should_not_detect_sonarlint_configuration_file_if_not_json() {
    var file = new ClientFile(URI.create("/path/to/.sonarlint/connectedMode.txt"), CONFIG_SCOPE_ID, Path.of("/path/to/.sonarlint/connectedMode.txt"), false, null, null, null, true);

    assertThat(file.isSonarlintConfigurationFile()).isFalse();
  }

  @Test
  void should_not_detect_sonarlint_configuration_file_if_wrong_folder() {
    var file = new ClientFile(URI.create("/path/to/.sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, Path.of("/path/to/.sonarlint2/connectedMode.json"), false, null, null, null, true);

    assertThat(file.isSonarlintConfigurationFile()).isFalse();
  }

  @Test
  void should_set_origin_properties_file_when_clue_created_from_properties() {
    mockFindFileByNamesInScope(List.of(
      buildClientFile("sonar-project.properties", "path/to/sonar-project.properties", "sonar.host.url=http://mysonarqube.org\nsonar.projectKey=" + PROJECT_KEY_1)
    ));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var cluesWithConn = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(cluesWithConn).hasSize(1);
    var clue = cluesWithConn.get(0).getBindingClue();
    assertThat(clue.getOrigin()).isEqualTo(BindingSuggestionOrigin.PROPERTIES_FILE);
  }

  @Test
  void should_set_origin_shared_configuration_when_clue_created_from_shared_config() {
    //ACR-2a3bc77a5722470191a86e14659b1b4f
    var file = new ClientFile(URI.create("file:///path/to/.sonarlint/connectedMode.json"), CONFIG_SCOPE_ID, Paths.get("/path/to/.sonarlint/connectedMode.json"), false, null, null, null, true);
    file.setDirty("{\"projectKey\": \"" + PROJECT_KEY_1 + "\", \"sonarQubeUri\": \"http://mysonarqube.org\"}");
    mockFindSonarlintConfigurationFilesByScope(List.of(file));

    when(connectionRepository.getConnectionById(SQ_CONNECTION_ID_1)).thenReturn(new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_1, "http://mysonarqube.org", true));

    var cluesWithConn = underTest.collectBindingCluesWithConnections(CONFIG_SCOPE_ID, Set.of(SQ_CONNECTION_ID_1), new SonarLintCancelMonitor());

    assertThat(cluesWithConn).hasSize(1);
    var clue = cluesWithConn.get(0).getBindingClue();
    assertThat(clue.getOrigin()).isEqualTo(BindingSuggestionOrigin.SHARED_CONFIGURATION);
  }

  private ClientFile buildClientFile(String filename, String relativePath, String content) {
    var file = new ClientFile(URI.create("file://" + relativePath), CONFIG_SCOPE_ID, Paths.get(relativePath), false, null, null, null, true);
    file.setDirty(content);
    return file;
  }

  private void mockFindFileByNamesInScope(List<ClientFile> files) {
    when(clientFs.findFilesByNamesInScope(any(), any())).thenReturn(files);
  }

  private void mockFindSonarlintConfigurationFilesByScope(List<ClientFile> files) {
    when(clientFs.findSonarlintConfigurationFilesByScope(any())).thenReturn(files);
  }

}
