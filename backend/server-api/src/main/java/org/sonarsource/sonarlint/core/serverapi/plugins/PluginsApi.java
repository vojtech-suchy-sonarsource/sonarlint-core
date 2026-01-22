/*
ACR-d7ec9396a2d848c794bb077fb24f4d89
ACR-b0e048a5c56d4bbdbbeb60baf3e08afa
ACR-be8dad5f65284667842f2039e60c2c56
ACR-8c321c889ff24ae6b79195465d702b84
ACR-4bf594248ca84aeeb601d1d68c377436
ACR-e11833828dd449079032f158e34d88dd
ACR-08c486161d784b0e852df838979ea03e
ACR-05b4a592e34e4867a3f1b5dd7509fab3
ACR-214a36b25d90466180a0820bdde0e24f
ACR-ca1da31c6c6548109b41af782cf2e03d
ACR-13f5e974785441368dbcf328471dff18
ACR-98c91d242a774cbb9f50b3431123d34f
ACR-966a8742a05a4bb9a57e88b9867bd259
ACR-0945142506674214ba3346c5d5543810
ACR-a44920674a034e8088b7330ede5bef62
ACR-5cc1842d3df345eb8354272e80eabc25
ACR-9c1f27342fab45ffb153fbba6a37c85a
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
