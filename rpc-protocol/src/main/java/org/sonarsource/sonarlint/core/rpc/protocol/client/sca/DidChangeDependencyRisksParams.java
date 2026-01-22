/*
ACR-23580b2008f2426faed30cff6f7c0e6a
ACR-3d13a511590b4e98aa7342ca04b4dca8
ACR-12be248bf2734488ae226dd98f4ba908
ACR-a4f2b36e4c7444bba3f9899d254f59f2
ACR-00faf0dd618e459884c7577813f6698f
ACR-c10ece8a198440f9bff441acdd9405d1
ACR-996988cbd9e2415097a08d53771137a6
ACR-6254079f0d3c4c80b99a97dff1261f18
ACR-d243394871844c2db8b56b2021437011
ACR-168b80a26a73448e9a6aedb39369b64c
ACR-4a13aeaa205745f29d8870c3e90ba688
ACR-a918f19a1de94aae8a373eac71fce939
ACR-3e8c39425e7d4c72a83d7e34cbbc1c55
ACR-554bc05a36554a4db2fd8c56e1cf3040
ACR-a89b49f4cbb44c19b4729f5f4792b2a6
ACR-159ef244c17b4b1f8fe8b7e1c616a1e1
ACR-7d8d1424bdb74c46ab3eb24083c9830f
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
