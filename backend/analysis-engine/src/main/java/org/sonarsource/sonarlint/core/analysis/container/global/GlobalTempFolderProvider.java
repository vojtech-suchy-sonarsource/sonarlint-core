/*
ACR-b971d549121f49a09315a252b8c375a8
ACR-1ca0554c5cd14053995291706810d2c7
ACR-3960592964894a10a7be12304b4f8f06
ACR-40214fc8bec748199e5d5bdaaa4b18e0
ACR-a4a833a541a746e5803a1a28ae05f472
ACR-262b7431cb26420d95ec197342a3dfee
ACR-7801fd543cc944d3a780c45bdb4fc3b0
ACR-18fffcee5cec4a80a2f86519e19bcc5c
ACR-616a492316c147f2908f363f91ece077
ACR-29e365835b1e4c07b96b0f827274cd53
ACR-4f1ca3cf37684c23af75a953e444fbf2
ACR-0ed1e1caefe44844b07a3e362737f5fc
ACR-1d3c76c7f53c49119fd6f8a72c6b7705
ACR-868ab7e7f63941ec9f0672e9a9d4cd23
ACR-c3370fc53377485fa310900e0ef26c3e
ACR-b30e7dace73d466c88bbb78d0ee6d778
ACR-d1becbfa20ba4d14806e8bfeea1288d9
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

      //ACR-ca997842d1034084b15c7060b7d00f1c
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
