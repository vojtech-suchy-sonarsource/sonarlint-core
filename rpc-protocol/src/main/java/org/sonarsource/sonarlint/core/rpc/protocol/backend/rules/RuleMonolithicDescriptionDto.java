/*
ACR-01b0bbaf2a3346c5a864f7391cc77d81
ACR-69aca2a3c39c40909bffe28814ef368f
ACR-ea0f257c584d41749fb31843f717225f
ACR-3e3a15a194d04a2880dd54e48bdda464
ACR-956f1bbcc7de43c0b2e97ffb349e80b2
ACR-08d1b34e07d54d8bbf9d8bc83808ec2a
ACR-ca5c2550ddb34b12adea811faae7009f
ACR-2d4dc7db5c7a4996a9c3826eb2b7d7d0
ACR-b43c560f81c2454997ff9eb53e0107b6
ACR-605d964f650845d396bb2fd5ff0a1743
ACR-488f12697bde4e7986ec39b5d4c72a60
ACR-bf729865190d4952ba50c0cd97c8c15e
ACR-27712e1693ca4c6f8790d7361954aaee
ACR-57e83fccf9104748b720b15aa247020e
ACR-ed42e90d6b06434d825cf56dd3e286f8
ACR-cd98fa33da42422e97256d1260e5ebf1
ACR-6a9ef0bd27c24d2cb19f68d6c6946c4a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class RuleMonolithicDescriptionDto {
  private final String htmlContent;

  public RuleMonolithicDescriptionDto(String htmlContent) {
    this.htmlContent = htmlContent;
  }

  /*ACR-15229871003448b7a17acde1d9a08a70
ACR-63d64c76c2214e7290ba5d85bc05968b
   */
  public String getHtmlContent() {
    return htmlContent;
  }
}
