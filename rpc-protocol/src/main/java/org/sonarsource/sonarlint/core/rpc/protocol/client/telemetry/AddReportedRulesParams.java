/*
ACR-5d891568297145998854d833f0143755
ACR-5e0d8245f68645ee906a02fb3ae1ed89
ACR-302fd7b368fd446e9c1dd3a5df905389
ACR-1990a5d5df1c485b8b04978be1b60dd5
ACR-0d5d586472d34ed3b655ae1e94a07391
ACR-2f5151e4ba364e83958d29649c2b500e
ACR-ad3e333d5f70428e993596680066a0f7
ACR-ccd682d529e049c4bb613daa00cf897c
ACR-c03b862e6d024867b22d1412aec28898
ACR-11204ac5dccc447d9fda22ec7beafc9d
ACR-1ad5caf140104ea185c0aa75377982c3
ACR-8269d1de4cba4f39a85bb0e3fb350417
ACR-62335ed579494f908a12217f66635d8c
ACR-205ca71cea044fe496bf76a03265bc9c
ACR-fcf54511d8d64e178c3b866b40eecfd6
ACR-1e2846866c15493490e9a69c2cc7588c
ACR-9ffb025a56ad4e37a50bcd66a2f8c95f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import java.util.Set;

public class AddReportedRulesParams {
  private final Set<String> ruleKeys;

  public AddReportedRulesParams(Set<String> ruleKeys) {
    this.ruleKeys = ruleKeys;
  }

  public Set<String> getRuleKeys() {
    return ruleKeys;
  }
}
