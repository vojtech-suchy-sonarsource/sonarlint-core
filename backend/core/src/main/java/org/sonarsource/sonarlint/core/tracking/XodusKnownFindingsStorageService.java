/*
ACR-34856a4b12a34f4fabeb86b07cd9a17b
ACR-57a746a3674548a7a49b2b774ce75c5d
ACR-2ecf06182c0b48b898d4596ef3055123
ACR-b05960999b174d698d41e182c6948844
ACR-89589af382b04561a8f5d981f479df14
ACR-909e6bbe512a4522a4d86983f59cd792
ACR-7ce2cae9862d4433932e8649df40f68a
ACR-600bdcb5c2a647249c10123c1e3fe319
ACR-1b8db5dc08a7437080612e680bec92c7
ACR-915767eda8b540bfbf9074f692038153
ACR-4f0fc5f980f04758bd14b36b897e4475
ACR-6c4479fb10fa4c299e4ed0ad56171808
ACR-d10a7831fead48a6b1ba771c7c93fe22
ACR-1a0e584a199b43f9a9b5e62f4173041a
ACR-04762bea1d7941228fcdc2e9b1ee4a72
ACR-e82c3e0a5ffd4a14a95d0591d815adb8
ACR-eb7128b919d84543a7a205abb4e2e86d
 */
package org.sonarsource.sonarlint.core.tracking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PreDestroy;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.UserPaths;

import static org.sonarsource.sonarlint.core.commons.storage.XodusPurgeUtils.deleteInFolderWithPattern;
import static org.sonarsource.sonarlint.core.tracking.XodusKnownFindingsStore.BACKUP_TAR_GZ;
import static org.sonarsource.sonarlint.core.tracking.XodusKnownFindingsStore.KNOWN_FINDINGS_STORE;

public class XodusKnownFindingsStorageService {

  private final Path projectsStorageBaseDir;
  private final Path workDir;
  private final AtomicReference<XodusKnownFindingsStore> trackedIssuesStore = new AtomicReference<>();

  public XodusKnownFindingsStorageService(UserPaths userPaths) {
    this.projectsStorageBaseDir = userPaths.getStorageRoot();
    this.workDir = userPaths.getWorkDir();
  }

  public boolean exists() {
    return Files.exists(projectsStorageBaseDir.resolve(XodusKnownFindingsStore.BACKUP_TAR_GZ));
  }

  public synchronized XodusKnownFindingsStore get() {
    var store = trackedIssuesStore.get();
    if (store == null) {
      try {
        store = new XodusKnownFindingsStore(projectsStorageBaseDir, workDir);
        trackedIssuesStore.set(store);
        return store;
      } catch (IOException e) {
        throw new IllegalStateException("Unable to create tracked issues database", e);
      }
    }
    return store;
  }

  @PreDestroy
  public void close() {
    var store = trackedIssuesStore.get();
    if (store != null) {
      store.close();
    }
  }

  public void delete() {
    var store = trackedIssuesStore.getAndSet(null);
    if (store != null) {
      store.close();
    }
    FileUtils.deleteQuietly(projectsStorageBaseDir.resolve(BACKUP_TAR_GZ).toFile());
    deleteInFolderWithPattern(workDir, KNOWN_FINDINGS_STORE + "*");
    deleteInFolderWithPattern(projectsStorageBaseDir, "known_findings_backup*");
  }
}
