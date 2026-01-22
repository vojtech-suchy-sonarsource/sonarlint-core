/*
ACR-588dab9344154161866a6adb0da7bb6e
ACR-ea43b2394afe41498752f948a7348dd1
ACR-90e66a114b5d492d9a2f108f5a231806
ACR-a1b91cd41bb245e99d516b7b4135177e
ACR-901cd2ef39eb4c0da63862d8bd929a37
ACR-6060ef8fd80348d085e7e36868a9e0ad
ACR-1ef1ae3b150043cea3ea53ab309bbe42
ACR-d46bb0eff87d4028843d0c86cfcb93ae
ACR-8c0938a92f0748a0ad8a8cbccd817e59
ACR-aaa01e89a1a145298fd2955f0ffe748f
ACR-efbd7af812314527a0f9b5699131cb6b
ACR-105eea62a7b44ead85a3df00333d2704
ACR-5a0baf01f43b4299a56c766d9a8d2939
ACR-f6c9e74f77d94cbba1782ba5bd1a6e14
ACR-cb950f654d9045549cd64ac0f1b71d02
ACR-a146f170951f4a438004b4b1d80ecea1
ACR-4e545b7e3a504582babcb2658fc664ce
 */
package org.sonarsource.sonarlint.core.sync;

import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.DependencyRisksSynchronizedEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;
import org.sonarsource.sonarlint.core.serverconnection.storage.UpdateSummary;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.stream.Collectors.toSet;

public class ScaSynchronizationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final StorageService storageService;
  private final ApplicationEventPublisher eventPublisher;
  private final boolean isScaSynchronizationEnabled;

  public ScaSynchronizationService(StorageService storageService, ApplicationEventPublisher eventPublisher, InitializeParams initializeParams) {
    this.storageService = storageService;
    this.eventPublisher = eventPublisher;
    this.isScaSynchronizationEnabled = initializeParams.getBackendCapabilities().contains(BackendCapability.SCA_SYNCHRONIZATION);
  }

  public void synchronize(ServerApi serverApi, String connectionId, String sonarProjectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    if (!isScaSynchronizationEnabled) {
      return;
    }
    if (!isScaSupported(connectionId)) {
      return;
    }
    LOG.info("[SYNC] Synchronizing dependency risks for project '{}' on branch '{}'", sonarProjectKey, branchName);

    var summary = updateServerDependencyRisksForProject(serverApi, connectionId, sonarProjectKey, branchName, cancelMonitor);
    if (summary.hasAnythingChanged()) {
      eventPublisher.publishEvent(new DependencyRisksSynchronizedEvent(connectionId, sonarProjectKey, branchName, summary));
    }
  }

  private UpdateSummary<ServerDependencyRisk> updateServerDependencyRisksForProject(ServerApi serverApi, String connectionId, String sonarProjectKey, String branchName,
    SonarLintCancelMonitor cancelMonitor) {
    var issuesReleases = serverApi.sca().getIssuesReleases(sonarProjectKey, branchName, cancelMonitor);
    var findingsStore = storageService.connection(connectionId).project(sonarProjectKey).findings();

    var previousDependencyRisks = findingsStore.loadDependencyRisks(branchName);
    var previousDependencyRiskKeys = previousDependencyRisks.stream().map(ServerDependencyRisk::key).collect(toSet());

    var serverDependencyRisks = issuesReleases.issuesReleases().stream()
      .map(issueRelease -> new ServerDependencyRisk(
        issueRelease.key(),
        ServerDependencyRisk.Type.valueOf(issueRelease.type().name()),
        ServerDependencyRisk.Severity.valueOf(issueRelease.severity().name()),
        ServerDependencyRisk.SoftwareQuality.valueOf(issueRelease.quality().name()),
        ServerDependencyRisk.Status.valueOf(issueRelease.status().name()),
        issueRelease.release().packageName(),
        issueRelease.release().version(),
        issueRelease.vulnerabilityId(),
        issueRelease.cvssScore(),
        issueRelease.transitions().stream().map(Enum::name).map(ServerDependencyRisk.Transition::valueOf).toList()))
      .toList();

    findingsStore.replaceAllDependencyRisksOfBranch(branchName, serverDependencyRisks);

    var newDependencyRiskKeys = serverDependencyRisks.stream().map(ServerDependencyRisk::key).collect(toSet());
    var deletedDependencyRiskIds = previousDependencyRisks.stream()
      .map(ServerDependencyRisk::key)
      .filter(key -> !newDependencyRiskKeys.contains(key))
      .collect(toSet());
    var addedDependencyRisks = serverDependencyRisks.stream()
      .filter(issue -> !previousDependencyRiskKeys.contains(issue.key()))
      .toList();
    var updatedDependencyRisks = serverDependencyRisks.stream()
      .filter(issue -> previousDependencyRiskKeys.contains(issue.key()))
      .toList();

    return new UpdateSummary<>(deletedDependencyRiskIds, addedDependencyRisks, updatedDependencyRisks);
  }

  private boolean isScaSupported(String connectionId) {
    var serverInfo = storageService.connection(connectionId).serverInfo().read();
    return serverInfo.map(info -> info.hasFeature(Feature.SCA)).orElse(false);
  }
}
