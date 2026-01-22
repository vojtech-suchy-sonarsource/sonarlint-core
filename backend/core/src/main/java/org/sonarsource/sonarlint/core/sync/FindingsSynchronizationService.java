/*
ACR-11f7dcbe42de453298dd1ed7108e1ee2
ACR-e837d5459fb7412f9223a0edcd7b0e1a
ACR-3dd2899471624643af040087c5426a90
ACR-8f4a1cd1606a45569bf957e72e970d68
ACR-c66069e49ce343fd88bb4bb3ca9c453d
ACR-bf634dc822324783abedc455f2b36070
ACR-7f470f7cda0042d1a350a77d87985191
ACR-e03f476d784c4448abaf00e50cd746a4
ACR-5955bef318974a83846c3e095882514c
ACR-042f4cd7054a483aa178322ee2e2b43e
ACR-7b8a90bc62e84cea8958f2688d8c1e0c
ACR-e5a6d76331684252b450b8221ae3a223
ACR-86e7176ac78445818d971992c4564d2d
ACR-f1c54f82592d402ba7edcad41fb420ec
ACR-a3c4063387824bef903a55062a8e3856
ACR-464ee3717181421492d78fa9ebebd144
ACR-21ed8b01c2224095a4068a594b9ed768
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
