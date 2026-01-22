/*
ACR-7930a8bcda844f3aa51b5804ea392569
ACR-ac4c915f3bc8413989fcd138ab0a96a0
ACR-be510f99867e425195d6246213c36b9f
ACR-3fe81fc445a241d89c0aa0d5c30be197
ACR-8ca8fae66725408d916c4ab3a9f175bb
ACR-a53a56267bc64b678aee53b6254f1582
ACR-47dc9f0e897e466cae8dc975a54942e4
ACR-5840f68521044cfebd4bc74e4ce61d43
ACR-598bec9088fd4bf4a6a1692a24a83a8b
ACR-7d5b401255cb44be91075a49dd8614a0
ACR-28bef637d7bb47c384b054df389249b8
ACR-05ce13e10d6c4488bc6d148336cbe606
ACR-6fba8c09b0184156949ad9a75c0fcb34
ACR-1328ff9626b14e2bb261b50ebec86b5f
ACR-77ceb81c8aed41f68ba8bf6d239aceec
ACR-ed50277935524ea58d506c50c5f95264
ACR-113781785f2e40c79256dd4d0e52275d
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
