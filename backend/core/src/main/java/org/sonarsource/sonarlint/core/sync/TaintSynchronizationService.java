/*
ACR-6344b0821bdc4c7d837c6f5bee0ffecc
ACR-351b21b0a8fc40c7bc672e825b992fa9
ACR-397c8e7b5aa043c3aa5f7bc4f155b51e
ACR-59b357e146404ab2b74c3b0e6804ae29
ACR-10711999d43440debd96aee1e975bece
ACR-36b842d4cf6246dea00352cb645ae3d5
ACR-0d4b11407fd84c8996949c2913de7bcb
ACR-77580f2138c34bb5983cf57145b0f931
ACR-9fa06cb694b74fdaa756adf762b1c66d
ACR-32d08b54ffe342309357623fe6471088
ACR-64888074ed984e44ae18439c3a7b53e1
ACR-a07cbd1f39754875ba0767308e7d2580
ACR-a3f59e41c0f44327a9de61b9cfc3acf2
ACR-1d6c0c2832574028b317b7476bb733f2
ACR-da0e796ad6f84679aff142ea8f005619
ACR-76ed884efa4b4050a97cb5451d987c35
ACR-42e059a46bb24754a5c74e5a8c86cfc8
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
