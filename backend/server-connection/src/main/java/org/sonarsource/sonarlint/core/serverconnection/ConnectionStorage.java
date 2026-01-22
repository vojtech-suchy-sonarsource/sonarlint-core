/*
ACR-ec34992427be4f31b7b18af75ff3d414
ACR-bba0f0e57c574bc59305bd1c1572eee5
ACR-3edbe06bc331417ebc04df4a4af07458
ACR-ba6076056b62484085c33d2a8ee1e0bb
ACR-b2fbfcef9900435bafc7dd6e4b83a25a
ACR-5a3e01487cf64c7dbc2fd1a736ddcb2e
ACR-d586ed773a4f4cbab150ff942e8eaced
ACR-b30c32eeb99c4436a0fd5bc267d1e547
ACR-32c81bad7ef3444fa6046f4fe046cd19
ACR-90ac1b14e9214a2faca93ba0eb757232
ACR-8c21c838c2ce48f1a681d14f15a09788
ACR-a622287ee76a4964a883d13902f8709a
ACR-17bfd123f5924b07b120002ae96231db
ACR-12d0c95481e84674aa8595c9aa8ad899
ACR-e2d9a02743a049edb6f1b178e127f133
ACR-a598cf2963a6497abe15321f69e4b8ee
ACR-dfd8b51f341649c58dee9f1b02967afe
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
