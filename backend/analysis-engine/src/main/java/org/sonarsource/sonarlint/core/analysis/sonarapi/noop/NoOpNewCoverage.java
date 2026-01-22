/*
ACR-84e18ebef9ad4e2ba3782da101ce4689
ACR-c55e2b0415694b6cb31554838901413c
ACR-0f5f4de767914704b4f4e4537ad93dca
ACR-801f198a12564b49ad33a87717e80bf1
ACR-c7f158f21a504416b290a78edddaae86
ACR-9c3893dcd7bc46beb33d7841a8e8e995
ACR-b3cee819b3734cb087b08b9019e1f8ad
ACR-62d2b925ec1e4a7584e448202c57286b
ACR-a3c4d50f0dba4526a68311fb4bf11bbf
ACR-654f3554104a46b4bfbdca48d4e3e260
ACR-8a8077360143459aabd078e2320ba9c9
ACR-2cfb3572a7da458c903bee8d58aadd3f
ACR-77dc4a07e72a46589251c3e3af22d87b
ACR-e550a68ac94b4a4eabab8f840aff410b
ACR-9c697efa378b4d97bf87acdbcd09f5ef
ACR-3ed1a0a1b3b14abdbc62d44c9dbe216f
ACR-dc173a901e8a4f85898e5105af95e2d6
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

public class NoOpNewCoverage implements NewCoverage {

  @Override
  public NewCoverage onFile(InputFile inputFile) {
    //ACR-768a871088fb4a27b96b0f8ae738b010
    return this;
  }

  @Override
  public NewCoverage lineHits(int line, int hits) {
    //ACR-6d48d2470cc04058b1e9241bec1f4286
    return this;
  }

  @Override
  public NewCoverage conditions(int line, int conditions, int coveredConditions) {
    //ACR-da6dcf88eea44a93a0f092975807d558
    return this;
  }

  @Override
  public void save() {
    //ACR-bacb8e7274b944768b42d5465e4c8df8
  }
}
