/*
ACR-7311bcda314d42c1b672e3f29892b307
ACR-3f13bfa7b7514232813525723362fd63
ACR-8eab5ac84ffa44d497e4a2d8fba93dff
ACR-72c35b7fcf1e4423b9aea161aec1d58f
ACR-750a19191bd94e178d676c2567eaa6ff
ACR-7df91955750341a7b162ed71ce836830
ACR-fe63da8d290e45f5ad1bb4210be68155
ACR-c93467b7e178487ba005ad598867b0d6
ACR-bff9ca97ab614fdb9de4207f26b26367
ACR-a0c779306eb74bb18463c9cc1ae6a3be
ACR-289b2ac3369a4b129b3dad5224fced9a
ACR-7ef7d515720f441f91bec3bef6f3e42c
ACR-1820e76192874db49ff381b66c39bfb4
ACR-24a6f0538bd14782a5c3efbf201144fc
ACR-16e5d50b27074145991dfabd61d575b1
ACR-580ad6f67ca44611a263e892720d9cb6
ACR-fb7ed2de1cf9467d96fd620b69a67e97
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.Map;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginRequirementsCheckResult;

public class PluginsLoadResult {
  private final LoadedPlugins loadedPlugins;
  private final Map<String, PluginRequirementsCheckResult> pluginCheckResultByKeys;

  PluginsLoadResult(LoadedPlugins loadedPlugins, Map<String, PluginRequirementsCheckResult> pluginCheckResultByKeys) {
    this.loadedPlugins = loadedPlugins;
    this.pluginCheckResultByKeys = pluginCheckResultByKeys;
  }

  public LoadedPlugins getLoadedPlugins() {
    return loadedPlugins;
  }

  public Map<String, PluginRequirementsCheckResult> getPluginCheckResultByKeys() {
    return pluginCheckResultByKeys;
  }
}
