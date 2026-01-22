/*
ACR-461e754024f64b0b948e595b985cab7a
ACR-da8552ab43ba41699398ceb8dd3f8dd5
ACR-b909beb419b04e0696e63855cc09e16c
ACR-363ed800f0c34a7a9a5f726b2fe24999
ACR-82ba9bf4b400455091f9756d5b0f81ee
ACR-e02f9d2376ac446da01914e379f9df9c
ACR-a9c368ade3f04fedb33a9177f2cca251
ACR-2e2402437b064007b33e1d67f868c099
ACR-d76b17fa1f1a458f99ac8a02dc81a147
ACR-346035e1525e4b95900f95fd9b5328a2
ACR-f747001c292341cf8ac2059d4eaf4ad3
ACR-3502c98dd71e45629c82408af3a9552e
ACR-4118f2802ada4791981383172ef5ba14
ACR-8a8de379336741f8b00f41a577d8b109
ACR-cd33d9b899e8484db45f75b5fa14b5ac
ACR-c4075978ac614a7c9974c0d59c08f1b8
ACR-2ca8b3a966dc4bb295a6c4480f25fc3d
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
