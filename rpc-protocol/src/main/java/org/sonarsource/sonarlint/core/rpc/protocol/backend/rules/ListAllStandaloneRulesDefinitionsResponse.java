/*
ACR-6402c7084885462b84cf4281f212530a
ACR-aaafcd05b40645e6a7e53634eb528bd5
ACR-4df644c3d43442a688caf52893a9f3f2
ACR-28bc3df1964e481b8bbc5c404f57eaff
ACR-ea9227b2b2ab46faa2cef95ae2b071a5
ACR-df4f00d4c8c14de3a97f1a192e1f7d9e
ACR-7aca3a6c453e4fbdb2fd99c47088807a
ACR-92175e00c6e945b9beb7bbaf94e9ee21
ACR-34cb15e4f689485ab1268fa21e6312f8
ACR-d15b06d5668c47db8fcdb0e915c8b24a
ACR-7811c2ef3dde4bbe86845789cb41858d
ACR-0bb75554cb174ae182ac22825f78544e
ACR-28069b55953a41f2a34f7fe7273e8cf8
ACR-64d3d874e857464781c721196100f924
ACR-4562f1ba368e4bbabab5edc98d60b448
ACR-f89b418f5f7d4a6a99d2c9813d2e0c41
ACR-07aedc2cc7024f6199cda4fe9347d56b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.Map;

public class ListAllStandaloneRulesDefinitionsResponse {

  private final Map<String, RuleDefinitionDto> rulesByKey;

  public ListAllStandaloneRulesDefinitionsResponse(Map<String, RuleDefinitionDto> rulesByKey) {
    this.rulesByKey = rulesByKey;
  }

  public Map<String, RuleDefinitionDto> getRulesByKey() {
    return rulesByKey;
  }
}
