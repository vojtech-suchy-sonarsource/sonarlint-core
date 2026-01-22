/*
ACR-d06ab545b36f417a817f78101398c189
ACR-c271132f345245bab83a988ea6be3e99
ACR-6586b0d526ed4e87bf3e21a634870f12
ACR-f04e40c187864191b8bb1a00dc16e037
ACR-366df1b8d754488d80dbfde806fbdb2c
ACR-04a9b2ea3e914e3fa5c375881a1f6b05
ACR-e002d35d5eff4fb7a5aedc43f24fe1e4
ACR-230a8f1a42034461aa2d2cdf28b7db00
ACR-229834d7a0294a0892cac8172f17de08
ACR-696c9e3cbee3409e9455d1d2146611eb
ACR-4955474f01b748b19ad0f9c94e07bc2f
ACR-4c289199edec4b1db14f53d617408f2f
ACR-67d8a6d67aa040008910095a5d7250d3
ACR-e2155299f16d4f75a3f88c2069c8bef3
ACR-176a1117b2e542f78640b1285ea73726
ACR-95bce495241842fdb5d2392eeec82fb8
ACR-7b17a0a45feb449789a28e728be32f8d
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineRangeTests {

  @Test
  void lineRangeShouldBeOrdered() {
    assertThrows(IllegalArgumentException.class, () -> new LineRange(25, 12));
  }

  @Test
  void shouldConvertLineRangeToLines() {
    var range = new LineRange(12, 15);

    assertThat(range.toLines()).containsOnly(12, 13, 14, 15);
  }

  @Test
  void shouldTestInclusionInRangeOfLines() {
    var range = new LineRange(12, 15);

    assertThat(range.in(3)).isFalse();
    assertThat(range.in(12)).isTrue();
    assertThat(range.in(13)).isTrue();
    assertThat(range.in(14)).isTrue();
    assertThat(range.in(15)).isTrue();
    assertThat(range.in(16)).isFalse();
  }

  @Test
  void testToString() {
    assertThat(new LineRange(12, 15)).hasToString("[12-15]");
  }

  @Test
  void testEquals() {
    var range = new LineRange(12, 15);
    assertThat(range).isEqualTo(range)
      .isEqualTo(new LineRange(12, 15))
      .isNotEqualTo(new LineRange(12, 2000))
      .isNotEqualTo(new LineRange(1000, 2000))
      .isNotEqualTo(null)
      .isNotEqualTo(new StringBuffer());
  }

  @Test
  void testHashCode() {
    assertThat(new LineRange(12, 15)).hasSameHashCodeAs(new LineRange(12, 15).hashCode());
  }
}
