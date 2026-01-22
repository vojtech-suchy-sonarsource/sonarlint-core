/*
ACR-76155d642a484230b71d6fdf40842850
ACR-2df8487d630f423389fd4bd8b126a6b8
ACR-fd3d94a874324cedb5c94b18137ec119
ACR-1e1f603106f74c23a41c51dbeed1ce56
ACR-638eae13665d49e6b7566b0677e3abe1
ACR-26490a2a86c94a21a480f76a2ac40a19
ACR-a458e101e97e425fa47ebdd9ce6ab06d
ACR-ad4993958d754aa7bc614921b94d2252
ACR-9e4f85e8eecd49a883c4af0b5cc86abc
ACR-f853c75bf473487c9d63aa0e32dae721
ACR-10f3a1c3e92449c5adc6b1a54cd72f70
ACR-87e370bc9127474aaf84e41e94920ff1
ACR-7690437d7e984e09b9132a76f43e5902
ACR-d44a8a0613454b4a9648ec39e1e616f6
ACR-a1784b5a4f284d478150a1cb980142f9
ACR-6a76b0660dd94e9cab53fc998927c483
ACR-3e2b245067eb455fac5b20de2fbdfde5
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
