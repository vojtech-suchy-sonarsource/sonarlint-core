/*
ACR-b93571934aa04866ab33e971d30e8ffa
ACR-b1adc1a0ef0a419aab43a7bd8395d581
ACR-41699a7989bf44d195b76eb8633bbc07
ACR-e4e2a6e53aaf47129e4d7ee5bcc295c0
ACR-3987073303a1417d817543637541b4e3
ACR-3189df88a6734d07b1fc8cb45ca023f3
ACR-12c0d246a62947be8a68133f492b57d8
ACR-52493dfaa5ce4a0b843a681abab18ecd
ACR-4957c188a1e747aaa81555b0e7508bda
ACR-7ecfbfb72e74409dbe2bbb3558db19a8
ACR-3d6d7746a2d845e8b27a4ec82647ae87
ACR-4ce97a9da1454c3fa022975efae7985d
ACR-cbd40dddde81410b86c4bdf94fbcc50f
ACR-c5c572946141415e970f7288ff5fd3a9
ACR-40ef052e9c9c4087976dce0245113648
ACR-0024c673a838434285d54dd39e5253b7
ACR-4a3692eb67bc46978fba53b53d770a4f
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
