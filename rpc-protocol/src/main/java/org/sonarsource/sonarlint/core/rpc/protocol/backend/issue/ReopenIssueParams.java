/*
ACR-95bdb55e6c05488da660aa8c3e771b1f
ACR-5c9212f5ddbc4dd696ec4374596eca88
ACR-87552f09f2354ac283403f401db528c8
ACR-ae8cb077347646a19fa0e8e3baa531ee
ACR-5cbae6e98d6b4dc5b790bb1a631c1b06
ACR-6493a70bfa424ccc972a5a2c5f272d98
ACR-f1a4d77d64744b62b67f963e387b3c2b
ACR-a36d4ecd052348c8af27a7f99b32f073
ACR-b25d6e6aa7bd4ea089c0fff87d424003
ACR-8e9c7a5daf3646eda801b477e55391fe
ACR-4d5f1f414005409a9894f2a52c9993ab
ACR-0be2b328bf9448de9f2aa60364125aa7
ACR-9c8b3ad8d26e4c3a96ca9372d53d8d06
ACR-beb6565f8cf444649661568adee89aaf
ACR-9d856a2125374bfda3a3c5dbdb644ed5
ACR-1cba895a65ae4e068b3e8db6412af230
ACR-35fe32394606415483b95cdb480c0420
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ReopenIssueParams {

  private final String configurationScopeId;
  private final String issueId;
  private final boolean isTaintIssue;

  public ReopenIssueParams(String configurationScopeId, String issueId, boolean isTaintIssue) {
    this.configurationScopeId = configurationScopeId;
    this.issueId = issueId;
    this.isTaintIssue = isTaintIssue;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getIssueId() {
    return issueId;
  }

  public boolean isTaintIssue() {
    return isTaintIssue;
  }

}
