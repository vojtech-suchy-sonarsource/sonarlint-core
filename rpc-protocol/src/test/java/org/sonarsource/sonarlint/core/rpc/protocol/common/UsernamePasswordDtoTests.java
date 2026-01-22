/*
ACR-5fa11e1c0e4f4424940ad964ed1f82bb
ACR-5b57b802e8e34521831cab4e475ce63f
ACR-e625fb706275403aba58b5e4b56dc894
ACR-31a4e7c299174344bc22dcc27ce30627
ACR-2b5ad3f6c19c4ec799cece9df7bfa66a
ACR-b0c550cf370c40c9beb4d03e3a6a8c2f
ACR-6f4c8491da104a53a4e164b404e1d4c6
ACR-5ebe825be20847bbbeb237e106903f87
ACR-1400526e671242c68213cacd9ceabdd0
ACR-44efe5be9b32440c9e218d6d9ed63eba
ACR-2242b09327a94b85a60d3ac364481eca
ACR-db313e311ced4a8fb2f5549bc11cf77b
ACR-9781fb28c37447d48599373d1ba92138
ACR-5ae488e722e84a4099f069ea27bdbd70
ACR-d0ce867aa0754d30a6470d6711ecd940
ACR-3f0df7b3d51c4907ac9a697db7381628
ACR-58d3fcd0e18a4c51b236eb747b587629
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

    //ACR-74892d15fe99487c8ab82551ae9da16a
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
