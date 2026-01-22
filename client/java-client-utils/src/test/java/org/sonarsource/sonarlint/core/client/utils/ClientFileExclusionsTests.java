/*
ACR-77408fda645a44cdb37e8d39326e30e3
ACR-ab63b4dc17b44a9ea99f4e66dba6f4be
ACR-cb88040aba3d46c9977083bc04a32524
ACR-0ded16e0d08144eda328222b379462b7
ACR-decb7723e20444b490b1a25a05d82484
ACR-ab3fa449bfd947708686d0f0f6d2ea8b
ACR-263e001cafef456db97ae263c90447a3
ACR-9ea0b6bdd48945bbae5e31253af8fcc3
ACR-6cbfea5995dc4ac19fb5f15748d63f69
ACR-b884a68f525743caadd2b25e845ce45d
ACR-2e912d616a904ee38152d61f0ce869df
ACR-37b2e2c263b442b38554a2a0ff717794
ACR-ff8464b78e3745cd8bccdf32dd2f605b
ACR-7dc326100f4347f6ae4cc6700864bc99
ACR-01ccef0436a9469cb6fd5e1e81ff1db2
ACR-3dd12b45b6624682a0cf27f21a1e178a
ACR-1c21be6e55d845c2ad79998ffbf66d9c
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

    //ACR-c017fef966244f9e80ed4ebf1c37a8ad
    Set<String> files = Set.of(
            new File("dir/file.java").getAbsolutePath(),
            "dir/file-with-slash.java",
            "other\\file-with-backslash.java"
    );

    //ACR-45a19716f13f4e62a9f2e02da9f7ccf8
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
