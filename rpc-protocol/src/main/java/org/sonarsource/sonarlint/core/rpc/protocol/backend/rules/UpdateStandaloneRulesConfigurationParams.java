/*
ACR-14a639525bae4032b3e5dcb4957b1442
ACR-514e74a288f44daaa4b468c5b30dac04
ACR-733052f0eb83471a92e5736960689c47
ACR-0f82547354c84c07a0bba3b534bffb32
ACR-6d6cedd3a17347ba9e2ce62c44b03ab9
ACR-e9a91f6e146240dcbdf183a75dfaa494
ACR-b1b2747160004167b4babc254722d42e
ACR-730188ccfdd64f26b0ea8ac8db8f78a4
ACR-df6dbe29fb5a4312810463aaf2d21cb9
ACR-40897e176cb34c9a95940a6e0040ae6d
ACR-ae4f9ab59c224084a5df04a54d387e08
ACR-e740007b442b49e1b9b2bd910ef68087
ACR-b2e548ff896343e3a96cccb1b10e738a
ACR-f549fa1db9de4c1ca9797b2c281d6a32
ACR-af4ad8c2d3be4dd3b9ebbdefbc982491
ACR-d1be9371359d4a059206b2586f250ef2
ACR-91db5acdf9e94c779250ac2d05d663df
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
