/*
ACR-5579ed20219b4c88bcf34532c3574f55
ACR-41e1ba89bcf848ba932429775d73563c
ACR-9ba8b9b44f0441929e6ac14d4547030b
ACR-0fe8161151a94e2c8eb7f57363597766
ACR-1f3ae5162a8f43eb93ec635a2e29c50b
ACR-cc4f3f84c3c644f4834ad514284cc2d1
ACR-4ea05c9f172043a188015d9bdc2e047e
ACR-ed424c3cc66941de979b5fd74e821369
ACR-1ad41c228bb14cb0b99ae3e283da8912
ACR-5c94b31e70a14df0b12150aa5ec25643
ACR-288ba0f24cd846ffb133ba0192cd379d
ACR-7962e31682b944ee883fda3c175bfebf
ACR-075d8c2afc07494eb7fa274c75bfbb98
ACR-bd381215a89e453cbf1c2126fc9ae27f
ACR-00dd876cfdb54792a71c489c298a7705
ACR-57e76cbaa1ed4df5a10ba06de2a258ca
ACR-12e025ef88b4482c84e83ce42f3f3fc8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class AddQuickFixAppliedForRuleParams {
  private final String ruleKey;

  public AddQuickFixAppliedForRuleParams(String ruleKey) {
    this.ruleKey = ruleKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }
}
