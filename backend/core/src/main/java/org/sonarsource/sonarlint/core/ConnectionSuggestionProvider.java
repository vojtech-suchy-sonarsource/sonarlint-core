/*
ACR-798b2693ab184a099a998d31bee66500
ACR-c4fafbe2f80b482aad241cab0e6eaa2d
ACR-81f9d32ab1004f39b2bd197eaa3a81bf
ACR-3d8aede87bda41d9be647abd6fb130ab
ACR-b958eecc46214813aa19dd57abb69aeb
ACR-c9ddac8274334efab5128c96212f347a
ACR-61a0a0b050044dcb978cacd47fe08189
ACR-8cd08e9049204c08a85c279c1e99c82b
ACR-9abac9cd7dc147a2bb7ce7861cc7a8a5
ACR-92a6179923a842928a80225cdb4faf44
ACR-76d0970dd9164f0fa75a61affbaec052
ACR-1c9b195894de4b44be1439f6f06f7d64
ACR-dfeb53c775034d95af3939abf116eddd
ACR-a4d6dab6ade54f6a9e8fac00640437ea
ACR-23ac8327c3e6441bbf5d154c81a157cf
ACR-606e51fbe7cd4148b82d0c79dfb323df
ACR-c9c3bc789dc9491cb9a6ff9b6ed94c3f
 */
package org.sonarsource.sonarlint.core;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ExecutorServiceShutdownWatchable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.fs.FileSystemUpdatedEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.ConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarCloudConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SonarQubeConnectionSuggestionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.connection.SuggestConnectionParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.springframework.context.event.EventListener;

import static org.sonarsource.sonarlint.core.BindingClueProvider.ALL_BINDING_CLUE_FILENAMES;

public class ConnectionSuggestionProvider {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConfigurationRepository configRepository;
  private final ConnectionConfigurationRepository connectionRepository;
  private final SonarLintRpcClient client;
  private final BindingClueProvider bindingClueProvider;
  private final ExecutorServiceShutdownWatchable<?> executorService;
  private final BindingSuggestionProvider bindingSuggestionProvider;
  private final ClientFileSystemService clientFs;

  @Inject
  public ConnectionSuggestionProvider(ConfigurationRepository configRepository, ConnectionConfigurationRepository connectionRepository, SonarLintRpcClient client,
    BindingClueProvider bindingClueProvider, BindingSuggestionProvider bindingSuggestionProvider, ClientFileSystemService clientFs) {
    this.configRepository = configRepository;
    this.connectionRepository = connectionRepository;
    this.client = client;
    this.bindingClueProvider = bindingClueProvider;
    this.executorService = new ExecutorServiceShutdownWatchable<>(FailSafeExecutors.newSingleThreadExecutor("Connection Suggestion Provider"));
    this.bindingSuggestionProvider = bindingSuggestionProvider;
    this.clientFs = clientFs;
  }

  @EventListener
  public void filesystemUpdated(FileSystemUpdatedEvent event) {
    var listConfigScopeIds = event.getAddedOrUpdated().stream()
      .filter(f -> ALL_BINDING_CLUE_FILENAMES.contains(f.getFileName()) || f.isSonarlintConfigurationFile())
      .map(ClientFile::getConfigScopeId)
      .collect(Collectors.toSet());

    queueConnectionSuggestion(listConfigScopeIds);
  }

  @EventListener
  public void configurationScopesAdded(ConfigurationScopesAddedWithBindingEvent event) {
    var listConfigScopeIds = event.getConfigScopeIds().stream()
      .map(clientFs::getFiles)
      .flatMap(List::stream)
      .filter(f -> ALL_BINDING_CLUE_FILENAMES.contains(f.getFileName()) || f.isSonarlintConfigurationFile())
      .map(ClientFile::getConfigScopeId)
      .collect(Collectors.toSet());

    if (!listConfigScopeIds.isEmpty()) {
      queueConnectionSuggestion(listConfigScopeIds);
    } else {
      bindingSuggestionProvider.suggestBindingForGivenScopesAndAllConnections(event.getConfigScopeIds());
    }
  }

  private void queueConnectionSuggestion(Set<String> listConfigScopeIds) {
    if (!listConfigScopeIds.isEmpty()) {
      var cancelMonitor = new SonarLintCancelMonitor();
      cancelMonitor.watchForShutdown(executorService);
      executorService.execute(() -> suggestConnectionAndBindingForGivenScopes(listConfigScopeIds, cancelMonitor));
    }
  }

  private void suggestConnectionAndBindingForGivenScopes(Set<String> configScopeIds, SonarLintCancelMonitor cancelMonitor) {
    var connectionAndBindingSuggestions = computeConnectionAndBindingSuggestions(configScopeIds, cancelMonitor);

    suggestConnectionToClientIfAny(connectionAndBindingSuggestions.connectionSuggestionsByConfigScopeIds());
    computeBindingSuggestionIfAny(connectionAndBindingSuggestions.bindingSuggestionsForConfigScopeIds());
  }

