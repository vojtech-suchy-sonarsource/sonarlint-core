/*
ACR-01da0a6983ed463bb3f8ceea9f4518fb
ACR-2cc9982cbbe845c0a62e2ce7a32bf48e
ACR-2476db018da742a0ae8154254c2307f1
ACR-ef8b6f7e83b241508cff940df2dcb32f
ACR-cd937dbb573a4e8f8eb2508ee12445b7
ACR-f289fda15d2d412c9227f19bb819dc06
ACR-c37cacb786134b4e822ae82219ca8361
ACR-00ffb6c155f24bd2a08623c33815a875
ACR-95c1d6571d85423fad7e2b2242df3a1e
ACR-89a6e94b08f24d709e6155b6eb7a3fe8
ACR-605bf81dfa4b4633857f14a09c672e42
ACR-f67493126b964c369643e34c4ae5e843
ACR-0a3c6ca25982476ca013c46f23ef4591
ACR-695e6fadc25843c09d13a55b260aa302
ACR-81d496a72bf643a590a3e697b1dc29a2
ACR-3b630daca9fa479aa808d40bb161d6ae
ACR-9d26810d9c94473fa8cae09a9ff7a7ab
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EitherTests {

  @Test
  void testToString() {
    Either<String, Object> left = Either.forLeft("left");
    assertThat(left).hasToString(left.getLsp4jEither().toString());
    Either<String, Object> right = Either.forLeft("right");
    assertThat(right).hasToString(right.getLsp4jEither().toString());
  }

  @Test
  void testEquals() {
    Either<String, Object> left = Either.forLeft("left");
    assertThat(left).isEqualTo(left)
      .isEqualTo(Either.forLeft("left"))
      .isNotEqualTo(Either.forLeft("left_2"))
      .isNotEqualTo(null);
  }

}
