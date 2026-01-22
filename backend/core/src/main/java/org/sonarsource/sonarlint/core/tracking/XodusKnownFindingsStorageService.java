/*
ACR-6eb7bfd0d48d475992af592ac8cf3ee1
ACR-9c4e4a2c121f403b8738c866c8101de8
ACR-bd6fcd3062ec487d82a94d3883a88803
ACR-ca92184dbef34fb78fc6fbdd1b0780ac
ACR-1fe124756fc34ae5bf2249cef99e3b8e
ACR-76f286e532e64ff298a9f4c133932d15
ACR-bee90325703f4ce097ef7178a38adeb2
ACR-4bc84e01ac0a4149aaac444d581f0a31
ACR-598ee07c5973483bb66d16178790c5fa
ACR-9318a215f3be45039ce82dbf543aa77d
ACR-22f81c8e6fd241c3ba940a77b7ced30b
ACR-26e39ef7f98545cfbc2dea5b1348ac4e
ACR-7354a0c1f87b4bffae239b06b3628323
ACR-38152d7e431543f8acc9f466abba6b4e
ACR-479537001010415da7a4df45954f53bc
ACR-5f8a53e6af2c4c0db8971cbb1e59ed51
ACR-c8e25bab68b642cebdb1df58b49a0b31
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
