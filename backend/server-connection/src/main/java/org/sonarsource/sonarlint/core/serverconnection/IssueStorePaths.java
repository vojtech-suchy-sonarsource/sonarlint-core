/*
ACR-85b2625ebfcb4ac690f8a9e1f4460762
ACR-9ecccd13619045f7b51307cf4d21ad02
ACR-a56ba761c12e4718b27bd738fac6ec07
ACR-687b9535ed804d04bf467dbec1c4f4cc
ACR-eb0e01daa3794f99b5bcd2cbd406d3db
ACR-4504a38bf6134fcfa3cfd3aea0dffd4b
ACR-9d2b57c3d8c74630b7a239e35d91954e
ACR-6ce2967b3b004f759522f5e5cf38d4ee
ACR-97a0e95265b54745b697e9862349d25a
ACR-fa52c07305e34cc7a8ade1b0a18c777f
ACR-cdaac5ba9bc049d88b0b3ecf043b2742
ACR-fcb05be3414b4a79b99e416e66429f71
ACR-1ea42ca6f9594f2699c4bad200411e7a
ACR-f236e61d60364b1d9719c046a45b4e47
ACR-18b941f283604b40b047910a2e285101
ACR-cbb4fc4afeb44e3ca228248b2d0c0c4d
ACR-bf193ccbe3d64204a22b863fe26fc1f2
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.CheckForNull;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.toSonarQubePath;

public class IssueStorePaths {

  private IssueStorePaths() {

  }

  @CheckForNull
  public static String idePathToFileKey(ProjectBinding projectBinding, Path ideFilePath) {
    var serverFilePath = idePathToServerPath(projectBinding, ideFilePath);

    if (serverFilePath == null) {
      return null;
    }
    return componentKey(projectBinding, serverFilePath);
  }

  public static String componentKey(ProjectBinding projectBinding, Path serverFilePath) {
    return componentKey(projectBinding.projectKey(), serverFilePath);
  }

  public static String componentKey(String projectKey, Path serverFilePath) {
    return projectKey + ":" + toSonarQubePath(serverFilePath);
  }

  @CheckForNull
  public static Path idePathToServerPath(ProjectBinding projectBinding, Path ideFilePath) {
    return idePathToServerPath(Paths.get(projectBinding.idePathPrefix()), Paths.get(projectBinding.serverPathPrefix()), ideFilePath);
  }

  @CheckForNull
  public static Path idePathToServerPath(Path idePathPrefix, Path serverPathPrefix, Path ideFilePath) {
    Path commonPart;
    if (!idePathPrefix.toString().isEmpty()) {
      if (!ideFilePath.startsWith(idePathPrefix)) {
        return null;
      }
      commonPart = idePathPrefix.relativize(ideFilePath);
    } else {
      commonPart = ideFilePath;
    }
    if (!serverPathPrefix.toString().isEmpty()) {
      return serverPathPrefix.resolve(commonPart);
    } else {
      return commonPart;
    }
  }

}
