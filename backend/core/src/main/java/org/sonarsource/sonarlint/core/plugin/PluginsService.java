/*
ACR-33ae901b2d3d40748785df79e267a0ba
ACR-c0bd932219824367b00f0290edfa4358
ACR-ae15fd8290944f3b9b394061d11b0523
ACR-34956df63a7b462b92aca6a665788621
ACR-a0fdbdcc121541fd81eb01c41aaded69
ACR-e290e8dbd330456b99c583992ae49b2a
ACR-618f0a9aa98e4d98a25dae80db9c7982
ACR-92a9bf5ce1a34098b685b72bebf1aa0c
ACR-8cf4c1a9b4da4283b5c30378513625ae
ACR-9c0baa0983d047bf9898f6d3077a6498
ACR-d21fe8872cd047e4a55574403d04640c
ACR-189650ba7f454a919fdbc5205d80939d
ACR-f18ae42c6ce642ea9d576d46ff992f1e
ACR-d48967b784234299b8f60187bd32b69f
ACR-a351bac50e6d439f83fc787a55a63f97
ACR-48405980b1df4a1cb9548bebe696c360
ACR-f020a75ddc9e4190a0f9897e86c3a5c5
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
    //ACR-321c329c110c49059259bde861bdf88b
    var pluginsStorage = storageService.connection(connectionId).plugins();

    Map<String, Path> pluginsToLoadByKey = new HashMap<>();
    //ACR-f09c6bcc07a346f887e7745b33071bcc
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
      //ACR-6a929da36570477889b18b8f628a9d58
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
      //ACR-d3562ed529e041099fcef57f7a4f9fcd
      return false;
    }
    //ACR-135fb82687e94f479d844be24fa4e602
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
        //ACR-6a0d0ee5cc834dd0871615bd01896724
        //ACR-630cebe2d84f427d95d4d71df21de1a7
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
