/*
ACR-3fd01bbfa7044aadb9edaf937375854a
ACR-42e708dec77040cdb450b57ddf226380
ACR-f6a8e80f3e5346c8a09f60994bc09a6e
ACR-76d845afaa114210b5f81a82eafcdffb
ACR-a42d16bdb5cb49f588ded396aa30d08d
ACR-62158db9c3724d4cab448b4ca84fbc87
ACR-a823678331eb4bc1b767be07c6f1a066
ACR-784178d9826e472dae78fadc91b01f5f
ACR-0ed3a7a3067948dfae33c24b34e03d4a
ACR-09bcc8aadc13419b89624131d2c2a1af
ACR-a09372baed3a40418214da433f64b18a
ACR-4eb247f10f3b412284f28408afcd1ef1
ACR-3fa65fdae9034fc190340f75f9374c02
ACR-92243fd094bb4fda8fe3b8be50378eb9
ACR-38dd73b12524442397941c5479c05223
ACR-0b7905d30dfa458b8a8662e8a74e024d
ACR-b1685b69f65e46a99b20952f0aa5d468
 */
package org.sonarsource.sonarlint.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.SonarLintUserHome;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;

public class UserPaths {

  public static UserPaths from(InitializeParams initializeParams) {
    var userHome = computeUserHome(initializeParams.getSonarlintUserHome());
    createFolderIfNeeded(userHome);
    var workDir = Optional.ofNullable(initializeParams.getWorkDir()).orElse(userHome.resolve("work"));
    createFolderIfNeeded(workDir);
    var storageRoot = Optional.ofNullable(initializeParams.getStorageRoot()).orElse(userHome.resolve("storage"));
    createFolderIfNeeded(storageRoot);
    return new UserPaths(userHome, workDir, storageRoot, initializeParams.getTelemetryConstantAttributes().getProductKey());
  }

  static Path computeUserHome(@Nullable String clientUserHome) {
    if (clientUserHome != null) {
      return Paths.get(clientUserHome);
    }
    return SonarLintUserHome.get();
  }

  private static void createFolderIfNeeded(Path path) {
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot create directory '" + path + "'", e);
    }
  }

  private final Path userHome;
  private final Path workDir;
  private final Path storageRoot;
  private final String productKey;

  private UserPaths(Path userHome, Path workDir, Path storageRoot, String productKey) {
    this.userHome = userHome;
    this.workDir = workDir;
    this.storageRoot = storageRoot;
    this.productKey = productKey;
  }

  public Path getUserHome() {
    return userHome;
  }

  public Path getWorkDir() {
    return workDir;
  }

  public Path getStorageRoot() {
    return storageRoot;
  }

  public Path getHomeIdeSpecificDir(String intermediateDir) {
    return userHome.resolve(intermediateDir).resolve(productKey);
  }
}
