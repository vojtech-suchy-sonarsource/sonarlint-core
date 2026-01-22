/*
ACR-63de1e0038f94e4282b7b7fb7e767152
ACR-7a776cb9af334d999d323ed9c93ebae3
ACR-5d472962256e49058e8866d7cd86ae75
ACR-ec8f618ceb844f96a5d78f3af9c17525
ACR-15de83ae69ae4d0b92ebb64f8cb50fbb
ACR-f8d88b8691c94f2fb41db309a141177f
ACR-52ff45d47a9b40c8b320b836be9e0800
ACR-f01430448ab245e0bff150e707dcf8e7
ACR-cfa5d55a085f4622bd12ee5682a0d021
ACR-31a0dc58372c4800ac773184451c0db3
ACR-51d257edfb974a52a29ed916567d1804
ACR-765c54237fd64cb7bcd35bc3360a647d
ACR-ff09a05c05c6480db40176bd6f3d8d57
ACR-7cae32afca50420f88915100c5738379
ACR-25c6ca0d63864e59a33419257fdac975
ACR-2c79017549ac4672834017095591fc5f
ACR-2c9e0d1b36ad4cd186224196b1af4d1a
 */
package org.sonarsource.sonarlint.core.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.http.ConnectionAwareHttpClientProvider;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;
import org.springframework.context.ApplicationEventPublisher;

public class WebSocketManager {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private SonarCloudWebSocket sonarCloudWebSocket;
  private final Set<String> connectionIdsInterestedInNotifications = new HashSet<>();
  private String connectionIdUsedToCreateConnection;
  private final Map<String, String> subscribedProjectKeysByConfigScopes = new HashMap<>();
  private final ExecutorService executorService = FailSafeExecutors.newSingleThreadExecutor("sonarlint-websocket-subscriber");
  private final ApplicationEventPublisher eventPublisher;
  private final ConnectionAwareHttpClientProvider connectionAwareHttpClientProvider;
  private final ConfigurationRepository configurationRepository;
  private final URI websocketEndpointUri;

  public WebSocketManager(ApplicationEventPublisher eventPublisher, ConnectionAwareHttpClientProvider connectionAwareHttpClientProvider,
    ConfigurationRepository configurationRepository, URI websocketEndpointUri) {
    this.eventPublisher = eventPublisher;
    this.connectionAwareHttpClientProvider = connectionAwareHttpClientProvider;
    this.configurationRepository = configurationRepository;
    this.websocketEndpointUri = websocketEndpointUri;
  }

  private void handleSonarServerEvent(SonarServerEvent event) {
    connectionIdsInterestedInNotifications.forEach(id -> eventPublisher.publishEvent(new SonarServerEventReceivedEvent(id, event)));
  }

  public void forgetConnection(String connectionId, String reason) {
    var previouslyInterestedInNotifications = connectionIdsInterestedInNotifications.remove(connectionId);
    if (!previouslyInterestedInNotifications) {
      return;
    }
    if (connectionIdsInterestedInNotifications.isEmpty()) {
      closeSocket(reason);
      subscribedProjectKeysByConfigScopes.clear();
    } else if (this.connectionIdUsedToCreateConnection.equals(connectionId)) {
      //ACR-1c12c2a4c7e744ae900ff6b71e6d61f2
      var otherConnectionId = connectionIdsInterestedInNotifications.stream().findAny().orElseThrow();
      removeProjectsFromSubscriptionListForConnection(connectionId);
      this.reopenConnection(otherConnectionId, reason + ", reopening for other SC connection");
    } else {
      configurationRepository.getBoundScopesToConnection(connectionId)
        .forEach(configScope -> forget(configScope.getConfigScopeId()));
    }
  }

  private void removeProjectsFromSubscriptionListForConnection(String updatedConnectionId) {
    var configurationScopesToUnsubscribe = configurationRepository.getBoundScopesToConnection(updatedConnectionId);
    for (var configScope : configurationScopesToUnsubscribe) {
      subscribedProjectKeysByConfigScopes.remove(configScope.getConfigScopeId());
    }
  }

  public void createConnectionIfNeeded(String connectionId) {
    connectionIdsInterestedInNotifications.add(connectionId);
    if (!hasOpenConnection()) {
      try {
        this.sonarCloudWebSocket = SonarCloudWebSocket.create(this.websocketEndpointUri,
          connectionAwareHttpClientProvider.getWebSocketClient(connectionId),
          this::handleSonarServerEvent, this::reopenConnectionOnClose);
        this.connectionIdUsedToCreateConnection = connectionId;
      } catch (Exception e) {
        LOG.error("Error while creating WebSocket connection", e);
      }
    }
  }

  public void reopenConnection(String connectionId, String reason) {
    closeSocket(reason);
    createConnectionIfNeeded(connectionId);
    resubscribeAll();
  }

  protected void reopenConnectionOnClose() {
    executorService.execute(() -> {
      var connectionId = connectionIdsInterestedInNotifications.stream().findFirst().orElse(null);
      if (this.sonarCloudWebSocket != null && connectionId != null) {
        //ACR-deb7526cefd647cd80fcd7d044307be4
        this.reopenConnection(connectionId, "WebSocket was closed by server or reached EOL");
      }
    });
  }

  public void closeSocketIfNoMoreNeeded() {
    if (subscribedProjectKeysByConfigScopes.isEmpty()) {
      closeSocket("No more bound project");
    }
  }

  public void subscribe(String configScopeId, Binding binding) {
    this.createConnectionIfNeeded(binding.connectionId());
    var projectKey = binding.sonarProjectKey();
    if (subscribedProjectKeysByConfigScopes.containsKey(configScopeId) && !subscribedProjectKeysByConfigScopes.get(configScopeId).equals(projectKey)) {
      this.forget(configScopeId);
    }
    if (!subscribedProjectKeysByConfigScopes.containsValue(projectKey)) {
      this.sonarCloudWebSocket.subscribe(projectKey);
    }
    subscribedProjectKeysByConfigScopes.put(configScopeId, projectKey);
  }

  private void resubscribeAll() {
    var uniqueProjectKeys = new HashSet<>(subscribedProjectKeysByConfigScopes.values());
    uniqueProjectKeys.forEach(projectKey -> sonarCloudWebSocket.subscribe(projectKey));
  }

  public void closeSocket(String reason) {
    if (this.sonarCloudWebSocket != null) {
      var socket = this.sonarCloudWebSocket;
      this.sonarCloudWebSocket = null;
      this.connectionIdUsedToCreateConnection = null;
      socket.close(reason);
    }
  }

  public boolean hasOpenConnection() {
    return sonarCloudWebSocket != null && sonarCloudWebSocket.isOpen();
  }

  public void forget(String configScopeId) {
    var projectKey = subscribedProjectKeysByConfigScopes.remove(configScopeId);
    if (projectKey != null && !subscribedProjectKeysByConfigScopes.containsValue(projectKey) && sonarCloudWebSocket != null) {
      sonarCloudWebSocket.unsubscribe(projectKey);
    }
  }

  public SonarCloudWebSocket getSonarCloudWebSocket() {
    return sonarCloudWebSocket;
  }

  public Map<String, String> getSubscribedProjectKeysByConfigScopes() {
    return subscribedProjectKeysByConfigScopes;
  }

  public boolean isInterestedInNotifications(String connectionId) {
    return connectionIdsInterestedInNotifications.contains(connectionId);
  }

  public Set<String> getConnectionIdsInterestedInNotifications() {
    return connectionIdsInterestedInNotifications;
  }
}
