/*
ACR-9f92ec8bc2354210885c4fffe109b139
ACR-02b17473fc1d412babad1262374ccdcd
ACR-04587e1ae07d4fd08ae8ebde42b874cf
ACR-f89985bb174b49138f6efcd7f53545ee
ACR-1c3d87f7353249aaa10ecee279ec0006
ACR-8e609cf6e9cd4bfebc785e0004210a50
ACR-462010e1bda440ee9f943b251ab08f2a
ACR-f69283437ed74f3fa7a8dcc66c42214c
ACR-eb133467a550466fb25b36cf421d858a
ACR-e7c978df846a4909b153f48587fe050b
ACR-724d5b5c0a00488fba36b15304358fb6
ACR-b8052af00c22498e9ea6fcce9946be61
ACR-a1d58a097a3a4ab4bd60885084550177
ACR-a3e725cd19ba4837ae8b6b7b2b8e4276
ACR-72888690c9b34572ba038a4b1200051a
ACR-666e906eebe84b33adf42e9c70180190
ACR-e3e8fc615857466dbfec802b89bc1e78
 */
package org.sonarsource.sonarlint.core.server.event;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
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
import org.sonarsource.sonarlint.core.event.SonarServerEventReceivedEvent;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;
import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.SERVER_SENT_EVENTS;

public class ServerEventsService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ConfigurationRepository configurationRepository;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final LanguageSupportRepository languageSupportRepository;
  private final boolean shouldManageServerSentEvents;
  private final ApplicationEventPublisher eventPublisher;
  private final Map<String, SonarQubeEventStream> streamsPerConnectionId = new ConcurrentHashMap<>();
  private final ExecutorService executorService = FailSafeExecutors.newSingleThreadExecutor("sonarlint-server-sent-events-subscriber");

  public ServerEventsService(ConfigurationRepository configurationRepository, ConnectionConfigurationRepository connectionConfigurationRepository,
    SonarQubeClientManager sonarQubeClientManager, LanguageSupportRepository languageSupportRepository, InitializeParams initializeParams,
    ApplicationEventPublisher eventPublisher) {
    this.configurationRepository = configurationRepository;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.languageSupportRepository = languageSupportRepository;
    this.shouldManageServerSentEvents = initializeParams.getBackendCapabilities().contains(SERVER_SENT_EVENTS);
    this.eventPublisher = eventPublisher;
  }

  @EventListener
  public void handle(ConfigurationScopesAddedWithBindingEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    executorService.execute(() -> subscribeAll(event.getConfigScopeIds()));
  }

  @EventListener
  public void handle(ConfigurationScopeRemovedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    var removedScope = event.getRemovedConfigurationScope();
    var removedBindingConfiguration = event.getRemovedBindingConfiguration();
    var bindingConfigurationFromRepository = configurationRepository.getBindingConfiguration(removedScope.id());
    if (bindingConfigurationFromRepository == null
      || isBindingDifferent(removedBindingConfiguration, bindingConfigurationFromRepository)) {
      //ACR-193486c04f9744e5884fb736987a5239
      executorService.execute(() -> unsubscribe(removedBindingConfiguration));
    }
  }

  @EventListener
  public void handle(BindingConfigChangedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    var previousBinding = event.previousConfig();
    if (isBindingDifferent(previousBinding, event.newConfig())) {
      executorService.execute(() -> {
        unsubscribe(previousBinding);
        subscribe(event.configScopeId());
      });
    }
  }

  @EventListener
  public void handle(ConnectionConfigurationAddedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    //ACR-f1055054c2764c0c905489fa9185fbcc
    var connectionId = event.addedConnectionId();
    executorService.execute(() -> subscribe(connectionId, configurationRepository.getSonarProjectsUsedForConnection(connectionId)));
  }

  @EventListener
  public void handle(ConnectionConfigurationRemovedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    executorService.execute(() -> {
      var stream = streamsPerConnectionId.remove(event.getRemovedConnectionId());
      if (stream != null) {
        stream.stop();
      }
    });
  }

  @EventListener
  public void handle(ConnectionConfigurationUpdatedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    //ACR-d4be0af63a8b42aeb45c65da67520a39
    executorService.execute(() -> resubscribe(event.updatedConnectionId()));
  }

  @EventListener
  public void handle(ConnectionCredentialsChangedEvent event) {
    if (!shouldManageServerSentEvents) {
      return;
    }
    executorService.execute(() -> resubscribe(event.getConnectionId()));
  }

  private static boolean isBindingDifferent(BindingConfiguration previousConfig, BindingConfiguration newConfig) {
    return !Objects.equals(previousConfig.sonarProjectKey(), newConfig.sonarProjectKey())
      || !Objects.equals(previousConfig.connectionId(), newConfig.connectionId());
  }

  private void subscribeAll(Set<String> configurationScopeIds) {
    configurationScopeIds.stream()
      .map(configurationRepository::getConfiguredBinding)
      .flatMap(Optional::stream)
      .collect(Collectors.groupingBy(Binding::connectionId, mapping(Binding::sonarProjectKey, toSet())))
      .forEach(this::subscribe);
  }

  private void subscribe(String scopeId) {
    configurationRepository.getConfiguredBinding(scopeId)
      .ifPresent(binding -> subscribe(binding.connectionId(), Set.of(binding.sonarProjectKey())));
  }

  private void subscribe(String connectionId, Set<String> possiblyNewProjectKeys) {
    if (supportsServerSentEvents(connectionId)) {
      var stream = streamsPerConnectionId.computeIfAbsent(connectionId, k -> openStream(connectionId));
      stream.subscribeNew(possiblyNewProjectKeys);
    }
  }

  private SonarQubeEventStream openStream(String connectionId) {
    return new SonarQubeEventStream(languageSupportRepository.getEnabledLanguagesInConnectedMode(), connectionId, sonarQubeClientManager,
      e -> eventPublisher.publishEvent(new SonarServerEventReceivedEvent(connectionId, e)));
  }

  private boolean supportsServerSentEvents(String connectionId) {
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    return connection != null && connection.getKind() == ConnectionKind.SONARQUBE;
  }

  private void unsubscribe(BindingConfiguration previousBindingConfiguration) {
    if (previousBindingConfiguration.isBound()) {
      var connectionId = requireNonNull(previousBindingConfiguration.connectionId());
      var projectKey = requireNonNull(previousBindingConfiguration.sonarProjectKey());
      if (supportsServerSentEvents(connectionId) && streamsPerConnectionId.containsKey(connectionId)
        && configurationRepository.getSonarProjectsUsedForConnection(connectionId).stream().noneMatch(usedProjectKey -> usedProjectKey.equals(projectKey))) {
        streamsPerConnectionId.get(connectionId).unsubscribe(projectKey);
      }
    }
  }

  private void resubscribe(String connectionId) {
    if (supportsServerSentEvents(connectionId) && streamsPerConnectionId.containsKey(connectionId)) {
      streamsPerConnectionId.get(connectionId).resubscribe();
    }
  }

  @PreDestroy
  public void shutdown() {
    if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop server-sent events subscriber service in a timely manner");
    }
    streamsPerConnectionId.values().forEach(SonarQubeEventStream::stop);
    streamsPerConnectionId.clear();
  }
}
