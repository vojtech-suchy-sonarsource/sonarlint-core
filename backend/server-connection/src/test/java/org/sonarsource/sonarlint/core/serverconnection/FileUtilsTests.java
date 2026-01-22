/*
ACR-fff562b1d8cc4c4f8d1a5d1de9e4b952
ACR-baa24fca3d084b84929a39037d9af9c9
ACR-68bbcefa6e434524aa31913a59c18ecb
ACR-cfb1a440ec844c2bafdece86564b5131
ACR-a844397d618e4264925542f64aa178a6
ACR-b69bc823fb124a3385db19afb69013ca
ACR-34fdf6a7c2c145ef9ebabe42bd08e09c
ACR-c84da91a006247329de10311de9bbfb5
ACR-a240b2cae8f44247a3732895048b4054
ACR-6fcb90db88834a0cb272788738ed81d5
ACR-7bb4f1aa5f244b0fae4758b2ee52fd7f
ACR-68bddb45156f472a8089678d8ff62023
ACR-b31a8917d6374944a968bb4a1c55bf58
ACR-f85a4f4a949545c588c7729386c976d7
ACR-3170118a73574f01a6bd483b1c2594e9
ACR-b6bd54dc4cd0455497aa5356a8d1f35e
ACR-15101a00f95d4176b60784450189ce24
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FileUtilsTests {

  @Test
  void deleteRecursively(@TempDir Path dir) {
    var fileInDir = createNewFile(dir, "dummy");
    assertThat(fileInDir).isFile();

    FileUtils.deleteRecursively(dir);
    assertThat(fileInDir).doesNotExist();
    assertThat(dir).doesNotExist();
  }

  @Test
  void deleteRecursively_should_ignore_nonexistent_dir(@TempDir Path temp) {
    var dir = new File(temp.toFile(), "nonexistent");
    assertThat(dir).doesNotExist();

    FileUtils.deleteRecursively(dir.toPath());
  }

  @Test
  void deleteRecursively_should_delete_file(@TempDir Path temp) {
    var file = createNewFile(temp, "foo.txt");
    assertThat(file).isFile();

    FileUtils.deleteRecursively(file.toPath());
    assertThat(file).doesNotExist();
  }

  @Test
  void deleteRecursively_should_delete_deeply_nested_dirs(@TempDir Path basedir) {
    var deeplyNestedDir = basedir.resolve("a").resolve("b").resolve("c");
    assertThat(deeplyNestedDir.toFile().isDirectory()).isFalse();
    FileUtils.mkdirs(deeplyNestedDir);

    FileUtils.deleteRecursively(basedir);
    assertThat(basedir.toFile()).doesNotExist();
  }

  @Test
  void mkdirs(@TempDir Path temp) {
    var deeplyNestedDir = temp.resolve("a").resolve("b").resolve("c");
    assertThat(deeplyNestedDir).doesNotExist();
    if (deeplyNestedDir.toFile().mkdir()) {
      throw new IllegalStateException("creating nested dir should have failed");
    }

    FileUtils.mkdirs(deeplyNestedDir);
    assertThat(deeplyNestedDir).isDirectory();
  }

  @Test
  void mkdirs_should_fail_if_destination_is_a_file(@TempDir Path temp) {
    var file = createNewFile(temp, "foo").toPath();
    assertThrows(IllegalStateException.class, () -> FileUtils.mkdirs(file));
  }

  @Test
  void always_retry_at_least_once() throws IOException {
    var runnable = mock(FileUtils.IORunnable.class);
    FileUtils.retry(runnable, 0);
    verify(runnable, times(1)).run();
  }

  @Test
  void retry_on_failure() throws IOException {
    int[] count = {0};
    FileUtils.IORunnable throwOnce = () -> {
      count[0]++;
      if (count[0] == 1) {
        throw new AccessDeniedException("foo");
      }
    };
    FileUtils.retry(throwOnce, 10);
    assertThat(count[0]).isEqualTo(2);
  }

  private File createNewFile(Path basedir, String filename) {
    var path = basedir.resolve(filename);
    try {
      return Files.createFile(path).toFile();
    } catch (IOException e) {
      fail("could not create file: " + path);
    }
    throw new IllegalStateException("should be unreachable");
  }
}
