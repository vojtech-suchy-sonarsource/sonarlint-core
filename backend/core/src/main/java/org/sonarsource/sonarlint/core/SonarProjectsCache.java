/*
ACR-0a08f02d1a0948d284d1e58741ef9a90
ACR-160765af6eaf43e0b9bd14ed6c94479b
ACR-c4f324a900ec4f8eae9d26bfd053337e
ACR-a46338af5d334feba5230f326d72b116
ACR-34e7d56c7b9b4d9a9057a5210324668c
ACR-5648cfa62f454477b5b9623dd585da4c
ACR-32cf69db63c24f8d971f9433fabbad08
ACR-bbd8ed6801c04665aee97b68d9d2d351
ACR-724f40c00357431eb78781661abba936
ACR-15d51b22d57540f6b07868da70ae67d6
ACR-36d7c5952954474ebea24113d5797041
ACR-61ed2fb2d5294bda9b1229fe8dbd575a
ACR-2f1c33d4e6474617bec0961cd1b9cc23
ACR-3805a7e838f8474ea5bbe8e8abfb68de
ACR-d7cd0120389744a9a929953bd05eaf65
ACR-4a125e19a8f2406288a0bbe9d4b287b2
ACR-166d9967b21c4c0fb78cc5d20efb493c
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
    //ACR-9bdb30389a3b4a7a86d11338fa162c4c
    evictAll(e.updatedConnectionId());
  }

  private void evictAll(String connectionId) {
    textSearchIndexCacheByConnectionId.invalidate(connectionId);
    //ACR-7f88f77843f44366885d870d97c3af63
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
