/*
ACR-6826d58c01cb4ada837ea98a34d7ac91
ACR-aff195d54e34459394bcdd21d5d1873f
ACR-bc649c7c28e148808164a7cb91f395cb
ACR-e552e35cd0f54cb9881ac5b3244303fd
ACR-be987060b0f04a528c04ed35e9bf3f2d
ACR-45a9b0906ca344bdbae8f45bc33e8b19
ACR-e7149e74b406401fb6e08369b163e048
ACR-a8059c650edd42c2ab074f8093cc192c
ACR-4bbf66e08678435b84ccf603ba7a3feb
ACR-ddec6db1bdd94bdd84ea651f36152c01
ACR-232d80f303184c9d8c048927e6259011
ACR-29f2d4c584694c328c8734d13e596ae9
ACR-004a0161bc264ddb98ca84eff6b8b967
ACR-f5357898df3d4215bb20cef9c448b4e9
ACR-2d93c5c43e17461897f5159f84344ef7
ACR-a778ff9a00bb49239e53ec377e2004de
ACR-d3af09406da3405fa030a888532b1565
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

import static org.assertj.core.api.Assertions.assertThat;

class TextRangeTests {

  @Test
  void test_getters() {
    var textRange = new TextRange(1, 2, 3, 4);
    assertThat(textRange.getStartLine()).isEqualTo(1);
    assertThat(textRange.getStartLineOffset()).isEqualTo(2);
    assertThat(textRange.getEndLine()).isEqualTo(3);
    assertThat(textRange.getEndLineOffset()).isEqualTo(4);
  }

  @Test
  void test_equals_hashcode() {
    var textRange = new TextRange(1, 2, 3, 4);
    assertThat(textRange).hasSameHashCodeAs(new TextRange(1, 2, 3, 4))
      .isEqualTo(textRange)
      .isEqualTo(new TextRange(1, 2, 3, 4))
      .isNotEqualTo(new TextRange(11, 2, 3, 4))
      .isNotEqualTo(new TextRange(1, 22, 3, 4))
      .isNotEqualTo(new TextRange(1, 2, 33, 4))
      .isNotEqualTo(new TextRange(1, 2, 3, 44))
      .isNotEqualTo("foo");
  }

}
