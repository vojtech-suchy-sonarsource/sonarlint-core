/*
ACR-2e43fe74efc44a71b716101d1a8d77cf
ACR-85701cd454a443b69b9d3f02069216c3
ACR-6e2f3474e8744965aaf32b46753c6e1b
ACR-0987067999c04f3c924f74c3578bd00b
ACR-18e199c220a4460f91e543399efea01b
ACR-4ddc552302814c8dbe53bd5446e8a05d
ACR-95563110d8cd4ac6852bc19a969a7668
ACR-3e13c56e6e524284947626a0011a56c3
ACR-5d261c15deb747c690e37e660b9f6632
ACR-06a6b318952e42b7ac7775ffb55d421c
ACR-1089230644184c89b1b7f09a0654165d
ACR-326e2455682047d99ba1242786260ecc
ACR-153ca5c0afff4280b4ca07469a3a547a
ACR-16637041ad9b42b7aa34f5f26f28f2b0
ACR-a1a99fd6ca7b4c928b3f5cd05bd1fac3
ACR-06849cb5562e450587e2bdd0cc2e07bb
ACR-98a5046a631b4b6ba0b97c9e1df03453
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
