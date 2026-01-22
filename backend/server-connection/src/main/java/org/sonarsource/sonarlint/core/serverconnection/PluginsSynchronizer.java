/*
ACR-395094d1e9c1473fb93f97b8500c7599
ACR-3e96a5e0f6a44a37ae92149727d57926
ACR-28706496abf54ddf9688a6f230b66c1a
ACR-040d317d14644265bcbab654c38dae98
ACR-0df120a218ce423abb83e56f5d7487c0
ACR-82551b605f0e4a6893df3c4d6083e7bc
ACR-4b27b4877cad402e87782b451257459f
ACR-5a99970379d549d7b36d75db0ca6f2b3
ACR-306aeda701af4f63924dfb1e0809cab9
ACR-6cb5d3a3eebb41b28a5c5463e026e20a
ACR-e8445cb74b5a4d95ae7836a0c129405b
ACR-ab0fe7e9d79243958d8197275d3ee362
ACR-0e36878c825943de969103145935bb8c
ACR-6fe62070b5714d88afafec85bce29930
ACR-8b6db7984d5148a48ed8415bacd4762e
ACR-e0beb8c351fc41488f49a62a6902379c
ACR-3883901f05ee4befb3d93f3e6b5a7171
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.plugins.ServerPlugin;

public class PluginsSynchronizer {
  public static final Version CUSTOM_SECRETS_MIN_SQ_VERSION = Version.create("10.4");
  public static final Version ENTERPRISE_IAC_MIN_SQ_VERSION = Version.create("2025.1");
  public static final Version ENTERPRISE_GO_MIN_SQ_VERSION = Version.create("2025.2");
  public static final String CSHARP_ENTERPRISE_PLUGIN_ID = "csharpenterprise";
  public static final String CSHARP_OSS_PLUGIN_ID = "csharp";
  public static final String VBNET_ENTERPRISE_PLUGIN_ID = "vbnetenterprise";
  public static final String VBNET_OSS_PLUGIN_ID = "vbnet";
  private static final String GO_ENTERPRISE_PLUGIN_ID = "goenterprise";
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Set<String> sonarSourceDisabledPluginKeys;
  private final Set<String> notSonarLintSupportedPluginsToSynchronize = new HashSet<>();
  private final ConnectionStorage storage;
  private Set<String> embeddedPluginKeys;

  public PluginsSynchronizer(Set<SonarLanguage> enabledLanguages, ConnectionStorage storage, Set<String> embeddedPluginKeys) {
    this.sonarSourceDisabledPluginKeys = getSonarSourceDisabledPluginKeys(enabledLanguages);
    if (enabledLanguages.contains(SonarLanguage.GO)) {
      //ACR-8edbc9d6f4184947a48984a0c98017c2
      this.notSonarLintSupportedPluginsToSynchronize.add(GO_ENTERPRISE_PLUGIN_ID);
    }
    if (enabledLanguages.contains(SonarLanguage.CS)) {
      //ACR-e55855711ae0474caac8ed65fa50d7a1
      this.notSonarLintSupportedPluginsToSynchronize.add(CSHARP_ENTERPRISE_PLUGIN_ID);
      //ACR-13d8c686dbdd41cd93d86108a7bb152c
      this.notSonarLintSupportedPluginsToSynchronize.add(CSHARP_OSS_PLUGIN_ID);
    }
    if (enabledLanguages.contains(SonarLanguage.VBNET)) {
      //ACR-bdb919f072fc4cccb3bb4d3ccf0b9e2b
      this.notSonarLintSupportedPluginsToSynchronize.add(VBNET_ENTERPRISE_PLUGIN_ID);
      //ACR-17f861062ccd4839b61bdc15546bb9d2
      this.notSonarLintSupportedPluginsToSynchronize.add(VBNET_OSS_PLUGIN_ID);
    }
    this.storage = storage;
    this.embeddedPluginKeys = embeddedPluginKeys;
  }

  public PluginSynchronizationSummary synchronize(ServerApi serverApi, Version serverVersion, SonarLintCancelMonitor cancelMonitor) {
    var qwirks = VersionSynchronizationQwirks.forServerAndVersion(serverApi, serverVersion);
    var embeddedPluginKeysCopy = new HashSet<>(embeddedPluginKeys);
    if (qwirks.usesIaCEnterprise) {
      embeddedPluginKeysCopy.remove(SonarLanguage.TERRAFORM.getPluginKey());
      embeddedPluginKeys = embeddedPluginKeysCopy;
    }
    if (qwirks.useSecretsFromServer) {
      embeddedPluginKeysCopy.remove(SonarLanguage.SECRETS.getPluginKey());
      embeddedPluginKeys = embeddedPluginKeysCopy;
    }
    if (qwirks.forceSyncGoEnterprise) {
      embeddedPluginKeysCopy.remove(SonarLanguage.GO.getPluginKey());
      embeddedPluginKeys = embeddedPluginKeysCopy;
    }

    var storedPluginsByKey = storage.plugins().getStoredPluginsByKey();
    var serverPlugins = serverApi.plugins().getInstalled(cancelMonitor);
    var downloadSkipReasonByServerPlugin = serverPlugins.stream()
      .collect(Collectors.toMap(Function.identity(), plugin -> determineIfShouldSkipDownload(plugin, storedPluginsByKey)));

    var pluginsToDownload = downloadSkipReasonByServerPlugin.entrySet().stream()
      .filter(entry -> entry.getValue().isEmpty())
      .map(Map.Entry::getKey)
      .toList();
    var serverPluginsExpectedInStorage = downloadSkipReasonByServerPlugin.entrySet().stream()
      .filter(entry -> entry.getValue().isEmpty() || entry.getValue().get().equals(DownloadSkipReason.UP_TO_DATE))
      .map(Map.Entry::getKey)
      .toList();

    if (pluginsToDownload.isEmpty()) {
      storage.plugins().storeNoPlugins();
      storage.plugins().cleanUpUnknownPlugins(serverPluginsExpectedInStorage);
      return new PluginSynchronizationSummary(false);
    }
    downloadAll(serverApi, pluginsToDownload, cancelMonitor);
    storage.plugins().cleanUpUnknownPlugins(serverPluginsExpectedInStorage);
    return new PluginSynchronizationSummary(true);
  }

  private void downloadAll(ServerApi serverApi, List<ServerPlugin> pluginsToDownload, SonarLintCancelMonitor cancelMonitor) {
    for (ServerPlugin p : pluginsToDownload) {
      downloadPlugin(serverApi, p, cancelMonitor);
    }
  }

  private void downloadPlugin(ServerApi serverApi, ServerPlugin plugin, SonarLintCancelMonitor cancelMonitor) {
    LOG.info("[SYNC] Downloading plugin '{}'", plugin.getFilename());
    serverApi.plugins().getPlugin(plugin.getKey(), pluginBinary -> storage.plugins().store(plugin, pluginBinary), cancelMonitor);
  }

  private Optional<DownloadSkipReason> determineIfShouldSkipDownload(ServerPlugin serverPlugin, Map<String, StoredPlugin> storedPluginsByKey) {
    if (embeddedPluginKeys.contains(serverPlugin.getKey())) {
      LOG.debug("[SYNC] Code analyzer '{}' is embedded in SonarLint. Skip downloading it.", serverPlugin.getKey());
      return Optional.of(DownloadSkipReason.EMBEDDED);
    }
    if (upToDate(serverPlugin, storedPluginsByKey)) {
      LOG.debug("[SYNC] Code analyzer '{}' is up-to-date. Skip downloading it.", serverPlugin.getKey());
      return Optional.of(DownloadSkipReason.UP_TO_DATE);
    }
    if (!serverPlugin.isSonarLintSupported() &&
      !notSonarLintSupportedPluginsToSynchronize.contains(serverPlugin.getKey())) {
      LOG.debug("[SYNC] Code analyzer '{}' does not support SonarLint. Skip downloading it.", serverPlugin.getKey());
      return Optional.of(DownloadSkipReason.NOT_SONARLINT_SUPPORTED);
    }
    if (sonarSourceDisabledPluginKeys.contains(serverPlugin.getKey())) {
      LOG.debug("[SYNC] Code analyzer '{}' is disabled in SonarLint (language not enabled). Skip downloading it.", serverPlugin.getKey());
      return Optional.of(DownloadSkipReason.LANGUAGE_NOT_ENABLED);
    }
    return Optional.empty();
  }

  private static boolean upToDate(ServerPlugin serverPlugin, Map<String, StoredPlugin> storedPluginsByKey) {
    return storedPluginsByKey.containsKey(serverPlugin.getKey())
      && storedPluginsByKey.get(serverPlugin.getKey()).hasSameHash(serverPlugin);
  }

  private static final String OLD_SONARTS_PLUGIN_KEY = "typescript";

  private static Set<String> getSonarSourceDisabledPluginKeys(Set<SonarLanguage> enabledLanguages) {
    var languagesByPluginKey = Arrays.stream(SonarLanguage.values()).collect(Collectors.groupingBy(SonarLanguage::getPluginKey));
    var disabledPluginKeys = languagesByPluginKey.entrySet().stream()
      .filter(e -> Collections.disjoint(enabledLanguages, e.getValue()))
      .map(Map.Entry::getKey)
      .collect(Collectors.toSet());
    if (!enabledLanguages.contains(SonarLanguage.TS)) {
      //ACR-164b12f510c545e3b6adf976ee47a771
      disabledPluginKeys.add(OLD_SONARTS_PLUGIN_KEY);
    }
    return disabledPluginKeys;
  }

  private enum DownloadSkipReason {
    EMBEDDED, UP_TO_DATE, NOT_SONARLINT_SUPPORTED, LANGUAGE_NOT_ENABLED
  }

  private record VersionSynchronizationQwirks(boolean useSecretsFromServer, boolean usesIaCEnterprise, boolean  forceSyncGoEnterprise) {
    private static VersionSynchronizationQwirks forServerAndVersion(ServerApi serverApi, Version version) {
      return new VersionSynchronizationQwirks(
        //ACR-4feb3ca41d3f416e937e55b0bf2e9476
        //ACR-cb0312ea00574acfa051e50532698f7b
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(CUSTOM_SECRETS_MIN_SQ_VERSION),
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(ENTERPRISE_IAC_MIN_SQ_VERSION),
        //ACR-598da5207f4849aa97de8e97ab73446a
        //ACR-148c092b13e4475285f59e7a43a50fee
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(ENTERPRISE_GO_MIN_SQ_VERSION)
      );
    }
  }
}
