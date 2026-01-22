/*
ACR-93e1405bae44454b91c950133845f0ae
ACR-02359bfc76d14d7da5e9cbe18b73b52f
ACR-8bb4beb1cb934d56b944d8d9fa73e724
ACR-65b34d2cc6d74e4c918cec8f3bcd8e2f
ACR-589c952cbbd942b49459031c3ed85c2c
ACR-65ce6b8bf23d441b9a2aab6f8021d42e
ACR-9cadc4fe5928467e95f1df2d30c5e439
ACR-cd516070dae1475fb742efc530995b62
ACR-bfd6c874da9745ffb14322935c78742c
ACR-68d8dd48734d42358ba149f6ea7405bb
ACR-4d087aa772ac4a7cb3df5a10de46b39f
ACR-169c0d301fa04292b6e9a84465a58048
ACR-e0216f10442a4574b7766c59a284d7ba
ACR-610e9e451b674e0094c75dcac54764fe
ACR-3105e0f8247244e8a17c84fcf12d0e50
ACR-ecee5b06d00243ab92540bc0181b62f4
ACR-9e17166ad0a649bf8c9938148c6d21ee
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