  private @NotNull ConnectionAndBindingSuggestions computeConnectionAndBindingSuggestions(Set<String> configScopeIds, SonarLintCancelMonitor cancelMonitor) {
    LOG.debug("Computing connection suggestions");
    var connectionSuggestionsByConfigScopeIds = new HashMap<String, List<ConnectionSuggestionDto>>();
    var bindingSuggestionsForConfigScopeIds = new HashSet<String>();

    for (var configScopeId : configScopeIds) {
      var effectiveBinding = configRepository.getEffectiveBinding(configScopeId);
      if (effectiveBinding.isPresent()) {
        LOG.debug("A binding already exists, skipping the connection suggestion");
        continue;
      }

      var bindingClues = bindingClueProvider.collectBindingClues(configScopeId, cancelMonitor);
      for (var bindingClue : bindingClues) {
        var projectKey = bindingClue.getSonarProjectKey();
        if (projectKey != null) {
          handleBindingClue(bindingClue).ifPresentOrElse(clue -> clue.map(
            serverUrl -> connectionSuggestionsByConfigScopeIds.computeIfAbsent(configScopeId, s -> new ArrayList<>())
              .add(new ConnectionSuggestionDto(new SonarQubeConnectionSuggestionDto(serverUrl, projectKey), bindingClue.getOrigin())),
            organization -> connectionSuggestionsByConfigScopeIds.computeIfAbsent(configScopeId, s -> new ArrayList<>())
              .add(new ConnectionSuggestionDto(new SonarCloudConnectionSuggestionDto(organization, projectKey,
                ((BindingClueProvider.SonarCloudBindingClue) bindingClue).getRegion()), bindingClue.getOrigin()))),
            () -> bindingSuggestionsForConfigScopeIds.add(configScopeId));
        }
      }
    }
    return new ConnectionAndBindingSuggestions(connectionSuggestionsByConfigScopeIds, bindingSuggestionsForConfigScopeIds);
  }

  private Optional<Either<String, String>> handleBindingClue(BindingClueProvider.BindingClue bindingClue) {
    if (bindingClue instanceof BindingClueProvider.SonarCloudBindingClue sonarCloudBindingClue) {
      LOG.debug("Found a SonarCloud binding clue");
      var organization = sonarCloudBindingClue.getOrganization();
      var connection = connectionRepository.findByOrganization(organization);
      if (connection.isEmpty()) {
        return Optional.of(Either.forRight(organization));
      }
    } else if (bindingClue instanceof BindingClueProvider.SonarQubeBindingClue sonarQubeBindingClue) {
      LOG.debug("Found a SonarQube binding clue");
      var serverUrl = sonarQubeBindingClue.getServerUrl();
      var connection = connectionRepository.findByUrl(serverUrl);
      if (connection.isEmpty()) {
        return Optional.of(Either.forLeft(Strings.CS.removeEnd(serverUrl, "/")));
      }
    } else {
      LOG.debug("Found an invalid binding clue for connection suggestion");
    }
    return Optional.empty();
  }

  private void suggestConnectionToClientIfAny(Map<String, List<ConnectionSuggestionDto>> connectionSuggestionsByConfigScopeIds) {
    if (!connectionSuggestionsByConfigScopeIds.isEmpty()) {
      var foundSuggestionsCount = connectionSuggestionsByConfigScopeIds.size();
      LOG.debug("Found {} connection {}", foundSuggestionsCount, SonarLintLogger.singlePlural(foundSuggestionsCount, "suggestion"));
      client.suggestConnection(new SuggestConnectionParams(connectionSuggestionsByConfigScopeIds));
    }
  }

  private void computeBindingSuggestionIfAny(Set<String> bindingSuggestionsForConfigScopeIds) {
    if (!bindingSuggestionsForConfigScopeIds.isEmpty()) {
      LOG.debug("Found binding suggestion(s) for %s configuration scope IDs", bindingSuggestionsForConfigScopeIds.size());
      bindingSuggestionProvider.suggestBindingForGivenScopesAndAllConnections(bindingSuggestionsForConfigScopeIds);
    }
  }

  public List<ConnectionSuggestionDto> getConnectionSuggestions(String configScopeId, SonarLintCancelMonitor cancelMonitor) {
    var connectionAndBindingSuggestions = computeConnectionAndBindingSuggestions(Set.of(configScopeId), cancelMonitor);
    return connectionAndBindingSuggestions.connectionSuggestionsByConfigScopeIds.containsKey(configScopeId) ?
      connectionAndBindingSuggestions.connectionSuggestionsByConfigScopeIds.get(configScopeId) :
      List.of();
  }

  private record ConnectionAndBindingSuggestions(
    Map<String, List<ConnectionSuggestionDto>> connectionSuggestionsByConfigScopeIds,
    Set<String> bindingSuggestionsForConfigScopeIds
  ) {}

}
