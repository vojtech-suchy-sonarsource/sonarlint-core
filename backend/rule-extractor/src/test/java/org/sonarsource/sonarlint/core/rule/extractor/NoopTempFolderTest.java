/*
ACR-fde5115909184c54acf7d937be3474a5
ACR-6943ad63baa74314b1928d598fcd5f44
ACR-dbceb4c2c211461b8537b41bbd40c416
ACR-fc9ef59aaa914a63a9d521b393ea9872
ACR-988068e873284eda80c2b2e0dacc3714
ACR-19d69bc8f11b468a8cb16b208123f2da
ACR-b8a061e08fe5470baab005c8090388e4
ACR-b50dc0e6b70846a8a2c4fff63d64f1ef
ACR-39f3129a7d6f48c79fb81e7a7cd337b6
ACR-9942f94b7ae64cad89ec75cef273576c
ACR-c397a6f199484955b4666100ce5a8556
ACR-2721a4231cdb49dbaa557df57c37b3b5
ACR-f03248bbe9724c76b650e1b2fccf7a93
ACR-545411fd52024875a69c346f2c26e597
ACR-5a55d0caae8f4176997411b32b91ac08
ACR-2aca620676db4e338a959e0a609793f4
ACR-6385220368634a73a5ce81344e9bc193
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NoopTempFolderTest {
  @Test
  void should_not_be_implemented() {
    var noopTempFolder = new NoopTempFolder();

    assertThrows(UnsupportedOperationException.class, noopTempFolder::newDir);
    assertThrows(UnsupportedOperationException.class, noopTempFolder::newFile);
    assertThrows(UnsupportedOperationException.class, () -> noopTempFolder.newDir("name"));
    assertThrows(UnsupportedOperationException.class, () -> noopTempFolder.newFile("prefix", "suffix"));
  }
}
