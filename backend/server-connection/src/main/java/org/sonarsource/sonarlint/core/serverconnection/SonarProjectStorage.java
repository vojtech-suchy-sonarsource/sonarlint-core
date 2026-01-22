/*
ACR-d6e7a8e3c3724b8392c57cd12438df66
ACR-5f23c45dab824779af424477364aaa3f
ACR-fa0cbc47f3ab42739d56bd572a328d68
ACR-211d68026e0d49499f50dfe3f858f513
ACR-0709f90cb21e4fb3a26f7210348a8066
ACR-48e2b69e13dc44f0ad60eaa23875fe29
ACR-990c543521de4b41912bbe9e5808a01d
ACR-86d418e67703441a85673b7635ccf792
ACR-3f7f7edd44fa4c59bc1b7ab58fae1f62
ACR-334d79cdc4554ebdb055dfefd1e4c960
ACR-e3deb73af4f34f88887ab43105aa8c32
ACR-70c82639a92a48d39166805ed99936b0
ACR-2365b9ca52af42c18097a3f584eb55dc
ACR-2ade9768e1cf48fc94ace664889cf2e7
ACR-c83bdd0c1e704b289ac6a4a76fb8a980
ACR-2d948ed69b4a4fee80b947c85318907d
ACR-d9b22b6b81304852bbbed8e2d590a70d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.serverconnection.storage.NewCodeDefinitionStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProjectServerIssueStore;
import org.sonarsource.sonarlint.core.serverconnection.storage.ServerIssueStoresManager;
import org.sonarsource.sonarlint.core.serverconnection.storage.SmartNotificationsStorage;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

public class SonarProjectStorage {

  private final ServerIssueStoresManager serverIssueStoresManager;
  private final String sonarProjectKey;
  private final AnalyzerConfigurationStorage analyzerConfigurationStorage;
  private final ProjectBranchesStorage projectBranchesStorage;
  private final SmartNotificationsStorage smartNotificationsStorage;
  private final NewCodeDefinitionStorage newCodeDefinitionStorage;
  private final Path projectStorageRoot;

  public SonarProjectStorage(Path projectsStorageRoot, ServerIssueStoresManager serverIssueStoresManager, String sonarProjectKey) {
    this.projectStorageRoot = projectsStorageRoot.resolve(encodeForFs(sonarProjectKey));
    this.serverIssueStoresManager = serverIssueStoresManager;
    this.sonarProjectKey = sonarProjectKey;
    this.analyzerConfigurationStorage = new AnalyzerConfigurationStorage(projectStorageRoot);
    this.projectBranchesStorage = new ProjectBranchesStorage(projectStorageRoot);
    this.smartNotificationsStorage = new SmartNotificationsStorage(projectStorageRoot);
    this.newCodeDefinitionStorage = new NewCodeDefinitionStorage(projectStorageRoot);
  }

  public ProjectServerIssueStore findings() {
    return serverIssueStoresManager.get(sonarProjectKey);
  }

  public AnalyzerConfigurationStorage analyzerConfiguration() {
    return analyzerConfigurationStorage;
  }

  public ProjectBranchesStorage branches() {
    return projectBranchesStorage;
  }

  public SmartNotificationsStorage smartNotifications() {
    return smartNotificationsStorage;
  }

  public NewCodeDefinitionStorage newCodeDefinition() {
    return newCodeDefinitionStorage;
  }

  public Path filePath() {
    return projectStorageRoot;
  }
}
