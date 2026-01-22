/*
ACR-c6d24d46d2944d638df4812c3febf304
ACR-7982496c9f134ba3a3f193ebb850bbdd
ACR-13be9d898a854052b4837a9879e010cb
ACR-90bc792eff4c4f3493e9237d37121f3d
ACR-e4dc541e72424d5180290bd46a76c7fd
ACR-93044941a74e435285b6ff0ef4b8c4ec
ACR-60ab512a638149ab9f18a85db9abd761
ACR-cf5ace3d38884f55a98b6b4994adee03
ACR-96f100d6a8ff4cefacf2bde4a272cb57
ACR-c64231373b32477189af92d89823e3b1
ACR-fd67285d9abc455eaf25cc47f457b418
ACR-994678b60582434b800a7391c8dce5a8
ACR-191760c417f9443c84a0925738ce7ae1
ACR-611113210c2b4664a76e2202f35e82f7
ACR-064c88646f0b4a2d986a9d33de262a44
ACR-30871642b80141a39e92ab390b5022dd
ACR-e413a762778642ef940fe1cbd2691dc4
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
