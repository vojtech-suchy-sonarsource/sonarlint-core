/*
ACR-89750465a0d74cdd83ba4d3cd3025dcf
ACR-f2691278442b44fc8191556fe1bde164
ACR-9431b9bf8a3b44a29f73d302d1804db2
ACR-7e435b68d1254cd5a530dc912476ba40
ACR-121f074c44bb45b1885b1b2befd77781
ACR-802cb7ef7eb84cf7b9e7acffccd0b622
ACR-84ddffdbf72443639393b8353bc8ced0
ACR-95857db675c94033a75923f5c272cd68
ACR-16c22e48b7b64e3db4fbffe71e200744
ACR-834debc25f424afcbf8549a60ebe786f
ACR-adc4143c4b974a498b8a659c22436b09
ACR-11376b1bdbd9436abce81f7152b7bf63
ACR-dbd1ae25c702424aa58708df14737c76
ACR-baee1eb33fa24e13a64d31712d14dcca
ACR-7552fc313de148feb325380c0cc93ec2
ACR-6866c69f970841dc81b120891e4727b4
ACR-68de50f8477e42d6bd277f949ddede99
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

/*ACR-9d3fa65c5d374d21be88c159dc47fc09
ACR-857998cf5625434282e7fb31ad850393
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
