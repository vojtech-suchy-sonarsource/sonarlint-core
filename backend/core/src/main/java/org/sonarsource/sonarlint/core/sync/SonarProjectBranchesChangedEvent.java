/*
ACR-bda6f6c9cd274060b07c9aaf18626363
ACR-f0b85d3212f5407c9ab13e70185446dc
ACR-73a789d1b76d48c1a9748cf5098c2e99
ACR-c1d84e6e4d6d4737adf1ff64d5b7284d
ACR-216fc385699a431788bb75762ecc56da
ACR-a9dad11bec644d7784b30c91f8a80e1b
ACR-b3ea0470de114cc488dd394eb7a5b155
ACR-6aaa546c9d004ba09870913da70c3e86
ACR-ce9468a8080e442a8b1487614d151ffb
ACR-e0bb44d14da1458eb29c0b22a36caa2f
ACR-7711f89ee4264d38beae4b62a2ef253a
ACR-53000ca2afc74a9b9187ce2e9e0a7c64
ACR-1e5f0b002ccf4b6eb3c8f622b86f5ce5
ACR-85d80ace1c1a47c2b3866a476d4ab4a7
ACR-7a771733e44c43a28551744b53eace71
ACR-97c8f5bce899401fba39f2f099389a25
ACR-d6b9cd361e37402abbb224b697324208
 */
package org.sonarsource.sonarlint.core.sync;

public class SonarProjectBranchesChangedEvent {
  private final String connectionId;
  private final String sonarProjectKey;

  public SonarProjectBranchesChangedEvent(String connectionId, String sonarProjectKey) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }
}
