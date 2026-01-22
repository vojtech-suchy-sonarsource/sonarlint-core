/*
ACR-f9392e694ba1402aad6ab21f435f8862
ACR-618629baa3134d95a60e7f66b50b1b51
ACR-41e142db829d4b639bcc27d9e8639650
ACR-f52a3a18412944bd8251483c61187784
ACR-2eb9699c77e2464dba049bca5d7542c8
ACR-318eed5c47cd4f8fb2f49a4a27a10b5f
ACR-76de66cf39624a47b1a9dca80e7d7c4e
ACR-27600e56993146e6b01a6174774d1ec8
ACR-4f12b6891f24446094466257fcf3d676
ACR-b74ca2abb06d4ece82aacd2577a7aecf
ACR-1f8637d3934044ecbfb96eb6b5653b23
ACR-78f38e438aa2421687ff789094f544b0
ACR-866be4c9e6554060abfbbf8a14b92e4e
ACR-a1009b81b8054a7082dee04d141fbece
ACR-be550897abe543fcbde122f103143010
ACR-1277ed97336f4373abd6ddddee20c101
ACR-16f60d2ef6e24a74986cdef0e9e032d6
 */
package org.sonarsource.sonarlint.core.storage;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.serverconnection.ConnectionStorage;
import org.sonarsource.sonarlint.core.serverconnection.SonarProjectStorage;
import org.springframework.context.event.EventListener;

public class StorageService {
  private final Path globalStorageRoot;
  private final Map<String, ConnectionStorage> connectionStorageById = new ConcurrentHashMap<>();
  private final SonarLintDatabaseService databaseService;

  public StorageService(UserPaths userPaths, SonarLintDatabaseService databaseService) {
    this.globalStorageRoot = userPaths.getStorageRoot();
    this.databaseService = databaseService;
  }

  public ConnectionStorage connection(String connectionId) {
    return connectionStorageById.computeIfAbsent(connectionId, k -> new ConnectionStorage(globalStorageRoot, connectionId, databaseService.getDatabase()));
  }

  public SonarProjectStorage binding(Binding binding) {
    return connection(binding.connectionId()).project(binding.sonarProjectKey());
  }

  @EventListener
  public void handleEvent(ConnectionConfigurationRemovedEvent connectionConfigurationRemovedEvent) {
    var removedConnectionId = connectionConfigurationRemovedEvent.getRemovedConnectionId();
    var connectionStorage = connection(removedConnectionId);
    connectionStorage.delete();
  }

}
