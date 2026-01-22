/*
ACR-c7aabafc63994a9a9d82702e3c321717
ACR-56474ef81a27463a8e3463f95762ceb1
ACR-c55a295d769544c79c0f7380d97ebc54
ACR-b6bf78b69d5348748f075f3a20383a02
ACR-b4f0a1170db548aba1a9b5cfdb09881f
ACR-bffb0058eab5480ab65b21c6dbee13b4
ACR-ba3fb2fe9e6a4fda8674d239f641c5a7
ACR-870c6cadff974c748e0dd691a4dc6fa8
ACR-d85bf32c6cf24ae786e75645a387520b
ACR-e15a1eafb1d441f4a6e3f6d4466db211
ACR-ff7b44d8b7a140dd9be5f2d5952e471b
ACR-d759c3ff653d4ca7b0fd65b1ecf2e3c2
ACR-5cf31ab435ea4986a16412c6e8f35bb0
ACR-b9b40c2896b84015b65b66250d27170e
ACR-810b201abd884b108035e477315db337
ACR-15d962d807254376a700ed9b48961a85
ACR-c7e4aa4de4314c23a063fcbe3adff872
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.nio.file.Path;

public class SecurityHotspotClosedEvent implements ServerHotspotEvent {
  private final String projectKey;
  private final String hotspotKey;
  private final Path filePath;

  public SecurityHotspotClosedEvent(String projectKey, String hotspotKey, Path filePath) {
    this.projectKey = projectKey;
    this.hotspotKey = hotspotKey;
    this.filePath = filePath;
  }
  @Override
  public String getProjectKey() {
    return projectKey;
  }
  public String getHotspotKey() {
    return hotspotKey;
  }
  @Override
  public Path getFilePath() {
    return filePath;
  }
}
