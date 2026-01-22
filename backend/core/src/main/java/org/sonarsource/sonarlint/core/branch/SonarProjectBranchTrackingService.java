/*
ACR-a3341d4ba4a7438db8bd03b676130e88
ACR-3f4b61a8d4914d7a9d36185a404e8424
ACR-51ff1902811e441db8a87e8fabdc2a54
ACR-96eb81179caf49ac9b4e391366bf8ded
ACR-90beb626aa324dc4ba6204bcc7d85249
ACR-840f379d0d8a413c9f58e3b84724fca0
ACR-5bd85f19c06f4bf48fccbdfd77078027
ACR-ed9b29c612d24817a2fa0995ad813c21
ACR-4ebb93deda16400e92daaf57db6f1ced
ACR-c4868f25779045548a1db7830716885c
ACR-cf328e261e9445389fd7770bdfa753f2
ACR-dfe75146977049dc82e73841659c02ad
ACR-3b20c06860544b63a3ceab322f7f45fb
ACR-16bee53bd55c44c7a4eebdc143b29d56
ACR-911d6d9f6ae24458be683646a35a913f
ACR-389e3f37e6114ea5ad8653dbeaded4b3
ACR-df1ccb166d2b46b8b425971b18ca7fee
 */
package org.sonarsource.sonarlint.core.branch;

import jakarta.annotation.PreDestroy;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.slf4j.MDC;
import org.sonarsource.sonarlint.core.commons.SmartCancelableLoadingCache;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.DidChangeMatchedSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.branch.MatchSonarProjectBranchParams;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.sync.SonarProjectBranchesChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

/*ACR-e05c97ba5f5a4cff8c8d4ad1677cc765
ACR-aa13b0ff6368489c8cb46c74a547288d
 */
