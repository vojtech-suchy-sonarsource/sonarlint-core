/*
ACR-16d4a21ffc354866a7ca3d54d5d273ac
ACR-3eddf201eb4140ddaf2162a0f5ba6e23
ACR-6b52b9a4c1754f44bf7500266c2a1c19
ACR-c96a38a519124bba8e49fa896ef29fc3
ACR-3ad7767a45eb4a0288c76741666923ce
ACR-8b9d857932b44116b38d1652dffc4174
ACR-6a29481cd29445bd9a47b1d9ae8cd1cf
ACR-503d66c4d57e4ceb90b3642dc45ec7ac
ACR-a46998f6eb864d81a9c70f331431d1d0
ACR-0898f257ce9d45e0b4360fc8f4a9387f
ACR-7fd08778a94b42c6ac24b1fbf570f8df
ACR-927e77e0e636429ea836fccd486f916d
ACR-5ee541186ab542e3a762ef75c674524f
ACR-046cbbaac2a04f8b91ba0a59137dd0e3
ACR-90f4a9b71996471f9e761ccfe4faede9
ACR-272c21a242bc4702a964400a664b96e8
ACR-6b46358de57d4b70a7d9f619d75e8b89
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class CheckStatusChangePermittedParams {
  private final String connectionId;
  private final String issueKey;

  public CheckStatusChangePermittedParams(String connectionId, String issueKey) {
    this.connectionId = connectionId;
    this.issueKey = issueKey;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getIssueKey() {
    return issueKey;
  }
}
