/*
ACR-9f2d0b7a16c24c1f8f170415bfb33018
ACR-adc3393e52fd424ab7b66ebd31219d29
ACR-14988c10b8be4c12a30e979332c91901
ACR-d218bb78c5504d7085a7e0b5f6647bd6
ACR-b448018c1b494a08af7500e8f643679e
ACR-d92d0cbaeb43402a82509ca1f5a4041b
ACR-ce39ea38efa144458693714d20ff1097
ACR-cf7e5077c121490693ef0940c96cd26c
ACR-2501310ff87f402b94f9578f89ef2ad0
ACR-47c0c11826f3482da5ff76a60c25cde9
ACR-2e5969343c3e4fa0976e2d12fb1542b0
ACR-9fb8b2f36b844755b6fe208d8c73c9ba
ACR-6dc75375b3294014b3910f006c797b07
ACR-710d933d3f31468faeb1b94d097694b7
ACR-e07e4016943f4b44bb54f63ccc14f9d7
ACR-fc98ceb1ad174dcf99249cb35f597e9c
ACR-9a6921dbe6d047548ffa88cb31f24b32
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org;

import java.util.List;

public class ListUserOrganizationsResponse {

  private final List<OrganizationDto> userOrganizations;

  public ListUserOrganizationsResponse(List<OrganizationDto> userOrganizations) {
    this.userOrganizations = userOrganizations;
  }

  public List<OrganizationDto> getUserOrganizations() {
    return userOrganizations;
  }
}
