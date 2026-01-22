/*
ACR-9e9aadc241db4fffbcefa8620dece1d7
ACR-00c491e8202e445683d16f459efc8f49
ACR-5494cadebe1e4fcc82aecb6d97eba1a0
ACR-6bf1a0ce14dd416ca479ebe1bd497703
ACR-27d325af6783479ebd12c99d25b59ae2
ACR-60703f7844084be8998c55baa9b9cef2
ACR-ef5fb889c7514e90b7038dae9f14bbfe
ACR-010f18bc14ce4bd1a9a5725f553835e2
ACR-0487738e678543b992e7255f244b100b
ACR-d39ab868635d4be5b3ecb90cea5413a1
ACR-394748762b29448e9df3666bf30b4ad4
ACR-31949b512d9c4268a6101f9a3498c5dc
ACR-626624901f21492ab2f65252c74af5e3
ACR-0f34a637ed77449d954e1907c00b0e0d
ACR-ec3d749b79414b53b9cc19247f35f31e
ACR-464366eb345e4957a39e55024182a07b
ACR-8bd06108703342ccb8b905d8aca781a8
 */
package org.sonarsource.sonarlint.core.smartnotifications;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.AbstractConnectionConfiguration;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.SonarCloudConnectionConfiguration;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.smartnotification.ShowSmartNotificationParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.developers.DevelopersApi;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;
import org.sonarsource.sonarlint.core.websocket.WebSocketService;
import org.sonarsource.sonarlint.core.websocket.events.SmartNotificationEvent;
import org.springframework.context.event.EventListener;

import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SMART_NOTIFICATIONS;

public class SmartNotifications {

  private final SonarLintLogger logger = SonarLintLogger.get();

  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final SonarLintRpcClient client;
  private final TelemetryService telemetryService;
  private final WebSocketService webSocketService;
  private final InitializeParams params;
  private final LastEventPolling lastEventPollingService;
  private ExecutorServiceShutdownWatchable<ScheduledExecutorService> smartNotificationsPolling;

  public SmartNotifications(ConfigurationRepository configurationRepository, ConnectionConfigurationRepository connectionRepository, SonarQubeClientManager sonarQubeClientManager,
    SonarLintRpcClient client, StorageService storageService, TelemetryService telemetryService, WebSocketService webSocketService, InitializeParams params) {
    this.configurationRepository = configurationRepository;
    this.connectionRepository = connectionRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.client = client;
    this.telemetryService = telemetryService;
    this.webSocketService = webSocketService;
    this.params = params;
    lastEventPollingService = new LastEventPolling(storageService);
  }

  @PostConstruct
  public void initialize() {
    if (!params.getBackendCapabilities().contains(SMART_NOTIFICATIONS)) {
      return;
    }
    smartNotificationsPolling = new ExecutorServiceShutdownWatchable<>(FailSafeExecutors.newSingleThreadScheduledExecutor("Smart Notifications Polling"));
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(smartNotificationsPolling);
    smartNotificationsPolling.getWrapped().scheduleAtFixedRate(() -> this.poll(cancelMonitor), 1, 60, TimeUnit.SECONDS);
  }

  private void poll(SonarLintCancelMonitor cancelMonitor) {
    var boundScopeByConnectionAndSonarProject = configurationRepository.getBoundScopeByConnectionAndSonarProject();
    boundScopeByConnectionAndSonarProject.forEach((connectionId, boundScopesByProject) -> {
      var connection = connectionRepository.getConnectionById(connectionId);
      if (connection != null && !connection.isDisableNotifications() && !shouldSkipPolling(connection)) {
        sonarQubeClientManager.withActiveClient(connectionId,
          serverApi -> manageNotificationsForConnection(serverApi, boundScopesByProject, connection, cancelMonitor));
      }
    });
  }

  private void manageNotificationsForConnection(ServerApi serverApi, Map<String, Collection<BoundScope>> boundScopesPerProjectKey,
    AbstractConnectionConfiguration connection, SonarLintCancelMonitor cancelMonitor) {
    var developersApi = serverApi.developers();
    var connectionId = connection.getConnectionId();
    var projectKeysByLastEventPolling = boundScopesPerProjectKey.keySet().stream()
      .collect(Collectors.toMap(Function.identity(),
        p -> getLastNotificationTime(lastEventPollingService.getLastEventPolling(connectionId, p))));

    var notifications = retrieveServerNotifications(developersApi, projectKeysByLastEventPolling, cancelMonitor);

    for (var n : notifications) {
      var scopeIds = boundScopesPerProjectKey.get(n.projectKey()).stream().map(BoundScope::getConfigScopeId).collect(Collectors.toSet());
      var smartNotification = new ShowSmartNotificationParams(n.message(), n.link(), scopeIds,
        n.category(), connectionId);
      client.showSmartNotification(smartNotification);
      telemetryService.smartNotificationsReceived(n.category());
    }

    projectKeysByLastEventPolling.keySet()
      .forEach(projectKey -> lastEventPollingService.setLastEventPolling(ZonedDateTime.now(), connectionId, projectKey));
  }

  private boolean shouldSkipPolling(AbstractConnectionConfiguration connection) {
    if (connection.getKind() == ConnectionKind.SONARCLOUD) {
      var region = ((SonarCloudConnectionConfiguration) connection).getRegion();
      return webSocketService.hasOpenConnection(region);
    }
    return false;
  }

  @PreDestroy
  public void shutdown() {
    if (smartNotificationsPolling != null && !MoreExecutors.shutdownAndAwaitTermination(smartNotificationsPolling, 5, TimeUnit.SECONDS)) {
      logger.warn("Unable to stop smart notifications executor service in a timely manner");
    }
  }

  private static ZonedDateTime getLastNotificationTime(ZonedDateTime lastTime) {
    var oneDayAgo = ZonedDateTime.now().minusDays(1);
    return lastTime.isAfter(oneDayAgo) ? lastTime : oneDayAgo;
  }

  private static List<ServerNotification> retrieveServerNotifications(DevelopersApi developersApi,
    Map<String, ZonedDateTime> projectKeysByLastEventPolling, SonarLintCancelMonitor cancelMonitor) {
    return developersApi.getEvents(projectKeysByLastEventPolling, cancelMonitor)
      .stream().map(e -> new ServerNotification(
        e.getCategory(),
        e.getMessage(),
        e.getLink(),
        e.getProjectKey(),
        e.getTime()))
      .toList();
  }

  @EventListener
  public void onServerEventReceived(SonarServerEventReceivedEvent eventReceived) {
    var serverEvent = eventReceived.getEvent();
    if (serverEvent instanceof SmartNotificationEvent smartNotificationEvent) {
      notifyClient(eventReceived.getConnectionId(), smartNotificationEvent);
    }
  }

  private void notifyClient(String connectionId, SmartNotificationEvent event) {
    var projectKey = event.project();
    var boundScopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, projectKey);
    client.showSmartNotification(new ShowSmartNotificationParams(event.message(), event.link(),
      boundScopes.stream().map(BoundScope::getConfigScopeId).collect(Collectors.toSet()), event.category(), connectionId));
    telemetryService.smartNotificationsReceived(event.category());
  }

}
