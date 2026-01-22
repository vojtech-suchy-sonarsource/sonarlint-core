/*
ACR-73976cd1dfad43f98b09fc69b96d0e50
ACR-34126693c11f46e3b624ea59c673685d
ACR-2e74228be5024a6cb3db74d0bcf1fa30
ACR-90ee53fc81fc4cd8a6ad54d51aa5dd05
ACR-5c08f8d79ae742168a75a4edb71fda2c
ACR-7bfd1bca3ef7479a9b5a0ca8cb514649
ACR-68ec7c92095e4ff0a7714d08f9f5672e
ACR-00fd47fa44cd4d4c95886ba9739c5244
ACR-97c67f5b61f74f58bc5251e36ef03591
ACR-d18d90acc2d5486a8ec00e286fdadd32
ACR-e7c66a013ad849599cd3169b29310b78
ACR-e3ee0406f9ab4dcf951a2d2d21d99e7a
ACR-710a69eadf91494491388067084270d6
ACR-39c9b02b19f349f5bb4c190590a7f547
ACR-79bbb70cab8d444296bbd67f08c42e37
ACR-6ecdc03498c7412590c361ba71a3d5be
ACR-bb11e6991b6847dc96f6f648c34f5123
 */
package org.sonarsource.sonarlint.core.file;

import jakarta.annotation.PreDestroy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.SonarLintMDC;
import org.sonarsource.sonarlint.core.branch.MatchedSonarProjectBranchChangedEvent;
import org.sonarsource.sonarlint.core.commons.SmartCancelableLoadingCache;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.fs.ClientFile;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.serverconnection.prefix.FileTreeMatcher;
import org.springframework.context.event.EventListener;

/*ACR-c281fed0bb414311939b12391b760e4e
ACR-f8da3065f4744cf5b380c5e648f1d48a
ACR-86525a88f88f49b9827adaf99deaabfa
ACR-8f4e44b40d684a389688ba7454cc20ed
 */
public class PathTranslationService {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ClientFileSystemService clientFs;
  private final ConfigurationRepository configurationRepository;
  private final ServerFilePathsProvider serverFilePathsProvider;
  private final SmartCancelableLoadingCache<String, FilePathTranslation> cachedPathsTranslationByConfigScope =
    new SmartCancelableLoadingCache<>("sonarlint-path-translation", this::computePaths, (key, oldValue, newValue) -> {
    });

  public PathTranslationService(ClientFileSystemService clientFs, ConfigurationRepository configurationRepository, ServerFilePathsProvider serverFilePathsProvider) {
    this.clientFs = clientFs;
    this.configurationRepository = configurationRepository;
    this.serverFilePathsProvider = serverFilePathsProvider;
  }

  @CheckForNull
  private FilePathTranslation computePaths(String configScopeId, SonarLintCancelMonitor cancelMonitor) {
    SonarLintMDC.putConfigScopeId(configScopeId);
    LOG.debug("Computing paths translation for config scope '{}'...", configScopeId);
    var fileMatcher = new FileTreeMatcher();
    var binding = configurationRepository.getEffectiveBinding(configScopeId).orElse(null);
    if (binding == null) {
      LOG.debug("Config scope '{}' does not exist or is not bound", configScopeId);
      return null;
    }
    return serverFilePathsProvider.getServerPaths(binding, cancelMonitor)
      .map(paths -> matchPaths(configScopeId, fileMatcher, paths))
      .orElse(null);
  }

  private FilePathTranslation matchPaths(String configScopeId, FileTreeMatcher fileMatcher, List<Path> serverFilePaths) {
    LOG.debug("Starting matching paths for config scope '{}'...", configScopeId);
    var localFilePaths = clientFs.getFiles(configScopeId);
    if (localFilePaths.isEmpty()) {
      LOG.debug("No client files for config scope '{}'. Skipping path matching.", configScopeId);
      //ACR-25eb8d44f81d43339dac40fe5e63f6fb
      return new FilePathTranslation(Paths.get(""), Paths.get(""));
    }
    var match = fileMatcher.match(serverFilePaths, localFilePaths.stream().map(ClientFile::getClientRelativePath).toList());
    LOG.debug("Matched paths for config scope '{}':\n  * idePrefix={}\n  * serverPrefix={}", configScopeId, match.idePrefix(), match.sqPrefix());
    return new FilePathTranslation(match.idePrefix(), match.sqPrefix());
  }

  @EventListener
  public void onConfigurationScopeRemoved(ConfigurationScopeRemovedEvent event) {
    cachedPathsTranslationByConfigScope.clear(event.getRemovedConfigurationScopeId());
  }

  @EventListener
  public void onBindingChanged(BindingConfigChangedEvent event) {
    var configScopeId = event.configScopeId();
    cachedPathsTranslationByConfigScope.refreshAsync(configScopeId);
  }

  @EventListener
  public void onBranchChanged(MatchedSonarProjectBranchChangedEvent event) {
    var configScopeId = event.getConfigurationScopeId();
    cachedPathsTranslationByConfigScope.refreshAsync(configScopeId);
  }

  public Optional<FilePathTranslation> getOrComputePathTranslation(String configurationScopeId) {
    return Optional.ofNullable(cachedPathsTranslationByConfigScope.get(configurationScopeId));
  }

  @PreDestroy
  public void shutdown() {
    cachedPathsTranslationByConfigScope.close();
  }
}
