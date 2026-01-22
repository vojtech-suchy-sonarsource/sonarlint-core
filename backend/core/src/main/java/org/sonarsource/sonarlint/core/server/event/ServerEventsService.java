/*
ACR-f882499585a24d0886551a303370a45e
ACR-2fea89beab744be092885e70986a03a9
ACR-f2af978b76d342adbce9b30d899038f9
ACR-ff39dcf5124b40a1a77f2aefff0990fc
ACR-67b94912c6f34100919bfec7d3d63559
ACR-d7576cc8b9104db9b222cda36aeb760e
ACR-6e35ed04f16840a5a9b41d64e601e109
ACR-8e142816e440464f8f96e32e27574547
ACR-c3e42c7ae68647a8a4cd542a88afaf55
ACR-e5888053e29f425ea20a16cafdd0060d
ACR-0b9864c2e1dd4e71a0f3f59a9b974f1a
ACR-add626aad305459b89840cf58579b936
ACR-0cc212d84cf84e85bce0c3528c2de88d
ACR-70fa104137454b158cb1e14b656510c9
ACR-834573d8c3744a298104ed7e8e6bb8ff
ACR-57338f261bfc43f4b26cc2ad9d3ce99f
ACR-7add15ba72a7438dab1b24cc25bf8b21
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
      //ACR-c416c8d425004ed89755c55f9cf324bd
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
    //ACR-8189661260a34d1d96f32d8aedf29153
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
    //ACR-5967e8da5279425d9c608ba1f5fef71c
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
