/*
ACR-9519075f18c1412894a7ddae6ff8f60c
ACR-dbf94921625246db98606d27c0a30956
ACR-bb284d73698048249655ae1b9cf45165
ACR-917e78b68e3946b9964ac02bc43e4a4b
ACR-f2f195c9067348659c8958399edafe59
ACR-04421ce06f72478eaa3f9f6697499019
ACR-b18c5bbca8984113a891aa46b23c2f4c
ACR-e485ffb95ba54698a5b979e0dd9cfc36
ACR-070e68f7fc994839828051390808c463
ACR-3b205d43fd6d4b4099730e085e61d89e
ACR-9d1a917793ae4eba95e2337599d4a6a4
ACR-c6250bd0738b4472b0a0c9aa3fdb5df8
ACR-0f8adf7ad3dd43088615878d7b8bab9f
ACR-a2b7701c9af540daba5d66670eaa5000
ACR-26982147e31948f79ea54a4ff34ba36d
ACR-6f98f5250c4e4e8a9441a3f218692001
ACR-0bb197fa6d54477d800e8c9a150afffe
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;

public class ServerIssueStoresManager {

  private final Map<String, ProjectServerIssueStore> serverIssueStoreByKey = new ConcurrentHashMap<>();
  private final SonarLintDatabase database;
  private final String connectionId;

  public ServerIssueStoresManager(String connectionId, SonarLintDatabase database) {
    this.database = database;
    this.connectionId = connectionId;
  }

  public ProjectServerIssueStore get(String projectKey) {
    return serverIssueStoreByKey.computeIfAbsent(projectKey, p -> new ServerFindingRepository(database.dsl(), connectionId, projectKey));
  }

  public void delete() {
    //ACR-f85947c8fe5341d79d46fe693ea577cb
    serverIssueStoreByKey.values().stream().findFirst()
      .ifPresent(repository -> repository.removeFindingsForConnection(connectionId));
  }
}
