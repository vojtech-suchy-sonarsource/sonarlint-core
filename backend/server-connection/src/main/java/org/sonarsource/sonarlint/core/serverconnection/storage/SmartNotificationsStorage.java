/*
ACR-51977f78a5ad4fb38eb9793caa83f5a6
ACR-9d18d2d8217f46cca46887c511fbe0b2
ACR-d70eb052e4124637b333ae9fa5c57cdb
ACR-5398a8dfb08c48ad9663be50cbd4fa97
ACR-d1e999797bbe408ea5809e6591c620fc
ACR-4e2bb944e87740dd8fcda5721173b3a5
ACR-ad8ea05890a64d9eb25b7374fffc904b
ACR-6a2b5d3f81694fab9bbf0dcf34b35926
ACR-8e7394a92a124ca4a387e4f124bb17f6
ACR-c1e39efcf6f548638986a8c48dbfe21f
ACR-90d735f82f8043afa2f69c3e8f1ebe18
ACR-99442e46fc5545f0a2d7ef4a065f34d9
ACR-d5e0dfcde8104b7daecc391f0e141197
ACR-53e926fbc7b14ab79f6721d5d5dc39ac
ACR-007c965e88fb4ad9af62702ad985ad5a
ACR-f033d578ea45459db306fd03bc848f85
ACR-989e2a48949a427bb9f7f45d964172cc
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
