/*
ACR-ba32c02f724740e7af5afd42006a435e
ACR-ab299ab2ef244d559d4352b1e8abc8fb
ACR-f497b4ded50b4c1d824b3238b40b6421
ACR-5a5f97e481244f1e9c542d66aec8b15c
ACR-b2801c7b53a84b69ab9c667119950820
ACR-3b77b61fd76f4838ba51aa2f7760bafe
ACR-1b2801a8ec79446fbcc8e213c0379304
ACR-35cb795d7309462fa664986f5000f9d7
ACR-6dfc17d7f4724a8c985d6b98916bc11c
ACR-914eb729f3aa4af9900c6c0991802adb
ACR-b5fd512f11024292b54c1f389eb10496
ACR-1ed0b03a8fe84917bef896f90c7a613b
ACR-46847d961334433698a2dc4f36a900f6
ACR-13c982707ea54290a4d648000936960d
ACR-623ee002a7d74507a9e2831cb490908a
ACR-86237b1c4ce4428ca6546083e16c4635
ACR-15131673cb7d4b06b77bc3a5f617aa16
 */
package org.sonarsource.sonarlint.core;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowSoonUnsupportedMessageParams;
import org.sonarsource.sonarlint.core.serverconnection.VersionUtils;
import org.sonarsource.sonarlint.core.sync.SynchronizationService;
import org.springframework.context.event.EventListener;

public class VersionSoonUnsupportedHelper {

  private static final String UNSUPPORTED_NOTIFICATION_ID = "sonarlint.unsupported.%s.%s.id";
  private static final String NOTIFICATION_MESSAGE = "The version '%s' used by the current connection '%s' will be soon unsupported. " +
    "Please consider upgrading to the latest %s LTS version to ensure continued support and access to the latest features.";
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarLintRpcClient client;
  private final ConfigurationRepository configRepository;
  private final ConnectionConfigurationRepository connectionRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final SynchronizationService synchronizationService;
  private final Map<String, Version> cacheConnectionIdPerVersion = new ConcurrentHashMap<>();
  private final ExecutorServiceShutdownWatchable<?> executorService;

  public VersionSoonUnsupportedHelper(SonarLintRpcClient client, ConfigurationRepository configRepository, SonarQubeClientManager sonarQubeClientManager,
    ConnectionConfigurationRepository connectionRepository, SynchronizationService synchronizationService) {
    this.client = client;
    this.configRepository = configRepository;
    this.connectionRepository = connectionRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.synchronizationService = synchronizationService;
    this.executorService = new ExecutorServiceShutdownWatchable<>(FailSafeExecutors.newSingleThreadExecutor("Version Soon Unsupported Helper"));
  }

  @EventListener
  public void configurationScopesAdded(ConfigurationScopesAddedWithBindingEvent event) {
    var configScopeIds = event.getConfigScopeIds();
    checkIfSoonUnsupportedOncePerConnection(configScopeIds);
  }

  @EventListener
  public void bindingConfigChanged(BindingConfigChangedEvent event) {
    var configScopeId = event.configScopeId();
    var connectionId = event.newConfig().connectionId();
    if (connectionId != null) {
      queueCheckIfSoonUnsupported(connectionId, configScopeId);
    }
  }

  private void checkIfSoonUnsupportedOncePerConnection(Set<String> configScopeIds) {
    //ACR-b0e5fcc57f1c47138afd481a70ba36c9
    var oneConfigScopeIdPerConnection = new HashMap<String, String>();
    configScopeIds.forEach(configScopeId -> {
      var effectiveBinding = configRepository.getEffectiveBinding(configScopeId);
      if (effectiveBinding.isPresent()) {
        var connectionId = effectiveBinding.get().connectionId();
        oneConfigScopeIdPerConnection.putIfAbsent(connectionId, configScopeId);
      }
    });
    oneConfigScopeIdPerConnection.forEach(this::queueCheckIfSoonUnsupported);
  }

  private void queueCheckIfSoonUnsupported(String connectionId, String configScopeId) {
    var cancelMonitor = new SonarLintCancelMonitor();
    cancelMonitor.watchForShutdown(executorService);
    executorService.execute(() -> {
      try {
        var connection = connectionRepository.getConnectionById(connectionId);
        if (connection != null && connection.getKind() == ConnectionKind.SONARQUBE) {
          sonarQubeClientManager.withActiveClient(connectionId, serverApi -> {
            var version = synchronizationService.readOrSynchronizeServerVersion(connectionId, serverApi, cancelMonitor);
            var isCached = cacheConnectionIdPerVersion.containsKey(connectionId) && cacheConnectionIdPerVersion.get(connectionId).compareTo(version) == 0;
            if (!isCached && VersionUtils.isVersionSupportedDuringGracePeriod(version)) {
              client.showSoonUnsupportedMessage(
                new ShowSoonUnsupportedMessageParams(
                  String.format(UNSUPPORTED_NOTIFICATION_ID, connectionId, version.getName()),
                  configScopeId,
                  String.format(NOTIFICATION_MESSAGE, version.getName(), connectionId, VersionUtils.getCurrentLts())));
              LOG.debug(String.format("Connection '%s' with version '%s' is detected to be soon unsupported",
                connection.getConnectionId(), version.getName()));
            }
            cacheConnectionIdPerVersion.put(connectionId, version);
          });
        }
      } catch (Exception e) {
        LOG.error("Error while checking if soon unsupported", e);
      }
    });
  }

  @PreDestroy
  public void shutdown() {
    if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop version soon unsupported executor service in a timely manner");
    }
  }

}
