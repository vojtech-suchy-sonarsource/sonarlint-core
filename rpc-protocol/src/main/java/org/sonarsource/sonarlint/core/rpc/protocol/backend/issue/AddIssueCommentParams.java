/*
ACR-26dcd90c3b124306b85d3b9b1910dcd3
ACR-32fef07a3b324edd93603b1f38616638
ACR-1cdb292c65ea4f9cb684bd4b98ce798e
ACR-818aa7b0c5904576a54482dbf3d52130
ACR-704012f62b9c4a10a5809ab0ba907920
ACR-8333f9b72cc642a1b78ccb24220f542b
ACR-9b67079a1e34410fbb1898741cc33add
ACR-e797ee1074e5476db16cc2a81c39a6c7
ACR-16ba311fff404b0f9e2ac02b8fc6f236
ACR-643d7d1edda04ecfb3afefedf4105305
ACR-a9600381288e4889954fd451c38112a0
ACR-e045e7f890fb4b28b5f6b283f59617e5
ACR-8a8a8c2fe79d45feab98c30044b19faf
ACR-cf2d6b6cbad442189e310b2726796d8a
ACR-95800ede61cb40a3b5b53a1475b390fc
ACR-7e96b5d33763447dace094a1fb3e04a1
ACR-87683be45fe548c0852f9ad0371aa06b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class AddIssueCommentParams {

  private final String configurationScopeId;
  private final String issueKey;
  private final String text;

  public AddIssueCommentParams(String configurationScopeId, String issueKey, String text) {
    this.configurationScopeId = configurationScopeId;
    this.issueKey = issueKey;
    this.text = text;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getIssueKey() {
    return issueKey;
  }

  public String getText() {
    return text;
  }
}
