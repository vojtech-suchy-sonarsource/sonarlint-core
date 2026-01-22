/*
ACR-f78edd53f07d4f1ebe9ce54a6830e698
ACR-118450e840a940d1afb287a6a79f462e
ACR-dee2edb286a2456888253af207636668
ACR-5a24b8623d194ad9866a0149d4f1a29c
ACR-07ac674c1f4749a7837a51832407ea13
ACR-4ea2e322cdcb437ca381a50f7f6a1c20
ACR-efca52bb506e4e9d98a801b25132adcf
ACR-de20c9999d754b24b34c9a8fe521f3c0
ACR-0a6723498f894ddfa12b1309d82b6ea5
ACR-8cde621c8e37408ba413a423ea974f92
ACR-49e749fc7d924c3a8a2dd495c1e6b88d
ACR-cccb3de3ee714c3bba971cc3916d8385
ACR-fea3b963d9df44188d674c676e049670
ACR-b3a0a50064004841a1f1825d29f56e1c
ACR-f73034dfd43d4b27889c29ea8ad7c3d3
ACR-48cb2359c1ab49e9a74d6a414364a6a1
ACR-ce3eefc2b9b145f5a2ce6a4de5e40551
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.serverconnection.storage.OrganizationStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.PluginsStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.ServerInfoStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.ServerIssueStoresManager;
import org.sonarsource.sonarlint.core.serverconnection.storage.UserStorage;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

public class ConnectionStorage {
  private final ServerIssueStoresManager serverIssueStoresManager;
  private final ServerInfoStorage serverInfoStorage;
  private final Map<String, SonarProjectStorage> sonarProjectStorageByKey = new ConcurrentHashMap<>();
  private final Path projectsStorageRoot;
  private final PluginsStorage pluginsStorage;
  private final Path connectionStorageRoot;
  private final OrganizationStorage organizationStorage;
  private final String connectionId;
  private final UserStorage userStorage;

  public ConnectionStorage(Path globalStorageRoot, String connectionId, SonarLintDatabase database) {
    this.connectionId = connectionId;
    this.connectionStorageRoot = globalStorageRoot.resolve(encodeForFs(connectionId));
    this.projectsStorageRoot = connectionStorageRoot.resolve("projects");
    this.serverIssueStoresManager = new ServerIssueStoresManager(connectionId, database);
    this.serverInfoStorage = new ServerInfoStorage(connectionStorageRoot);
    this.pluginsStorage = new PluginsStorage(connectionStorageRoot);
    this.organizationStorage = new OrganizationStorage(connectionStorageRoot);
    this.userStorage = new UserStorage(connectionStorageRoot);
  }

  public ServerInfoStorage serverInfo() {
    return serverInfoStorage;
  }

  public SonarProjectStorage project(String sonarProjectKey) {
    return sonarProjectStorageByKey.computeIfAbsent(sonarProjectKey,
      k -> new SonarProjectStorage(projectsStorageRoot, serverIssueStoresManager, sonarProjectKey));
  }

  public PluginsStorage plugins() {
    return pluginsStorage;
  }

  public OrganizationStorage organization() {
    return organizationStorage;
  }

  public UserStorage user() {
    return userStorage;
  }

  public String connectionId() {
    return connectionId;
  }

  public void delete() {
    FileUtils.deleteRecursively(connectionStorageRoot);
    serverIssueStoresManager.delete();
  }
}
