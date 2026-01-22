/*
ACR-5243557f95444a82bf8ecf56882d6387
ACR-ff6e057ff98e4630b4cd2e70c89563e0
ACR-74ddd24809bd450d94b1d37178e9a91a
ACR-5595e4ce772e4a5fb8f6581966ff0766
ACR-2bfc67e2314d436fb2631cea93291120
ACR-c46bf5019e3647cc8f76915e0d7f222d
ACR-b6745319589843e989af47cc5c632cc6
ACR-a7b5b29d4ad24f7f94eaef8118ec3357
ACR-c8b3f0f072f1450d85d84752f4a4c27f
ACR-37dce0a4da8541d4a1e666cd70ee4a81
ACR-ea08f03115d9417dbdcbf44ebb12dfe8
ACR-2e56dea17c844f608517f49ce0d349b6
ACR-dccd4be33ad64a618005e229dc15f211
ACR-741be9b9c19946419281e9b25f023ceb
ACR-cfdca71c8aa04dde973a74ff3cc5fe6a
ACR-62cce914d0d54eccbbfea47ca583eb1b
ACR-f01b3a02da00447d90220fc5030bd16e
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
