/*
ACR-4099db142db64673879fe0a98e5d5873
ACR-1d6c51c22c564ed385bf937cb02db90b
ACR-2a22f36b7ff04a789918742e8c0e4a5c
ACR-6256900af2af4d09912e3d121274b387
ACR-cd5a0082a0d4402e812f639c99014c6a
ACR-e6deae36dd9a413cad241ab8f554c556
ACR-3aa99fd81db646e4a13fae881125fdbb
ACR-cd3f8dbba1cc4f5cb34f7b2632af1449
ACR-9e4df9a4e8934ad9a1f4828523e62665
ACR-f3c2a13585ed443c86a1615f17741e9e
ACR-001edaa4787e4614a984c6665404638f
ACR-764285dc3dbb49e4904d5b6af9fe784b
ACR-0028bd85f9f3496782b6ec9a86e467fc
ACR-a65a4504b54e40a0bef1d56938170118
ACR-f31b0dbe3e594d84bb1c6fdeeabf5063
ACR-f8d9eb3e9dae42ef819dcfc427a3cbd6
ACR-f995f6c8d0d642ce8413ebbe1fb62d63
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

/*ACR-aae4a16ab6e04b52adbd872b224ea3a1
ACR-34f01a87c2114f328e69d0802ca2927c
ACR-46c18fd8db3e48fc9d09ef2d11742f8c
ACR-541211379e6c4f16a8f876e1effeb4d6
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
      //ACR-a4ceaed4696d4da3af2de9e639a2b0f7
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
