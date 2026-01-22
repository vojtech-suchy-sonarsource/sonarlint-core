/*
ACR-123d7de7576448c1994969935aa5c0fd
ACR-c01cc2bc122c4f179d7ac5ac46a8c4b2
ACR-730262aa0e9e47c291908bfe5a9c6fca
ACR-acd8f27401cd4991800d812d1ce04216
ACR-cb4f5c460f5a4a6b867415373e922b52
ACR-b62a66115fee481a8d2b8b753773aafb
ACR-8e30dd58cee243bc97ea7be522a37572
ACR-8b08ca70458347f38cff2a3ca9b475b8
ACR-55ef2b1c427040889aac8c26242847fa
ACR-d8f1f36e725d4f778eb5148bd2e1dd6d
ACR-7d0f86cc2f9446788aba8889a4cf23aa
ACR-d14fee73eec14c2d9e4a8eaf803c8e48
ACR-77e139924fe24e86a123888ad0ccf7e7
ACR-d6c515d78a4b4afa8bece744a44cf08b
ACR-32df4b3986e54277a0f58ff8ff89ccd3
ACR-c8f40adb2b1e4fee880f2a70e4acb0bd
ACR-a2ff430a317543ee8b946f6f3332b5a0
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.util.EnumSet;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TelemetryServiceTests {

  private TelemetryManager telemetryManager;
  private TelemetryServerAttributesProvider telemetryServerAttributesProvider;
  private SonarLintRpcClient client;

  private TelemetryService underTest;

  @BeforeEach
  void setUp() {
    telemetryManager = mock(TelemetryManager.class);
    telemetryServerAttributesProvider = mock(TelemetryServerAttributesProvider.class);
    client = mock(SonarLintRpcClient.class);

    var init = mock(InitializeParams.class);
    when(init.getBackendCapabilities()).thenReturn(EnumSet.of(BackendCapability.TELEMETRY));
    when(init.isFocusOnNewCode()).thenReturn(false);

    when(telemetryManager.isTelemetryEnabledByUser()).thenReturn(true);

    var applicationEventPublisher = mock(ApplicationEventPublisher.class);
    underTest = spy(new TelemetryService(init, client, telemetryServerAttributesProvider, telemetryManager, applicationEventPublisher));
    doReturn(true).when(underTest).isEnabled();

    clearInvocations(telemetryManager);
  }

  @Test
  void should_increment_manual_counter_for_manual_binding() {
    underTest.addedManualBindings();

    ArgumentCaptor<Consumer<TelemetryLocalStorage>> captor = ArgumentCaptor.forClass(Consumer.class);
    verify(telemetryManager).updateTelemetry(captor.capture());
    var storage = mock(TelemetryLocalStorage.class);
    captor.getValue().accept(storage);
    verify(storage).incrementManualAddedBindingsCount();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void should_increment_remote_url_counter_for_assisted_remote_url() {
    underTest.acceptedBindingSuggestion(BindingSuggestionOrigin.REMOTE_URL);

    ArgumentCaptor<Consumer<TelemetryLocalStorage>> captor = ArgumentCaptor.forClass(Consumer.class);
    verify(telemetryManager).updateTelemetry(captor.capture());
    var storage = mock(TelemetryLocalStorage.class);
    captor.getValue().accept(storage);
    verify(storage).incrementNewBindingsRemoteUrlCount();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void should_increment_project_name_counter_for_assisted_project_name() {
    underTest.acceptedBindingSuggestion(BindingSuggestionOrigin.PROJECT_NAME);

    ArgumentCaptor<Consumer<TelemetryLocalStorage>> captor = ArgumentCaptor.forClass(Consumer.class);
    verify(telemetryManager).updateTelemetry(captor.capture());
    var storage = mock(TelemetryLocalStorage.class);
    captor.getValue().accept(storage);
    verify(storage).incrementNewBindingsProjectNameCount();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void should_increment_shared_config_counter_for_assisted_shared_config() {
    underTest.acceptedBindingSuggestion(BindingSuggestionOrigin.SHARED_CONFIGURATION);

    ArgumentCaptor<Consumer<TelemetryLocalStorage>> captor = ArgumentCaptor.forClass(Consumer.class);
    verify(telemetryManager).updateTelemetry(captor.capture());
    var storage = mock(TelemetryLocalStorage.class);
    captor.getValue().accept(storage);
    verify(storage).incrementNewBindingsSharedConfigurationCount();
    verifyNoMoreInteractions(storage);
  }

  @Test
  void should_increment_properties_file_counter_for_assisted_properties_file() {
    underTest.acceptedBindingSuggestion(BindingSuggestionOrigin.PROPERTIES_FILE);

    ArgumentCaptor<Consumer<TelemetryLocalStorage>> captor = ArgumentCaptor.forClass(Consumer.class);
    verify(telemetryManager).updateTelemetry(captor.capture());
    var storage = mock(TelemetryLocalStorage.class);
    captor.getValue().accept(storage);
    verify(storage).incrementNewBindingsPropertiesFileCount();
    verifyNoMoreInteractions(storage);
  }
}
