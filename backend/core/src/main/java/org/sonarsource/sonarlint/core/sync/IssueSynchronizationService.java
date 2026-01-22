/*
ACR-66ec620128ec4fb197c4736456c09c4c
ACR-ea3a35f5123f4d7e9c4088e39cbbeabe
ACR-148bf955166d4cd49e9b0ccd02a51a80
ACR-e24fe7dd4f154f4ab3f887f259c2220f
ACR-3f2558a08c4449bca0758e218da21966
ACR-3d1d6d7666ee4f69a0372d294fb3b918
ACR-3dd8397e7bc74dcda707597746c46771
ACR-f8cc597559b24f51b453dc494f4aeb2f
ACR-201dbbd1f4d74be08d2adcf74045c554
ACR-1d3ef7c05ba9473685fecb93c385de55
ACR-416e9cd5e7cf4af2bdf0d61d8907914d
ACR-683e7f36485f4990989ef33100163d55
ACR-b88d9ce087f548dba1217e9cc7982668
ACR-4bcf48ee035e454a9cc02d7274619e35
ACR-3347ecb2bf3c4216a23742bcf15314ba
ACR-8fefc4bfbb7c4a4ebf94e2a4b521f1ce
ACR-141aa14916bd40a69f2a0fd90fa77827
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
