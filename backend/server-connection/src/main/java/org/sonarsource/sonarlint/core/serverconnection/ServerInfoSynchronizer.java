/*
ACR-0e0d371fd7a24c9097cbf6e35a72df22
ACR-ac78597571b34470b1341ad9620a2352
ACR-cb9919ee836844da94e17d0ef4040813
ACR-18345bd5fa01402f8099761b727fa92d
ACR-2b83f7fec41a4bc8823e3a9bc17160ee
ACR-31483a8c94c2464ca6b274a81097c398
ACR-2eae0bfe2611484f90366af44279ab26
ACR-90ca053f74c043e2a5256c17280ca98f
ACR-8a2b8bae70c248b6824e6cee2f1671b7
ACR-ecff6ab682484f8883d9f9fdcdc4e62a
ACR-646ed50705324d28acd4b4f41f16261b
ACR-83d3990c4c304d349ee95eb2ffe44134
ACR-2ef469c4edd9400981ea072599f3fb25
ACR-9bcbb8ce0cf44420b4e0fd7098593ef8
ACR-f731389ce4764235a175fff72d60800e
ACR-1ccfeb9a76d84915a071b7a4b0776b78
ACR-a1b418be46f64cf9a2b60e824bdfe9a2
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
