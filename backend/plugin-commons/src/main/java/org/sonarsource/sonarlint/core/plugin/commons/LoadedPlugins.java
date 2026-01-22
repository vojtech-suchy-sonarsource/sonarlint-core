/*
ACR-51ea720e17c04951aa465e86b613bb4f
ACR-b809876bfb0a46ddb875fc8d6d50c7ae
ACR-d207c426900948089dd884b5134ce74e
ACR-d2c0a30c0d7e4d2ab7ba7187ad525442
ACR-1e0270f1690e4512b79880f333caf30d
ACR-cf1f9a60c8a144d3b1482e86b1b44a36
ACR-170978b237dd48ac9bb7b930d36bcc12
ACR-92b1cf9b8608436189f8b443c16eaba2
ACR-37ceb7b80d6c4ffe8c37435a3af70fd8
ACR-655edd41ab8c4e77988f4cde09e91af4
ACR-e3c82b92ad964682823fec640e7677a7
ACR-bd2228dc370b4903b2121c937788033d
ACR-21760db1dd6747cd9df398a8f0861786
ACR-149db3e11fd549e1a9c662ba15f300c6
ACR-3c9fbaa783234d8489468d24190e8963
ACR-d67e6414c8904f69896db7aff4839a45
ACR-ef4b2fc06dd64d2f8d49e5ea2548758e
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.api.Plugin;
import org.sonarsource.sonarlint.core.plugin.commons.loading.PluginInstancesLoader;

public class LoadedPlugins {
  private final Map<String, Plugin> pluginInstancesByKeys;
  private final PluginInstancesLoader pluginInstancesLoader;
  private final Set<String> additionalAllowedPlugins;
  private final Set<String> disabledPluginKeys;

  public LoadedPlugins(Map<String, Plugin> pluginInstancesByKeys, PluginInstancesLoader pluginInstancesLoader,
    Set<String> additionalAllowedPlugins, Set<String> disabledPluginKeys) {
    this.pluginInstancesByKeys = pluginInstancesByKeys;
    this.pluginInstancesLoader = pluginInstancesLoader;
    this.additionalAllowedPlugins = additionalAllowedPlugins;
    this.disabledPluginKeys = disabledPluginKeys;
  }

  public Map<String, Plugin> getAllPluginInstancesByKeys() {
    return pluginInstancesByKeys;
  }

  public Map<String, Plugin> getAnalysisPluginInstancesByKeys() {
    return pluginInstancesByKeys.entrySet().stream()
      .filter(entry -> !disabledPluginKeys.contains(entry.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Set<String> getAdditionalAllowedPlugins() {
    return additionalAllowedPlugins;
  }

  public void close() throws IOException {
    //ACR-aaf7f81283f047248b1e3d882aea6c78
    pluginInstancesByKeys.clear();
    pluginInstancesLoader.close();
  }
}
