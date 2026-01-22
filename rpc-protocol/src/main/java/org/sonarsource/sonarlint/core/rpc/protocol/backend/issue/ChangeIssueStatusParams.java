/*
ACR-b90617f6f5cb4ddfaf46f9e53a3ee56e
ACR-9befc29cad2b491db30742789bece4e7
ACR-abd784b6a44b4d5a9b451f8ea3381510
ACR-e58b115660e94ef59ac4c7fd9142522c
ACR-9f1d3302e1254416818fde2a2bbd57ee
ACR-60dbbb039f734977abf1c644ce489897
ACR-752d2c375b314486bb7311b57d62fe1a
ACR-e4fbc37b060b495bbff638fd546b317f
ACR-6b34cde4bb4f486eb4b4d81a09b7be8c
ACR-66b8a21685744c8e9e3ca64025bc448b
ACR-6bd5dbb3f9cf4bc49b5b203371f641b6
ACR-909cb17825ec4a369dd4d14e029fdc5a
ACR-b097704aaae747d087bdca8d5dd9d99e
ACR-16c0192a9a744de1b38407fb0386394d
ACR-a1fa9c165a90463ab71d8c2612a47c6e
ACR-4fde916b5105402d87a1269e5787d0ad
ACR-4570d9201f5442ce8cf9066c34a4dbe6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ChangeIssueStatusParams {

  private final String configurationScopeId;
  private final String issueKey;
  private final ResolutionStatus newStatus;
  private final boolean isTaintIssue;

  public ChangeIssueStatusParams(String configurationScopeId, String issueKey, ResolutionStatus newStatus,
    boolean isTaintIssue) {
    this.configurationScopeId = configurationScopeId;
    this.issueKey = issueKey;
    this.newStatus = newStatus;
    this.isTaintIssue = isTaintIssue;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getIssueKey() {
    return issueKey;
  }

  public ResolutionStatus getNewStatus() {
    return newStatus;
  }

  public boolean isTaintIssue() {
    return isTaintIssue;
  }
}
