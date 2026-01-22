/*
ACR-04a3eea6f79447f3abdfa20ad438cb26
ACR-d2b3ccb864084ab5ab073c7d3b773171
ACR-2daed93236bb410cb00a3517e47d55d9
ACR-e42520102c29457a8de870b3e8d7e469
ACR-c064d80f95914421943f379e10a19f42
ACR-2805016851f7405a80dd3887e422f4b8
ACR-01f378755eac4f658725019d2554bbf4
ACR-33193572f2c84f8189ea2e8a53d5e484
ACR-b18de9caa6b241678ec67e1727d99e63
ACR-1b7e44b7422042b49ce57e56851fc1dd
ACR-c4c2ca089d92447397a0437f6d876a50
ACR-545306c0cff940b89717cf7a194544a4
ACR-bc9d84434df948f9af6c98c4dcf8bde3
ACR-bb0be45199854bacbd2ffa76a35c378e
ACR-50f2ceb8bfd6486bba2557720ba956a3
ACR-ce5f7d510439472fad2a87bf08013b15
ACR-d129d0f12f674d739643310bc14d3b97
 */
package org.sonarsource.sonarlint.core;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.active.rules.ActiveRulesService;
import org.sonarsource.sonarlint.core.analysis.NodeJsService;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.SonarQubeConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryServerAttributesProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelemetryServerAttributesProviderTests {

  @Test
  void it_should_calculate_connectedMode_usesSC_notDisabledNotifications_telemetry_attrs() {
    var configurationScopeId = "scopeId";
    var connectionId = "connectionId";
    var projectKey = "projectKey";

    var configurationRepository = mock(ConfigurationRepository.class);
    when(configurationRepository.getAllBoundScopes()).thenReturn(Set.of(new BoundScope(configurationScopeId, connectionId, projectKey)));

    var connectionConfigurationRepository = mock(ConnectionConfigurationRepository.class);
    when(connectionConfigurationRepository.getConnectionById(connectionId)).thenReturn(new SonarCloudConnectionConfiguration(SonarCloudRegion.EU.getProductionUri(), SonarCloudRegion.EU.getApiProductionUri(), connectionId, "myTestOrg", SonarCloudRegion.EU, false));
    var underTest = new TelemetryServerAttributesProvider(configurationRepository, connectionConfigurationRepository, mock(ActiveRulesService.class), mock(RulesRepository.class), mock(NodeJsService.class), mock(StorageService.class));

    var telemetryLiveAttributes = underTest.getTelemetryServerLiveAttributes();
    assertThat(telemetryLiveAttributes.usesConnectedMode()).isTrue();
    assertThat(telemetryLiveAttributes.usesSonarCloud()).isTrue();
    assertThat(telemetryLiveAttributes.childBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeServerBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeCloudEUBindingCount()).isEqualTo(1);
    assertThat(telemetryLiveAttributes.sonarQubeCloudUSBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.devNotificationsDisabled()).isFalse();
    assertThat(telemetryLiveAttributes.nonDefaultEnabledRules()).isEmpty();
    assertThat(telemetryLiveAttributes.defaultDisabledRules()).isEmpty();
  }

  @Test
  void it_should_calculate_connectedMode_notUsesSC_disabledDevNotifications_telemetry_attrs() {
    var configurationScopeId_1 = "scopeId_1";
    var configurationScopeId_2 = "scopeId_2";
    var configurationScopeId_3 = "scopeId_3";
    var connectionId_1 = "connectionId_1";
    var connectionId_2 = "connectionId_2";
    var projectKey_1 = "projectKey1";
    var projectKey_2 = "projectKey2";

    var configurationRepository = mock(ConfigurationRepository.class);
    when(configurationRepository.getAllBoundScopes()).thenReturn(Set.of(
      new BoundScope(configurationScopeId_1, connectionId_1, projectKey_1),
      new BoundScope(configurationScopeId_2, connectionId_2, projectKey_2)));

    when(configurationRepository.getLeafConfigScopeIds()).thenReturn(Set.of(configurationScopeId_2, configurationScopeId_3));

    when(configurationRepository.getConfigurationScope(configurationScopeId_1)).thenReturn(new ConfigurationScope(configurationScopeId_1, null, false, "1"));
    when(configurationRepository.getConfigurationScope(configurationScopeId_2)).thenReturn(new ConfigurationScope(configurationScopeId_2, configurationScopeId_1, false, "2"));
    when(configurationRepository.getConfigurationScope(configurationScopeId_3)).thenReturn(new ConfigurationScope(configurationScopeId_3, configurationScopeId_1, false, "3"));
    when(configurationRepository.getBindingConfiguration(configurationScopeId_1)).thenReturn(new BindingConfiguration(configurationScopeId_1, projectKey_1, false));
    when(configurationRepository.getBindingConfiguration(configurationScopeId_2)).thenReturn(new BindingConfiguration(configurationScopeId_2, projectKey_2, false));
    when(configurationRepository.getBindingConfiguration(configurationScopeId_3)).thenReturn(new BindingConfiguration(null, null, false));

    var connectionConfigurationRepository = mock(ConnectionConfigurationRepository.class);
    when(connectionConfigurationRepository.getConnectionById(connectionId_1)).thenReturn(new SonarQubeConnectionConfiguration(connectionId_1, "www.squrl1.org", false));
    when(connectionConfigurationRepository.getConnectionById(connectionId_2)).thenReturn(new SonarQubeConnectionConfiguration(connectionId_2, "www.squrl2.org", true));
    var underTest = new TelemetryServerAttributesProvider(configurationRepository, connectionConfigurationRepository, mock(ActiveRulesService.class), mock(RulesRepository.class), mock(NodeJsService.class), mock(StorageService.class));

    var telemetryLiveAttributes = underTest.getTelemetryServerLiveAttributes();
    assertThat(telemetryLiveAttributes.usesConnectedMode()).isTrue();
    assertThat(telemetryLiveAttributes.usesSonarCloud()).isFalse();
    assertThat(telemetryLiveAttributes.childBindingCount()).isEqualTo(1);
    assertThat(telemetryLiveAttributes.sonarQubeServerBindingCount()).isEqualTo(2);
    assertThat(telemetryLiveAttributes.sonarQubeCloudEUBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeCloudUSBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.devNotificationsDisabled()).isTrue();
    assertThat(telemetryLiveAttributes.nonDefaultEnabledRules()).isEmpty();
    assertThat(telemetryLiveAttributes.defaultDisabledRules()).isEmpty();
  }

  @Test
  void it_should_calculate_disabledRules_enabledRules_telemetry_attrs() {
    var activeRulesService = mock(ActiveRulesService.class);
    when(activeRulesService.getStandaloneRuleConfig()).thenReturn(
      Map.of("ruleKey_1", new StandaloneRuleConfigDto(true, Map.of()),
        "ruleKey_2", new StandaloneRuleConfigDto(true, Map.of()),
        "ruleKey_3", new StandaloneRuleConfigDto(false, Map.of()),
        "ruleKey_4", new StandaloneRuleConfigDto(false, Map.of())));

    var rulesRepository = mock(RulesRepository.class);
    var sonarLintRuleDefinition_1 = getSonarLintRuleDefinition(true);
    var sonarLintRuleDefinition_2 = getSonarLintRuleDefinition(false);
    var sonarLintRuleDefinition_3 = getSonarLintRuleDefinition(true);
    var sonarLintRuleDefinition_4 = getSonarLintRuleDefinition(false);
    when(rulesRepository.getEmbeddedRule("ruleKey_1")).thenReturn(sonarLintRuleDefinition_1);
    when(rulesRepository.getEmbeddedRule("ruleKey_2")).thenReturn(sonarLintRuleDefinition_2);
    when(rulesRepository.getEmbeddedRule("ruleKey_3")).thenReturn(sonarLintRuleDefinition_3);
    when(rulesRepository.getEmbeddedRule("ruleKey_4")).thenReturn(sonarLintRuleDefinition_4);

    var underTest = new TelemetryServerAttributesProvider(mock(ConfigurationRepository.class), mock(ConnectionConfigurationRepository.class), activeRulesService, rulesRepository, mock(NodeJsService.class), mock(StorageService.class));
    var telemetryLiveAttributes = underTest.getTelemetryServerLiveAttributes();

    assertThat(telemetryLiveAttributes.nonDefaultEnabledRules()).containsExactly("ruleKey_2");
    assertThat(telemetryLiveAttributes.defaultDisabledRules()).containsExactly("ruleKey_3");
    assertThat(telemetryLiveAttributes.usesConnectedMode()).isFalse();
    assertThat(telemetryLiveAttributes.childBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeServerBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeCloudEUBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.sonarQubeCloudUSBindingCount()).isZero();
    assertThat(telemetryLiveAttributes.usesSonarCloud()).isFalse();
    assertThat(telemetryLiveAttributes.devNotificationsDisabled()).isFalse();
  }

  @Test
  void it_should_test_nodejs_version_telemetry_attr() {
    var nodeJsService = mock(NodeJsService.class);
    var version = "3.1.4.159";
    when(nodeJsService.getActiveNodeJsVersion()).thenReturn(Optional.of(Version.create(version)));
    var underTest = new TelemetryServerAttributesProvider(mock(ConfigurationRepository.class), mock(ConnectionConfigurationRepository.class),  mock(ActiveRulesService.class), mock(RulesRepository.class), nodeJsService, mock(StorageService.class));

    assertThat(underTest.getTelemetryServerLiveAttributes().nodeVersion()).isEqualTo(version);
  }

  @NotNull
  private static Optional<SonarLintRuleDefinition> getSonarLintRuleDefinition(boolean isActiveByDefault) {
    var sonarLintRuleDefinition = mock(SonarLintRuleDefinition.class);
    when(sonarLintRuleDefinition.isActiveByDefault()).thenReturn(isActiveByDefault);
    return Optional.of(sonarLintRuleDefinition);
  }
}
