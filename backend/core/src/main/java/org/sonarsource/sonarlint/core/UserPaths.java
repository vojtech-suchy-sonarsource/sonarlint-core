/*
ACR-2b2062cda6244f99bdcfd524bfc3759f
ACR-a67cda1ecd174224a01e04c689634f84
ACR-f37bb07cf48f4131a372726a5639485e
ACR-9992bc25ed0d45a49a54d35fa2b4e9f5
ACR-f0583a063b0f45329538557f99a2c5af
ACR-e1b447d0af5b48f5afdfa4f3a940b73d
ACR-535c45f4d9bd4a86a33acab9bb69066d
ACR-7b8730ec18644d42b9dd154f0e5db3c9
ACR-e9df0568ddfd4ffab2e124c0fdfa71d6
ACR-3bfb84670e604559a5b994e575a4fca8
ACR-51be182da8e946be845e3ac73880efe4
ACR-2e7eddb5891f4298ac52003412aae0a0
ACR-8bb4b319a1204f7d95c316304d709588
ACR-fa7d93ba9ab34c6e90bc94815de4f0a4
ACR-7bb462f5c01745c5a075362a4ea4ca91
ACR-c1b36442abbb41a5981ed1a2c5cbe337
ACR-41cabac2282d4e0ab8dc9c5355a903ac
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
