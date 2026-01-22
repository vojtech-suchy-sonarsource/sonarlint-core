/*
ACR-403b0bfc6b7c4055b647c0705c83dcb8
ACR-b8495edeaefb4beebf7e166b1aaf0ba3
ACR-ec6a181fa35e400e961d717bb7d507d6
ACR-8d4a1f2977074d628413ba2411d41400
ACR-f8b0584db9d9486287e5ef1dbe1e25b4
ACR-e66d94e6563846c4b2607cdebd41453e
ACR-fd399ada079841c9bdabd0bb10c852b5
ACR-8ed6d33939fa4d65876ba2081759db7d
ACR-51938ef0a3f54a0baa385bbebe2d9167
ACR-864b49ec27e94129aa4e5f3d0eba61d5
ACR-6ec4aad5d0e340368f124edfe8adc782
ACR-1543d098e0714294974834782d1cfa42
ACR-812e15db70884a80abe3a4999893a2a2
ACR-e5c9d6ae03554b5b9d5c518675b55f0d
ACR-2ade795fb42744ccb5ca560df52276c7
ACR-648564e9a81f4d89a1bdb7ece120533e
ACR-e4c1b8f2ed804280bf0a741fcf383551
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFileExclusionsTests {
  ClientFileExclusions underTest;

  @BeforeEach
  void before() {
    Set<String> glob = Collections.singleton("**/*.js");

    //ACR-a314f7f284d949c0bb4ca8a75b4996b3
    Set<String> files = Set.of(
            new File("dir/file.java").getAbsolutePath(),
            "dir/file-with-slash.java",
            "other\\file-with-backslash.java"
    );

    //ACR-f58090a279554aa4b482a37aa97fca13
    Set<String> dirs = Set.of(
            "src",
            "excluded/dir",
            "another\\excluded\\dir"
    );

    underTest = new ClientFileExclusions(files, dirs, glob);
  }

  @Test
  void should_exclude_with_glob_relative_path() {
    assertThat(underTest.test(new File("dir2/file.js").getAbsolutePath())).isTrue();
    assertThat(underTest.test(new File("dir2/file.java").getAbsolutePath())).isFalse();
  }

  @Test
  void should_exclude_with_glob_absolute_path() {
    assertThat(underTest.test(new File("/absolute/dir/file.js").getAbsolutePath())).isTrue();
    assertThat(underTest.test(new File("/absolute/dir/file.java").getAbsolutePath())).isFalse();
  }

  @Test
  void should_exclude_with_file() {
    assertThat(underTest.test(new File("dir/file2.java").getAbsolutePath())).isFalse();
    assertThat(underTest.test(new File("dir/file.java").getAbsolutePath())).isTrue();
  }

  @Test
  void should_exclude_with_dir() {
    assertThat(underTest.test(new File("dir/class2.java").getAbsolutePath())).isFalse();
    assertThat(underTest.test("src/class.java")).isTrue();
  }

  @Test
  void should_handle_file_exclusions_with_different_separators() {
    assertThat(underTest.test("dir/file-with-slash.java")).isTrue();
    assertThat(underTest.test("other/file-with-backslash.java")).isTrue();

    assertThat(underTest.test("different/dir/file-with-slash.java")).isFalse();
    assertThat(underTest.test("other2/file-with-backslash.java")).isFalse();
  }

  @Test
  void should_handle_directory_exclusions_with_different_separators() {
    assertThat(underTest.test("excluded/dir/some-file.java")).isTrue();
    assertThat(underTest.test("another/excluded/dir/some-file.java")).isTrue();

    assertThat(underTest.test("different/excluded/some-file.java")).isFalse();
    assertThat(underTest.test("another2\\excluded\\dir\\some-file.java")).isFalse();
  }

  @EnabledOnOs(OS.WINDOWS)
  @Test
  void testFileExclusionsWithBackslashes() {
    assertThat(underTest.test("dir\\file-with-slash.java")).isTrue();
  }

  @EnabledOnOs(OS.WINDOWS)
  @Test
  void testDirectoryExclusionsWithBackslashes() {
    assertThat(underTest.test("excluded\\dir\\some-file.java")).isTrue();
  }
}
