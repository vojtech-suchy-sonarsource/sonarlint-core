/*
ACR-72b16ac7042247b6b6a902199e59a201
ACR-aa24c5b9ff6343eebca47854239c445a
ACR-8558c521a97c4e1aa15df8c1881704bb
ACR-65e4bc52c2af47339f29b899691f0a03
ACR-f51abcc0d9cd4cd498d634b108e0c44c
ACR-fe6a0f06b02c427b910c3d1efe526d22
ACR-57ceac373412451a9b8ba20e3ec058df
ACR-e8eac7998e7f49dc8633edc7649a3b11
ACR-c3581462cc72468999264acc5dc7a35f
ACR-ff0c3c0d9250481a8258443766fab904
ACR-972c44a2504f4e73ad83386e6256e64b
ACR-1ff3915f296a4d5fa3d7aa6b750d1aa6
ACR-97f7a09e8463484eb2b8caaf6a59ce82
ACR-84b060de6ae447e5a33c4bf784ae511a
ACR-28c5134ad9e6403baa6778c51c824041
ACR-226ee5bd2a5e4c82b59dec5b21325664
ACR-89bcab61429043ec8f5aa853c2d4e909
 */
package org.sonarsource.sonarlint.core.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.plugin.commons.LoadedPlugins;

import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.throwFirstWithOtherSuppressed;
import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.tryAndCollectIOException;

public class PluginsRepository {
  private final AtomicReference<LoadedPlugins> loadedEmbeddedPlugins = new AtomicReference<>();
  private final Map<String, LoadedPlugins> loadedPluginsByConnectionId = new HashMap<>();

  public void setLoadedEmbeddedPlugins(LoadedPlugins loadedEmbeddedPlugins) {
    this.loadedEmbeddedPlugins.set(loadedEmbeddedPlugins);
  }

  @CheckForNull
  public LoadedPlugins getLoadedEmbeddedPlugins() {
    return loadedEmbeddedPlugins.get();
  }

  @CheckForNull
  public LoadedPlugins getLoadedPlugins(String connectionId) {
    return loadedPluginsByConnectionId.get(connectionId);
  }

  public void setLoadedPlugins(String connectionId, LoadedPlugins loadedPlugins) {
    loadedPluginsByConnectionId.put(connectionId, loadedPlugins);
  }

  void unloadAllPlugins() throws IOException {
    Queue<IOException> exceptions = new LinkedList<>();
    var embeddedPlugins = loadedEmbeddedPlugins.get();
    if (embeddedPlugins != null) {
      tryAndCollectIOException(embeddedPlugins::close, exceptions);
      loadedEmbeddedPlugins.set(null);
    }
    synchronized (loadedPluginsByConnectionId) {
      loadedPluginsByConnectionId.values().forEach(l -> tryAndCollectIOException(l::close, exceptions));
      loadedPluginsByConnectionId.clear();
    }
    throwFirstWithOtherSuppressed(exceptions);
  }

  public void unload(String connectionId) {
    var loadedPlugins = loadedPluginsByConnectionId.remove(connectionId);
    if (loadedPlugins != null) {
      try {
        loadedPlugins.close();
      } catch (IOException e) {
        throw new IllegalStateException("Unable to unload plugins", e);
      }
    }
  }
}
