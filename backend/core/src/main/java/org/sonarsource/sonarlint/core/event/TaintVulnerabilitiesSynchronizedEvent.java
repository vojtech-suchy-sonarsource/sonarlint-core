/*
ACR-a58fc0e0042142e99d4cca9cfdc038d5
ACR-a1e1e8e3274f49d2b397c7519f5f6c4c
ACR-b47bdcd63e9c4b50949ca117ff697d21
ACR-9c419ccc3a1b44a29073490c42e3335e
ACR-bc7d9f635f5640658addcc4688c1fddf
ACR-b155af930a1845fdbb9d3d71a77c412d
ACR-c1c036311fea4ed5a57830c723e92f12
ACR-a3085380c9cf46eaab93d8dd32a6a1a6
ACR-3db3175bd5534190a78e42446b0a32ce
ACR-ee014715b15c4d928c9ce7967dc1cdab
ACR-a2f8671eebb94a929f3c651350fdb40d
ACR-40e8daafcd0047eb88b6fdb7bfe73e3a
ACR-f6e8b6c3b8564d01a39bcbd12c839433
ACR-48507d6b56844382b686a9ba8e6c162b
ACR-d47164f5a78c44de95bfd19e9b5cd1a0
ACR-35a15692d1224c4da6784785f0be30e2
ACR-d1c1da660f82447a87740dea15175a1f
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
