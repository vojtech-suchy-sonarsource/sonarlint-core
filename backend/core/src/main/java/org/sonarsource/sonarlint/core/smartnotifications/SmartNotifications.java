/*
ACR-81868e7fc73347e79424aa991bb7d9a9
ACR-72bb23838009489a87c921ca071af7ee
ACR-8bde7cc25d7a4491a82d82629ede05e2
ACR-6ddf56d492d24d4e9f47e95a8e473b77
ACR-f92de849c07b4f409084c9bc6fa1638a
ACR-6b9aa419ce2947338701a25bddf02f59
ACR-5b92dd075cbf409cb653837eeb3b8f32
ACR-47955dfd54c048408c196e0681708750
ACR-ebe57a56868242bba61525a6ecf79d53
ACR-0ee445c521a149048b0b73f27c5f87be
ACR-a9cd778286804052ae9df41c510df185
ACR-8e77981cc6a94e77a52f5e44414595ab
ACR-657bc9efd81741af8d8a5583ed32b490
ACR-e844b8052ce94752b270ae92ef9507f1
ACR-ba85a322ff254238868a33fac51baafe
ACR-0022e83005bd4e1fbb3cbdefc27338f4
ACR-8ab8040f84454f79ab21c3eb69ee0cfb
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
