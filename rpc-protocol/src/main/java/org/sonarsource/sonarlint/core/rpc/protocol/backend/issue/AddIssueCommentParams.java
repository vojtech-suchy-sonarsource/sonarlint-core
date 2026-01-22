/*
ACR-b7f20389ee7d4724bfd9211581876a9c
ACR-ab4f35c979f84b7fa2bded9b2129238b
ACR-03593d024d144888be20f96b5ab77476
ACR-b116f8bdebfa4af19d195f21d2e0020b
ACR-abf73f6a0a5f4fc4bbb754800809fdb5
ACR-16b63ac713764b31bfdaf4d5a052f0e9
ACR-fd40f1fbb72749b6a850f5ec4da651f9
ACR-6f3eab5f941c4d059bbf6dbc62bc46f1
ACR-5b4e5196add149278096f0f999706471
ACR-1b4fd36a4e8d4644a5d495d90199c61e
ACR-5e01fc217e9045a49d20782026b848fe
ACR-64579e618a194ce682565b328ee4c0af
ACR-1db2a8f45dac4816a42a1d76374abfd8
ACR-25863c3262d3414ea0da6adc848198a9
ACR-2195e24455d3475ab55f082394815a41
ACR-aff32f8c007a4ba6930f3853bcbb3d31
ACR-b4c62f78c9be4b70add1b8b501672508
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
