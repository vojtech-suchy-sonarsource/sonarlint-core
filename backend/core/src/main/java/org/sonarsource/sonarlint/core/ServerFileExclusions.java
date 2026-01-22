/*
ACR-c609e12ce12e41c29de1b3111b1b9813
ACR-c548d9e3b38a4f38aa6363ba79046433
ACR-19745885d22a4c6ab1f7351bd682b7fd
ACR-a4615bef48ca4995abd7c9d796b30d67
ACR-313db3771031478eba284ee7608c94b9
ACR-8c8a834a9d8c412f8d0613828e7c1e30
ACR-936c9fed250149ddabdc59a4c7aad464
ACR-c008945a0f6f47d09f5d9633c683b706
ACR-fe24f32702364a9785648993002f870d
ACR-3f29b8da8f794dcd896cc9f394b34cc2
ACR-309acd2bec194c278887f4972d3a0954
ACR-07ee25543afa43ef9e452a0f632fd1f9
ACR-284f0579cc2748379ad5115e469c56ba
ACR-6cf9b0f89229419988dd963b935baf70
ACR-daec571caee34818a052760984a22653
ACR-2d3e8514ebb9427582a0bebd4968c96f
ACR-6b7f30d1ad9b4340b4be1d6c82bcdc25
 */
package org.sonarsource.sonarlint.core;

import org.apache.commons.lang3.ArrayUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Configuration;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonarsource.sonarlint.core.analysis.container.analysis.SonarLintPathPattern;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class ServerFileExclusions {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final FileExclusions exclusionSettings;

  private SonarLintPathPattern[] mainInclusions;
  private SonarLintPathPattern[] mainExclusions;
  private SonarLintPathPattern[] testInclusions;
  private SonarLintPathPattern[] testExclusions;

  public ServerFileExclusions(Configuration configuration) {
    this.exclusionSettings = new FileExclusions(configuration);
  }

  public void prepare() {
    mainInclusions = prepareMainInclusions();
    mainExclusions = prepareMainExclusions();
    testInclusions = prepareTestInclusions();
    testExclusions = prepareTestExclusions();
    log("Server included sources: ", mainInclusions);
    log("Server excluded sources: ", mainExclusions);
    log("Server included tests: ", testInclusions);
    log("Server excluded tests: ", testExclusions);
  }

  private static void log(String title, SonarLintPathPattern[] patterns) {
    if (patterns.length > 0) {
      LOG.debug(title);
      for (SonarLintPathPattern pattern : patterns) {
        LOG.debug("  {}", pattern);
      }
    }
  }

  public boolean accept(String relativePath, InputFile.Type type) {
    SonarLintPathPattern[] inclusionPatterns;
    SonarLintPathPattern[] exclusionPatterns;
    if (InputFile.Type.MAIN == type) {
      inclusionPatterns = mainInclusions;
      exclusionPatterns = mainExclusions;
    } else if (InputFile.Type.TEST == type) {
      inclusionPatterns = testInclusions;
      exclusionPatterns = testExclusions;
    } else {
      throw new IllegalArgumentException("Unknown file type: " + type);
    }

    if (inclusionPatterns.length > 0) {
      var matchInclusion = false;
      for (SonarLintPathPattern pattern : inclusionPatterns) {
        matchInclusion |= pattern.match(relativePath);
      }
      if (!matchInclusion) {
        return false;
      }
    }
    for (SonarLintPathPattern pattern : exclusionPatterns) {
      if (pattern.match(relativePath)) {
        return false;
      }
    }
    return true;
  }

  SonarLintPathPattern[] prepareMainInclusions() {
    if (exclusionSettings.sourceInclusions().length > 0) {
      //ACR-a6b53b47c1dd4214ae2d7185d8dfdf93
      return SonarLintPathPattern.create(exclusionSettings.sourceInclusions());
    }
    return new SonarLintPathPattern[0];
  }

  SonarLintPathPattern[] prepareTestInclusions() {
    return SonarLintPathPattern.create(computeTestInclusions());
  }

  private String[] computeTestInclusions() {
    if (exclusionSettings.testInclusions().length > 0) {
      //ACR-0d18920c6739451daa0b43e6e60e1758
      return exclusionSettings.testInclusions();
    }
    return ArrayUtils.EMPTY_STRING_ARRAY;
  }

  SonarLintPathPattern[] prepareMainExclusions() {
    var patterns = ArrayUtils.addAll(
      exclusionSettings.sourceExclusions(), computeTestInclusions());
    return SonarLintPathPattern.create(patterns);
  }

  SonarLintPathPattern[] prepareTestExclusions() {
    return SonarLintPathPattern.create(exclusionSettings.testExclusions());
  }

}
