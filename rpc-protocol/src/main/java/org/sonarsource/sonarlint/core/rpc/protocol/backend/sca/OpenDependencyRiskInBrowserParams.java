/*
ACR-8affd3e8eb6049f981dca5b496440e61
ACR-ac94b366ade944cdb26fa5d9d9738816
ACR-9c59e2c8c58346989a85a218cfaf7181
ACR-bd435de5ac694c6ba721c7e9556047be
ACR-fae4e39ec39f40a898c663bd6de5e771
ACR-6ab33499b5ad466ab7f6f0f22f4908ce
ACR-fa1af2fbfcb5471e8ee71b1428cbc787
ACR-cc1a5260592a43ecaf24231542ec5380
ACR-425b5a11a267426db88f3b3b639b825a
ACR-628008005f2c4ac79d3bab14f3400ca3
ACR-2a97f2932bd64995baa1aa639bc54dde
ACR-a6713b03c5374e5386a540c87d34ad93
ACR-faf13fc43c93489fba488f09fce6dee1
ACR-2be252ca6ada404489618e70a07a9125
ACR-8f93e6490f124fd98c9dfc306a5b58ff
ACR-561a0595bf3a4d368173279f304fa6b8
ACR-57d96cc929334be6a63e0a4c5dc6b444
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

import java.util.UUID;

public class OpenDependencyRiskInBrowserParams {
  private final String configScopeId;
  private final UUID dependencyRiskKey;

  public OpenDependencyRiskInBrowserParams(String configScopeId, UUID dependencyRiskKey) {
    this.configScopeId = configScopeId;
    this.dependencyRiskKey = dependencyRiskKey;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public UUID getDependencyRiskKey() {
    return dependencyRiskKey;
  }
}
