/*
ACR-413e3c11a1d34a25bd3fa43407bd0b2b
ACR-94c700d8de6244eaa9a38602c3f68011
ACR-f2f7581b0af347729998d3480ee29bc6
ACR-013e9005baca42c9b2c82262c923530a
ACR-a7d59ff83fb146e1b8d6c58547d73ec8
ACR-63a67b1968654d69a2750f134ba7a2d7
ACR-cea49563c86c44efb8624518a45a772e
ACR-2adb4a8ab5854d029d7d5009ee0b6884
ACR-aa67dfb9a53f41de8b395cacae505df9
ACR-4d3ce78377a4468f80d9a5ba4910f44e
ACR-3c2dcf1b3beb46ce92a35db14b76918a
ACR-cfe5cbfed2a54e6f962f5114bb4c3f71
ACR-4d053d3cd9d448aeb0ce69a601b2ba48
ACR-511ca6536bc645a6918de0a7d7256d39
ACR-3c3db1b5a74344e385793cac131d0abc
ACR-04b8a4af027c4a2a81ddd442cf39e93b
ACR-4ff036c9c9e24b8ab6790871d770de79
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

    //ACR-51e5d90750cd473a819ece44ff1ac80d
    //ACR-429fd42f29304231ab93feb1cced7617
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
    //ACR-17bde3dc6c4244edb3addd127cf0677a
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
