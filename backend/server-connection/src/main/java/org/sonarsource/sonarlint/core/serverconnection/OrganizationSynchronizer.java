/*
ACR-86dbae6c4b494ead951b25671e9f3ae2
ACR-d9af50f37a424e03a717db6f8d9afe64
ACR-7fe7bb3917514186a4fe945e7891d6fb
ACR-c1301d5d916d4047861c45e78bf4dc37
ACR-5fe9ec4c8a234ac082a49d01022bd905
ACR-28fa74f680d2440eba75778c9b014b74
ACR-61c1e9734d2e4941bc48443cc38735fe
ACR-735da46cd2d0414f967f7f5680803727
ACR-afecbf5a39664338af7aabacbfaa1835
ACR-c3f3a712b07145c7ab53e2f824332c2d
ACR-f00c2ae1010744b19b8008774a7893b0
ACR-6acbb065c91a4fdbab4aab1378bdef0a
ACR-26823f07fb1c4c948b16409468a3bc3d
ACR-dc31c514a38f4911a8e7dce9d5a647f4
ACR-a3ac159a27b443ed83dafb89c75cc386
ACR-7a0b0508c9d54aaeaf15299ac42e1a97
ACR-d3b95a8342a44c9692899074c503e927
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;

public class OrganizationSynchronizer {
  private final ConnectionStorage storage;

  public OrganizationSynchronizer(ConnectionStorage storage) {
    this.storage = storage;
  }

  //ACR-4f2323e4b19d4be3895439a95ec9e9a3
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
