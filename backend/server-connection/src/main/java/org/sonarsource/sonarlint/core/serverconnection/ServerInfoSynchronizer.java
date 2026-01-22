/*
ACR-61b4547c6fb445ed9ae2c914a878e6c2
ACR-09c8014e703745e989db27fa36624990
ACR-631b8c09b9f4435eb375327f66d85f7c
ACR-ecd0c933b515491a9fffa4b4f18ba86c
ACR-32761670c13741aab565841e2708090e
ACR-2b7f59b951364893bc66033a24543ce0
ACR-59c1ec8ad7c94a4fa85dbcd7c11f519e
ACR-686ff0d5d80c4725935dec6b1fcaafa6
ACR-8daf280fe1bb49f7bc8f72fb0b5c0d26
ACR-53d361e2b0574f32a6d1e0e05793019e
ACR-0eb4d6c5c9234bf69b3d6b79d7d133d5
ACR-2e2c6a8ec1f14fafa397a646ad7fc930
ACR-c00957f0bb8d4ffa8f1c9a2b726f7513
ACR-4e7fc05d33b14cec8cd378ed38a770e9
ACR-6a6f2b1e769a44859e65e56ad14fd304
ACR-1abee843a37e4f8caa6966fbaa0ee33e
ACR-613d6c09a66c434598a974aeaa1b2643
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Set;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;

public class ServerInfoSynchronizer {
  private final ConnectionStorage storage;

  public ServerInfoSynchronizer(ConnectionStorage storage) {
    this.storage = storage;
  }

  public StoredServerInfo readOrSynchronizeServerInfo(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    return storage.serverInfo().read()
      .orElseGet(() -> {
        synchronize(serverApi, cancelMonitor);
        return storage.serverInfo().read().get();
      });
  }

  public void synchronize(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    var serverStatus = serverApi.system().getStatus(cancelMonitor);
    var serverVersionAndStatusChecker = new ServerVersionAndStatusChecker(serverApi);
    serverVersionAndStatusChecker.checkVersionAndStatus(cancelMonitor);
    var globalSettings = serverApi.settings().getGlobalSettings(cancelMonitor);
    var supportedFeatures = serverApi.isSonarCloud() ? getSupportedFeaturesForSonarQubeCloud(serverApi, cancelMonitor) : serverApi.features().list(cancelMonitor);
    storage.serverInfo().store(serverStatus, supportedFeatures, globalSettings);
  }

  private static Set<Feature> getSupportedFeaturesForSonarQubeCloud(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    return serverApi.sca().isScaEnabled(cancelMonitor).enabled() ? Set.of(Feature.SCA) : Set.of();
  }
}
