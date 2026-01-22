/*
ACR-038acfc0cfaa4094b9760e18c9804ff6
ACR-ceeeb319a1c64622bde289bdae57f3e7
ACR-7e2a00e4d1a14c8b8320a4810d81491b
ACR-a978d3f567f54a0585e517d830b3df59
ACR-9141c13286c24b3d9316cb9af2ece325
ACR-de65cab432c44089aa1efe333122ef2f
ACR-dd551013093041d08123fe9e77bfd370
ACR-7ea661b55a6a46c3966da8c9e2ebf436
ACR-f337c328070441d29c34139332a3e8dc
ACR-7be663925e0949a8af8f96201975d978
ACR-b03eef89206c4e10af11527e4e1530e2
ACR-8059efb64c5e4d7db81afed755dba819
ACR-64a184367e7e4180b520c0aeaf8caf55
ACR-1be0b0aa01c64775b2732f575f526e25
ACR-cf6bab1ed8d4487dbfbc755b937ce38b
ACR-e98951dcb7af44159a5bc83151f2161e
ACR-ddf501cbcc5743a580191ac92ffbeafc
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
