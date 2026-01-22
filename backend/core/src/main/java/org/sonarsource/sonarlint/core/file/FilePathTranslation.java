/*
ACR-1b1b551d0b4b48b494b3dc328d0b8447
ACR-0d2c744abec547b0a40775096ddd1378
ACR-51878ce5c0c040878b3d892ac3fb851f
ACR-696b1fea84c94652b6d38bc861b0cf5c
ACR-f802de5d37ef47198d220d996d31c4e4
ACR-654e06935c53485d8db24493224f60ed
ACR-4d67921150144801ac5edd2c001938ec
ACR-67ad9221dd474fd4977c935b58a81a80
ACR-0d210e0a55d04c26a3376eb99592d09e
ACR-f19f8125dd394823b34f7aad69d0b6fe
ACR-b6446278c12d43b188b2f62f79ef7220
ACR-4759aa743ded4410bfc168915b10d17e
ACR-39d8cb0508cb4adcb2f035657eb13428
ACR-a57a3654c67046b88b5f7ef32510c41f
ACR-2386d3c19c9c402ab94925f205e3512a
ACR-fb1229a6dba8487194f56b3855ce05ea
ACR-3676b8fc3c0d448cb59b7ef3f940cc0d
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
