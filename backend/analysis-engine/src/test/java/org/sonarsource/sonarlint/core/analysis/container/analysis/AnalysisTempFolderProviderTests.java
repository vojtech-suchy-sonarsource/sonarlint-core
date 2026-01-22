/*
ACR-b4bad0ecdba94c5489cacc64a3143c60
ACR-0cf8f2ca83c54c6ebda05de99147578b
ACR-be5334418af14e2eb6d13a59222240d8
ACR-2f6814c8b2594e27a9b2afa5d34abc47
ACR-d26061cb3b254330a1bb0629c47731d9
ACR-a4e422904e444b76a4b2203200c11ee2
ACR-593aa84b180745d4ac07f59d23088e56
ACR-8090ffe662fa4751a22ec79ff74b0fa1
ACR-1bbf4673ee024a70b17aa8c83aca524a
ACR-7ca115bd98394d47a03e9cc46b0bfb48
ACR-58beb7459ab1424c88e6fc0370c0ed5c
ACR-6df8c8a75927401889a88ce5fbecfe73
ACR-e6a74b8147094208afdc815619b5245e
ACR-4c44e66f91f5454a976aae1db1127d97
ACR-800ce4ffa2974cc78880dcdf81d6ca10
ACR-f3240227996c46cf852e6d5606be7bb0
ACR-9f4c5bb9647543eb84055163fd9e9811
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
