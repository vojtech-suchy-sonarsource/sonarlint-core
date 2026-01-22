/*
ACR-39f01afb1e614ea0b871008784fda3c0
ACR-1077eb520ede41a9aec86f02cc5c1d63
ACR-e611286eadca44b88dd70a92a9efe320
ACR-c2b9717f9408496bb19aa8effea41744
ACR-9b252027c2e542a885dd2e67a8ed22c6
ACR-74ee29360c3b4dbca4c024e36f5c6d38
ACR-b7f31e04fc9746a7a6d3ff84174385a2
ACR-53abb33f87c34008af15a1787abebe13
ACR-d71c58ff6e6843e4913ea8df245cffbd
ACR-4a8e8f9ea1434b978ac8dee1224f14a5
ACR-d1a8a945081a43369248cd147efa74a0
ACR-d3218a99bad24f31808eed3b22b37410
ACR-56e2b9a765da403992ead72240d04cb8
ACR-847feee20535400f8f66dfe5171ffd81
ACR-b0ef009db8c4441db035f2419f7fb4eb
ACR-6ee47a53b4f1458486679f3d4944f8dd
ACR-878caf70fef941e5bf32462b7b1898dd
 */
package org.sonarsource.sonarlint.core.sync;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.TaintVulnerabilitiesSynchronizedEvent;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverconnection.IssueDownloader;
import org.sonarsource.sonarlint.core.serverconnection.ServerIssueUpdater;
import org.sonarsource.sonarlint.core.serverconnection.TaintIssueDownloader;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;
import org.sonarsource.sonarlint.core.serverconnection.storage.UpdateSummary;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.stream.Collectors.groupingBy;

public class TaintSynchronizationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ConfigurationRepository configurationRepository;
  private final SonarProjectBranchTrackingService branchTrackingService;
  private final StorageService storageService;
  private final LanguageSupportRepository languageSupportRepository;
  private final SonarQubeClientManager sonarQubeClientManager;
  private final ApplicationEventPublisher eventPublisher;

  public TaintSynchronizationService(ConfigurationRepository configurationRepository, SonarProjectBranchTrackingService branchTrackingService,
                                     StorageService storageService, LanguageSupportRepository languageSupportRepository,
                                     SonarQubeClientManager sonarQubeClientManager, ApplicationEventPublisher eventPublisher) {
    this.configurationRepository = configurationRepository;
    this.branchTrackingService = branchTrackingService;
    this.storageService = storageService;
    this.languageSupportRepository = languageSupportRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.eventPublisher = eventPublisher;
  }

  public void synchronizeTaintVulnerabilities(String connectionId, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(connectionId, serverApi -> {
      var allScopes = configurationRepository.getBoundScopesToConnectionAndSonarProject(connectionId, projectKey);
      var allScopesByOptBranch = allScopes.stream()
        .collect(groupingBy(b -> branchTrackingService.awaitEffectiveSonarProjectBranch(b.getConfigScopeId())));
      allScopesByOptBranch
        .forEach((branchNameOpt, scopes) -> branchNameOpt.ifPresent(branchName -> synchronizeTaintVulnerabilities(serverApi, connectionId, projectKey, branchName, cancelMonitor)));
    });
  }

  public void synchronizeTaintVulnerabilities(ServerApi serverApi, String connectionId, String projectKey, String branch, SonarLintCancelMonitor cancelMonitor) {
    if (languageSupportRepository.areTaintVulnerabilitiesSupported()) {
      var summary = updateServerTaintIssuesForProject(connectionId, serverApi, projectKey, branch, cancelMonitor);
      if (summary.hasAnythingChanged()) {
        eventPublisher.publishEvent(new TaintVulnerabilitiesSynchronizedEvent(connectionId, projectKey, branch, summary));
      }
    }
  }

  private UpdateSummary<ServerTaintIssue> updateServerTaintIssuesForProject(String connectionId, ServerApi serverApi, String projectKey,
    String branchName, SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var issuesUpdater = new ServerIssueUpdater(storage, new IssueDownloader(enabledLanguagesToSync), new TaintIssueDownloader(enabledLanguagesToSync));
    if (serverApi.isSonarCloud()) {
      return issuesUpdater.downloadProjectTaints(serverApi, projectKey, branchName, enabledLanguagesToSync, cancelMonitor);
    } else {
      LOG.info("[SYNC] Synchronizing taint issues for project '{}' on branch '{}'", projectKey, branchName);
      return issuesUpdater.syncTaints(serverApi, projectKey, branchName, enabledLanguagesToSync, cancelMonitor);
    }
  }

}