public class SonarProjectBranchTrackingService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarLintRpcClient client;
  private final StorageService storageService;
  private final ConfigurationRepository configurationRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final SmartCancelableLoadingCache<String, String> cachedMatchingBranchByConfigScope = new SmartCancelableLoadingCache<>("sonarlint-branch-matcher",
    this::matchSonarProjectBranch, this::afterCachedValueRefreshed);

  public SonarProjectBranchTrackingService(SonarLintRpcClient client, StorageService storageService,
    ConfigurationRepository configurationRepository, ApplicationEventPublisher applicationEventPublisher) {
    this.client = client;
    this.storageService = storageService;
    this.configurationRepository = configurationRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public Optional<String> awaitEffectiveSonarProjectBranch(String configurationScopeId) {
    return Optional.ofNullable(cachedMatchingBranchByConfigScope.get(configurationScopeId));
  }

  private void afterCachedValueRefreshed(String configScopeId, @Nullable String oldValue, @Nullable String newValue) {
    if (!Objects.equals(newValue, oldValue)) {
      LOG.debug("Matched Sonar project branch for configuration scope '{}' changed from '{}' to '{}'", configScopeId, oldValue, newValue);
      if (newValue != null) {
        client.didChangeMatchedSonarProjectBranch(new DidChangeMatchedSonarProjectBranchParams(configScopeId, newValue));
        applicationEventPublisher.publishEvent(new MatchedSonarProjectBranchChangedEvent(configScopeId, newValue));
      }
    } else {
      LOG.debug("Matched Sonar project branch for configuration scope '{}' is still '{}'", configScopeId, newValue);
    }
  }

  @EventListener
  public void onConfigurationScopeRemoved(ConfigurationScopeRemovedEvent event) {
    var removedConfigScopeId = event.getRemovedConfigurationScopeId();
    LOG.debug("Configuration scope '{}' removed, clearing matched branch", removedConfigScopeId);
    cachedMatchingBranchByConfigScope.clear(removedConfigScopeId);
  }

  @EventListener
  public void onConfigurationScopesAdded(ConfigurationScopesAddedWithBindingEvent event) {
    var configScopeIds = event.getConfigScopeIds();
    configScopeIds.forEach(configScopeId -> {
      var effectiveBinding = configurationRepository.getEffectiveBinding(configScopeId);
      if (effectiveBinding.isPresent()) {
        var branchesStorage = storageService.binding(effectiveBinding.get()).branches();
        if (branchesStorage.exists()) {
          LOG.debug("Bound configuration scope '{}' added with an existing storage, queuing matching of the Sonar project branch...", configScopeId);
          cachedMatchingBranchByConfigScope.refreshAsync(configScopeId);
        }
      }
    });
  }

  @EventListener
  public void onBindingChanged(BindingConfigChangedEvent bindingChanged) {
    var configScopeId = bindingChanged.configScopeId();
    if (!bindingChanged.newConfig().isBound()) {
      LOG.debug("Configuration scope '{}' unbound, clearing matched branch", configScopeId);
      cachedMatchingBranchByConfigScope.clear(configScopeId);
    } else {
      LOG.debug("Configuration scope '{}' binding changed, queuing matching of the Sonar project branch...", configScopeId);
      cachedMatchingBranchByConfigScope.refreshAsync(configScopeId);
    }
  }

  @EventListener
  public void onSonarProjectBranchChanged(SonarProjectBranchesChangedEvent event) {
    var configScopeIds = configurationRepository.getBoundScopesToConnectionAndSonarProject(event.getConnectionId(), event.getSonarProjectKey());
    configScopeIds.forEach(boundScope -> {
      LOG.debug("Sonar project branch changed for configuration scope '{}', queuing matching of the Sonar project branch...", boundScope.getConfigScopeId());
      cachedMatchingBranchByConfigScope.refreshAsync(boundScope.getConfigScopeId());
    });
  }

  public void didVcsRepositoryChange(String configScopeId) {
    LOG.debug("VCS repository changed for configuration scope '{}', queuing matching of the Sonar project branch...", configScopeId);
    cachedMatchingBranchByConfigScope.refreshAsync(configScopeId);
  }

  private String matchSonarProjectBranch(String configurationScopeId, SonarLintCancelMonitor cancelMonitor) {
    MDC.put("configScopeId", configurationScopeId);
    LOG.debug("Matching Sonar project branch");
    var effectiveBindingOpt = configurationRepository.getEffectiveBinding(configurationScopeId);
    if (effectiveBindingOpt.isEmpty()) {
      LOG.debug("No binding for configuration scope");
      return null;
    }
    var effectiveBinding = effectiveBindingOpt.get();

    var branchesStorage = storageService.binding(effectiveBinding).branches();
    if (!branchesStorage.exists()) {
      LOG.info("Cannot match Sonar branch, storage is empty");
      return null;
    }
    var storedBranches = branchesStorage.read();
    var mainBranchName = storedBranches.getMainBranchName();
    var matchedSonarBranch = requestClientToMatchSonarProjectBranch(configurationScopeId, mainBranchName, storedBranches.getBranchNames(), cancelMonitor);
    if (matchedSonarBranch == null) {
      matchedSonarBranch = mainBranchName;
    }
    cancelMonitor.checkCanceled();
    return matchedSonarBranch;
  }

  @CheckForNull
  private String requestClientToMatchSonarProjectBranch(String configurationScopeId, String mainSonarBranchName, Set<String> allSonarBranchesNames,
    SonarLintCancelMonitor cancelMonitor) {
    var matchSonarProjectBranchResponseCompletableFuture = client
      .matchSonarProjectBranch(new MatchSonarProjectBranchParams(configurationScopeId, mainSonarBranchName, allSonarBranchesNames));
    cancelMonitor.onCancel(() -> matchSonarProjectBranchResponseCompletableFuture.cancel(true));
    try {
      return matchSonarProjectBranchResponseCompletableFuture.join().getMatchedSonarProjectBranch();
    } catch (CancellationException e) {
      throw e;
    } catch (Exception e) {
      LOG.debug("Error while matching Sonar project branch for configuration scope '{}'", configurationScopeId, e);
      return null;
    }
  }

  @PreDestroy
  public void shutdown() {
    cachedMatchingBranchByConfigScope.close();
  }
}
