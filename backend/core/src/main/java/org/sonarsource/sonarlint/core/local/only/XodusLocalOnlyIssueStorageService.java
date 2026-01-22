/*
ACR-009a162bf8ed451aa3cea2884e3ae76e
ACR-773b31d7e42f49a6a1c3eb0a44ab0796
ACR-401c0e0172094235a8288fa9867f11ab
ACR-11407303dcb94709b2df30a00c91307e
ACR-adfab34ec91649809337ed9bc898d545
ACR-3cbc6f27eb1b41b49423715fe43cfb31
ACR-e5093bae838347099bebf76005a91678
ACR-c9ece07c97fa4d77855e8326e6a0d8f2
ACR-96d105701b554ee8a06b78cda96115c0
ACR-b39ff04d141244d3bcb680265062cab4
ACR-f281877b73384a50822f1ca3219cff49
ACR-aa3debb4fbf848eaabb1107f5d0b87f9
ACR-4765998da3134b9ea2210613153752f4
ACR-bbf3cfcc1ff8433aab38180c8de9e0fd
ACR-67a3406d7ab743e2ab8268dfa7dc03d2
ACR-0163bb921a574edb8baa70b999be0cca
ACR-d5ee6d019b844c25b9c054875d863889
 */
package org.sonarsource.sonarlint.core.local.only;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.UserPaths;

import static org.sonarsource.sonarlint.core.commons.storage.XodusPurgeUtils.deleteInFolderWithPattern;
import static org.sonarsource.sonarlint.core.local.only.XodusLocalOnlyIssueStore.LOCAL_ONLY_ISSUE;

public class XodusLocalOnlyIssueStorageService {

  private final Path projectsStorageBaseDir;
  private final Path workDir;
  private XodusLocalOnlyIssueStore localOnlyIssueStore;

  public XodusLocalOnlyIssueStorageService(UserPaths userPaths) {
    this.projectsStorageBaseDir = userPaths.getStorageRoot();
    this.workDir = userPaths.getWorkDir();
  }

  public boolean exists() {
    return Files.exists(projectsStorageBaseDir.resolve(XodusLocalOnlyIssueStore.BACKUP_TAR_GZ));
  }

  public XodusLocalOnlyIssueStore get() {
    if (localOnlyIssueStore == null) {
      try {
        localOnlyIssueStore = new XodusLocalOnlyIssueStore(projectsStorageBaseDir, workDir);
        return localOnlyIssueStore;
      } catch (IOException e) {
        throw new IllegalStateException("Unable to create local-only issue database", e);
      }
    }
    return localOnlyIssueStore;
  }

  @PreDestroy
  public void close() {
    if (localOnlyIssueStore != null) {
      localOnlyIssueStore.close();
    }
  }

  public void delete() {
    if (localOnlyIssueStore != null) {
      localOnlyIssueStore.close();
      localOnlyIssueStore = null;
    }
    FileUtils.deleteQuietly(projectsStorageBaseDir.resolve(XodusLocalOnlyIssueStore.BACKUP_TAR_GZ).toFile());
    deleteInFolderWithPattern(workDir, LOCAL_ONLY_ISSUE + "*");
    deleteInFolderWithPattern(projectsStorageBaseDir, "local_only_issue_backup*");
  }
}
