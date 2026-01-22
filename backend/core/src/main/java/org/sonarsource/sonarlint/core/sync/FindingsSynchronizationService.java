/*
ACR-781a043da81e4113829bee35fdc60d13
ACR-3e026652e7424ce39b0b156a9845565a
ACR-413f2266b7ce461c890dfadc028cfd2f
ACR-2fee777cb50e4d059988fe4bd46ae820
ACR-478b372c74ba49188e256cf5e9562694
ACR-c0367cb71b2a4b09a10b06fc1aa5fa49
ACR-102feb96baf046418361c43133dfa200
ACR-39befb74cf374392aedbea579e3fe0dc
ACR-683087555cd94084bfe18874bde2e9c4
ACR-1a2f9b81ecad4f44b26305ca056fdd71
ACR-ec214bc5229e4aa2848e9c6e9d0f8428
ACR-733ff584d74a47279a7896b322029b1f
ACR-87e930b7e2ed4765915177c7d641ea77
ACR-61de933a9321430f93690d5c02f73119
ACR-44ee8e09704a4083b4bb7a5a41dc179a
ACR-51729e733b0e40be88911372ee0c87b6
ACR-1b44f145bc374abd85584a7263eee2fc
 */
package org.sonarsource.sonarlint.core.sync;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.branch.SonarProjectBranchTrackingService;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.file.FilePathTranslation;
import org.sonarsource.sonarlint.core.file.PathTranslationService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;

public class FindingsSynchronizationService {
  private static final int FETCH_ALL_ISSUES_THRESHOLD = 10;
  private final ConfigurationRepository configurationRepository;
  private final SonarProjectBranchTrackingService branchTrackingService;
  private final PathTranslationService pathTranslationService;
  private final IssueSynchronizationService issueSynchronizationService;
  private final HotspotSynchronizationService hotspotSynchronizationService;
  private final ExecutorService issueUpdaterExecutorService;
  private final boolean shouldRefreshHotspots;

  public FindingsSynchronizationService(ConfigurationRepository configurationRepository, SonarProjectBranchTrackingService branchTrackingService,
    PathTranslationService pathTranslationService, IssueSynchronizationService issueSynchronizationService, HotspotSynchronizationService hotspotSynchronizationService,
    InitializeParams initializeParams) {
    this.configurationRepository = configurationRepository;
    this.branchTrackingService = branchTrackingService;
    this.pathTranslationService = pathTranslationService;
    this.issueSynchronizationService = issueSynchronizationService;
    this.hotspotSynchronizationService = hotspotSynchronizationService;
    this.issueUpdaterExecutorService = FailSafeExecutors.newSingleThreadExecutor("sonarlint-server-tracking-issue-updater");
    this.shouldRefreshHotspots = initializeParams.getBackendCapabilities().contains(BackendCapability.SECURITY_HOTSPOTS);
  }

  public void refreshServerFindings(String configurationScopeId, Set<Path> pathsToRefresh) {
    var effectiveBindingOpt = configurationRepository.getEffectiveBinding(configurationScopeId);
    var activeBranchOpt = branchTrackingService.awaitEffectiveSonarProjectBranch(configurationScopeId);
    var translationOpt = pathTranslationService.getOrComputePathTranslation(configurationScopeId);
    if (effectiveBindingOpt.isPresent() && activeBranchOpt.isPresent() && translationOpt.isPresent()) {
      var binding = effectiveBindingOpt.get();
      var activeBranch = activeBranchOpt.get();
      var translation = translationOpt.get();
      var cancelMonitor = new SonarLintCancelMonitor();
      refreshServerIssues(cancelMonitor, binding, activeBranch, pathsToRefresh, translation);
      if (shouldRefreshHotspots) {
        refreshServerSecurityHotspots(cancelMonitor, binding, activeBranch, pathsToRefresh, translationOpt.get());
      }
    }
  }

  private void refreshServerIssues(SonarLintCancelMonitor cancelMonitor, Binding binding, String activeBranch,
    Set<Path> pathsInvolved, FilePathTranslation translation) {
    var serverFileRelativePaths = pathsInvolved.stream().map(translation::ideToServerPath).collect(Collectors.toSet());
    var downloadAllIssuesAtOnce = serverFileRelativePaths.size() > FETCH_ALL_ISSUES_THRESHOLD;
    var fetchTasks = new LinkedList<CompletableFuture<?>>();
    if (downloadAllIssuesAtOnce) {
      fetchTasks.add(CompletableFuture.runAsync(() -> issueSynchronizationService.fetchProjectIssues(binding, activeBranch, cancelMonitor), issueUpdaterExecutorService));
    } else {
      fetchTasks.addAll(serverFileRelativePaths.stream()
        .map(serverFileRelativePath -> CompletableFuture.runAsync(() -> issueSynchronizationService
          .fetchFileIssues(binding, serverFileRelativePath, activeBranch, cancelMonitor), issueUpdaterExecutorService))
        .toList());
    }
    CompletableFuture.allOf(fetchTasks.toArray(new CompletableFuture[0])).join();
  }

  private void refreshServerSecurityHotspots(SonarLintCancelMonitor cancelMonitor, Binding binding, String activeBranch,
    Set<Path> pathsInvolved, FilePathTranslation translation) {
    var serverFileRelativePaths = pathsInvolved.stream().map(translation::ideToServerPath).collect(Collectors.toSet());
    var downloadAllSecurityHotspotsAtOnce = serverFileRelativePaths.size() > FETCH_ALL_ISSUES_THRESHOLD;
    var fetchTasks = new LinkedList<CompletableFuture<?>>();
    if (downloadAllSecurityHotspotsAtOnce) {
      fetchTasks.add(CompletableFuture.runAsync(() -> hotspotSynchronizationService.fetchProjectHotspots(binding, activeBranch, cancelMonitor), issueUpdaterExecutorService));
    } else {
      fetchTasks.addAll(serverFileRelativePaths.stream()
        .map(serverFileRelativePath -> CompletableFuture
          .runAsync(() -> hotspotSynchronizationService.fetchFileHotspots(binding, activeBranch, serverFileRelativePath, cancelMonitor), issueUpdaterExecutorService))
        .toList());
    }
    CompletableFuture.allOf(fetchTasks.toArray(new CompletableFuture[0])).join();
  }
}
