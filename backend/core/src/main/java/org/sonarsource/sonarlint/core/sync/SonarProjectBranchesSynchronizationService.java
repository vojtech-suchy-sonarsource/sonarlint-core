/*
ACR-61a2e99486594c2599c8fba3cdd1e2ef
ACR-35489be5d65f4ba191faee621f9d5e04
ACR-e4640b74bd7e49ffb31a93e6f0d5cfcf
ACR-0ae6122bafbb40389ae0e4a6993ab713
ACR-196e2f98a0a84c60b3083f72aec02220
ACR-59902b482cdf40ebaaa8a38c4ef90592
ACR-d834285bb98c472dbdc1fb5b8cbc25f3
ACR-48f4acdb081145b4a7e6fdc7cf775a04
ACR-37fb460633a1438d8b2fc4c50d523028
ACR-72939994b3644feb9f5682bc4f4be371
ACR-87565fee498e402ba99330b3e59c7173
ACR-af9ea12e59bd4d98b9bb1a7828b7f00b
ACR-63ace5e69441453da8a2b84649d0ecd7
ACR-63e247462a224bcfa0efbe4dcebb3a59
ACR-2e9cd2cb639048b78f111bacea255fe2
ACR-1c9cbd81b67d429fa351568435eb59b2
ACR-f71af63429ae45a29b96bdefee1ac013
 */
package org.sonarsource.sonarlint.core.sync;

import java.util.Optional;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.branches.ServerBranch;
import org.sonarsource.sonarlint.core.serverconnection.ProjectBranches;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.stream.Collectors.toSet;

/*ACR-1fde23ed228a4e16a3a744532fdb310b
ACR-44b6a8847f28487b9a82af06083315e9
 */
public class SonarProjectBranchesSynchronizationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final StorageService storageService;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final ApplicationEventPublisher eventPublisher;

  public SonarProjectBranchesSynchronizationService(StorageService storageService, SonarQubeClientManager sonarQubeClientManager, ApplicationEventPublisher eventPublisher) {
    this.storageService = storageService;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.eventPublisher = eventPublisher;
  }

  public void sync(String connectionId, String sonarProjectKey, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(connectionId, serverApi -> {
      var branchesStorage = storageService.connection(connectionId).project(sonarProjectKey).branches();
      Optional<ProjectBranches> oldBranches = Optional.empty();
      if (branchesStorage.exists()) {
        oldBranches = Optional.of(branchesStorage.read());
      }
      var newBranches = getProjectBranches(serverApi, sonarProjectKey, cancelMonitor);
      branchesStorage.store(newBranches);
      if (oldBranches.isEmpty() || !oldBranches.get().equals(newBranches)) {
        LOG.debug("Project branches changed for project '{}'", sonarProjectKey);
        eventPublisher.publishEvent(new SonarProjectBranchesChangedEvent(connectionId, sonarProjectKey));
      }
    });
  }

  public ProjectBranches getProjectBranches(ServerApi serverApi, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    LOG.info("Synchronizing project branches for project '{}'", projectKey);
    var allBranches = serverApi.branches().getAllBranches(projectKey, cancelMonitor);
    var mainBranch = allBranches.stream().filter(ServerBranch::isMain).findFirst().map(ServerBranch::getName)
      .orElseThrow(() -> new IllegalStateException("No main branch for project '" + projectKey + "'"));
    return new ProjectBranches(allBranches.stream().map(ServerBranch::getName).collect(toSet()), mainBranch);
  }

  public String findMainBranch(String connectionId, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var branchesStorage = storageService.binding(new Binding(connectionId, projectKey)).branches();
    if (branchesStorage.exists()) {
      var storedBranches = branchesStorage.read();
      return storedBranches.getMainBranchName();
    } else {
      return sonarQubeClientManager.withActiveClientAndReturn(connectionId,
          serverApi -> getProjectBranches(serverApi, projectKey, cancelMonitor))
        .map(ProjectBranches::getMainBranchName).orElseThrow();
    }
  }

}
