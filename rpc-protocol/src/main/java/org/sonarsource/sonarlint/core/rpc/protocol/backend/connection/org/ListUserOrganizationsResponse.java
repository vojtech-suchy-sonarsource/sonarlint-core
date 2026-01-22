/*
ACR-53d78b22720448a3a13f0aec061e21ac
ACR-6b0a4b7ea01e4c54b3739035dd1182d2
ACR-bf03ef7f5b4e495a9f66fde08dfb397c
ACR-67ec381aec4743b4a204694f837e6d08
ACR-5b67bc73b9eb4f6486b5c656c30b5bbf
ACR-9a5d553fb3eb4a42ac09caca4a46e399
ACR-a7692bc29e484ae8a18eafa5619bfca6
ACR-80e8e94bd788414da7aea7c7824faa65
ACR-e87127c7bfd0487db04124fa966d7393
ACR-992f1eca988e4726a22769651fd5ce26
ACR-a8bf688bc55848f38b2aa67aa8ee0a00
ACR-5830d662823a488d8e6038e6f3941ad3
ACR-ce8d82bc7c0a4aaebb20c29e9e9471b9
ACR-2406fbebb2c1415e9215229367d91d38
ACR-9e766e9c496949e4818416a3623183ff
ACR-9076bac2dee0481c9734e10a8abd2f24
ACR-b907455766fe455fa6b5e3a7b3a9b753
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
