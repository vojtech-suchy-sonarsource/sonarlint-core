/*
ACR-606121aa52fe40a882602fd905f33744
ACR-66427d0c159c488d8adc884d0a0bc83e
ACR-5557e8a2978a4da4a59809488f6c9638
ACR-a24923b295cf47409f5dd17fb14a96c6
ACR-23ba2800767b40a68cf5cbb3d9347e08
ACR-3117ce928e304059b71e2e1ec30bf34c
ACR-59a4a92574734e448fb2f07b37f0cd43
ACR-3abec476270142abaddb6604c2fddd3e
ACR-038ac64e3d754633b4cdca78f1c8542f
ACR-3f5019ff9a994d3fb43705d639975c2b
ACR-dbda05499612439aa664ca4c6912dc31
ACR-2b5c8711e75448c6bdc6a8a31ab5bdc8
ACR-bfc7cc700419409daed4d469c849012f
ACR-6e2dbcb27dc0457e9f84658e8f599219
ACR-542c8de143bd40c983369afe8b4fb14a
ACR-c24e4f1e3a6c4c7eb26ab717aba396a1
ACR-810f345ee7e0407a824f682c573a6fbd
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
      //ACR-8971cb339dfb44908a0925e5b183e53f
      if (didChangeRegion(bindingConfigChangedEvent.previousConfig(), bindingConfigChangedEvent.newConfig())) {
        //ACR-d45dadf57e8e40d2a87bcc4f7eeba532
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
    //ACR-1532a479186640a79786156da60df034
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
      //ACR-7bff65c67e4540a9b40d7c780299acee
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
      //ACR-8b6090c67dea414991f6225cba754a82
      return false;
    } else if (previousConnection instanceof SonarCloudConnectionConfiguration previousConn &&
      newConnection instanceof SonarCloudConnectionConfiguration newConn) {
      //ACR-00331b53c58b4956938812773dcc92e2
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
        //ACR-24256b545b774994ad42be81334a02af
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
