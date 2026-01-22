/*
ACR-9816b7e6cbf448bfa9d781baaab26e09
ACR-bacd4e14f8c345458ac16212f7e659df
ACR-a2494984aade494087e043477bdb8841
ACR-61c5d9c6de0d4d8fb21a176e91649cfb
ACR-3e7a79f2169e4b92ba1af463081d79e3
ACR-f5ed03dc68964a30a12f603a5ce71a4a
ACR-009fc3f65ec74ffc844555df457c88dc
ACR-2c429957fec143ab988a5dd95d57ee08
ACR-1848f3d49f3d4468981320e4eccaf1de
ACR-2061cf293df24ae4a9a5f85ec103cc38
ACR-a1e96c946a63492bbe87f2308adc0581
ACR-787d92feddba456aa144f608e8e844ea
ACR-9f4511cb038949cda343e42c3a5cdf29
ACR-852b5a29a02e4dafb7643b814601db3f
ACR-d9acd88ebdcf452e808daf459ad56149
ACR-0b3b467323764c53a2926d364eb673bc
ACR-a76c859352d94d6eae20058ea6f32ff5
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.List;
import java.util.stream.Collectors;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;

public class Flow {
  private final List<org.sonarsource.sonarlint.core.analysis.api.IssueLocation> locations;

  public Flow(List<IssueLocation> issueLocations) {
    this.locations = issueLocations.stream()
      .map(i -> new DefaultLocation(
        i.inputComponent().isFile() ? ((SonarLintInputFile) i.inputComponent()).getClientInputFile() : null,
        i.textRange(),
        i.message()))
      .collect(Collectors.toList());
  }

  public List<org.sonarsource.sonarlint.core.analysis.api.IssueLocation> locations() {
    return locations;
  }
}
