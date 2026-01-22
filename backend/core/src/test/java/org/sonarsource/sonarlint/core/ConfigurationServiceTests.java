/*
ACR-b83ebe23db9646a48a8d0032777c72ca
ACR-a3059ec34c1e4de5bffb665e9a8f2397
ACR-d015a9732ed34d419afa3529500546e8
ACR-c24f7789653e4b85992a4bce9eadb0bf
ACR-da6105fa7d124409b2d8a01ecb1620a0
ACR-646ee87621af4930b1b11917434c8457
ACR-fc7cbf496d244893a409f9fa17989238
ACR-e2ead155da3e48d88efabdbdb09ce092
ACR-651ed42b93a241bab633b47d23abaa9b
ACR-bd68a4a1a4804c18829851cea32ddf31
ACR-d1c57423bbe9431ab7567dc4a1cc3bcc
ACR-e595897bfe9547cea97e1c62f2e1897b
ACR-03716b26f4f7488f965083b55bb4912b
ACR-d00458279045405c8765b640fb6dd842
ACR-9cc7837f074f4834af5080c3fb21a58e
ACR-3c42773013304d2da7fd501cb3184ce8
ACR-a362e241f20d47f389c0ce1ea5413f32
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

    //ACR-76b6a664bb7f463b8275a6ec7c0db4b4
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
