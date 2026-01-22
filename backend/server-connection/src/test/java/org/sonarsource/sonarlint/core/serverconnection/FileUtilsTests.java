/*
ACR-6b13fbc328fe447baca7854484803f97
ACR-66e2f313bcae4f539c56f6f18b352fcc
ACR-e8965836a24d47f1bf9f32a06307a7b9
ACR-147ebcf0e130403a9ad7cf55bfcc53a4
ACR-64ba97b40d614874b0045244deaa0c95
ACR-5fcde07f5c25419a894c8fbbd8e4c38c
ACR-6ff92dab07374936ba9ca4119843886f
ACR-f6948acd4cee4647a732c59003a67d84
ACR-fd61d6a3c765456e81c672be10d21044
ACR-73c2db44fa5c4bb7aeca4be844572b83
ACR-f81d76bb171a4b59b011f1c6e77e5ccd
ACR-4543f8f648fc43a890e1ba97ce6946b8
ACR-c95330f9ed6149209f0c2514dabb6ef2
ACR-d4af985f043b44048131b868ff33f4be
ACR-40877d1863084702a92aa21db5204804
ACR-fb37d8aa0251409ead4563cca6b8d04d
ACR-e31b2d2cc8754cefad3f9d1fd3a6b05d
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
