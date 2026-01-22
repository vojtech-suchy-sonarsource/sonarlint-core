/*
ACR-588b7b85b34249709e0a3e6b1596ad40
ACR-daaf10f628e6426e8681a3d317efa85a
ACR-b29c773792d34fcdb977c79259511595
ACR-78bf2037f4fe48288cdfaf94563ec1ba
ACR-f52fba23dc8a4eab80cdc81a98df5257
ACR-0c8c1518e2c74898b144811c8d3e4536
ACR-c8849c2b596548e9ad3478936299a8fc
ACR-2be56c44542241a3b877f6f6ac7eb9b8
ACR-d37ba6a809a34d0eb536ecd2535e5a73
ACR-8321b9296e7043a286cae9d7da9015c8
ACR-afa617ce2b35497993f46f120bc081d4
ACR-49e21a15461942d5b2150d9dda411019
ACR-0adde493683c44b492cdbd7a8512fedc
ACR-db43fe0ecf4b401ca6ad2f61c749a61e
ACR-d5dce2da857349e29fe7f2c786670a38
ACR-543946aa127e4be293f435ce53b2630f
ACR-ab1a1df759694d0daa14090c8da136a3
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AnalysisTempFolderProviderTests {

  @Test
  void allMethodsShouldThrow() {
    var underTest = new AnalysisTempFolderProvider();
    var tempFolder = underTest.provide();

    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(tempFolder::newDir)
      .withMessage("Don't create temp folders during analysis");

    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(() -> tempFolder.newDir("foo"))
      .withMessage("Don't create temp folders during analysis");

    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(tempFolder::newFile)
      .withMessage("Don't create temp files during analysis");

    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(() -> tempFolder.newFile("foo", "bar"))
      .withMessage("Don't create temp files during analysis");
  }

}
