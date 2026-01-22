/*
ACR-810ab6cf65614f8bb460c4df2f31b5e5
ACR-f2547147ac324b53bac29935a7e8852a
ACR-45a895f8026d400690e033ae074eae29
ACR-a162f26336f7462193c02a538fd83f26
ACR-7e32963c667c461ebe4ec0488bc1adba
ACR-d52a336385024abaabe45e0966cf4650
ACR-ab7ffd9faca64726914287812894bf07
ACR-847c745c708845ccab97ab98aa156ded
ACR-f1dc7203e6f24b89b65ff1ba35345e8a
ACR-8c168b0aae1a4486addf014c7f7677da
ACR-9ed0a67bc28e42a090de7dce6ce0b542
ACR-9550659274b64b119391b385cfc055d8
ACR-9d27c9c6d4694506a8a1f950d38fc1e8
ACR-16cd6b8924af4df89ae141b7b9978d80
ACR-a8be564ae79f447d9a8d552d1a97f35f
ACR-7f84b92647f34c4380785cc3d81e921c
ACR-f1ad35aa32ae4489bffe71d6af39d32d
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
