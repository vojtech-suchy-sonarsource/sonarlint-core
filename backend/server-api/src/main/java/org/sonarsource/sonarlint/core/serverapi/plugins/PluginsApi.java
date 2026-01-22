/*
ACR-294013b66f1b44119a20c2bdcd5bb8d8
ACR-2ff088f95b7848a592508aa8aec1445b
ACR-ad8a5c364e0d4e7385cd0191ef9bc4b7
ACR-fdb30cf9fe9541b1b5f2ea0ab14cd04a
ACR-6b2a126e857f42529f0ba55d1154f99a
ACR-cfe303d8413a4cdeb26400cd4ad6995c
ACR-d1faa5694f2242df82f6c8787556458d
ACR-251e058e14c24009bdb2ff212ea665ca
ACR-0879842f9af24e1ca569f3a153e1deca
ACR-608c915d6c664e1494905facf3e757df
ACR-c55e596b8008436a879e978312402896
ACR-cb879538e825455a95b68006d57a4453
ACR-75b6ea1a92f2469c9b10d7a809ec2137
ACR-68029171f79d454f84b25b1b81c8a995
ACR-edac63d65e1c45ff91101980d8e07d0b
ACR-53f0a721ba31438a8b781a878d25919d
ACR-b8777a15922249838b2904d87b849b78
 */
package org.sonarsource.sonarlint.core.serverapi.plugins;

import com.google.gson.Gson;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

public class PluginsApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public PluginsApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<ServerPlugin> getInstalled(SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> get("/api/plugins/installed", cancelMonitor),
      response -> {
        var plugins = new Gson().fromJson(response.bodyAsString(), InstalledPluginsPayload.class);
        return Arrays.stream(plugins.plugins).map(PluginsApi::toInstalledPlugin).toList();
      },
      duration -> LOG.info("Downloaded plugin list in {}ms", duration));
  }

  private static ServerPlugin toInstalledPlugin(InstalledPluginPayload payload) {
    return new ServerPlugin(payload.key, payload.hash, payload.filename, payload.sonarLintSupported);
  }

  public void getPlugin(String key, ServerApiHelper.IOConsumer<InputStream> pluginFileConsumer, SonarLintCancelMonitor cancelMonitor) {
    var url = "api/plugins/download?plugin=" + key;
    ServerApiHelper.consumeTimed(
      () -> get(url, cancelMonitor),
      response -> pluginFileConsumer.accept(response.bodyAsStream()),
      duration -> LOG.info("Downloaded '{}' in {}ms", key, duration));
  }

  private HttpClient.Response get(String path, SonarLintCancelMonitor cancelMonitor) {
    return helper.isSonarCloud() ? helper.getAnonymous(path, cancelMonitor) : helper.get(path, cancelMonitor);
  }

  private static class InstalledPluginsPayload {
    InstalledPluginPayload[] plugins;
  }

  static class InstalledPluginPayload {
    String key;
    String hash;
    String filename;
    boolean sonarLintSupported;
  }
}
