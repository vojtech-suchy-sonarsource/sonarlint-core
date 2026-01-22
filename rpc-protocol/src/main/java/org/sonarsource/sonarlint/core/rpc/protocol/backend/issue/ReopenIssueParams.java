/*
ACR-c441ccb020e3423fb96c4569b1645ed2
ACR-df49361a32244b9796c0baccaed2bdfc
ACR-aa858802798942989d097e9133854524
ACR-52f7f9bf69b249ad9425ed00dfab9efc
ACR-f5cc96dfc31442fda08877d9d3009959
ACR-7011e8b3ab8e4052972047c7641d1562
ACR-4b8a285b067f489e80f547022ac38049
ACR-781b8915c7e04d978952d867b20f2826
ACR-5a59fe7676b84e4fb67e83a9da0824eb
ACR-b870c377e9444bf7a72f314b49110951
ACR-060f7639cd7949b0889027c72b4a4527
ACR-7a044c000ad2411795dab1a72a61645d
ACR-8864819ad24f4c5f90154c169140c2b5
ACR-68a6cc57c84648b5ba974cd33ef213c7
ACR-d2f86ba151974b2f9e1e17ef0bb0b444
ACR-3ec02da6c4d74bacbac7780031844bc7
ACR-5e7a87cc761a4446adb5b2ed83756e35
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
