/*
ACR-c730996b9ae24a609ae2f4c1532f99bf
ACR-2a9af9cca6734de3894f87c9d873ef86
ACR-0d84ac9f577b45ad9fdb75a32e9335f1
ACR-ba74ac1a99fb400697d338304b829062
ACR-cbd8158d6d7c4dbe884c40107ae50fe4
ACR-bfe1454828f14b4490caf4fb82cb4a4f
ACR-f7916282dbd944b98cac93b5464de52d
ACR-a843eae953464063a5d60af51ce97c55
ACR-5759a802a98f48b2ad9469ad019129aa
ACR-343d90031b1446f7bb7b9cb762092bb6
ACR-6f0f8c5282524df1983f0ec231264f02
ACR-e2e12ac416aa4bd0ae0118d82f847c94
ACR-76f07e69aa6e433aa3cc1716717eaa6e
ACR-5d5bae88fae447ebbf05dd9c64fda7de
ACR-599b18872b3d467b9eb92f002be66b42
ACR-e03373f20bb8413689199f36695168fa
ACR-222dd0f671194bdcbe2fa262ad55ddb1
 */
package org.sonarsource.sonarlint.core.event;

public class ConnectionCredentialsChangedEvent {

  private final String connectionId;

  public ConnectionCredentialsChangedEvent(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
