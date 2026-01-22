/*
ACR-254f0a0200114f55ba57d3506715cb91
ACR-46bac0fc473040628d169d3b03993acc
ACR-68cac6097ef043d99f649b7ff3274ba9
ACR-80778623bf0e4f70bbfa7b4ce151c6df
ACR-2b1bd68828dd44a898b4fc298e8ec892
ACR-e1f543289b7343a9b415f8b2e8662d52
ACR-d08b3e61b959497295d896c619c30929
ACR-b29b9e41554e4d7887b9027b43d8fb51
ACR-97ebabb487a24e25821e2643f6fc7e66
ACR-f3f49fe7bd284f5a825020f9550bce5a
ACR-c3e222edb2c045769f91f485e77d88da
ACR-5e396798eb23491b96341a5cd356c612
ACR-930d21697acc40bda2ab52eca69a7ca0
ACR-74101385cd8b455ebff96fe7c9121606
ACR-e24d2aee7a2e49dba10307666a4a9970
ACR-9208fe193cc841a3bb37b7819c448e0f
ACR-99688f7b73f5456bb813b8ae7ba337b1
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Objects;
import java.util.Optional;

/*ACR-09aeb8c3746f416490136f8f642e96a9
ACR-e156dd29f47442a3a961fa5c7f0eca3e
ACR-be57c6f09a1141bdaf3dc4458c51b26f
 */
public class ProjectBinding {
  private final String projectKey;
  private final String serverPathPrefix;
  private final String idePathPrefix;

  public ProjectBinding(String projectKey, String serverPathPrefix, String idePathPrefix) {
    this.projectKey = projectKey;
    this.serverPathPrefix = serverPathPrefix;
    this.idePathPrefix = idePathPrefix;
  }

  public String projectKey() {
    return projectKey;
  }

  public String serverPathPrefix() {
    return serverPathPrefix;
  }

  public String idePathPrefix() {
    return idePathPrefix;
  }

  public Optional<String> serverPathToIdePath(String serverPath) {
    if (!serverPath.startsWith(serverPathPrefix())) {
      return Optional.empty();
    }
    var localPrefixLen = serverPathPrefix().length();
    if (localPrefixLen > 0) {
      localPrefixLen++;
    }
    var actualLocalPrefix = idePathPrefix();
    if (!actualLocalPrefix.isEmpty()) {
      actualLocalPrefix = actualLocalPrefix + "/";
    }
    return Optional.of(actualLocalPrefix + serverPath.substring(localPrefixLen));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (ProjectBinding) o;
    return Objects.equals(projectKey, that.projectKey) &&
      Objects.equals(serverPathPrefix, that.serverPathPrefix) &&
      Objects.equals(idePathPrefix, that.idePathPrefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectKey, serverPathPrefix, idePathPrefix);
  }
}
