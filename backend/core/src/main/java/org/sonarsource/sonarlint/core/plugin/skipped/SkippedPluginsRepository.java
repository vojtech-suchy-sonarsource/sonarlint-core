/*
ACR-57d0536092134dca920445e11b093e1a
ACR-dd600fa2f3a54087bbcd610d23b7705a
ACR-13045cd02e80494198771a380994fc65
ACR-b5ed5f65ef474b37bb1ae180d301f4aa
ACR-dd7bde4aca214b8fa4e642b4b99fd7e4
ACR-3e9faa82841d41a39f49236320408c77
ACR-ce69dc4b753c47e0939de010fbcf726c
ACR-81b71b60d0434795bcee7b184ed4c1d0
ACR-d3f600acb5954d1cb1f6a7fbde7f2d88
ACR-49b6f15b74754da88162f8e959f73718
ACR-f9562b2fba514ca3ae60db6b5629eeef
ACR-08ddd57ee11e4db9a7a1bb2be77fc7e2
ACR-bd389b5626d04f9b8b29bfe37b27a2c9
ACR-3ad9ca9be12e4c0b837ec3b868184d13
ACR-c2f302c962fd4a1a8fe5ee1f7326f3f1
ACR-bfa93d9a5fa2449e9a3e82a90648b047
ACR-9fb85f5a63344dd98b28db9c7090b3ad
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
