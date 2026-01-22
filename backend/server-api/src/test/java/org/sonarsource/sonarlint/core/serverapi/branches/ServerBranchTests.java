/*
ACR-80580d998b2949abaec1a78c91fa2c13
ACR-1ff02d09d12544438800a3d8fce3eb8f
ACR-efb159b4ffa143c3a6922c8446a4af65
ACR-2e65543ab14449b7be4ae8c4a6a6cbdc
ACR-bb1b0fe953c24f23aea77a7d43516cec
ACR-3e4eb54eaa604c1f94a98efb065ef079
ACR-ca28393c67bc4975a21df418526c61f7
ACR-1bb2dc9e8f8f433dba4aa93e4e1bafb9
ACR-e217fea4680a473394dc965c2dd164fe
ACR-f98c2dbebbcb452c8059588c2dd1472a
ACR-5592602025d847418cdaf376fc86ca53
ACR-75e658c8022b430bbfa7a66f86ffff86
ACR-9baae40f07544c528bcb57736c4ae301
ACR-5809e3fd7b53472f822e9516023d94a2
ACR-ce78ea7988ae44998673d26e672424e4
ACR-aeb8dadbb44248d99459b29032e6eebb
ACR-5566bb5e5ee04605b05c9e6648b218f9
 */
package org.sonarsource.sonarlint.core.serverapi.branches;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ServerBranchTests {

  @Test
  void serverBranchTest() {
    ServerBranch branch = new ServerBranch("foo", true);

    assertThat(branch.getName()).isEqualTo("foo");
    assertThat(branch.isMain()).isTrue();
  }

}
