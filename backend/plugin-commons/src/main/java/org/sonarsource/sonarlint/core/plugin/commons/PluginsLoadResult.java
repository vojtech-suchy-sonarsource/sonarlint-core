/*
ACR-2a1f3653287f4c6999015bd6d62a48c1
ACR-0386c047b2404e11a28d28c7202cec58
ACR-39f126f12eea484f9e5e6e8b637e0a36
ACR-149430244d5b42a09ed9ebf17bd6181c
ACR-4b1f533f4ff3426f961a9a01bc5f7139
ACR-afb9da700daa4ea083e75a49c221ceec
ACR-6eb309ed92b34c1cb289bd633698ea14
ACR-d968d2bdaa6d40249e0a3878c8200408
ACR-04a93877731f4889b74efe2c6a55f5bd
ACR-810e206dbd924eb5b88388ac4e51d0a2
ACR-841dae0130064c60a036eaa43bb88d50
ACR-97506e952ced4b018d2682763f51a0ab
ACR-5335845cea1348e5928bc1878b5cefc2
ACR-220b22f5373c4d23823f9ba3087c2d52
ACR-b083212942814ce094ee940d6163c981
ACR-d5024f69ff8d4c278ec2a8996e5c44b6
ACR-be9edae2df974aeb90a7e7c77e1c1ff2
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
