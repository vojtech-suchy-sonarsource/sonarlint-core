/*
ACR-266e192d15c049cf8bdc86f55a63953c
ACR-1b84b005f58f48c5bc0fac0dc64be959
ACR-8bc147d5201a4e56a28eb7349f52cc45
ACR-3aba9e7be78b488dad742811f1ea4ed2
ACR-be5248681dfb4b8fb8caa0d59d2c7c34
ACR-4b5dd33ec387492caf1aa89b21bed24b
ACR-d314f5efe0e646baac45706c34c22c4f
ACR-af6739d42eae4556b84f6eddcd0df447
ACR-e9424ac894914a829794fb39aecb284b
ACR-c14fc5db24b941f49e65934f8731a3c5
ACR-c2e6e7bf1d8f46e4a52c8710b15ffd09
ACR-6b3f27de0fcc47c682a47403519a1c32
ACR-708f3b6a1209405fba90f38b8c8384a2
ACR-771dc29a2ce74b5c9847c1b5c5c9150f
ACR-a2a81edeeb704fc68bd15283baa8a834
ACR-d814f1b318cf424490ee805af1698105
ACR-8f7f492968ea4b2a85725d662619a5dd
 */
package org.sonarsource.sonarlint.core.plugin;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.analysis.NodeJsService;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.languages.LanguageSupportRepository;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;
import org.sonarsource.sonarlint.core.plugin.commons.PluginsLoadResult;
import org.sonarsource.sonarlint.core.plugin.commons.PluginsLoader;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginRequirementsCheckResult;
import org.sonarsource.sonarlint.core.plugin.skipped.SkippedPlugin;
import org.sonarsource.sonarlint.core.plugin.skipped.SkippedPluginsRepository;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.LanguageSpecificRequirements;
import org.sonarsource.sonarlint.core.serverconnection.PluginsSynchronizer;
import org.sonarsource.sonarlint.core.serverconnection.StoredPlugin;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.springframework.context.event.EventListener;

import static org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability.DATAFLOW_BUG_DETECTION;
import static org.sonarsource.sonarlint.core.serverconnection.PluginsSynchronizer.CUSTOM_SECRETS_MIN_SQ_VERSION;
import static org.sonarsource.sonarlint.core.serverconnection.PluginsSynchronizer.ENTERPRISE_GO_MIN_SQ_VERSION;
import static org.sonarsource.sonarlint.core.serverconnection.PluginsSynchronizer.ENTERPRISE_IAC_MIN_SQ_VERSION;

public class PluginsService {
  private static final Version REPACKAGED_DOTNET_ANALYZER_MIN_SQ_VERSION = Version.create("10.8");

  private final SonarLintLogger logger = SonarLintLogger.get();
  private final PluginsRepository pluginsRepository;
  private final SkippedPluginsRepository skippedPluginsRepository;
  private final LanguageSupportRepository languageSupportRepository;
  private final StorageService storageService;
  private final Set<Path> embeddedPluginPaths;
  private final CSharpSupport csharpSupport;
  private final Set<String> disabledPluginKeysForAnalysis;
  private final Map<String, Path> connectedModeEmbeddedPluginPathsByKey;
  private final InitializeParams initializeParams;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;
  private final NodeJsService nodeJsService;
  private final boolean enableDataflowBugDetection;

  public PluginsService(PluginsRepository pluginsRepository, SkippedPluginsRepository skippedPluginsRepository, LanguageSupportRepository languageSupportRepository,
    StorageService storageService, InitializeParams params, ConnectionConfigurationRepository connectionConfigurationRepository, NodeJsService nodeJsService) {
    this.pluginsRepository = pluginsRepository;
    this.skippedPluginsRepository = skippedPluginsRepository;
    this.languageSupportRepository = languageSupportRepository;
    this.storageService = storageService;
    this.embeddedPluginPaths = params.getEmbeddedPluginPaths();
    this.connectedModeEmbeddedPluginPathsByKey = params.getConnectedModeEmbeddedPluginPathsByKey();
    this.enableDataflowBugDetection = params.getBackendCapabilities().contains(DATAFLOW_BUG_DETECTION);
    this.initializeParams = params;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
    this.nodeJsService = nodeJsService;
    this.disabledPluginKeysForAnalysis = params.getDisabledPluginKeysForAnalysis();
    this.csharpSupport = new CSharpSupport(params.getLanguageSpecificRequirements());
  }

  @NotNull
  private static List<SkippedPlugin> getSkippedPlugins(PluginsLoadResult result) {
    return result.getPluginCheckResultByKeys().values().stream()
      .filter(PluginRequirementsCheckResult::isSkipped)
      .map(plugin -> new SkippedPlugin(plugin.getPlugin().getKey(), plugin.getSkipReason().get()))
      .toList();
  }

