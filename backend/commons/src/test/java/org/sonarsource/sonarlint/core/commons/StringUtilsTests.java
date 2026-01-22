/*
ACR-2c3f228a6fc9405884f7a16c146e5167
ACR-a05ad55fd41d45dc92a7b6fdcda0af90
ACR-c4fb501e151d41f69ef2e7d1a796bf1c
ACR-6d79499e6a2e4b62a39a1b5270d3cbed
ACR-6ffe46dc062a44f1a615c2400e6936fc
ACR-7cecafb799c44c7687744e56cd2b891f
ACR-789bc96472c3434d836eeed4f56380f0
ACR-0acbac3e34354c03ae976c0afae86ca3
ACR-0045e2d09959491583e52e27b6972209
ACR-e6d031083d0b4822964bc74d8c92a33e
ACR-a6907b090a53467fb82a400be4cb760f
ACR-5afe7b792e53435ea79252343b683c97
ACR-b13dcaf0805f4fab88bae142e64decc9
ACR-8cf5f71ce34a457ab28b0507d195ec83
ACR-6d5df0a659be449384160c05692a99d4
ACR-6162ee693da642acafcede36f961ea65
ACR-517be801ff8249ada492999a5c6806c8
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.commons.util.StringUtils.pluralize;
import static org.sonarsource.sonarlint.core.commons.util.StringUtils.sanitizeAgainstRTLO;

class StringUtilsTests {

  @Test
  void should_pluralize_words() {
    assertThat(pluralize(0, "word")).isEqualTo("0 words");
    assertThat(pluralize(1, "word")).isEqualTo("1 word");
    assertThat(pluralize(2, "word")).isEqualTo("2 words");
  }

  @Test
  void should_sanitize_against_rtlo() {
    assertThat(sanitizeAgainstRTLO("This is a \u202eegassem")).isEqualTo("This is a egassem");
  }

  @Test
  void should_sanitize_with_null() {
    assertThat(sanitizeAgainstRTLO(null)).isNull();
  }

}
