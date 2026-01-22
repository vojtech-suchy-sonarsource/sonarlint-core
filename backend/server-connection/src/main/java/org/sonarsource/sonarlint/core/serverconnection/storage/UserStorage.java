/*
ACR-9e210bd72daa4c078c0b4997fe99eb13
ACR-92d00a8ef1374167848a93bb3e77c259
ACR-62cd2b646fdb4cbd8a881f3729824513
ACR-3f7e9f02f82141b98eca16caeeeec4f9
ACR-3ad279aa3c0a49edbe95816f73ecfc65
ACR-514a6497b30c48928ed54cd99f4b32e5
ACR-f33337a49def4ab3a01d139af63ce589
ACR-f327a4171302486ca9649035bd2d6e49
ACR-8700ba9ea94746549824865e54901d82
ACR-0ccd67bc2a0e4fccac8493f5ad09c8eb
ACR-e226642ad8f044fe852afc64f0c0d5cb
ACR-36080963d2044072b1ffe6e8de1df0a6
ACR-d8297827d70e4ac9b54bf4f94c948f5f
ACR-eb049c3d9b894ceba7c342b3a39d151c
ACR-ae20b4cbf601444ca76020e1612cdd8c
ACR-4c53bfc053f446ddb25fca95d0e1b662
ACR-c0be5dcc134a4d569770581af5bfd8bb
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class UserStorage {
  public static final String USER_PB = "user.pb";
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public UserStorage(Path rootPath) {
    this.storageFilePath = rootPath.resolve(USER_PB);
  }

  public void store(String userId) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var user = Sonarlint.User.newBuilder().setId(userId).build();
    LOG.debug("Storing user in {}", storageFilePath);
    rwLock.write(() -> writeToFile(user, storageFilePath));
    LOG.debug("Stored user");
  }

  public Optional<String> read() {
    return rwLock.read(() -> Files.exists(storageFilePath) ? Optional.of(ProtobufFileUtil.readFile(storageFilePath, Sonarlint.User.parser()).getId())
      : Optional.empty());
  }
}


