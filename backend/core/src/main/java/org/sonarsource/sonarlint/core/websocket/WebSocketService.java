/*
ACR-139bb67bfa2b4828a0e7e0c698c93290
ACR-1c182f7a2cb74e44ae05d7720353fc1f
ACR-52ba9f8a8f204cddbeb4fdeab8a95e5f
ACR-19e935de790f4c79bf4b8cc49abfeea0
ACR-17e8869c8cc34da6a0a3231c5c4bcd0f
ACR-7abf9323a1b242ba8816466a16389bf2
ACR-623801baee8246df804620c553ba88c3
ACR-57116763336547a3a08d4a4a6975444a
ACR-862194fec0f84d24a4c63f32644a6d75
ACR-5e69bdca289d4be8aad8985e7cdce1a7
ACR-794d71d9eb0940448dbc1944e5149176
ACR-7690f4ce75a54574b4936cc05da19b65
ACR-27c7fc74ab97401aacd9f2be553e5734
ACR-909298d2e80e4c97bff84d16d7db7ad2
ACR-354f870ee78c4c4da0ab1f571fd7b94a
ACR-60d923f65bc04ff59bd5faeecb35d717
ACR-1d83a1eecdba4b0da8a5d85e10ebcf3d
 */
package org.sonarsource.sonarlint.core.websocket;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.SonarCloudActiveEnvironment;
import org.sonarsource.sonarlint.core.SonarCloudRegion;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationAddedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionCredentialsChangedEvent;
import org.sonarsource.sonarlint.core.http.ConnectionAwareHttpClientProvider;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SERVER_SENT_EVENTS;

