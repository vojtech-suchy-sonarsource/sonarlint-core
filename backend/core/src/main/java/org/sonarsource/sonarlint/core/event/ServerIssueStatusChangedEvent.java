/*
ACR-2aa7dfc024a2447682726372159f9601
ACR-df72bf5f58354cbb95f02e32ba7ad1d6
ACR-c578c877ec2b463e9081c14c46482c0e
ACR-24ccb4a6cf2b4bd4976d7db6837e819b
ACR-4a41a5b1b64f43428bca536ae6bc5ab1
ACR-208be8c43e4044fa8a6aed3bc0dbefb7
ACR-9617d1711819489d8afa9cb7e68c02f9
ACR-404d2d394c934d7292d0054830c66e07
ACR-fda67baf76614ac1818a2fc1f526d46e
ACR-37f51c45405141bbbd70f62ef5c62889
ACR-2bcbe7e4f0324ebaa7cbcbfddba2e437
ACR-07f60fb5c8f64c9b94157f0e86b225d1
ACR-a683fc96a8d3479099b633f38481b460
ACR-01ecf256b19b45ca8b907875e2c14c58
ACR-870373ea175049abab60a0396dfc19d3
ACR-a2e54c45dcf44151bb6f26fc363bd1ed
ACR-6841621e7fd2494e827d9304779375af
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.serverconnection.issues.ServerFinding;

public class ServerIssueStatusChangedEvent {
  private final String connectionId;
  private final String projectKey;
  private final ServerFinding finding;

  public ServerIssueStatusChangedEvent(String connectionId, String projectKey, ServerFinding finding) {
    this.connectionId = connectionId;
    this.projectKey = projectKey;
    this.finding = finding;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public ServerFinding getFinding() {
    return finding;
  }
}
