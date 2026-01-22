/*
ACR-5e9bb434334d46b39eb8ad460db6c46f
ACR-33662cef51324b449ed22150d21b5ae9
ACR-fa19eb1fe10748aaa257996e8fcacb95
ACR-9e256d10386b4ab08b88a65454d03efa
ACR-a8b9a13b839844b897408cccc73881c3
ACR-fb67b6685c874a8a8199ca340a42c185
ACR-60f2e79c0e554cb9b893b1a6b9978618
ACR-7e9f770bee554ba7abab8ad29821afcb
ACR-c70ca5a95c294e27a6e8be8c53466fdf
ACR-94e0f6c47bc84018a65034ba84927dd6
ACR-9fd988893b20450b9b94b7c65d1f6da3
ACR-556fa47703e54c12b9710e1a0b8ef956
ACR-6529e7f1de77421ea20aa02d73d59031
ACR-262134e21adf4c7cb51af55cce2a0ee6
ACR-00f0592aa16049f2b80162f6f05612be
ACR-8ec819c63b5d48e5892587c70c8561a7
ACR-513424a7f63642ba94c5452d8e675add
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
