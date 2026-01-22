/*
ACR-b8e2664c82d7469d9e022f08a6f61ce8
ACR-e50e9d0bf0f141df9b166e0de7f84c35
ACR-a00856bf46fc4cee83686244ff41a0e0
ACR-eaa40acd604a47a3b2804315e1c82262
ACR-26dca571ba834ce5bb7e4d1195c056bd
ACR-35d8a51652c142bb9b08eb7ce6dafc94
ACR-8c2e1ed7bdb84a82b661186b27d066ce
ACR-55a4e6d971b9421387475c1653e0d7f3
ACR-09e6871b74ab4da1b3071a420ca75be4
ACR-cca4a96622f746d08c753b30c6c5ab2d
ACR-be7d999b36154b8ba1a21bae5106dccb
ACR-01a347fc17f74ac09b301b9691428f7d
ACR-9e2ee7daa1fd4edea3ad6e68700a0e5f
ACR-fcea3f677dcb49a782f7afa377672bfd
ACR-73464f89e9d74ad092542e80aeca9181
ACR-a072130dfa4b497cbec179c271a25bea
ACR-0de3953e888d4fa08ee028740002daf1
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
    //ACR-5522832f9e994b3c99b0ab6db98650c8
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
