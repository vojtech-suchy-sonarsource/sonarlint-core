/*
ACR-16e7629da14a489d84e357510f547702
ACR-f9065f08f2134a478fb3322c66d08c80
ACR-18e4f312d94940829a1f92e671779811
ACR-ad7c9b86ea5e46afb39527cf4c93d4fe
ACR-1f84ade8f2df4b07965d35d77436e34d
ACR-c3ed0831c84c42d9b8d74d67b6aa1459
ACR-ab1a6683890240259cb6148ca7271a1a
ACR-a7f21402498f45f591aa701a8ea8fcc2
ACR-e8a5e247a5d74cadb8c1583d57e6bb45
ACR-937f1a7010574d329691a26b7bd62e9d
ACR-e300fe677b9348e6b10f8696df49416c
ACR-bbaab8cf34284e238a18247f46edceb3
ACR-82799f1c50cd4ab98fe8d79a0a61bf25
ACR-31b9379a0367497d919d33a9c47c219e
ACR-6ef6b61dd5884a41be07823f8f3a0a56
ACR-916ef74534274360980d71e76a7a7111
ACR-bdc403af909949c1aceb04945d2fc0de
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
