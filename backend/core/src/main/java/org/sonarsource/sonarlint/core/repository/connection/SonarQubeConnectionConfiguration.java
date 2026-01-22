/*
ACR-df18dcfd1a004714a07bcbb8bf29f179
ACR-24fbd1fc661f4e1ca2cfe93c4639c0fc
ACR-b055e38da34349609e9f266991ed2b59
ACR-b2678eb24c4b45d2aa4ac0b2378f68f6
ACR-1aac510e31de4735998d56b567f489d0
ACR-40607f5c35074460943d448355defa5e
ACR-a0985ade157e4783a8bbdf54e35cde18
ACR-8f26f8460b3c4086aca5708034ae3b9d
ACR-fb4f2dd39bb0417d8b89fff1db140dd9
ACR-903fca7a8c054dde835b53afd829bc83
ACR-4da5b2b9c7574067b5dea0ef4d4e0096
ACR-9972c19ce1d4473783982acdb0abbb8b
ACR-6e762a5346314f3a9307fcbdfe5f1270
ACR-05b7e711260247bda13a4229bdd06666
ACR-f6ff70721e494e298a4c96be61ee6dac
ACR-463e76dee6a74e11ad4a94dac8a4d603
ACR-cd0bc2f80ae44e49abbee93bc68ce721
 */
package org.sonarsource.sonarlint.core.repository.connection;

import org.sonarsource.sonarlint.core.commons.ConnectionKind;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

public class SonarQubeConnectionConfiguration extends AbstractConnectionConfiguration {

  public SonarQubeConnectionConfiguration(String connectionId, String serverUrl, boolean disableNotifications) {
    super(connectionId, ConnectionKind.SONARQUBE, disableNotifications, serverUrl);
  }

  @Override
  public EndpointParams getEndpointParams() {
    return new EndpointParams(getUrl(), null, false, null);
  }
}
