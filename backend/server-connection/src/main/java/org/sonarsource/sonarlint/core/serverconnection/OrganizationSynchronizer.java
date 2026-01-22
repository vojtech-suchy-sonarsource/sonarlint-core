/*
ACR-2c5d24294134497fbdd4aec03a65d714
ACR-bcd3c39f072045b69eb88d243626a567
ACR-e8e5708d11834b738d38566af4e56641
ACR-f7c0de4b233846f6a13ea3a8096ddab9
ACR-27a1274d9ba844ef92038f3940a232fe
ACR-1d48f59feeda4fe7a60551b590986aa3
ACR-3753ec5122134e07b1699cc44d62e64c
ACR-b950a19d9da54d52ad48a447b43a81b0
ACR-edf2c494877c41ea820e02cc4ab8639b
ACR-8a5612b6b62a4659aa12ebc4207a07ac
ACR-d19acd379fa84f6cbd65d0253100a920
ACR-4087110cdf00493c838ed53de7dcaf7e
ACR-25b23183f44c459da2be372776e0f407
ACR-e87aac3f6cee45a9b665c947a3496234
ACR-d6789c52ec604212911b8e15da8d3e27
ACR-f23dbbe03286419aacb0b2f13e2038cc
ACR-643fa2789b2f40238911c07b36cfab57
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;

public class OrganizationSynchronizer {
  private final ConnectionStorage storage;

  public OrganizationSynchronizer(ConnectionStorage storage) {
    this.storage = storage;
  }

  //ACR-4ff3fa09d76545c5828b158b063cfd96
  public Organization readOrSynchronizeOrganization(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    return storage.organization().read()
      .orElseGet(() -> synchronize(serverApi, cancelMonitor));
  }

  private Organization synchronize(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    var organizationDto = serverApi.organization().getOrganizationByKey(cancelMonitor);
    var organization = new Organization(organizationDto.id(), organizationDto.uuidV4());
    storage.organization().store(organization);
    return organization;
  }
}
