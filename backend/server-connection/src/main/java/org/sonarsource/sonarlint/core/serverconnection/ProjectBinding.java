/*
ACR-1a1bfc28273f4dd3872e448da3954481
ACR-f0766b6637ed4d0d84ae82f874e64349
ACR-aafe64fe883a42f4a5fc1568ac19de93
ACR-c22c0fd9f69b4bc4ac274390cfb507f1
ACR-2dab709fce61446384cb3feda5747e02
ACR-6145d9afde86497099e32f6d52b00c67
ACR-13c19374ab9641aaa915f7d8de83a1b6
ACR-a70d7e643808401bb19ce954402a08dd
ACR-cee39fac8912474ca6dd2578ad82cd05
ACR-aa645b08510343d784460cc73461a1c8
ACR-ae07721329a04b339bc745dfdf44a896
ACR-9956edf1c1a4407e95e767a18a868586
ACR-e0b0bfddb7824eea9fc43d35185f695e
ACR-d0d7c114f5ee4259a3601033098e9dca
ACR-7c3ceb1601d44cae967e8e989ea927f2
ACR-dee8d7ba8510445ea768877cd07dcffc
ACR-7bb712169caf43fca882d41e7fcd6851
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Objects;
import java.util.Optional;

/*ACR-38664663d5a64a0f8208d7522f4081a3
ACR-87bf257ff7bd4a8ea2662332aeae4edf
ACR-f0e3ab063b854fb78ef73afd67692606
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