public class WebSocketService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final boolean shouldEnableWebSockets;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final ConfigurationRepository configurationRepository;
  private final Map<SonarCloudRegion, WebSocketManager> webSocketsByRegion;
  private final ExecutorService executorService = FailSafeExecutors.newSingleThreadExecutor("sonarlint-websocket-subscriber");

  public WebSocketService(ConnectionConfigurationRepository connectionConfigurationRepository, ConfigurationRepository configurationRepository,
    ConnectionAwareHttpClientProvider connectionAwareHttpClientProvider, InitializeParams params, SonarCloudActiveEnvironment sonarCloudActiveEnvironment,
    ApplicationEventPublisher eventPublisher) {
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.configurationRepository = configurationRepository;
    this.shouldEnableWebSockets = params.getBackendCapabilities().contains(SERVER_SENT_EVENTS);
    this.webSocketsByRegion = Map.of(
      SonarCloudRegion.US,
      new WebSocketManager(eventPublisher, connectionAwareHttpClientProvider, configurationRepository, sonarCloudActiveEnvironment.getWebSocketsEndpointUri(SonarCloudRegion.US)),
      SonarCloudRegion.EU,
      new WebSocketManager(eventPublisher, connectionAwareHttpClientProvider, configurationRepository, sonarCloudActiveEnvironment.getWebSocketsEndpointUri(SonarCloudRegion.EU))
    );
  }

  @EventListener
  public void handleEvent(BindingConfigChangedEvent bindingConfigChangedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    executorService.execute(() -> {
      considerScope(bindingConfigChangedEvent.configScopeId());
      //ACR-739c376efa9f41aca4a4c13d2478af04
      if (didChangeRegion(bindingConfigChangedEvent.previousConfig(), bindingConfigChangedEvent.newConfig())) {
        //ACR-c328e6349cc14992979c17e337c38815
        var previousRegion = ((SonarCloudConnectionConfiguration) connectionConfigurationRepository
          .getConnectionById(bindingConfigChangedEvent.previousConfig().connectionId())).getRegion();
        webSocketsByRegion.get(previousRegion).forget(bindingConfigChangedEvent.configScopeId());
        webSocketsByRegion.get(previousRegion).closeSocketIfNoMoreNeeded();
      }
    });
  }

  @EventListener
  public void handleEvent(ConfigurationScopesAddedWithBindingEvent configurationScopesAddedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    executorService.execute(() -> considerAllBoundConfigurationScopes(configurationScopesAddedEvent.getConfigScopeIds()));
  }

  @EventListener
  public void handleEvent(ConfigurationScopeRemovedEvent configurationScopeRemovedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    var removedConfigurationScopeId = configurationScopeRemovedEvent.getRemovedConfigurationScopeId();
    executorService.execute(() ->
      webSocketsByRegion.forEach((region, webSocketManager) -> {
        webSocketManager.forget(removedConfigurationScopeId);
        webSocketManager.closeSocketIfNoMoreNeeded();
      })
    );
  }

  @EventListener
  public void handleEvent(ConnectionConfigurationAddedEvent connectionConfigurationAddedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    //ACR-46ec57288c9842e78abf517a4edb620f
    executorService.execute(() -> considerConnection(connectionConfigurationAddedEvent.addedConnectionId()));
  }

  @EventListener
  public void handleEvent(ConnectionConfigurationUpdatedEvent connectionConfigurationUpdatedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    var updatedConnectionId = connectionConfigurationUpdatedEvent.updatedConnectionId();
    executorService.execute(() -> {
      if (didDisableNotifications(updatedConnectionId)) {
        webSocketsByRegion.forEach((region, webSocketManager) ->
          webSocketManager.forgetConnection(updatedConnectionId, "Notifications were disabled")
        );
      } else if (didEnableNotifications(updatedConnectionId)) {
        considerConnection(updatedConnectionId);
      }
    });
  }

  @EventListener
  public void handleEvent(ConnectionConfigurationRemovedEvent connectionConfigurationRemovedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    String removedConnectionId = connectionConfigurationRemovedEvent.getRemovedConnectionId();
    executorService.execute(() ->
      webSocketsByRegion.forEach((region, webSocketManager) ->
        webSocketManager.forgetConnection(removedConnectionId, "Connection was removed")
      )
    );
  }

  @EventListener
  public void handleEvent(ConnectionCredentialsChangedEvent connectionCredentialsChangedEvent) {
    if (!shouldEnableWebSockets) {
      return;
    }
    var connectionId = connectionCredentialsChangedEvent.getConnectionId();
    executorService.execute(() -> {
      if (isEligibleConnection(connectionId) && isInterestedInNotifications(connectionId)) {
        var region = ((SonarCloudConnectionConfiguration) connectionConfigurationRepository.getConnectionById(connectionId)).getRegion();
        webSocketsByRegion.get(region).reopenConnection(connectionId, "Credentials have changed");
      }
    });
  }

  private void considerConnection(String connectionId) {
    var configScopeIds = configurationRepository.getBoundScopesToConnection(connectionId)
      .stream().map(BoundScope::getConfigScopeId)
      .collect(Collectors.toSet());
    considerAllBoundConfigurationScopes(configScopeIds);
  }

  private void considerAllBoundConfigurationScopes(Set<String> configScopeIds) {
    for (String scopeId : configScopeIds) {
      considerScope(scopeId);
    }
  }

  private void considerScope(String scopeId) {
    var binding = getCurrentBinding(scopeId);
    if (binding != null && isEligibleConnection(binding.connectionId())) {
      var connection = requireNonNull(connectionConfigurationRepository.getConnectionById(binding.connectionId()));
      var region = ((SonarCloudConnectionConfiguration) connection).getRegion();
      webSocketsByRegion.get(region).subscribe(scopeId, binding);
    } else if (isSubscribedToAProject(scopeId)) {
      //ACR-1a060cfa43fe4d2f8b4a74cc448fcd78
      webSocketsByRegion.forEach((region, webSocketManager) -> {
        webSocketManager.forget(scopeId);
        webSocketManager.closeSocketIfNoMoreNeeded();
      });
    }
  }

  private boolean isInterestedInNotifications(String connectionId) {
    return webSocketsByRegion.values().stream().anyMatch(webSocketManager -> webSocketManager.isInterestedInNotifications(connectionId));
  }

  private boolean isEligibleConnection(String connectionId) {
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    return connection != null && connection.getKind().equals(ConnectionKind.SONARCLOUD) && !connection.isDisableNotifications();
  }

  private boolean didChangeRegion(BindingConfiguration previousBindingConfiguration, BindingConfiguration newBindingConfiguration) {
    var previousConnectionId = previousBindingConfiguration.connectionId();
    var previousConnection = previousConnectionId != null ? connectionConfigurationRepository.getConnectionById(previousConnectionId) : null;
    var newConnectionId = newBindingConfiguration.connectionId();
    var newConnection = newConnectionId != null ? connectionConfigurationRepository.getConnectionById(newConnectionId) : null;
    if (newConnection == null || previousConnection == null) {
      //ACR-f4cb9a6b32344078ab47ec5f3913a849
      return false;
    } else if (previousConnection instanceof SonarCloudConnectionConfiguration previousConn &&
      newConnection instanceof SonarCloudConnectionConfiguration newConn) {
      //ACR-42c6e3015b99447da13e366102896f26
      return previousConn.getRegion() != newConn.getRegion();
    }
    return false;
  }

  @CheckForNull
  private Binding getCurrentBinding(String configScopeId) {
    var bindingConfiguration = configurationRepository.getBindingConfiguration(configScopeId);
    if (bindingConfiguration != null && bindingConfiguration.isBound()) {
      return new Binding(requireNonNull(bindingConfiguration.connectionId()), requireNonNull(bindingConfiguration.sonarProjectKey()));
    }
    return null;
  }

  private boolean didDisableNotifications(String connectionId) {
    if (isInterestedInNotifications(connectionId)) {
      var connection = connectionConfigurationRepository.getConnectionById(connectionId);
      return connection != null && connection.getKind().equals(ConnectionKind.SONARCLOUD) && connection.isDisableNotifications();
    }
    return false;
  }

  private boolean didEnableNotifications(String connectionId) {
    return isEligibleConnection(connectionId) && !isInterestedInNotifications(connectionId);
  }

  private boolean isSubscribedToAProject(String configScopeId) {
    for (var webSocketManager : webSocketsByRegion.values()) {
      var subscribedProjectKey = webSocketManager.getSubscribedProjectKeysByConfigScopes().get(configScopeId);
      if (subscribedProjectKey != null) {
        //ACR-c812aeb77c814e1aa16bbe0977f85b71
        return true;
      }
    }
    return false;
  }

  public boolean hasOpenConnection(SonarCloudRegion region) {
    var sonarCloudWebSocket = webSocketsByRegion.get(region).getSonarCloudWebSocket();
    return sonarCloudWebSocket != null && sonarCloudWebSocket.isOpen();
  }

  @PreDestroy
  public void shutdown() {
    if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop websockets subscriber service in a timely manner");
    }
    webSocketsByRegion.forEach((region, webSocketManager) -> {
      webSocketManager.closeSocket("Backend is shutting down");
      webSocketManager.getSubscribedProjectKeysByConfigScopes().clear();
      webSocketManager.getConnectionIdsInterestedInNotifications().clear();
    });
  }
}
