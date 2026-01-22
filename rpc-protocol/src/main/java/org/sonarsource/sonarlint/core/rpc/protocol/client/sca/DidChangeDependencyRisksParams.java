/*
ACR-a7eeb88f1c844d9aad69ec716f4d21f5
ACR-41e3319aed9a4451870efd25a34be7f3
ACR-f8748e97cb3b46e7bc636f79bdbdc153
ACR-9c911a974fbd40d9892abb323b423328
ACR-cce5e36f1fe4424f9c58ef1178635dbb
ACR-20da1f0a13844703b27f08361014cbfb
ACR-6e640c0909a04072b5b49ad6ef1ce3e1
ACR-5c87287b98f44c95adf4a2220fe9f449
ACR-4a9db30936144568b56c4ed2ebd08c7f
ACR-49b53f2293134cef80fdbd73df7a613f
ACR-5b0987499bdb41898b536a792f9637fc
ACR-20d7816a63d94f0eb1aaac86f12f3391
ACR-9e503ee7ff2a4b7796694d37bd01733e
ACR-a0734f5321ee421ca4ba238ea9d20acc
ACR-346e6967b6554665979c04a6a93439cb
ACR-894fecf75dee4806b3b35deb9a05dc40
ACR-87669ca884144ea696af729881cbcfed
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.sca;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;

public class DidChangeDependencyRisksParams {
  private final String configurationScopeId;
  private final Set<UUID> closedDependencyRiskIds;
  private final List<DependencyRiskDto> addedDependencyRisks;
  private final List<DependencyRiskDto> updatedDependencyRisks;

  public DidChangeDependencyRisksParams(String configurationScopeId, Set<UUID> closedDependencyRiskIds, List<DependencyRiskDto> addedDependencyRisks,
    List<DependencyRiskDto> updatedDependencyRisks) {
    this.configurationScopeId = configurationScopeId;
    this.closedDependencyRiskIds = closedDependencyRiskIds;
    this.addedDependencyRisks = addedDependencyRisks;
    this.updatedDependencyRisks = updatedDependencyRisks;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Set<UUID> getClosedDependencyRiskIds() {
    return closedDependencyRiskIds;
  }

  public List<DependencyRiskDto> getAddedDependencyRisks() {
    return addedDependencyRisks;
  }

  public List<DependencyRiskDto> getUpdatedDependencyRisks() {
    return updatedDependencyRisks;
  }
}
