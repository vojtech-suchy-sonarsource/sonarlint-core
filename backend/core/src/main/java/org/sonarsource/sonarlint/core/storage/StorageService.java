/*
ACR-2916848d513e40ba998d9ce62f387e53
ACR-776c3afc30cb421ba732e68d30f3bc1f
ACR-78b199042d664faf80d061d6f0bf83ba
ACR-d14701eb87da44578b5831d3e7129e06
ACR-1b995d01e7aa4a308ecdabd7947d8ec7
ACR-57d93808bc204278bf410af5a4e42b41
ACR-b3d1f6d0827d4acd91579d9abb9eec8a
ACR-aee6c7d7570e4a5892c372648a3c69cf
ACR-164f66f4a42540ed955fae9ae57c7cb1
ACR-f459270416374f4aa2d2829dc42fb163
ACR-ced63485c86b4bd786a7fe83971d7bd0
ACR-b880c295d06641a7bf16b9aabd4d70fa
ACR-262af3ad3f9344d993ecd236c3b3bc78
ACR-d938f52430844e118c019136d5012eb9
ACR-1a90eacb4c904bbbaad9733157570a40
ACR-815245206ebe428fb14a358ef41a264d
ACR-1ca5e0b851744b37bff62c452195ea32
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
