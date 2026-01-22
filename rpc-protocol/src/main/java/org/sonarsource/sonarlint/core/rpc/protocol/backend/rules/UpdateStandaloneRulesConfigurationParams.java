/*
ACR-c706146703fb469786e9c042aea6495c
ACR-259309af6ebe40cf9bf6f8b4501905a7
ACR-358557e90104452090c3453bd4d0cd32
ACR-b1fb1c20f5964e658e12ecb700ae4e83
ACR-3a34e251996948d8a40bc2452804e785
ACR-622c7dc58f674049a8eb044431cc533f
ACR-ab0b280161bc42da90f6d5b793ff3260
ACR-2efb1da1ae5f4d3d8a70a3bcf36dd50d
ACR-a2205db88df34f159ddf012256809e70
ACR-f68521f49969417989dc7063328f47ee
ACR-1d6de9dffe8842d0b9c6aacea2b3fff1
ACR-13a3c2b21e0243b69856d84148c70b6c
ACR-6740821fb3df475c908e124942f869cf
ACR-6ca1954ee6d74096ab672d724b40064f
ACR-e3e287eba21243df8a56d7d1c4d1e8ec
ACR-9b3135f492264888af59d4e39e5dde69
ACR-f6959dc1fd754cec862af5bbbf5d6917
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.Map;

public class UpdateStandaloneRulesConfigurationParams {

  private final Map<String, StandaloneRuleConfigDto> ruleConfigByKey;


  public UpdateStandaloneRulesConfigurationParams(Map<String, StandaloneRuleConfigDto> ruleConfigByKey) {
    this.ruleConfigByKey = ruleConfigByKey;
  }

  public Map<String, StandaloneRuleConfigDto> getRuleConfigByKey() {
    return ruleConfigByKey;
  }
}
