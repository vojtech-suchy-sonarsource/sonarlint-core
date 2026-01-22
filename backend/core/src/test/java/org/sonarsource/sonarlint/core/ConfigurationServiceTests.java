/*
ACR-42e07500ddee4bd9b65dca02e8d3ff8b
ACR-f22670a939d54e999a2f19ac4a9da219
ACR-4b5aa8d9e07142e5aad232ebe9f68084
ACR-aa8261b88d3d4f89b6c47b62ccb12e52
ACR-a9455e6ac85a43118b645e63eacc724c
ACR-d0d0d8b36a84485b9742a7a1aa14fae6
ACR-dbeef1b11a1847b69c0dbd637c24e472
ACR-25461a0401074d80b7b5e8e182fd22db
ACR-f9afcfac8e1243a6bd7053ce956016fb
ACR-5ea90a76e9314e31a4cf779eb6c55dd3
ACR-3d4cbd88b0424492976534372a2c7b9c
ACR-86ad3a88d5d44f62849645ea6ca4face
ACR-4b818c62ef5c4fada9a8fc954c7b63ce
ACR-03e800cbcadf498aae05dada49c591e1
ACR-357e7f3ad0444f62af50dec65cbc47c9
ACR-3a305b4f7c104093b58d8c743749b0cd
ACR-82a77097619e4e6ca0cbf2297d67c07c
 */
