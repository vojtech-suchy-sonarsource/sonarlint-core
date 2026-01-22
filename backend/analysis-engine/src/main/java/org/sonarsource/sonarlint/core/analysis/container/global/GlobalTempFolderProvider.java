/*
ACR-e7e8b90b8f93466b976308e23fd1c5a7
ACR-24b1b46f6cfb44c19d73b583139140b6
ACR-2f7f7f00cca649449b40b24ca78dcc20
ACR-248d0f8ec4aa40e6abefbc240b539e9c
ACR-d696d53f3a6e41d99e18bb008ef4db74
ACR-1ed840dec1f34d4c88ff0c156b11fbf1
ACR-32ecd1cfb0ff40a780988eb3d10bb655
ACR-07d3b4a5596b4138b52a68b7a7051192
ACR-cbf87efdffb84322ba94c6b93d7e493e
ACR-da306e359833498c854502fe4a2ffc6f
ACR-5433e47e3d414ab3adde7d2b885404c7
ACR-1a0fa2d885cf44179c4f3216745d4cde
ACR-355461e36240458d906bbf014b72b904
ACR-92d31877fafb47b39fec6ddc452d03ea
ACR-ae2b503b5cfa4cd18ec8358e3b3b19f9
ACR-4815e90ba19e4cc696428eaf4ad35c64
ACR-e0b9af2ae7944efaa6f84826014ce087
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.springframework.context.annotation.Bean;

public class GlobalTempFolderProvider {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final long CLEAN_MAX_AGE = TimeUnit.DAYS.toMillis(7);
  private static final String TMP_NAME_PREFIX = ".sonarlinttmp_";

  private GlobalTempFolder tempFolder;

  @Bean("GlobalTempFolder")
  public GlobalTempFolder provide(AnalysisSchedulerConfiguration globalConfiguration) {
    if (tempFolder == null) {
      tempFolder = cleanAndCreateTempFolder(globalConfiguration.getWorkDir());
    }
    return tempFolder;
  }

  private static GlobalTempFolder cleanAndCreateTempFolder(Path workingPath) {
    try {
      cleanTempFolders(workingPath);
    } catch (IOException e) {
      LOG.error(String.format("failed to clean global working directory: %s", workingPath), e);
    }
    var tempDir = createTempFolder(workingPath);
    return new GlobalTempFolder(tempDir.toFile(), true);
  }

  private static Path createTempFolder(Path workingPath) {
    try {
      Files.createDirectories(workingPath);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create working path: " + workingPath, e);
    }

    try {
      return Files.createTempDirectory(workingPath, TMP_NAME_PREFIX);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create temporary folder in " + workingPath, e);
    }
  }

  private static void cleanTempFolders(Path path) throws IOException {
    if (Files.exists(path)) {
      try (var stream = Files.newDirectoryStream(path, new CleanFilter())) {
        for (Path p : stream) {
          FileUtils.deleteQuietly(p.toFile());
        }
      }
    }
  }

  private static class CleanFilter implements DirectoryStream.Filter<Path> {
    @Override
    public boolean accept(Path path) throws IOException {
      if (!Files.isDirectory(path) || !path.getFileName().toString().startsWith(TMP_NAME_PREFIX)) {
        return false;
      }

      var threshold = System.currentTimeMillis() - CLEAN_MAX_AGE;

      //ACR-6c8970c30cba408f89ed8b8b9b12c0d8
      BasicFileAttributes attrs;

      try {
        attrs = Files.readAttributes(path, BasicFileAttributes.class);
      } catch (IOException ioe) {
        LOG.error(String.format("Couldn't read file attributes for %s : ", path), ioe);
        return false;
      }

      var creationTime = attrs.creationTime().toMillis();
      return creationTime < threshold;
    }
  }

}
