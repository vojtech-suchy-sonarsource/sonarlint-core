/*
ACR-a1000c5f5dc740358a4bc1628ff2ebdc
ACR-267c87665a6b4be380ca1e8b4ab50f8c
ACR-f7485dbfe37145aba7108a94dd713b88
ACR-751546a478fa483aa1149375bd8d5ab2
ACR-aed000506b37409ca6451a5b8c15da42
ACR-1bd7148afc6f4791b185bfaea5a041d3
ACR-11210f4b7066499c805ff1873c8a37dc
ACR-4744cd07fa124ceb8d751c873055a805
ACR-1009b6896d2f4310a8f75d773d46497d
ACR-c9c7d410c8e640b2a0c4f5b1407cbb45
ACR-cdcc2651fdbc46ca9e1fb352db82af80
ACR-e8ba023d26954caeb809a274af9e128e
ACR-babb38ff11fe49138489c673dedad01f
ACR-bf72056abdbd466ca003b9bed4f6969f
ACR-517c2e0a2f504ad39885a1271d954789
ACR-5a3eb0ad249c4dac873558af372a3372
ACR-3c3a923a96b54c9db30375f749edf925
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
