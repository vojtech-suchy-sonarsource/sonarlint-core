/*
ACR-73b7b0b580d146499d2f1b88958808c1
ACR-8ded6e106474491d9a9fd57e86cc91f6
ACR-dcbb7470f0a84a6dbb3bece1c24fec37
ACR-7cab903a4edb437091c55b7b049e1397
ACR-ea5e8ff6a2364f54b19cf8175f8688dd
ACR-30f3dd2c0445432ebf9d9c24082a6b87
ACR-0eb932b603fb4215ab465d6a4707c2c8
ACR-0a691fd8a809496a81f7cded3748fd2e
ACR-32e0a491fd5e42b29aa8a4e74a7faf62
ACR-fa5977d0f6ca4b4e99215b52b66e4570
ACR-b298354b86444903913e05fd42b906ec
ACR-684c48880513470c8effb745ce72935f
ACR-27a9b6cb8bd34850a8c98d38c590e143
ACR-683c401459e64898bfaed3da2ce32ea6
ACR-3e6b39bea3b748d5aef74c196897b0f2
ACR-63d7d0072f7e4af09de665c82fb4264f
ACR-4f2b52c39a144744b0b480dec51addef
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import static java.util.Objects.requireNonNull;

public class GetStandaloneRuleDescriptionParams {

  private final String ruleKey;

  public GetStandaloneRuleDescriptionParams(String ruleKey) {
    this.ruleKey = requireNonNull(ruleKey);
  }

  public String getRuleKey() {
    return ruleKey;
  }
}
