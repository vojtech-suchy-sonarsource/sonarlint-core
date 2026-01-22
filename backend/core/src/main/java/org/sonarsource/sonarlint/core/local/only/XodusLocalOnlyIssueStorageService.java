/*
ACR-3235a3a3c2b54c6a8ebb773723657dbc
ACR-0c9ec870773b489fb79e7635630fab81
ACR-9ab4f3cf49d243a2a8f5d4f4f65137c6
ACR-f03f91aab6a1456d9ae829cd0bfcca70
ACR-a1946faacddf4480ba6d68208a0751fb
ACR-8d839742375245688b1fc789afe2f3e5
ACR-4e9a7b160f6444928a55e50ee120be5c
ACR-c7f4e31d5576437295fae574714c0a56
ACR-cbc5387872be48669aba4790f73caeb5
ACR-6d8feef3c6f74ec196bdda2c88bf2748
ACR-76e0794e4480466fb25fa58a6abe8d23
ACR-a052243b7aa04b8a8938dfcb63521e4a
ACR-3a998542f2e4436991ed79bf65895b6c
ACR-4aa0b4d12947405c8ea4082c4429c73d
ACR-dfb535d8547c4819b7952835e7bec6c9
ACR-cfaf8972413c4343aa9a78756b2f6cd7
ACR-1b65733edef149468e591af7befa3660
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
