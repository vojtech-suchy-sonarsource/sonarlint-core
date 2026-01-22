/*
ACR-3b85001637d441618647f033c015e4a3
ACR-c42a1da6e2b94911adc6d97c29e8221c
ACR-acd1c91c21d84145af979b72d9c81db2
ACR-528ac112f16848f781be25550a50542e
ACR-829b1da51d104cc59c329c4d80312aa3
ACR-a63d121b2fa14dd9a0d6e6d5084ed6c2
ACR-81710c0efba643d7a164f5cf4831236d
ACR-2da2becdcbc54d1fa91322e7dac21544
ACR-977deb69ddc64d8bb81c787344f34113
ACR-913f34b7d3e74255bc10bbcecbe1f717
ACR-d9e04821e998463199991c904b92f593
ACR-c668b7f028dd4d18b11f7c2fa0fb3222
ACR-9c136aac16904f2f8c6d2b9fe56c8f5b
ACR-707e2b4a3ac1415fac7e6f056c5a5097
ACR-d2c5de94780c48ae80a936169c98f777
ACR-aee7e67dd5f7430aa689106c79f33113
ACR-c89025ff0f39488bb5041f73f89887ae
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.serverconnection.storage.RWLock;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class ProjectBranchesStorage {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public ProjectBranchesStorage(Path projectStorageRoot) {
    this.storageFilePath = projectStorageRoot.resolve("project_branches.pb");
  }

  public boolean exists() {
    return Files.exists(storageFilePath);
  }

  public void store(ProjectBranches projectBranches) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var data = adapt(projectBranches);
    LOG.debug("Storing project branches in {}", storageFilePath);
    rwLock.write(() -> writeToFile(data, storageFilePath));
  }

  public ProjectBranches read() {
    return adapt(rwLock.read(() -> ProtobufFileUtil.readFile(storageFilePath, Sonarlint.ProjectBranches.parser())));
  }

  private static ProjectBranches adapt(Sonarlint.ProjectBranches projectBranches) {
    return new ProjectBranches(Set.copyOf(projectBranches.getBranchNameList()), projectBranches.getMainBranchName());
  }

  private static Sonarlint.ProjectBranches adapt(ProjectBranches projectBranches) {
    return Sonarlint.ProjectBranches.newBuilder()
      .addAllBranchName(projectBranches.getBranchNames())
      .setMainBranchName(projectBranches.getMainBranchName())
      .build();
  }
}
