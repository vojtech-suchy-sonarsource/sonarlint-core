/*
ACR-d1ce3fc96b7e4852851a51db52f752da
ACR-46e3cefc40f44606b4e40f27c0a12ed0
ACR-58f92a9ae8884545867abc065fde5457
ACR-007c3ea5c46f48f4a5d223a5f4eb3268
ACR-d94b7b7dea814e73ab8e74c312e43e15
ACR-64512be507a546039d92f1d617029025
ACR-ab01fbc810cf4faba9ac76d66ee33784
ACR-6aa1241a9c7c41c0885d6aba64091c9b
ACR-8c6ae69d128c4623b3a064bbd7d19afc
ACR-eca1abe96c7d40eda5db136e9886a0ed
ACR-1d3aa3d335a44a46bc2e91e0d6543e3f
ACR-a4789ce883c04737b28be0fab93ede62
ACR-9adaca1d5971435d970c3db462703bea
ACR-dd8caeac09a14868b8d896c29f2f5a78
ACR-fc803331283a487f846340ddb35e7cd9
ACR-361f5a15eae94dc086d906c5d31b91f2
ACR-ad9bb41a73e647aa9c56a989b222f837
 */
package org.sonarsource.sonarlint.core;

import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BindingCandidatesFinder {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ConfigurationRepository configRepository;
  private final BindingClueProvider bindingClueProvider;
  private final SonarProjectsCache sonarProjectsCache;

  @Inject
  public BindingCandidatesFinder(ConfigurationRepository configRepository, BindingClueProvider bindingClueProvider, SonarProjectsCache sonarProjectsCache) {
    this.configRepository = configRepository;
    this.bindingClueProvider = bindingClueProvider;
    this.sonarProjectsCache = sonarProjectsCache;
  }

  public Set<ConfigurationScopeSharedContext> findConfigScopesToBind(String connectionId, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var configScopeCandidates = configRepository.getAllBindableUnboundScopes();
    if (configScopeCandidates.isEmpty()) {
      return Set.of();
    }

    var goodConfigScopeCandidates = new HashSet<ConfigurationScopeSharedContext>();

    for (var scope : configScopeCandidates) {
      checkIfScopeIsGoodCandidateForBinding(scope, connectionId, projectKey, cancelMonitor)
        .ifPresent(goodConfigScopeCandidates::add);
    }

    //ACR-5ec218b7e86c43b592f7195eb833ccea
    //ACR-524c429e0e2743eabf7944b262705b89
    return filterOutLeafCandidates(goodConfigScopeCandidates);
  }

  private Optional<ConfigurationScopeSharedContext> checkIfScopeIsGoodCandidateForBinding(
    ConfigurationScope scope, String connectionId, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    cancelMonitor.checkCanceled();

    var cluesAndConnections = bindingClueProvider.collectBindingCluesWithConnections(scope.id(), Set.of(connectionId), cancelMonitor);

    var cluesWithMatchingProjectKey = cluesAndConnections.stream()
      .filter(c -> projectKey.equals(c.getBindingClue().getSonarProjectKey()))
      .toList();


    if (!cluesWithMatchingProjectKey.isEmpty()) {
      var isFromSharedConfiguration = cluesWithMatchingProjectKey.stream().anyMatch(
        c -> c.getBindingClue().getOrigin() == BindingSuggestionOrigin.SHARED_CONFIGURATION);
      if (isFromSharedConfiguration) {
        return Optional.of(new ConfigurationScopeSharedContext(scope, BindingSuggestionOrigin.SHARED_CONFIGURATION));
      }
      var isFromPropertiesFile = cluesWithMatchingProjectKey.stream().anyMatch(
        c -> c.getBindingClue().getOrigin() == BindingSuggestionOrigin.PROPERTIES_FILE);
      if (isFromPropertiesFile) {
        return Optional.of(new ConfigurationScopeSharedContext(scope, BindingSuggestionOrigin.PROPERTIES_FILE));
      }

      var firstOrigin = cluesWithMatchingProjectKey.get(0).getBindingClue().getOrigin();
      return Optional.of(new ConfigurationScopeSharedContext(scope, firstOrigin));
    }
    var configScopeName = scope.name();
    if (isNotBlank(configScopeName) && isConfigScopeNameCloseEnoughToSonarProject(configScopeName, connectionId, projectKey, cancelMonitor)) {
      return Optional.of(new ConfigurationScopeSharedContext(scope, BindingSuggestionOrigin.PROJECT_NAME));
    }
    return Optional.empty();
  }

  private static Set<ConfigurationScopeSharedContext> filterOutLeafCandidates(Set<ConfigurationScopeSharedContext> candidates) {
    var candidateIds = candidates.stream().map(ConfigurationScopeSharedContext::getConfigurationScope).map(ConfigurationScope::id).collect(Collectors.toSet());
    return candidates.stream().filter(bindableConfig -> {
      var scope = bindableConfig.getConfigurationScope();
      var parentId = scope.parentId();
      return parentId == null || !candidateIds.contains(parentId);
    }).collect(Collectors.toSet());
  }

  private boolean isConfigScopeNameCloseEnoughToSonarProject(String configScopeName, String connectionId, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    //ACR-a3bb90f6f62c40eb888475d3218b3bee
    var sonarProjectOpt = sonarProjectsCache.getSonarProject(connectionId, projectKey, cancelMonitor);
    if (sonarProjectOpt.isEmpty()) {
      LOG.debug("Unable to find SonarProject with key '{}' on connection '{}' in the cache", projectKey, connectionId);
      return false;
    }
    TextSearchIndex<ServerProject> index = new TextSearchIndex<>();
    var p = sonarProjectOpt.get();
    index.index(p, p.key() + " " + p.name());
    var searchResult = index.search(configScopeName);
    return !searchResult.isEmpty();
  }
}
