/*
ACR-d9d15e2b343345589df4a2cbbb84f2db
ACR-8e5bc6c41290463e8a2d4e798d09749d
ACR-592c1c2661e24165bab52d4c61ae5046
ACR-3079e3b9e7ed4fd2ab74014b5e392edb
ACR-8be051593d644f60905b87830c44c551
ACR-04f024afc48949cdbbc0320b483f4e6c
ACR-dce3a856c7574ebcb5f6b3bde23eec1d
ACR-b57e5cffd2424b30bacc3bf356cce0a4
ACR-e5fba71e10074c6db83436c0d4b45a89
ACR-da184f00ba5346278ae02d0b65b68838
ACR-61e05178ce6b4734b243772e4ed90193
ACR-8eceeb0b23354e0ca51025818b58e95b
ACR-872e8008e9b241ff8e8ed3050a18f3db
ACR-1207c66d36c04081ab8a8e555f55f188
ACR-a8606c70c54a49629cc723828497320b
ACR-750bed0b66ab47e2a7302374a339722d
ACR-b15c8aa778434c31a5b1182f8d578f87
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

/*ACR-83203a29736f47229cf00d5136ae65ec
ACR-c1eaa0d32b664e9ea8b16a7c86835dd6
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
