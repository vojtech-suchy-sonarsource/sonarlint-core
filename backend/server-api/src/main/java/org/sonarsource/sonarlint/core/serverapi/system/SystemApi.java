/*
ACR-9c6dad2700be475f860fb79458fce13e
ACR-f90f1bda965a4a03b468a694bb64a073
ACR-d4768c1d9d484041bb0fee9bb57508ab
ACR-60d03bac5feb4e2c9e001bb90fcfe63f
ACR-9f4d5b629f874f7a8b2732b6c7514e5f
ACR-8accd2f59b2f452ea99f0a6df80c7d48
ACR-0beeeca4d18b42ef8959cdac2d797278
ACR-29e6797cf0b8492484b88e307b1a4cca
ACR-e5cecec847234acea6772e9f8b48baf8
ACR-07b082c7d9774378925a76e7db756ac2
ACR-db3d1b018d484e4092dd211b69192e51
ACR-cd79f311feae4844a48f3b9d00f33cfd
ACR-55e405b80b8448a09b9de01e10523d2a
ACR-64a4738d85b24034b5db2d71928367a8
ACR-058425d526da47b98f6626c18222a1df
ACR-5d55f3a4f5224b5faf365c72b1e1ede8
ACR-008807684f8b48969dadf4c108558728
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
