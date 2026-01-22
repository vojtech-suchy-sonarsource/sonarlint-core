/*
ACR-30be41af631d4776a6cb30bf96e1b8a1
ACR-d1dea25e552a4a53ac568131230cb541
ACR-f4648c62e23542519f54374e0d3c162f
ACR-7761b28b7b18488b973e0050c58f44f6
ACR-d083268c8228485283d098c275a95c06
ACR-67d9cb0eeb36434dbc32d161cc7f0c35
ACR-7ad40f55398e4aababdd848d519d2b54
ACR-909da75f9a824b87b5ae6da86a3e41a2
ACR-9d18f5c66d9e44bc80f4570760f27ebd
ACR-71b55ac63fd5419296df55ee7abc74d6
ACR-df6e164090754cf59884ca67461efd8f
ACR-87778649959b4775ac9e919ba5ddb7c7
ACR-1d91373b32c54c57a772ecf07f073f0f
ACR-2063a507f87c4cb481ef97fbd85ff019
ACR-92e49de7f433493481f8c374040f7fc5
ACR-7a6c3df2def24feaa601f7c6719b6988
ACR-cc26eaba55ec436c82cf7345822451e3
 */
package org.sonarsource.sonarlint.core.serverapi.organization;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations.Organization;

import static org.assertj.core.api.Assertions.assertThat;

class ServerOrganizationTests {
  @Test
  void testRoundTrip() {
    var org = Organization.newBuilder()
      .setName("name")
      .setKey("key")
      .setDescription("desc")
      .build();
    ServerOrganization remoteOrg = new ServerOrganization(org);
    assertThat(remoteOrg.getKey()).isEqualTo("key");
    assertThat(remoteOrg.getName()).isEqualTo("name");
    assertThat(remoteOrg.getDescription()).isEqualTo("desc");
  }
}
