/*
ACR-43da1e7943f248cdb65c95ba9fd5d1cd
ACR-c5272ca7b4494f1f8f34929634965ea3
ACR-accaf87d217b47aaa9ba050184f4ea0b
ACR-92fbeaa532004e9e9b47a29ff705bf5f
ACR-cd0d45bb35af498bbe1ba331ca09c020
ACR-f24a716172634375ba97e7b91f371d7a
ACR-2f4339dee5ed4d47af13f994f7b38f78
ACR-3b14a44142824b8baf4c3e91fedd27ae
ACR-16d0d88bdd9242afacd2f10425ef0afc
ACR-1d500658274b42f7bb680da50c842d4e
ACR-1da70a76b8624a5a8e1424385bea653b
ACR-2aecf2c8fd964765b63fa2a9c0746cdd
ACR-af887224b0c549eb998285367a681960
ACR-fbc1e57eb882449488eafc3edf560b1e
ACR-bdc42d92a9b8461588a4f3d2ccf35fad
ACR-d0ccfeab1f1f4a7881fcf325953894ad
ACR-d2bd88bd4ecb4348b4230e1daad63fad
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
