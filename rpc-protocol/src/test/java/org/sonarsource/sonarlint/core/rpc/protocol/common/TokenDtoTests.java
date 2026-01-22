/*
ACR-33a6512db1624f8383fa1ff3315be5b2
ACR-ce8c65015c4b40e2be5d0c4eef8c846b
ACR-1d78fa3dc67c4e44af80b5364d0ea876
ACR-294b4289fead4dccbc6d0135b5203185
ACR-d8696f17e9644b9d840e746c4af2ea51
ACR-8b92ff310ce64aa4ad3ed437c827961e
ACR-12724cfc521b4105b74b07fc18df89d6
ACR-254d77d16f1a430182fe6bfe204851f0
ACR-9a116d3192b248269ba56c467439240c
ACR-42e5d6014419404795c780ff5008f33d
ACR-85ee71e47f154bfa90e85259c92216fd
ACR-8308531474054bbbb7b5be7a7ba00d36
ACR-582f0d5bb7b2478f883d2d676f9ecd48
ACR-d0a1ba5465444e12845b57e41ded79a1
ACR-17b664da684f4aa1bf02305d297975dd
ACR-2fedef811edf42f5bd284e44c7d3ccba
ACR-e6b167d0597544eb81be47003ff71406
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDtoTests {

  @Test
  void testEqualsAndHashCode() {
    TokenDto token = new TokenDto("token1");
    TokenDto sameToken = new TokenDto("token1");
    TokenDto differentToken = new TokenDto("token2");

    //ACR-4a451cf58e4b453fb342d25dfa2d3d15
    assertThat(token)
      .isEqualTo(token)
      .isEqualTo(sameToken)
      .isNotEqualTo(differentToken)
      .isNotEqualTo("token1")
      .hasSameHashCodeAs(sameToken)
      .doesNotHaveSameHashCodeAs(differentToken);
  }
}