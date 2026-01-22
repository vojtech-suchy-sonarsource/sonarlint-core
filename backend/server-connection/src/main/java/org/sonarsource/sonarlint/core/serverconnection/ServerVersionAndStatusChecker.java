/*
ACR-95b48e2133974d69994bd8650a7e795e
ACR-4d40d4dc140a450a823ba846da488cdf
ACR-6b78f1d887fc47cca90cb5d77e137c68
ACR-f0ceb21c433e4f94998b3e2b50b22a19
ACR-a17107c2ddc14d03b56423262a22bd4d
ACR-939d01ec799e4ae4a336a41c87f9d881
ACR-dd9b379a142642c5b798f135b2311e05
ACR-7ffa1d3bda874cbba9f8212e292095ab
ACR-1fca36abac7f4bcd89bad38e018a069c
ACR-bac69ae4f4b84163949862b9f08f978f
ACR-1a91357023d54ae898c2b5ebf8787d78
ACR-70afd1a137a1470a930b3ff6d3e0b13d
ACR-37154e01f2ae460ab5179e0692365f9f
ACR-d6ccd55c91454279b68f6c35364e4121
ACR-852501ce2aea4245acebf877c38bf0ea
ACR-7713897e0dbc4a22b5704c0ea19e7654
ACR-ed042c5a5b7d4bfd991be8719e0594ce
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

  /*ACR-31b9ab7c4d6d4f9db1fd48afd2b4dd6e
ACR-e2e6c717107b46a78e39074a8c37f400
ACR-63c587bb2cdb467a97b986ce6225e11e
ACR-23344a0d9a6b4957a1ec595736b49be9
ACR-2843bf8def8f41f4968af426146717b6
ACR-5d28aaaea76a4f108f87ad7c2607ac6c
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
