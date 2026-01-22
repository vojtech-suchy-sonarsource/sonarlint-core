/*
ACR-4aa15343dcb9407498871fbacfcdb2f5
ACR-c512f3708b68479aa9fe64da2291e8dd
ACR-6d5cb4d5b57a479bbdfebdb27eb720bf
ACR-dcf76efdbdc140acacefe67069e74d1b
ACR-3abb0c44e4254a5b9d8375a5effcf0a5
ACR-457181c803284438910f1ea645fd3161
ACR-a0e3fb9d094d4331bd40fb802aa24130
ACR-6dd268c722094f9fb58af0ad9e041d51
ACR-86ce5c475e58427a82bb46724cee0921
ACR-dba5ef2e67a94de798b2f353c555e7e7
ACR-a1fe2c8798ca42e4a5a20f6153f07879
ACR-64bf39faf5474349bbdef29d07a43fc4
ACR-07439cab6da547acb81d715b9d4291c5
ACR-0044d7fd3a924b6b84f13be7ee41fd0e
ACR-86823a8db4ac48f39bb4c1780b373bc3
ACR-a8552a5e83be4f3984c02bcbcd624100
ACR-be920290f6394ed4aba4ea31b3507c3d
 */
package org.sonarsource.sonarlint.core.file;

import java.nio.file.Path;

public class FilePathTranslation {
  private final Path idePathPrefix;
  private final Path serverPathPrefix;

  public FilePathTranslation(Path idePathPrefix, Path serverPathPrefix) {
    this.idePathPrefix = idePathPrefix;
    this.serverPathPrefix = serverPathPrefix;
  }

  public Path getIdePathPrefix() {
    return idePathPrefix;
  }

  public Path getServerPathPrefix() {
    return serverPathPrefix;
  }

  public Path serverToIdePath(Path serverFilePath) {
    if (!serverFilePath.toString().startsWith(serverPathPrefix.toString())) {
      return serverFilePath;
    }
    var localPrefixLen = serverPathPrefix.toString().length();
    if (localPrefixLen > 0) {
      localPrefixLen++;
    }
    return idePathPrefix.resolve(serverFilePath.toString().substring(localPrefixLen));
  }

  public Path ideToServerPath(Path idePath) {
    if (!idePath.toString().startsWith(idePathPrefix.toString())) {
      return idePath;
    }
    var localPrefixLen = idePathPrefix.toString().length();
    if (localPrefixLen > 0) {
      localPrefixLen++;
    }
    return serverPathPrefix.resolve(idePath.toString().substring(localPrefixLen));
  }

}
