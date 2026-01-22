/*
ACR-213a127e617345ac8cc2095aae3aa48b
ACR-4a0ab929bc634d5ba5262ce7993bb4d5
ACR-01137684ca7b4a14a757114f53925a0c
ACR-eefcd102608e4b0e91c2496def0ade72
ACR-7ed2cb6d1bbd4846a758a419e5e77f17
ACR-7b87283296cd483c8cce7f3af3b9668e
ACR-de4f4c8800f5413cb38f2c275a00c235
ACR-16fa430c93e34ca7ba08349910fadacf
ACR-a18f9faafb43464894b04594942a0847
ACR-5dd441663fce4d1a92476b4b3df8b4ab
ACR-f17c8cd350c3409b825afeb77181472b
ACR-9239f4a74b574fe391f7602141cb9805
ACR-c7dca66532d04d5196af1cc4ff3ea476
ACR-c5f742d3e9324d2db5d917fa16122680
ACR-0bee170c237948fab1267930a5a39871
ACR-3e8516805ddc4f4da0673383d11a181c
ACR-3856c50987ba4133acdae5fc2e9379d6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsernamePasswordDtoTests {


  @Test
  void testEqualsAndHashCode() {
    var up1 = new UsernamePasswordDto("user1", "password1");
    var sameUp = new UsernamePasswordDto("user1", "password1");
    var differentUp = new UsernamePasswordDto("user2", "password1");
    var differentUp2 = new UsernamePasswordDto("user1", "password2");

    //ACR-89d4b91c3eab4ff9bf09083153feb7e8
    assertThat(up1)
      .isEqualTo(up1)
      .isEqualTo(sameUp)
      .isNotEqualTo(differentUp)
      .isNotEqualTo(differentUp2)
      .isNotEqualTo("token1")
      .hasSameHashCodeAs(sameUp)
      .doesNotHaveSameHashCodeAs(differentUp)
      .doesNotHaveSameHashCodeAs(differentUp2);
  }
}
