/*
ACR-d02848d2da1846f3b1877c3abf8d9d2c
ACR-08446fe3b0a84413b7583d3a9ebc63c9
ACR-2f1004fd5e1e43cb9278b6cae7a8c6c1
ACR-f1aeeb4b26b44c0fb56aa1100775656d
ACR-e519ce3dde8d40b2b791611a5d144f32
ACR-94ff7ec71f0f429dadb325e14fee4aaf
ACR-634fc155590e406280622873ee2ecf7f
ACR-fd10445add7a40728581708c0f229f49
ACR-46f2f95f13c341828a87c62ef8aee309
ACR-a82bf1029ac54b7bae253e00a45903ec
ACR-3ce92a1e81fe40f782a90fe51a811b00
ACR-9f36180ab3ba458f832c1d8fd01e30dd
ACR-3f3bca2d2be2463186523b8228d76c5d
ACR-d22f411cbf8242f2a25e54ed84b57039
ACR-82d9948747f7481da36aa720f280b027
ACR-1f01becac9a746449508253a06ea65fe
ACR-c3ce6a57fbd54b869a51d49697c47e28
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
