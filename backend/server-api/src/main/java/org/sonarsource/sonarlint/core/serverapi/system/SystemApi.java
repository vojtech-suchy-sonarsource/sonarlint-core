/*
ACR-4e7c40c7dd3e4b049cb8a226e9e5c4c4
ACR-0f359efae85b46638f9046574a08883d
ACR-dc84a43d2f1d40be921486a8d7d521d8
ACR-fd3944b09f5141adb54ab68bb3bc04ac
ACR-4c0e85f302844fc78f1d44f513567d64
ACR-0098051badee45d387ecd0641ccf1e91
ACR-19cd144f6ad14dfb8f23dd7c728c994e
ACR-67599eba4cdc4afe9048fb75c9a71ba8
ACR-c97c9a3795084d2aaf3f5f4b1a2e6ba8
ACR-df00d0070a3b472ba5fa994875e38240
ACR-11f56adc4aed4231b42c0cc706492f66
ACR-ad90976a43dc4ef58ba453b34746703f
ACR-b06d9233c4fe426694e268689123d025
ACR-ca5b966f0eb6477c94ac99559c38705d
ACR-89731ebe5d5b48e8a44c4699d34a5045
ACR-7981e5bc2e514ffbbcbf8ed2c533b95a
ACR-6d3d8e6ca3b24c8b9d77b52e8f03e37a
 */
package org.sonarsource.sonarlint.core.serverapi.system;

import com.google.gson.Gson;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

public class SystemApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public SystemApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public ServerStatusInfo getStatus(SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> helper.getAnonymous("api/system/status", cancelMonitor),
      response -> {
        var responseStr = response.bodyAsString();
        try {
          var status = new Gson().fromJson(responseStr, SystemStatus.class);
          return new ServerStatusInfo(status.id, status.status, status.version);
        } catch (Exception e) {
          throw new IllegalStateException("Unable to parse server infos from: " + responseStr, e);
        }
      },
      duration -> LOG.debug("Downloaded server infos in {}ms", duration));
  }

  private static class SystemStatus {
    String id;
    String version;
    String status;
  }
}
