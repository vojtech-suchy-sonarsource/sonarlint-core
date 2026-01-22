/*
ACR-19c6c0eed135438c960eec386416aa2e
ACR-2c611514d07645ec91aec8458608e1a6
ACR-faa28e85ce9c45a58829f8a71466a446
ACR-f8d94faba10f4b6c835d5301d3a31eee
ACR-d39d576b9ab64adca36bda6742613d84
ACR-1489691984df46439f10ee90dd085dd4
ACR-3f48c877867742c5ad235a1a92191e14
ACR-6a3b1b9cdb244d079e500f562f0080be
ACR-c65bf842e4854405be2b4ba0f9b4cdd7
ACR-45498cf9834d4e558683010b98ed2f31
ACR-934d610738324d3ea5a27abb1f54dd50
ACR-2dd0c8d0061f4188af078e85b636cafc
ACR-ce21586011c34cdb87a817a01470f1ab
ACR-b515ca78c6b24ea19cc7fc8fca8f90b2
ACR-eef6b87007394d11871cbf00949d896a
ACR-abee30a5df1a42f8a433f900e3fe111b
ACR-9c5fa1ca42c14273990dbfbbd286792f
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
