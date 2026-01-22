/*
ACR-c5098c8854b444b38e19a65e14badd56
ACR-b76882861c9f4d5286823872896ef285
ACR-623d97e9ab574cbc86dbf2ceadbbaf7b
ACR-cc04dfa805b74f498926df36b7ec9c09
ACR-c6817646326049d5a42c617865310038
ACR-c3aed16d789f4050b4fe0cefc0db3040
ACR-b0dcedfef0e44c66aa8c4e2e57db049e
ACR-292d4371bef24222a4365055a31bc54d
ACR-612049a90fd14a18b4ea61c68fb62553
ACR-02dd192400e248679bc3bc88bf90110a
ACR-14c07e1c71f74658b6382836e37f9ee8
ACR-9de51f692bd143249528d49e44a93068
ACR-2b44e5cb18c848faac048d031c684b61
ACR-dc21344943d9430ea8eb208a407cf3bc
ACR-7ebdca1c6d6f4d4ebaf5b13285892979
ACR-9c68a9e9fde64d4b84b87c22e145dc9e
ACR-e5071955e5e445e3bb41559778672169
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
    //ACR-3ec66317fe1d49a29ea7368c43e9e971
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
