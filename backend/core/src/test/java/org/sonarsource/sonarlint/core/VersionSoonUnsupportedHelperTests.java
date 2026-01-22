/*
ACR-d559491cc36742b39a271aef9850e5cf
ACR-abdcc780d5e7494b943035e122cd9eca
ACR-290882d1d9f249a69a50572b91daff60
ACR-b08e6eaf2edc45808f6838c379a39dc5
ACR-ba419ee672484c72a4e9beb82489dcee
ACR-4b851ab3ec3644ccab5c5e6dfbd3db45
ACR-0a2c29567fdf41fc933f2f0fa5e1c15d
ACR-e5068dc418f747cab4dfd959a12d9402
ACR-f987f840410b419e8936b274dbced0a4
ACR-86b48ae0a4254eb6873718c5f5ee713f
ACR-527178e322684adb9f943c6be1f15b44
ACR-44104b6559f94d1ea52ab616f6d5e237
ACR-4122c3fe744f4b33b9c0141e4fdb424f
ACR-0308e31f77d14c0382d780ac499895c9
ACR-dad286d2fd3e422a9aadaf913c36386c
ACR-51d280ea9623448db0b16b9a9b6b4870
ACR-35c8c1a0687b49d8b31648eeaf23a9f5
 */
package org.sonarsource.sonarlint.core;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScopeWithBinding;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverconnection.VersionUtils;
import org.sonarsource.sonarlint.core.sync.SynchronizationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled("SLCORE-685 Some tests fail depending on the current date")
class VersionSoonUnsupportedHelperTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String CONFIG_SCOPE_ID = "configScopeId";
  private static final String CONFIG_SCOPE_ID_2 = "configScopeId2";
  private static final String SQ_CONNECTION_ID = "sqConnectionId";
  private static final String SQ_CONNECTION_ID_2 = "sqConnectionId2";
  private static final String SC_CONNECTION_ID = "scConnectionId";
  private static final SonarQubeConnectionConfiguration SQ_CONNECTION = new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID, "https://mysonarqube.com", true);
  private static final SonarQubeConnectionConfiguration SQ_CONNECTION_2 = new SonarQubeConnectionConfiguration(SQ_CONNECTION_ID_2, "https://mysonarqube2.com", true);
  private static final SonarCloudConnectionConfiguration SC_CONNECTION = new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(),
    SonarCloudRegion.EU.getApiProductionUri(), SC_CONNECTION_ID, "https://sonarcloud.com", SonarCloudRegion.EU, true);

  private final SonarLintRpcClient client = mock(SonarLintRpcClient.class);
  private final SynchronizationService synchronizationService = mock(SynchronizationService.class);

  private ConfigurationRepository configRepository;
  private ConnectionConfigurationRepository connectionRepository;
  private VersionSoonUnsupportedHelper underTest;

  @BeforeEach
  void init() {
    configRepository = new ConfigurationRepository();
    connectionRepository = new ConnectionConfigurationRepository();
    underTest = new VersionSoonUnsupportedHelper(client, configRepository, mock(SonarQubeClientManager.class), connectionRepository, synchronizationService);
  }

  @Test
  void should_trigger_notification_when_new_binding_to_previous_lts_detected_on_config_scope_event() {
    var bindingConfiguration = new BindingConfiguration(SQ_CONNECTION_ID, "", true);
    configRepository.addOrReplace(new ConfigurationScope(CONFIG_SCOPE_ID, null, false, ""), bindingConfiguration);
    configRepository.addOrReplace(new ConfigurationScope(CONFIG_SCOPE_ID_2, null, false, ""), bindingConfiguration);
    connectionRepository.addOrReplace(SQ_CONNECTION);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), any(), any(SonarLintCancelMonitor.class)))
      .thenReturn(VersionUtils.getMinimalSupportedVersion());

    underTest.configurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope1"),
        BindingConfiguration.noBinding()),
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID_2, null, true, "scope2"),
        BindingConfiguration.noBinding()))));

    await().untilAsserted(() -> assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .containsOnly("Connection '" + SQ_CONNECTION_ID + "' with version '" + VersionUtils.getMinimalSupportedVersion().getName() + "' is detected to be soon unsupported"));
  }

  @Test
  void should_trigger_multiple_notification_when_new_bindings_to_previous_lts_detected_on_config_scope_event() {
    var bindingConfiguration = new BindingConfiguration(SQ_CONNECTION_ID, "", true);
    var bindingConfiguration2 = new BindingConfiguration(SQ_CONNECTION_ID_2, "", true);
    configRepository.addOrReplace(new ConfigurationScope(CONFIG_SCOPE_ID, null, false, ""), bindingConfiguration);
    configRepository.addOrReplace(new ConfigurationScope(CONFIG_SCOPE_ID_2, null, false, ""), bindingConfiguration2);
    connectionRepository.addOrReplace(SQ_CONNECTION);
    connectionRepository.addOrReplace(SQ_CONNECTION_2);
    var serverApi = mock(ServerApi.class);
    var serverApi2 = mock(ServerApi.class);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), eq(serverApi), any(SonarLintCancelMonitor.class)))
      .thenReturn(VersionUtils.getMinimalSupportedVersion());
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID_2), eq(serverApi2), any(SonarLintCancelMonitor.class)))
      .thenReturn(Version.create(VersionUtils.getMinimalSupportedVersion() + ".9"));

    underTest.configurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope1"),
        BindingConfiguration.noBinding()),
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID_2, null, true, "scope2"),
        BindingConfiguration.noBinding()))));

    await().untilAsserted(() -> assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .containsOnly(
        "Connection '" + SQ_CONNECTION_ID + "' with version '" + VersionUtils.getMinimalSupportedVersion().getName() + "' is detected to be soon unsupported",
        "Connection '" + SQ_CONNECTION_ID_2 + "' with version '" + VersionUtils.getMinimalSupportedVersion() + ".9' is detected to be soon unsupported"));
  }

  @Test
  void should_not_trigger_notification_when_config_scope_has_no_effective_binding() {
    underTest.configurationScopesAdded(new ConfigurationScopesAddedWithBindingEvent(Set.of(
      new ConfigurationScopeWithBinding(
        new ConfigurationScope(CONFIG_SCOPE_ID, null, true, "scope1"),
        BindingConfiguration.noBinding()))));

    assertThat(logTester.logs()).isEmpty();
  }

  @Test
  void should_trigger_notification_when_new_binding_to_previous_lts_detected() {
    connectionRepository.addOrReplace(SQ_CONNECTION);
    var serverApi = mock(ServerApi.class);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), eq(serverApi), any(SonarLintCancelMonitor.class)))
      .thenReturn(VersionUtils.getMinimalSupportedVersion());

    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SQ_CONNECTION_ID, "", false)));

    await().untilAsserted(() -> assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .containsOnly("Connection '" + SQ_CONNECTION_ID + "' with version '" + VersionUtils.getMinimalSupportedVersion().getName() + "' is detected to be soon unsupported"));
  }

  @Test
  void should_trigger_once_when_same_binding_to_previous_lts_detected_twice() {
    connectionRepository.addOrReplace(SQ_CONNECTION);
    var serverApi = mock(ServerApi.class);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), eq(serverApi), any(SonarLintCancelMonitor.class)))
      .thenReturn(VersionUtils.getMinimalSupportedVersion());

    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SQ_CONNECTION_ID, "", false)));
    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SQ_CONNECTION_ID, "", false)));

    await().untilAsserted(() -> assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .containsOnly("Connection '" + SQ_CONNECTION_ID + "' with version '" + VersionUtils.getMinimalSupportedVersion().getName() + "' is detected to be soon unsupported"));
  }

  @Test
  void should_trigger_notification_when_new_binding_to_in_between_lts_detected() {
    connectionRepository.addOrReplace(SQ_CONNECTION);
    var serverApi = mock(ServerApi.class);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), eq(serverApi), any(SonarLintCancelMonitor.class)))
      .thenReturn(Version.create(VersionUtils.getMinimalSupportedVersion().getName() + ".9"));

    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SQ_CONNECTION_ID, "", false)));

    await().untilAsserted(() -> assertThat(logTester.logs(LogOutput.Level.DEBUG))
      .containsOnly("Connection '" + SQ_CONNECTION_ID + "' with version '" + VersionUtils.getMinimalSupportedVersion().getName() + ".9' is detected to be soon unsupported"));
  }

  @Test
  void should_not_trigger_notification_when_new_binding_to_current_lts_detected() {
    connectionRepository.addOrReplace(SQ_CONNECTION);
    var serverApi = mock(ServerApi.class);
    when(synchronizationService.readOrSynchronizeServerVersion(eq(SQ_CONNECTION_ID), eq(serverApi), any(SonarLintCancelMonitor.class))).thenReturn(VersionUtils.getCurrentLts());

    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SQ_CONNECTION_ID, "", false)));

    assertThat(logTester.logs()).isEmpty();
  }

  @Test
  void should_not_trigger_notification_when_sonarcloud_binding_detected() {
    connectionRepository.addOrReplace(SC_CONNECTION);

    underTest.bindingConfigChanged(new BindingConfigChangedEvent(CONFIG_SCOPE_ID, null,
      new BindingConfiguration(SC_CONNECTION_ID, "", false)));

    assertThat(logTester.logs()).isEmpty();
  }

}
