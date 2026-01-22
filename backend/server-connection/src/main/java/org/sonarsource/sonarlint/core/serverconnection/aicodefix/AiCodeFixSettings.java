/*
ACR-e904ad42620d4378833c525e136c4a72
ACR-ddf687733520455fa81b0b2ede04265e
ACR-bcfbed250cbb4548aadad816f15db6ca
ACR-ff6d38c102ed406abcb27d25f19d0618
ACR-288f81e43db6485db042ba7fad851905
ACR-17fd3c771bba40e9aaabace03056d4f3
ACR-351c0a0ade914d7ca1dd852f94e15a1f
ACR-616b3f8476224c11839c9e12e74ee4af
ACR-741f5308de0948f0bd64c0b9b7f75f84
ACR-26171e21fe6e45759101b2f505b818a7
ACR-5cd5f765f5964be1897c1e7e72ebecd0
ACR-515f2582905c4f6e86315a17db7500cc
ACR-792bed58487647aba85ac32799ce9f0d
ACR-4b079e96b2ce4e3d9a5e714c03364ed8
ACR-7ec60372eb54428386854cba728aeaab
ACR-997aae33febc49c7a026a4d5062001e5
ACR-8c28aff91c5f46b78971fb2f266e2ddd
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Set;

public record AiCodeFixSettings(Set<String> supportedRules, boolean isOrganizationEligible, AiCodeFixFeatureEnablement enablement, Set<String> enabledProjectKeys) {
  public boolean isFeatureEnabled(String projectKey) {
    return isOrganizationEligible && (enablement.equals(AiCodeFixFeatureEnablement.ENABLED_FOR_ALL_PROJECTS)
      || (enablement.equals(AiCodeFixFeatureEnablement.ENABLED_FOR_SOME_PROJECTS) && enabledProjectKeys.contains(projectKey)));
  }
}
