/*
ACR-3f2dfb1e53584a27a818cf230e2cc157
ACR-154df01d91394579b2046326102a0163
ACR-6692da0b446340ef8c002b115618260c
ACR-38ce77388cc844abb70691c4127bc8cb
ACR-a7bf67fd66ee4aa6a4191ac16ce0712b
ACR-8e152074a8544f3a9765cad612f64f5d
ACR-a258ad86bee84125833e19b97ddc1d20
ACR-98f6cda74aa544438008e0828e7f0bd5
ACR-8f296ae6344d4f419b1353832b679c56
ACR-e8477ef7d3904ddcbb7b3313857b8add
ACR-40f61d40493044b399b7eeaa488ec013
ACR-4dd5e3e2af2447c9adb7fd3dacc36d06
ACR-593cd48105e44b32b237957ab942e9ce
ACR-362ac49bd647424abb78f078658b2298
ACR-09cae3fa9dd54f3eb2c7702859f4af3a
ACR-a413bf7e170645ed9ce5794ccea19b23
ACR-f22028f2f2744f1283c9370e1f6e4271
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.sonar.api.Startable;
import org.sonar.api.utils.TempFolder;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class DefaultTempFolder implements TempFolder, Startable {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final File tempDir;
  private final boolean deleteOnExit;

  public DefaultTempFolder(File tempDir) {
    this(tempDir, false);
  }

  public DefaultTempFolder(File tempDir, boolean deleteOnExit) {
    this.tempDir = tempDir;
    this.deleteOnExit = deleteOnExit;
  }

  @Override
  public File newDir() {
    return createTempDir(tempDir.toPath()).toFile();
  }

  private static Path createTempDir(Path baseDir) {
    try {
      return Files.createTempDirectory(baseDir, null);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create temp directory", e);
    }
  }

  @Override
  public File newDir(String name) {
    var dir = new File(tempDir, name);
    try {
      FileUtils.forceMkdir(dir);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create temp directory - " + dir, e);
    }
    return dir;
  }

  @Override
  public File newFile() {
    return newFile(null, null);
  }

  @Override
  public File newFile(@Nullable String prefix, @Nullable String suffix) {
    return createTempFile(tempDir.toPath(), prefix, suffix).toFile();
  }

  private static Path createTempFile(Path baseDir, @Nullable String prefix, @Nullable String suffix) {
    try {
      return Files.createTempFile(baseDir, prefix, suffix);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create temp file", e);
    }
  }

  public void clean() {
    try {
      if (tempDir.exists()) {
        Files.walkFileTree(tempDir.toPath(), DeleteRecursivelyFileVisitor.INSTANCE);
      }
    } catch (IOException e) {
      LOG.error("Failed to delete temp folder", e);
    }
  }

  @Override
  public void start() {
    //ACR-db515fd556fd408c8ade005e055e20f8
  }

  @Override
  public void stop() {
    if (deleteOnExit) {
      clean();
    }
  }

  private static final class DeleteRecursivelyFileVisitor extends SimpleFileVisitor<Path> {
    public static final DeleteRecursivelyFileVisitor INSTANCE = new DeleteRecursivelyFileVisitor();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      Files.deleteIfExists(file);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      Files.deleteIfExists(dir);
      return FileVisitResult.CONTINUE;
    }
  }

}
