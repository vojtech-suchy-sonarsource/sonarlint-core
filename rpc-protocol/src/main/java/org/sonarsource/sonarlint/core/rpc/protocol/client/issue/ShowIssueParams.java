/*
ACR-58020903eb214c4f8800589d44f1c5e4
ACR-2f290aa873d24950985a441919d85356
ACR-dd63ef7f727f49f793f891206216f36f
ACR-948569646e93404fa5471981b137fe22
ACR-46b46b9b2a294f00a574e34a9bff4ab6
ACR-37bb1ccec2b94402b42b83a5dcfc9459
ACR-20a6c11416454470804fc59a822d6ec5
ACR-5074ad0d6d0142ce95e27dcebcbb6ebf
ACR-746467062de74ff0b95fd21cbb2be636
ACR-444e3aec48734b5da112b4db4d3f8627
ACR-4b439038f99a4029b03566c23e153e5e
ACR-bcac1a8e525b4b9098053ca7622fe248
ACR-b27d1139175642f8a2dd906e1876adfc
ACR-e28bc101cc6d41ddb2a51d3b90ef4386
ACR-fbcdf6f05c064a79abd99b37487efe2b
ACR-b4f3135cf2fd46db9c3574d0e7bce513
ACR-9fa84c1efc3d437582da1f2fe8aacfca
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

public class ShowIssueParams {

  private final String configurationScopeId;
  private final IssueDetailsDto issueDetails;

  public ShowIssueParams(String configurationScopeId, IssueDetailsDto issueDetails) {
    this.configurationScopeId = configurationScopeId;
    this.issueDetails = issueDetails;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public IssueDetailsDto getIssueDetails() {
    return issueDetails;
  }
}
