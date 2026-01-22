/*
ACR-10e6a72f61fe4258b039b86746dc34f1
ACR-600cbdf04bba483c843185e2bfae7918
ACR-a20c8b567a1743e0bdbd1694bc00b1ce
ACR-5fa020833ee14fadb78686e17d406dff
ACR-72a162d22c4249c1a01fcb0034eff2d6
ACR-7c085c05ef0f454fa094103dfbe25b82
ACR-5431968345aa4400bad5956f2761eeb1
ACR-2dc91cfe281741bea0d0e6b347af044b
ACR-68228e8e4cb04cecb355ea4f784b1433
ACR-ccdd4b9574984b3b959efeba373e2b62
ACR-4d16caf0b7494626bde81816c5d78ced
ACR-d8582d3b1db34d6ca66907ffb302726a
ACR-91c268372c944537b9377980f7424ed1
ACR-d5ae1c8422a243ddb324c7f49e58b7e1
ACR-c0982b6377504da7b1e11dfa8b3c5b6f
ACR-1922ae6e56e74f0a98ebf889d924ea3d
ACR-6786a75a88584cabba2c5029c9721c09
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
      //ACR-755c37d07b8c416aa56bcdadd5aa400e
      this.notSonarLintSupportedPluginsToSynchronize.add(GO_ENTERPRISE_PLUGIN_ID);
    }
    if (enabledLanguages.contains(SonarLanguage.CS)) {
      //ACR-8e4e83dbe63c4d8198b1396d410b4b7f
      this.notSonarLintSupportedPluginsToSynchronize.add(CSHARP_ENTERPRISE_PLUGIN_ID);
      //ACR-3da60d1d10b3463ea6718f4517727d1b
      this.notSonarLintSupportedPluginsToSynchronize.add(CSHARP_OSS_PLUGIN_ID);
    }
    if (enabledLanguages.contains(SonarLanguage.VBNET)) {
      //ACR-86444d67a0f042ad8ee08c69c5d46887
      this.notSonarLintSupportedPluginsToSynchronize.add(VBNET_ENTERPRISE_PLUGIN_ID);
      //ACR-900af205f0094ff69ffc2c04f0eee757
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
      //ACR-b444905b06814fc883d29f51d779a01b
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
        //ACR-cfad82d827c844d4818845f2c69d4597
        //ACR-3c78a53c770444f6b6e5726ac3920af1
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(CUSTOM_SECRETS_MIN_SQ_VERSION),
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(ENTERPRISE_IAC_MIN_SQ_VERSION),
        //ACR-c99717c0040544bd9a88ba55cc23ef29
        //ACR-96d06c6155f2477e9363d8c78479e116
        serverApi.isSonarCloud() || version.satisfiesMinRequirement(ENTERPRISE_GO_MIN_SQ_VERSION)
      );
    }
  }
}