  public LoadedPlugins reloadPluginsFromStorage(String connectionId) {
    pluginsRepository.unload(connectionId);
    return getPlugins(connectionId);
  }

  public LoadedPlugins getEmbeddedPlugins() {
    var loadedEmbeddedPlugins = pluginsRepository.getLoadedEmbeddedPlugins();
    if (loadedEmbeddedPlugins == null) {
      var allEmbeddedPlugins = new HashSet<>(embeddedPluginPaths);
      if (csharpSupport.csharpOssPluginPath != null) {
        allEmbeddedPlugins.add(csharpSupport.csharpOssPluginPath);
      }
      var result = loadPlugins(languageSupportRepository.getEnabledLanguagesInStandaloneMode(), allEmbeddedPlugins, enableDataflowBugDetection);
      loadedEmbeddedPlugins = result.getLoadedPlugins();
      pluginsRepository.setLoadedEmbeddedPlugins(loadedEmbeddedPlugins);
      skippedPluginsRepository.setSkippedEmbeddedPlugins(getSkippedPlugins(result));
    }
    return loadedEmbeddedPlugins;
  }

  public LoadedPlugins getPlugins(String connectionId) {
    var loadedPlugins = pluginsRepository.getLoadedPlugins(connectionId);
    if (loadedPlugins == null) {
      var result = loadPlugins(connectionId);
      loadedPlugins = result.getLoadedPlugins();
      pluginsRepository.setLoadedPlugins(connectionId, loadedPlugins);
      skippedPluginsRepository.setSkippedPlugins(connectionId, getSkippedPlugins(result));
    }
    return loadedPlugins;
  }

  private PluginsLoadResult loadPlugins(String connectionId) {
    var pluginPaths = getPluginPathsForConnection(connectionId);

    return loadPlugins(languageSupportRepository.getEnabledLanguagesInConnectedMode(), pluginPaths, enableDataflowBugDetection);
  }

  private Set<Path> getPluginPathsForConnection(String connectionId) {
    //ACR-abb82c0bae924d909fdddd2ac73e85a8
    var pluginsStorage = storageService.connection(connectionId).plugins();

    Map<String, Path> pluginsToLoadByKey = new HashMap<>();
    //ACR-4a22922fba7647a686be421fbbe4c2cf
    pluginsToLoadByKey.putAll(pluginsStorage.getStoredPluginPathsByKey());
    pluginsToLoadByKey.putAll(getEmbeddedPluginPathsByKey(connectionId));
    if (languageSupportRepository.getEnabledLanguagesInConnectedMode().contains(SonarLanguage.CS)) {
      if (shouldUseEnterpriseCSharpAnalyzer(connectionId) && csharpSupport.csharpEnterprisePluginPath != null) {
        pluginsToLoadByKey.put(PluginsSynchronizer.CSHARP_ENTERPRISE_PLUGIN_ID, csharpSupport.csharpEnterprisePluginPath);
      } else if (csharpSupport.csharpOssPluginPath != null) {
        pluginsToLoadByKey.put(SonarLanguage.CS.getPluginKey(), csharpSupport.csharpOssPluginPath);
      }
    }
    return Set.copyOf(pluginsToLoadByKey.values());
  }

  private Map<String, Path> getEmbeddedPluginPathsByKey(String connectionId) {
    var embeddedPlugins = new HashMap<>(connectedModeEmbeddedPluginPathsByKey);
    if (supportsCustomSecrets(connectionId)) {
      embeddedPlugins.remove(SonarLanguage.SECRETS.getPluginKey());
    }
    if (supportsIaCEnterprise(connectionId)) {
      //ACR-e25e4b78193a4abc9c4778f1ea4c015c
      embeddedPlugins.remove(SonarLanguage.AZURERESOURCEMANAGER.getPluginKey());
    }
    if (supportsGoEnterprise(connectionId)) {
      embeddedPlugins.remove(SonarLanguage.GO.getPluginKey());
    }
    return embeddedPlugins;
  }

  public boolean supportsIaCEnterprise(String connectionId) {
    return isSonarQubeCloudOrVersionHigherThan(ENTERPRISE_IAC_MIN_SQ_VERSION, connectionId);
  }

  public boolean supportsCustomSecrets(String connectionId) {
    return isSonarQubeCloudOrVersionHigherThan(CUSTOM_SECRETS_MIN_SQ_VERSION, connectionId);
  }

  public boolean supportsGoEnterprise(String connectionId) {
    return isSonarQubeCloudOrVersionHigherThan(ENTERPRISE_GO_MIN_SQ_VERSION, connectionId);
  }

