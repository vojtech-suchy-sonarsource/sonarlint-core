/*
ACR-18de7bfbdbe14faa9c6bc7dcacdda325
ACR-1dae1a1f5e3f48968d2b441bf840ac83
ACR-074b3fc751344d51b9ad6f7ead996ae3
ACR-bce1f825c0e2429dbaee6a6958ba6f03
ACR-4361a7d636c641f0bdc3763176ee7fa5
ACR-36514f5fc4f7444fb938b54ee3be4af9
ACR-70fe2f3d55674f7ba6d3ed3182af92a3
ACR-b16475edd7fc4aa6bbd06a24cd2a21d6
ACR-310665d46b564203939e7dfd583960cf
ACR-065efaaf833a45e2999900961bdb0a1f
ACR-2c70b975f35947f7aad7cda4e960a83b
ACR-41ee4be5775044c5b31976593188a51b
ACR-7e03720c81794957b9ecb371ee293be9
ACR-b9b889b170ac4bb3a30ba2147c5b9a76
ACR-5649278cd4164eef8e4c7c8dcf4c6d65
ACR-49932dcb40f14c4e95e2980db00ff775
ACR-8c2aa3c531f846d3823cc50c7846c488
 */
package org.sonarsource.sonarlint.core.sync;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverconnection.IssueDownloader;
import org.sonarsource.sonarlint.core.serverconnection.ServerIssueUpdater;
import org.sonarsource.sonarlint.core.serverconnection.TaintIssueDownloader;
import org.sonarsource.sonarlint.core.storage.StorageService;

public class IssueSynchronizationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final StorageService storageService;
  private final LanguageSupportRepository languageSupportRepository;
  private final SonarQubeClientManager sonarQubeClientManager;

  public IssueSynchronizationService(StorageService storageService, LanguageSupportRepository languageSupportRepository,
    SonarQubeClientManager sonarQubeClientManager) {
    this.storageService = storageService;
    this.languageSupportRepository = languageSupportRepository;
    this.sonarQubeClientManager = sonarQubeClientManager;
  }

  public void syncServerIssuesForProject(ServerApi serverApi, String connectionId, String projectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var issuesUpdater = new ServerIssueUpdater(storage, new IssueDownloader(enabledLanguagesToSync), new TaintIssueDownloader(enabledLanguagesToSync));
    if (serverApi.isSonarCloud()) {
      LOG.debug("Incremental issue sync is not supported by SonarCloud. Skipping.");
    } else {
      LOG.info("[SYNC] Synchronizing issues for project '{}' on branch '{}'", projectKey, branchName);
      issuesUpdater.sync(serverApi, projectKey, branchName, enabledLanguagesToSync, cancelMonitor);
    }
  }

  public void fetchProjectIssues(Binding binding, String activeBranch, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(binding.connectionId(),
      serverApi -> downloadServerIssuesForProject(binding.connectionId(), serverApi, binding.sonarProjectKey(), activeBranch, cancelMonitor));
  }

  private void downloadServerIssuesForProject(String connectionId, ServerApi serverApi, String projectKey, String branchName, SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var issuesUpdater = new ServerIssueUpdater(storage, new IssueDownloader(enabledLanguagesToSync()), new TaintIssueDownloader(enabledLanguagesToSync()));
    issuesUpdater.update(serverApi, projectKey, branchName, enabledLanguagesToSync(), cancelMonitor);
  }

  public void fetchFileIssues(Binding binding, Path serverFileRelativePath, String activeBranch, SonarLintCancelMonitor cancelMonitor) {
    sonarQubeClientManager.withActiveClient(binding.connectionId(),
      serverApi -> downloadServerIssuesForFile(binding.connectionId(), serverApi, binding.sonarProjectKey(), serverFileRelativePath, activeBranch, cancelMonitor));
  }

  public void downloadServerIssuesForFile(String connectionId, ServerApi serverApi, String projectKey, Path serverFileRelativePath, String branchName,
    SonarLintCancelMonitor cancelMonitor) {
    var storage = storageService.connection(connectionId);
    var enabledLanguagesToSync = languageSupportRepository.getEnabledLanguagesInConnectedMode().stream().filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
    var issuesUpdater = new ServerIssueUpdater(storage, new IssueDownloader(enabledLanguagesToSync), new TaintIssueDownloader(enabledLanguagesToSync));
    issuesUpdater.updateFileIssuesIfNeeded(serverApi, projectKey, serverFileRelativePath, branchName, cancelMonitor);
  }

  private Set<SonarLanguage> enabledLanguagesToSync() {
    return languageSupportRepository.getEnabledLanguagesInConnectedMode().stream()
      .filter(SonarLanguage::shouldSyncInConnectedMode)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

}
