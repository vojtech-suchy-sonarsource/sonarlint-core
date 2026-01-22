/*
ACR-6259883d1bb34858b1a8545a670a7657
ACR-4fee1a98f7ae4664a1dcdf7572d47bfe
ACR-70abb3bd36ec4bedbc13c0ea463aa058
ACR-136724359db2415c8894bbd89bac1223
ACR-90613bcdb8a64e9c9a0d21dc59abf981
ACR-e8ec12e3adb5407f93d37dd77ef9b656
ACR-f9ff5f1819b3449e95774c86beacbd9c
ACR-234b86c460044e05bd7c80091bb8b284
ACR-1cbd34d037f742aea26ed3bd05963cf4
ACR-41bbbe4f61724b5cb7690e698d1ae6f8
ACR-0913d6a827914e809d456bc860fce827
ACR-1b36b44862124e34aaf2ec75f94a2c81
ACR-a1fde94135024f328d9407cfc062ce25
ACR-24da3ca8854a4eeba973d8908c36329e
ACR-ff5409258afc48019e4373ff8db7eddd
ACR-ac8e86627d994c7995b947a933c120ea
ACR-6da671d1da3f4b888219b2fe494845e7
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


