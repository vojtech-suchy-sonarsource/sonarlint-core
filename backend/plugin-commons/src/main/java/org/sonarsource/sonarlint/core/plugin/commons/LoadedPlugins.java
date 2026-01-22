/*
ACR-cc45f60250274841a339852a5ee7e29d
ACR-90f4829bcdfc4c66ac9d98cad9c9a411
ACR-69b4db2e05024943ba331d526caaf3f5
ACR-5413d49ba24149cf87f01940ed5dcbf8
ACR-1430638b7ce2420c8db99d2ebc401476
ACR-e14828723f9144ac89d6c2c1db3cb733
ACR-8b7f1007c5774e35ab820747a1b37319
ACR-754479c990c942b0856684ba5e8c3779
ACR-56cc19b046da4f05b7324f3d44cd6c00
ACR-88b715c16c734a5ab4e0296d61060acb
ACR-c5f828518e3244c89902dedab0a8966c
ACR-5bae1910c0eb41f7ba505a2846d9d14d
ACR-9ae2252b22fd4ad3acb5119426c541cc
ACR-78f3157b249e4ef0b73d48250323b6d9
ACR-8cf8d93bfd4c46fb83e30e01e36fa8c1
ACR-e790cb590c064896afaa8a42be11d009
ACR-e7e44606e391470e8c5a2fd2287ac94e
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
    //ACR-329700d39a1f4c6d8bf9444aa8bf0e79
    pluginInstancesByKeys.clear();
    pluginInstancesLoader.close();
  }
}
