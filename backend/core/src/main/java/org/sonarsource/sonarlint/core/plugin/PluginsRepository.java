/*
ACR-2c6d5d397bec4cecacfc3ea131666984
ACR-8f9d56e1a08445f88ede6f9eb1732e0e
ACR-8103daa9e032433e86215e9295d4bb33
ACR-5f85d8e99e49485fbaa3b7fabdbd3ab1
ACR-1374e2b086a54b4ba053f6996480c33e
ACR-14c639c3fa1f479b945a9dd845672181
ACR-5c3fe2f1b0734ac29429a1dd0fc39901
ACR-39139658fd5b4482bfc8eedc3fc5c898
ACR-457ab33f1faa4d2ea0d20e339211d556
ACR-c267ed98e8244f2c8cf6f41578a0cd6e
ACR-2d0835877f864dd8a0b1cddb3528ecd8
ACR-52e15f4a122c4bd0873b3244e4b6fbc1
ACR-a456afb9dc2c46bea07aa6291fb8eec1
ACR-cbfae468d2fc49a9b64a697e7c82b834
ACR-80e11cc80b184aa2b4639a1998071904
ACR-8cce70d838ef4455b4984289a19f66fb
ACR-87d45d160c5a4f46bd2936ed89b27a14
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
