/*
ACR-2f7f465904664f6283d6a463486a733b
ACR-426b3cde39c04adabda9af02a84b7997
ACR-2dc6d2644a3e46aab2783d35b8ed4735
ACR-fbeac02cff534770a49a930a2ca260e2
ACR-cfc54c09483d47479d58539cad8c1c86
ACR-69a4d7d545094c6b90badebbbab55813
ACR-31e60065db5b485180e37cfdde82c3fa
ACR-aa33d1ff4cf54febaef6147343f5637d
ACR-e84b97235d2a404894850c45867e6d78
ACR-d6f86a7b57e34211a7170c02823020b9
ACR-833787c57ca14dc49ee37111cb7b7629
ACR-62c9215770b348d0bb07eab04fe92fcb
ACR-cf4d172181f340e092f40b5ac8948eea
ACR-d7825929957b4b76bd35e9cfeef76801
ACR-b5867b6d4ac6492cba308ca723f6c2df
ACR-9a6527ca5bcf4d4e95d2ec5b2d8691ea
ACR-21c93f2375754f13a1f5aa3048e01d22
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import java.nio.file.Path;

public class ReopenAllIssuesForFileParams {

  private final String configurationScopeId;
  private final Path ideRelativePath;

  public ReopenAllIssuesForFileParams(String configurationScopeId, Path ideRelativePath) {
    this.configurationScopeId = configurationScopeId;
    this.ideRelativePath = ideRelativePath;
  }

  public Path getIdeRelativePath() {
    return ideRelativePath;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