  private boolean isSonarQubeCloudOrVersionHigherThan(Version version, String connectionId) {
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    if (connection == null) {
      //ACR-e79a7ccff76842eea65b7b96cc2ca27b
      return false;
    }
    //ACR-98ba9d7e96c549e8bed6bd0f0ef23eb6
    return connection.getKind() == ConnectionKind.SONARCLOUD || storageService.connection(connectionId).serverInfo().read()
      .map(serverInfo -> serverInfo.version().compareToIgnoreQualifier(version) >= 0)
      .orElse(false);
  }

  private PluginsLoadResult loadPlugins(Set<SonarLanguage> enabledLanguages, Set<Path> pluginPaths, boolean enableDataflowBugDetection) {
    var config = new PluginsLoader.Configuration(pluginPaths, enabledLanguages, enableDataflowBugDetection, nodeJsService.getActiveNodeJsVersion());
    return new PluginsLoader().load(config, disabledPluginKeysForAnalysis);
  }

  @EventListener
  public void connectionRemoved(ConnectionConfigurationRemovedEvent e) {
    evictAll(e.getRemovedConnectionId());
  }

  private void evictAll(String connectionId) {
    logger.debug("Evict loaded plugins for connection '{}'", connectionId);
    pluginsRepository.unload(connectionId);
  }

  public boolean shouldUseEnterpriseCSharpAnalyzer(String connectionId) {
    return shouldUseEnterpriseDotNetAnalyzer(connectionId, PluginsSynchronizer.CSHARP_ENTERPRISE_PLUGIN_ID);
  }

  private boolean shouldUseEnterpriseDotNetAnalyzer(String connectionId, String analyzerName) {
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    var isSonarCloud = connection != null && connection.getKind() == ConnectionKind.SONARCLOUD;
    if (isSonarCloud) {
      return true;
    } else {
      var connectionStorage = storageService.connection(connectionId);
      var serverInfo = connectionStorage.serverInfo().read();
      if (serverInfo.isEmpty()) {
        return false;
      } else {
        //ACR-ae34bc659ae546389cf37181c8c67a3b
        //ACR-d6529cfda4d149cc9a6ff6490613b3dc
        var serverVersion = serverInfo.get().version();
        var supportsRepackagedDotnetAnalyzer = serverVersion.compareToIgnoreQualifier(REPACKAGED_DOTNET_ANALYZER_MIN_SQ_VERSION) >= 0;
        var hasEnterprisePlugin = connectionStorage.plugins().getStoredPlugins().stream().map(StoredPlugin::getKey).anyMatch(analyzerName::equals);
        return !supportsRepackagedDotnetAnalyzer || hasEnterprisePlugin;
      }
    }
  }

  public boolean shouldUseEnterpriseVbAnalyzer(String connectionId) {
    return shouldUseEnterpriseDotNetAnalyzer(connectionId, PluginsSynchronizer.VBNET_ENTERPRISE_PLUGIN_ID);
  }

  public DotnetSupport getDotnetSupport(@Nullable String connectionId) {
    if (connectionId == null) {
      return new DotnetSupport(initializeParams, csharpSupport.csharpOssPluginPath, false, false);
    }
    var actualCsharpAnalyzerPath = shouldUseEnterpriseCSharpAnalyzer(connectionId) ? csharpSupport.csharpEnterprisePluginPath :
      csharpSupport.csharpOssPluginPath;
    var shouldUseCsharpEnterprise = shouldUseEnterpriseCSharpAnalyzer(connectionId);
    var shouldUseVbEnterprise = shouldUseEnterpriseVbAnalyzer(connectionId);
    return new DotnetSupport(initializeParams, actualCsharpAnalyzerPath, shouldUseCsharpEnterprise, shouldUseVbEnterprise);
  }

  @PreDestroy
  public void shutdown() throws IOException {
    try {
      pluginsRepository.unloadAllPlugins();
    } catch (Exception e) {
      SonarLintLogger.get().error("Error shutting down plugins service", e);
    }
  }

  static class CSharpSupport {
    final Path csharpOssPluginPath;
    final Path csharpEnterprisePluginPath;

    CSharpSupport(@Nullable LanguageSpecificRequirements languageSpecificRequirements) {
      if (languageSpecificRequirements == null) {
        csharpOssPluginPath = null;
        csharpEnterprisePluginPath = null;
      } else {
        var omnisharpRequirements = languageSpecificRequirements.getOmnisharpRequirements();
        if (omnisharpRequirements == null) {
          csharpOssPluginPath = null;
          csharpEnterprisePluginPath = null;
        } else {
          csharpOssPluginPath = omnisharpRequirements.getOssAnalyzerPath();
          csharpEnterprisePluginPath = omnisharpRequirements.getEnterpriseAnalyzerPath();
        }
      }
    }
  }
}
