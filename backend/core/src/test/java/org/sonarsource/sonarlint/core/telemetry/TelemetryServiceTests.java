/*
ACR-a638b11f7a4244c089d2ba9d19890854
ACR-2931cf2f13b14bd08e981bf7429fb17b
ACR-c16feb8bc0564a609a5341d844dd9c71
ACR-a62d8920aac74e1cb70659767ca4f68f
ACR-c1cee310cd68429b88fbbee35d2dd1cb
ACR-a54ef7a3b87a44538806bd415ee67096
ACR-984f73354ce044899065f0deefdec342
ACR-937a559d5df4449c95fb05fdb3dff29c
ACR-deb2cfdbb57b416b913298c3d13d8695
ACR-a415c6f482c94992bc0326364646b58d
ACR-06bb9a2997134eba9d117005e3dde737
ACR-0111c46fe15346f48a4611b88db8e4d7
ACR-ddd652397628433592b313015235944d
ACR-4bccbf794919409784b822cdcc30afbb
ACR-76b35a22544349a79bfd37ac84fb16a0
ACR-b09f1f5d2c304e698552a4e95c5e11a2
ACR-0aae7869772543df9266af8d47bdfdff
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
