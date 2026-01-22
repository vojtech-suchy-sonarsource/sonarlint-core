/*
ACR-0470c41da10d47e99dfd469294b7eb3a
ACR-84911832569d4a868a37935b32e117bd
ACR-6108e7e78731400dad945dd92266eca3
ACR-9e9d4733fdce457aa3831e28bff62873
ACR-e25199bc36414f39847a680382dd7c9c
ACR-f39b33281b4845b4986ce146d1ae66f7
ACR-4c1265587a004907bf02c0acf254f780
ACR-c573adf67d4b4b149c0caa4646bbd40a
ACR-41cc3c22eed54045b00927276064ab78
ACR-34931b40416646448bc57f4f8f1e9467
ACR-cffd5bfc77a040819afb26b96c0efdba
ACR-f2d164b5ff934b5385a67fb7b1c5b103
ACR-d92097cba0844be088721d41888db1e1
ACR-59d2cbf699444f16960d6a9afade6fc2
ACR-430bba56e2794d5bbec641e71283fc84
ACR-8edbdcb865f14459b8b3bb1173a2e4a8
ACR-603d0d4efc164bc2be0805fde5eb5aca
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
    //ACR-5b8ba699489440aca2912f92b247edc9
    serverIssueStoreByKey.values().stream().findFirst()
      .ifPresent(repository -> repository.removeFindingsForConnection(connectionId));
  }
}
