/*
ACR-72a02002666e4b7e968d6812cb4cad84
ACR-806ec71ebc224831b7847f4bc7ddf740
ACR-6f98d5cd61f143e3a1cd956a929ee55f
ACR-466321e27dd746f3832c40883c6abdaa
ACR-8178125f55c74b8c821a3b7859eb811a
ACR-3824f29273ae468c98bb7a002f9350ce
ACR-d9223133bd664626952155640691a138
ACR-3b49e990007348e2bd647f9687d5ba5d
ACR-8edcbc955d1a4d5281b3e075a7d32d57
ACR-1ba517ed275e4aee8a0bfbd4d6b55d92
ACR-e9b29ddab6384e51ab2a3fda8278cd04
ACR-fa70a42073d94209b2145e90d162d80a
ACR-298298ae260e4917a03f1c33cde67974
ACR-78121be44aca4b6c9cdf15ef040f99fc
ACR-7e2cd6fae108462396967248197e5bc4
ACR-0507b058845345f0aa7bb94bef17b414
ACR-7314d399fb9a4490b9ba59618f9f52bf
 */
package org.sonarsource.sonarlint.core.event;

import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;
import org.sonarsource.sonarlint.core.serverconnection.storage.UpdateSummary;

public class TaintVulnerabilitiesSynchronizedEvent {
  private final String connectionId;
  private final String sonarProjectKey;
  private final String sonarBranch;
  private final UpdateSummary<ServerTaintIssue> summary;

  public TaintVulnerabilitiesSynchronizedEvent(String connectionId, String sonarProjectKey, String sonarBranch, UpdateSummary<ServerTaintIssue> summary) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
    this.sonarBranch = sonarBranch;
    this.summary = summary;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }

  public String getSonarBranch() {
    return sonarBranch;
  }

  public UpdateSummary<ServerTaintIssue> getSummary() {
    return summary;
  }
}
