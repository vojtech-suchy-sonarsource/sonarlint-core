/*
ACR-a814078ece8a452aa84a5c384b02879a
ACR-b9f4aa202677465c9bd105e48b8bc7d4
ACR-66e63174c76142d48dc741dc51f54bc1
ACR-c5c82f4a325e412086ba009eb3e9c521
ACR-f1b128081309440aaea5de8eccf93254
ACR-086a1024963f4f43abd07ec8d2e5cd1f
ACR-aa18371718f84b95ae392e692299a700
ACR-e217a0b7655f4ad4add5ed20fda76828
ACR-c1db05323ee24cec93012ebff0eea25d
ACR-f0464c2a77d04d49a03e0086b3874147
ACR-23a021a389ee495eb840987c433b4cac
ACR-a3fcd50b30f04241b462438acf048775
ACR-7812cdf798bf4f6c9ff23e6c384827e3
ACR-94d5c9b7684e43a5bc16f699141b1b2f
ACR-25dd495292e940a38e2ad7df7d4d8eb3
ACR-07034e92bd87468b94bcfab4e4a0ed7b
ACR-59b8a4437a60400badbb6e4a5e808d39
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
      //ACR-dbaf1a9924db49c591a13606bd211607
      return SonarLintPathPattern.create(exclusionSettings.sourceInclusions());
    }
    return new SonarLintPathPattern[0];
  }

  SonarLintPathPattern[] prepareTestInclusions() {
    return SonarLintPathPattern.create(computeTestInclusions());
  }

  private String[] computeTestInclusions() {
    if (exclusionSettings.testInclusions().length > 0) {
      //ACR-8b18c7c8e6b84dcf81b4ac890dfc050a
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