package org.sonarsource.sonarlint.core;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConfigurationServiceTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String CONNECTION_1 = "connection1";
  private static final String CONNECTION_2 = "connection2";
  public static final BindingConfigurationDto BINDING_DTO_1 = new BindingConfigurationDto(CONNECTION_1, "projectKey1", false);
  public static final BindingConfigurationDto BINDING_DTO_2 = new BindingConfigurationDto(CONNECTION_1, "projectKey2", true);
  public static final BindingConfigurationDto BINDING_DTO_3 = new BindingConfigurationDto(CONNECTION_2, "projectKey3", true);
  public static final ConfigurationScopeDto CONFIG_DTO_1 = new ConfigurationScopeDto("id1", null, true, "Scope 1", BINDING_DTO_1);
  public static final ConfigurationScopeDto CONFIG_DTO_1_DUP = new ConfigurationScopeDto("id1", null, false, "Scope 1 dup", BINDING_DTO_2);
  public static final ConfigurationScopeDto CONFIG_DTO_2 = new ConfigurationScopeDto("id2", null, true, "Scope 2", BINDING_DTO_2);
  public static final ConfigurationScopeDto CONFIG_DTO_3 = new ConfigurationScopeDto("id3", null, true, "Scope 2", BINDING_DTO_3);
  private final ConfigurationRepository repository = new ConfigurationRepository();

  private ApplicationEventPublisher eventPublisher;
  private ConfigurationService underTest;

  @BeforeEach
  void setUp() {
    eventPublisher = mock(ApplicationEventPublisher.class);
    underTest = new ConfigurationService(eventPublisher, repository);
  }

  @Test
  void initialize_empty() {
    assertThat(repository.getConfigScopeIds()).isEmpty();
  }

  @Test
  void get_binding_of_unknown_config_returns_null() {
    assertThat(repository.getBindingConfiguration("not_found")).isNull();
  }

  @Test
  void add_configuration_should_post_event() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_2));

    assertThat(repository.getConfigScopeIds()).containsOnly("id2");
    assertThat(repository.getBindingConfiguration("id2")).usingRecursiveComparison().isEqualTo(BINDING_DTO_2);

    ArgumentCaptor<ConfigurationScopesAddedWithBindingEvent> captor = ArgumentCaptor.forClass(ConfigurationScopesAddedWithBindingEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    var event = captor.getValue();

    assertThat(event.getConfigScopeIds()).containsOnly("id2");
  }

  @Test
  void add_multiple_configurations_should_post_batch_event() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1, CONFIG_DTO_2));

    assertThat(repository.getConfigScopeIds()).containsOnly("id1", "id2");
    assertThat(repository.getBindingConfiguration("id1")).usingRecursiveComparison().isEqualTo(BINDING_DTO_1);
    assertThat(repository.getBindingConfiguration("id2")).usingRecursiveComparison().isEqualTo(BINDING_DTO_2);

    ArgumentCaptor<ConfigurationScopesAddedWithBindingEvent> captor = ArgumentCaptor.forClass(ConfigurationScopesAddedWithBindingEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    var event = captor.getValue();

    assertThat(event.getConfigScopeIds()).containsOnly("id1", "id2");
  }

  @Test
  void add_duplicate_should_log_and_update() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1));
    assertThat(repository.getBindingConfiguration("id1")).usingRecursiveComparison().isEqualTo(BINDING_DTO_1);

    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1_DUP));

    assertThat(repository.getConfigScopeIds()).containsOnly("id1");
    assertThat(repository.getBindingConfiguration("id1")).usingRecursiveComparison().isEqualTo(BINDING_DTO_2);

    assertThat(logTester.logs(LogOutput.Level.ERROR)).containsExactly("Duplicate configuration scope registered: id1");
  }

  @Test
  void remove_configuration() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1));
    assertThat(repository.getConfigScopeIds()).containsOnly("id1");

    underTest.didRemoveConfigurationScope("id1");

    assertThat(repository.getConfigScopeIds()).isEmpty();
  }

  @Test
  void remove_unknown_configuration_should_log() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1));
    assertThat(repository.getConfigScopeIds()).containsOnly("id1");

    underTest.didRemoveConfigurationScope("id2");

    assertThat(repository.getConfigScopeIds()).containsOnly("id1");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).contains("Attempt to remove configuration scope 'id2' that was not registered");
    assertThat(logTester.logs(LogOutput.Level.ERROR)).isEmpty();
  }

  @Test
  void update_binding_config_and_post_event() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1));
    assertThat(repository.getBindingConfiguration("id1")).usingRecursiveComparison().isEqualTo(BINDING_DTO_1);

    //ACR-8e65f1bba99f49e2b36afc980ef0fe46
    Mockito.reset(eventPublisher);

    underTest.didUpdateBinding("id1", BINDING_DTO_2);

    assertThat(repository.getConfigScopeIds()).containsOnly("id1");
    assertThat(repository.getBindingConfiguration("id1")).usingRecursiveComparison().isEqualTo(BINDING_DTO_2);

    ArgumentCaptor<BindingConfigChangedEvent> captor = ArgumentCaptor.forClass(BindingConfigChangedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    var event = captor.getValue();

    assertThat(event.configScopeId()).isEqualTo("id1");
    assertThat(event.previousConfig().connectionId()).isEqualTo(CONNECTION_1);
    assertThat(event.previousConfig().sonarProjectKey()).isEqualTo("projectKey1");
    assertThat(event.previousConfig().bindingSuggestionDisabled()).isFalse();

    assertThat(event.newConfig().connectionId()).isEqualTo(CONNECTION_1);
    assertThat(event.newConfig().sonarProjectKey()).isEqualTo("projectKey2");
    assertThat(event.newConfig().bindingSuggestionDisabled()).isTrue();
  }

  @Test
  void update_binding_config_for_unknown_config_scope_should_log() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1));

    underTest.didUpdateBinding("id2", BINDING_DTO_2);

    assertThat(logTester.logs(LogOutput.Level.ERROR)).containsExactly("Attempt to update binding in configuration scope 'id2' that was not registered");
  }

  @Test
  void should_clear_binding_if_connection_removed() {
    underTest.didAddConfigurationScopes(List.of(CONFIG_DTO_1, CONFIG_DTO_3));
    assertThat(repository.getConfigScopeIds()).containsOnly("id1", "id3");

    underTest.connectionRemoved(new ConnectionConfigurationRemovedEvent(CONNECTION_1));
    assertThat(repository.getAllBoundScopes()).hasSize(1);
    assertThat(repository.getBoundScope("id3"))
      .isNotNull()
      .extracting(BoundScope::getConnectionId).isEqualTo(CONNECTION_2);
    assertThat(repository.getBindingConfiguration(CONFIG_DTO_1.getId()))
      .extracting(BindingConfiguration::connectionId, BindingConfiguration::sonarProjectKey, BindingConfiguration::bindingSuggestionDisabled)
      .containsExactly(null, null, false);
    assertThat(repository.getBindingConfiguration(CONFIG_DTO_3.getId()))
      .extracting(BindingConfiguration::connectionId, BindingConfiguration::sonarProjectKey, BindingConfiguration::bindingSuggestionDisabled)
      .containsExactly(BINDING_DTO_3.getConnectionId(), BINDING_DTO_3.getSonarProjectKey(), BINDING_DTO_3.isBindingSuggestionDisabled());
  }
}
