/*
ACR-cecbb5af101946a8ab8367409e6a807a
ACR-1ae748fd044f49cc89223d9b7862c612
ACR-00ce3853b74542e3b0b2fbd1a3bfa53d
ACR-db3277af68564760a4624fd5f6e3c2f9
ACR-f06c4932ead140f6bea4fe024646d7f3
ACR-ce19a12f72434676b0e73fdd53eeb093
ACR-6eafe97fe5684cf98c7cf1175a09d660
ACR-701c200f56a14af79281a115a68913e2
ACR-d0edf97469334aa8b6e8e73bff8864fc
ACR-8d62264a5a0a48c8a5ddc39963588f98
ACR-2ac28e711e344cfa90c05af6281c6b94
ACR-6d50fd5777d34da692e45cf30004df96
ACR-cff8cd877d5c40c482a4f6cd5ac1479c
ACR-d4edc6474c59430282d2c1745024ae62
ACR-2709869cc0114d4f926f233d9b3091c2
ACR-0804044607ec43aca85d53dee7bf2610
ACR-e1ed5281836340c180bb750be9caa8e9
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
