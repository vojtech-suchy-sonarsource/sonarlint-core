/*
ACR-e0cb780baf2a4b3a90a9d998007603f7
ACR-53d01bd9fa1343c3831739dfc4be9a19
ACR-e807dd7028904cd89e98ed876f280eb6
ACR-3b9b20cfbeb24898adafc431fd0640b7
ACR-699fe92b8964494186b210bc6dbfbac5
ACR-317787c8bab04e41bc9df51e3871c868
ACR-59bedd18aaf54924ab44b10aab3b0805
ACR-d4deb417014f4c1391a72dfa33605a43
ACR-ff3c66e5922c4832ad49e0070d5b2390
ACR-b9c4445038ea44fda95ca6c6ab6f5ef7
ACR-8e32999cb4cd4e4db110eaed65864c88
ACR-1e0c60276c6842b9b21dd47de1381527
ACR-5a7c125ff1be4c17872f92cf07ed6951
ACR-f0fd44f071724b56916bc12449ec7de5
ACR-12ab80fe025249c992a36637be3dc3c1
ACR-84ecbc8ded3b4396b1f5ab15a8bc5b46
ACR-2b69ade2f3eb4aa1976068fcb2a333e3
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.exception.UnsupportedServerException;
import org.sonarsource.sonarlint.core.serverapi.system.ServerStatusInfo;
import org.sonarsource.sonarlint.core.serverapi.system.SystemApi;

public class ServerVersionAndStatusChecker {

  private static final String MIN_SQ_VERSION = "9.9";
  private static final String MIN_SQ_VERSION_SUPPORTING_BEARER = "10.4";
  private final SystemApi systemApi;
  private final boolean isSonarCloud;

  public ServerVersionAndStatusChecker(ServerApi serverApi) {
    this.systemApi = serverApi.system();
    this.isSonarCloud = serverApi.isSonarCloud();
  }

  /*ACR-d53dc52bf72e4dbb8b3ce5a8334e3393
ACR-95d76db3357c43a7afb478dce6429e8a
ACR-63e4035c3dfb4b4eb0a7124abef13266
ACR-8481c0fbe2ca4f97a9eaf5c7d5fa2d37
ACR-311dc00c098547459493bd3576e1287b
ACR-ce0cae40925a4628ba8bc7429b7e1020
   */
  public void checkVersionAndStatus(SonarLintCancelMonitor cancelMonitor) {
    var serverStatus = systemApi.getStatus(cancelMonitor);
    if (isSonarCloud) {
      checkServerUp(serverStatus);
    } else {
      checkServerUpAndSupported(serverStatus);
    }
  }

  public boolean isSupportingBearer(ServerStatusInfo serverStatus) {
    if (isSonarCloud) {
      return true;
    } else {
      var serverVersion = Version.create(serverStatus.version());
      return serverVersion.compareToIgnoreQualifier(Version.create(MIN_SQ_VERSION_SUPPORTING_BEARER)) >= 0;
    }
  }

  private static void checkServerUp(ServerStatusInfo serverStatus) {
    if (!serverStatus.isUp()) {
      throw new IllegalStateException(serverNotReady(serverStatus));
    }
  }

  private static void checkServerUpAndSupported(ServerStatusInfo serverStatus) {
    checkServerUp(serverStatus);
    var serverVersion = Version.create(serverStatus.version());
    if (serverVersion.compareToIgnoreQualifier(Version.create(MIN_SQ_VERSION)) < 0) {
      throw new UnsupportedServerException(unsupportedVersion(serverStatus));
    }
  }

  private static String unsupportedVersion(ServerStatusInfo serverStatus) {
    return "Your SonarQube Server instance has version " + serverStatus.version() + ". Version should be greater or equal to " + MIN_SQ_VERSION;
  }

  private static String serverNotReady(ServerStatusInfo serverStatus) {
    return "Server not ready (" + serverStatus.status() + ")";
  }

}
