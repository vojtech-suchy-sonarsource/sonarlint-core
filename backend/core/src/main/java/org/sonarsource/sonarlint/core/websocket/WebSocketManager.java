/*
ACR-6c8d753028a442aab32789d4effe5c54
ACR-40e7dd8fb4514c0291f674ef6b287ace
ACR-6f9b6deaf8334600acea817d3917b0de
ACR-e79b131372014450b330c86d2a6e761f
ACR-636584344da94c4d95a26cc9fc684e4b
ACR-8440277953e04508b722cc04818a83ff
ACR-927d9a17ea9c419384e2b668e15c4fe7
ACR-c86fb32428c74a6883b51a5cefa5292c
ACR-e19a082b46a343a785b146d92564916e
ACR-5d94076f44ee4d9abcd6bd25ab174fc8
ACR-b22885417bce4267b21125a1969f8ecf
ACR-115a4f26dbb54a4c9584d4ceaec20c13
ACR-875dbd408e9f4743ba896adba0ed282a
ACR-e596f567a5f448048364b83f77975946
ACR-0cfa06dbe15f43ff980d477c5751d4cc
ACR-1a85e90cfbe44c2eb8e79ef59928d245
ACR-7ba1911eacbc446d9e8e141538f8a507
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
      //ACR-751e0e35d2074633889943bd568f2985
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
        //ACR-5cde5dad878747878bdcbcb15218d591
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
