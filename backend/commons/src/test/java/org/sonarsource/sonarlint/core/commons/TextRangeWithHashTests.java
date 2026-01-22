/*
ACR-68724e5541a144cd942289aeb65cbe75
ACR-1a7d27869de743df907c880cea520e53
ACR-88e5b2c3b32c4e7cab0d0ece08cf323e
ACR-2003b70639824923ba1d459a45b15833
ACR-39d99ede6d444b24814344a1c361413e
ACR-0471bcbf8cca4e29adee5ee85e777625
ACR-1873161e169940ea8959aa2e74090d85
ACR-72507603e5a14d7393a1abde2a8d23fd
ACR-8dc33c616a5d4159b550a44b7e3b4d67
ACR-4d6d85b26ed44ec5956757053096d8f4
ACR-1db6389d1ade4a35ad6ca97026bb91a4
ACR-a173384cead34eef8a6396ba63392e49
ACR-e09ea896e2b84b2585ba9af477412d4b
ACR-ee99fc125fac4f4abf1e5b3b12dd2d54
ACR-578a49ee0abe488eab1085dfeb27211f
ACR-f9cc056e93684237b0678d6ba68da875
ACR-09f22daa87954d558e3fee2dc94a723f
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.api.TextRange;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;

import static org.assertj.core.api.Assertions.assertThat;

class TextRangeWithHashTests {

  @Test
  void test_getters() {
    var textRange = new TextRangeWithHash(1, 2, 3, 4, "md5");
    assertThat(textRange.getHash()).isEqualTo("md5");
  }

  @Test
  void test_equals_hashcode() {
    var textRange = new TextRangeWithHash(1, 2, 3, 4, "md5");
    assertThat(textRange).hasSameHashCodeAs(new TextRangeWithHash(1, 2, 3, 4, "md5"))
      .isEqualTo(textRange)
      .isEqualTo(new TextRangeWithHash(1, 2, 3, 4, "md5"))
      .isNotEqualTo(new TextRange(1, 2, 3, 4))
      .isNotEqualTo(new TextRangeWithHash(11, 2, 3, 4, "md5"))
      .isNotEqualTo(new TextRangeWithHash(1, 22, 3, 4, "md5"))
      .isNotEqualTo(new TextRangeWithHash(1, 2, 33, 4, "md5"))
      .isNotEqualTo(new TextRangeWithHash(1, 2, 3, 44, "md5"))
      .isNotEqualTo(new TextRangeWithHash(1, 2, 3, 4, "md55"))
      .isNotEqualTo("foo");
  }

}
