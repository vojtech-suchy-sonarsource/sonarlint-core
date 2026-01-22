/*
ACR-73647aabb79948b78e355d36d990efce
ACR-6927afbb62eb4ebf8803cbc3d41887a7
ACR-9eb6a3626f4447b99d9953a3df2d5b8d
ACR-e308ed5f46044267bddcd80be69e43eb
ACR-13f97ca11af44737868f17715702eeab
ACR-6a168215132b4746812fc55d053afece
ACR-310eb9ff3e484946ab84abbaa46cc602
ACR-98da54dbe7474cd3bb35efcc7cea3dbf
ACR-589ee4612f5b41fba6733f247e392270
ACR-9774cf0b1b3740bda79dd0cf34376b32
ACR-99aeae5e7cd7484c9a3d6c0628477926
ACR-1aee8b206ff84d86a42ed3807923215c
ACR-61ec1d7097684cfe8b8bd05c0726b97e
ACR-d0ea153d906c48999364bdde26d3afbe
ACR-815d7040a7e74268bf3aabf0ca2afc22
ACR-8e89f689e603436998023677b36fd0c8
ACR-0af203c84a2b4bee857043487160a6fd
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
