/*
ACR-4c7f79b8d6604d9f84ed248d1dbc12d0
ACR-fe52a9f009f9442787af7865e582d4b5
ACR-d6d9e9e316cf41ddbc80e23424a2ecfe
ACR-44efda3930de41d18743e959e6a96c25
ACR-4d4e80acf60e4a81844cc5126c1d6759
ACR-2d4f88697a7c43eda9f7d1f6f478046d
ACR-9ca159cdff044ff9bb42e1e52da67016
ACR-a04593de8cff48d4a4a0ffce436b41b8
ACR-c21ac4a4d35e48cdbc940158b0cdb2bb
ACR-56598c9fda784c8cb107e46600bcdc70
ACR-f47e47a599c8406fad5e55427e31aea4
ACR-8ef242b30e324ae89de24a51eb1919e0
ACR-31fc3f1b4de344a29080ec1d0a54d461
ACR-edf496e697ae462eb905bddab1d65bb7
ACR-cb08532a456c4ca184b25cbc805c066e
ACR-8afdb948b76c4be2b053696659f046b1
ACR-f0b757520e474965a568581d99bb3f6c
 */
package org.sonarsource.sonarlint.core.mode;

import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.repository.connection.ConnectionConfigurationRepository;
import org.sonarsource.sonarlint.core.serverconnection.StoredServerInfo;
import org.sonarsource.sonarlint.core.storage.StorageService;

public class SeverityModeService {

  private final StorageService storageService;
  private final ConnectionConfigurationRepository connectionConfigurationRepository;

  public SeverityModeService(StorageService storageService, ConnectionConfigurationRepository connectionConfigurationRepository) {
    this.storageService = storageService;
    this.connectionConfigurationRepository = connectionConfigurationRepository;
  }

  public boolean isMQRModeForConnection(@Nullable String connectionId) {
    if (connectionId == null) {
      return true;
    }
    var connection = connectionConfigurationRepository.getConnectionById(connectionId);
    if (connection == null) {
      throw new IllegalArgumentException("Connection with id '" + connectionId + "' not found");
    }
    if (connection.getKind() == ConnectionKind.SONARCLOUD) {
      return true;
    }
    return storageService.connection(connectionId).serverInfo().read()
      .map(StoredServerInfo::shouldConsiderMultiQualityModeEnabled)
      //ACR-0f33328fc150437f97e68c3c7d5da318
      .orElse(true);
  }

}
