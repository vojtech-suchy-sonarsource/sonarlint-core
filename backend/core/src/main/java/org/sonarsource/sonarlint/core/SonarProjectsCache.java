/*
ACR-2a60f9cffc0e414da250e12250b93024
ACR-49f3ea41278a48c5a322372e4b010b49
ACR-63eee175440941aa87a538146e81a4b1
ACR-ed7836859f264b9ba20efa8c2efd3541
ACR-a9dc2cae002a45379e39fba498e442c9
ACR-db683746103b4d3d96ecd4cda060cc02
ACR-b268deec189441898f6d1a04a910e662
ACR-5655ed581c3846f0aacc078466288157
ACR-0a36da3b32f54c9d8542ce2c034f3356
ACR-0a8520e6167c41f4a30cdbe9267f6100
ACR-9a8c5a7bda7a487aa6bc28c6bbec43a3
ACR-2cd3811aabff453cbb34736f7ef9666d
ACR-a435a731cfef491488eb0553e47db561
ACR-5ca027ff4cbe4d81aa5607c3722913a6
ACR-11523743801148bfb78bcd11cebd08c6
ACR-a30ec9998f67463f930c0af8f3b779f6
ACR-3cee49c8576a469a906638e970a3fbe3
 */
package org.sonarsource.sonarlint.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.SonarProjectDto;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;
import org.springframework.context.event.EventListener;

import static org.sonarsource.sonarlint.core.commons.log.SonarLintLogger.singlePlural;

public class SonarProjectsCache {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarQubeClientManager sonarQubeClientManager;

  private final Cache<String, TextSearchIndex<ServerProject>> textSearchIndexCacheByConnectionId = CacheBuilder.newBuilder()
    .expireAfterWrite(1, TimeUnit.HOURS)
    .build();

  private final Cache<SonarProjectKey, Optional<ServerProject>> singleProjectsCache = CacheBuilder.newBuilder()
    .expireAfterWrite(1, TimeUnit.HOURS)
    .build();

  public SonarProjectsCache(SonarQubeClientManager sonarQubeClientManager) {
    this.sonarQubeClientManager = sonarQubeClientManager;
  }

  public List<SonarProjectDto> fuzzySearchProjects(String connectionId, String searchText, SonarLintCancelMonitor cancelMonitor) {
    return getTextSearchIndex(connectionId, cancelMonitor).search(searchText)
      .entrySet()
      .stream()
      .sorted(Comparator.comparing(Map.Entry<ServerProject, Double>::getValue).reversed()
        .thenComparing(Comparator.comparing(e -> e.getKey().name(), String.CASE_INSENSITIVE_ORDER)))
      .limit(10)
      .map(e -> new SonarProjectDto(e.getKey().key(), e.getKey().name()))
      .toList();
  }

  private static class SonarProjectKey {
    private final String connectionId;
    private final String projectKey;

    private SonarProjectKey(String connectionId, String projectKey) {
      this.connectionId = connectionId;
      this.projectKey = projectKey;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      var that = (SonarProjectKey) o;
      return connectionId.equals(that.connectionId) && projectKey.equals(that.projectKey);
    }

    @Override
    public int hashCode() {
      return Objects.hash(connectionId, projectKey);
    }
  }

  @EventListener
  public void connectionRemoved(ConnectionConfigurationRemovedEvent e) {
    evictAll(e.getRemovedConnectionId());
  }

  @EventListener
  public void connectionUpdated(ConnectionConfigurationUpdatedEvent e) {
    //ACR-5e2822a1680e44f3a1f38d533cfbdbc4
    evictAll(e.updatedConnectionId());
  }

  private void evictAll(String connectionId) {
    textSearchIndexCacheByConnectionId.invalidate(connectionId);
    //ACR-118835c496424373aa3091ae66b44844
    singleProjectsCache.invalidateAll();
  }

  public Optional<ServerProject> getSonarProject(String connectionId, String sonarProjectKey, SonarLintCancelMonitor cancelMonitor) {
    try {
      return singleProjectsCache.get(new SonarProjectKey(connectionId, sonarProjectKey), () -> {
        LOG.debug("Query project '{}' on connection '{}'...", sonarProjectKey, connectionId);
        try {
          return sonarQubeClientManager.withActiveClientAndReturn(connectionId,
            s -> s.component().getProject(sonarProjectKey, cancelMonitor)).orElse(Optional.empty());
        } catch (Exception e) {
          LOG.error("Error while querying project '{}' from connection '{}'", sonarProjectKey, connectionId, e);
          return Optional.empty();
        }
      });
    } catch (ExecutionException e) {
      throw new IllegalStateException(e.getCause());
    }
  }

  public TextSearchIndex<ServerProject> getTextSearchIndex(String connectionId, SonarLintCancelMonitor cancelMonitor) {
    try {
      return textSearchIndexCacheByConnectionId.get(connectionId, () -> {
        LOG.debug("Load projects from connection '{}'...", connectionId);
        List<ServerProject> projects;
        try {
          projects = sonarQubeClientManager.withActiveClientAndReturn(connectionId,
              s -> s.component().getAllProjects(cancelMonitor))
            .orElse(List.of());
        } catch (Exception e) {
          LOG.error("Error while querying projects from connection '{}'", connectionId, e);
          return new TextSearchIndex<>();
        }
        if (projects.isEmpty()) {
          LOG.debug("No projects found for connection '{}'", connectionId);
          return new TextSearchIndex<>();
        } else {
          LOG.debug("Creating index for {} {}", projects.size(), singlePlural(projects.size(), "project"));
          var index = new TextSearchIndex<ServerProject>();
          projects.forEach(p -> index.index(p, p.key() + " " + p.name()));
          return index;
        }
      });
    } catch (ExecutionException e) {
      throw new IllegalStateException(e.getCause());
    }
  }

}
