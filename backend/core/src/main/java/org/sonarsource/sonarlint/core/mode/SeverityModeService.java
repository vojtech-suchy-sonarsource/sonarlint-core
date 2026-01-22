/*
ACR-a3d02b71e221472f8f493b2187bc1232
ACR-e94ff9148af343069e9d190a7c0eba79
ACR-dc96388bca314330a513ae5d73cd3a77
ACR-6b3110d25f47410b876066764e1698ce
ACR-22ad5d4c86334191acbbff6dd5884c33
ACR-48a9b9d6a2844574a2a4d8e8f7eda00b
ACR-cdfa7f6a676849e58d474f24f3c39ffb
ACR-10da3f5f37114c9a8eafae2a2420e750
ACR-d9fbde376a3f4c548ef2986cc02b3f96
ACR-b3045579b0a74995abf241af6e06f675
ACR-39c42bd245594c93ae43c03d855229bb
ACR-69ec2bc72a8a4fdbb0557a357c38b785
ACR-52ffece805354f03b88391d52332ef74
ACR-e0abbeb4e1764a3fa59ffbc9a80ccdbe
ACR-fe5ceaf198c14808bf181440c3e1db2f
ACR-6f11bd3fd15945c69f9223fc61d2b1e4
ACR-fb5459c77f274da59f412abbc364bf25
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
      //ACR-54d295afe50d4c02a80127222dcc8dc3
      .orElse(true);
  }

}
