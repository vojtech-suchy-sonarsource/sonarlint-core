/*
ACR-cc851f2d162c4d60a282ce6ae344e72e
ACR-e2c317af616c462287e8b2b49f46083d
ACR-46ad0720c2584789b877e7f3678fd1ff
ACR-c3b801f77e7c4b85b074ac27dd86470c
ACR-ce516fb452294a67ba1ae6c8ab5ff233
ACR-e2bd162ab17148648ba50fae8ead67c9
ACR-93fd6e57c1d54157b362bc007af8dcfc
ACR-6951621e455447cf87bae73ecb055802
ACR-5a92215fcd084c75a9c7d53abe0c3faf
ACR-9cf2ede33aa54a4e9846ea828bd486da
ACR-52ce7cbac85243e1a7b5588ba6bc3c7c
ACR-52c18567b60744da8780d7743319d690
ACR-2b2b51dc66b74cb78c39f2a07f85712d
ACR-86c7207d9d384881972272b86dbfae40
ACR-1d01c13cd9234afeb6b702c22cb132fd
ACR-9c4ac07319e149c98daea0b7b084d577
ACR-37f884a9b07d405b895a75bb27e7958c
 */
package org.sonarsource.sonarlint.core.file;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FilePathTranslationTests {

  @Test
  void serverToPathTranslation() {
    var underTest = new FilePathTranslation(Path.of("/foo"), Path.of("/bar"));

    assertThat(underTest.serverToIdePath(Path.of("/baz"))).isEqualTo(Path.of("/baz"));
    assertThat(underTest.serverToIdePath(Path.of("/bar/baz"))).isEqualTo(Path.of("/foo/baz"));
  }

  @Test
  void serverToPathTranslationWhenPrefixIsEmpty() {
    var underTest = new FilePathTranslation(Path.of("ide"), Path.of(""));

    assertThat(underTest.serverToIdePath(Path.of("baz"))).isEqualTo(Path.of("ide/baz"));
    assertThat(underTest.serverToIdePath(Path.of("bar/baz"))).isEqualTo(Path.of("ide/bar/baz"));
  }

  @Test
  void ideToServerPathTranslation() {
    var underTest = new FilePathTranslation(Path.of("/foo"), Path.of("/bar"));

    assertThat(underTest.ideToServerPath(Path.of("/baz"))).isEqualTo(Path.of("/baz"));
    assertThat(underTest.ideToServerPath(Path.of("/foo/baz"))).isEqualTo(Path.of("/bar/baz"));
  }

  @Test
  void ideToServerPathTranslationWhenPrefixIsEmpty() {
    var underTest = new FilePathTranslation(Path.of(""), Path.of("server"));

    assertThat(underTest.ideToServerPath(Path.of("baz"))).isEqualTo(Path.of("server/baz"));
    assertThat(underTest.ideToServerPath(Path.of("foo/baz"))).isEqualTo(Path.of("server/foo/baz"));
  }

}
