/*
ACR-dacf813fc43749168813275e8b347c09
ACR-df5eb1cae7d34558bd9fe0eb87e29425
ACR-4122a1122a3941ea9d117c742fb7bb37
ACR-e280e709a8ed4b61b20ca58edb413356
ACR-2f17eaefdda64149bf67bb9678a57632
ACR-aa562940c1d7451db2dfe2c86193995d
ACR-7df6cfb17a22489e8b0d0e65f0d12e08
ACR-3af95a8840124e0d84c2612a593206cf
ACR-25fce6c7e31f4adba2a32a0260923812
ACR-d6dbbe06536b4df08eb67d73bfe7d509
ACR-ff166d75ab90480b9bc91074810338f9
ACR-a165c37da9d4480f87f944dad4021fbc
ACR-5e1488d4cc17415787d9ddb11a599029
ACR-824dfe8d4297444ea6fc0ce85c3dadd7
ACR-51525420e28c4e328b47150fb6662de2
ACR-11c57fdebee94f3992f60ca5674c08af
ACR-ab8917dafafb42ba8f5af5dce7a9adc0
 */
package org.sonarsource.sonarlint.core.plugin.skipped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

public class SkippedPluginsRepository {
  private List<SkippedPlugin> skippedEmbeddedPlugins;
  private final Map<String, List<SkippedPlugin>> skippedPluginsByConnectionId = new HashMap<>();

  public void setSkippedEmbeddedPlugins(List<SkippedPlugin> skippedPlugins) {
    this.skippedEmbeddedPlugins = skippedPlugins;
  }

  @CheckForNull
  public List<SkippedPlugin> getSkippedEmbeddedPlugins() {
    return skippedEmbeddedPlugins;
  }

  public List<SkippedPlugin> getSkippedPlugins(String connectionId) {
    return skippedPluginsByConnectionId.get(connectionId);
  }

  public void setSkippedPlugins(String connectionId, List<SkippedPlugin> skippedPlugins) {
    skippedPluginsByConnectionId.put(connectionId, skippedPlugins);
  }
}
