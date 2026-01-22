/*
ACR-c403e66ebf3e4dab858e6803af3bbe51
ACR-d1a6593b8e374761b12a937fd062ea00
ACR-9492e480575740569ed81756b1c33231
ACR-9b4abb2ba6b24b0b9d3430a322d08048
ACR-a79d2a461fd849ee8601945d3b4df1cf
ACR-58f79a5fe36d4f9880293392554ff483
ACR-ecfd45f678dc45ba8075df1102499d35
ACR-6f740fd97f854e618b3c0e00819ccfed
ACR-1c36d33e66334a849be149be529badb1
ACR-a436ee41c69c4fc0b45e6f78b477863f
ACR-df4e1a3eb1f84c3b8c9f9cfd27c7639d
ACR-d553675c74aa406dab8d7bfa13bb2c60
ACR-5863dcd6e05446cda79d126546a67c16
ACR-11e26bf212334bc39876a63a878abd97
ACR-77de90a030d84e2fb2c4fe89a125ae8f
ACR-f0c1ca298e0742dca65485e1036d8119
ACR-6e68c01820704d88b38f9db9b9539579
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class SmartNotificationsStorage {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String LAST_EVENT_POLLING_PB = "last_event_polling.pb";
  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public SmartNotificationsStorage(Path projectStorageRoot) {
    this.storageFilePath = projectStorageRoot.resolve(LAST_EVENT_POLLING_PB);
  }

  public void store(Long lastEventPolling) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var serverInfoToStore = adapt(lastEventPolling);
    LOG.debug("Storing last event polling in {}", storageFilePath);
    rwLock.write(() -> writeToFile(serverInfoToStore, storageFilePath));
  }

  public Optional<Long> readLastEventPolling() {
    try {
      return rwLock.read(() -> Files.exists(storageFilePath) ?
        Optional.of(adapt(ProtobufFileUtil.readFile(storageFilePath, Sonarlint.LastEventPolling.parser()))) : Optional.empty());
    } catch (StorageException e) {
      LOG.debug("Couldn't access storage to read and update last event polling: " + storageFilePath);
      return Optional.empty();
    }
  }

  private static Sonarlint.LastEventPolling adapt(Long lastEventPolling) {
    return Sonarlint.LastEventPolling.newBuilder().setLastEventPolling(lastEventPolling).build();
  }

  private static Long adapt(Sonarlint.LastEventPolling lastEventPolling) {
    return lastEventPolling.getLastEventPolling();
  }

}
