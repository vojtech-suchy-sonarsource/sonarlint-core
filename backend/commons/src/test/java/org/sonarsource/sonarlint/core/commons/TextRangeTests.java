/*
ACR-447f9873d3a64adc9b6e6cd2881e10b1
ACR-50502202aa4948a1a0abed73f7e1a242
ACR-cc89253a0282413980535922d219a9df
ACR-0deb19291bac4793800eb1909cfbaba6
ACR-664eb551c37f42e0965f6ea02ccbfda6
ACR-e4f1a3505144441bb2bf4aee2e852cf5
ACR-79712351d3284c1f800042dcbdc3b06f
ACR-fbf85cb3af7b4cfab71dad4833d31063
ACR-588339e156374f67a1db6917136f29e0
ACR-3757e5758f614dc4809d68dafe8cf74a
ACR-9746f4f1f9b04792a116a46abdc242f5
ACR-2773abd1adff4c78a67704a09a9df182
ACR-9d3b4063735f46bbb269a561aacaa517
ACR-88e833cb168042e894fb148ead1893b7
ACR-53a040fe19fa46c38ba64ae171cf575a
ACR-5da81238f41641759b77c5f81674128d
ACR-2e033ec00da440be955d1e5d4907356a
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
