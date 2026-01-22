/*
ACR-fa838acf416548e39b7dc31b4801cef5
ACR-670b6c2cba7e436ab456b88401618167
ACR-8a5ff1c57102454fa25d3941f8aa7199
ACR-ca78702f14a84ef78a93c1f64e15fb82
ACR-16ed836884394ef890349288fd58363d
ACR-663a1f4aee224537b00e98d69a526134
ACR-f1e285cd542d4c878ede849342bd46ee
ACR-6083ca233cfc40bd9a91ec035405de52
ACR-e79f9c35df3844ffa317adb8305a143b
ACR-c445889f67984311acac3c4466d32391
ACR-0092f1fff9cf4444853b388a4ffc8940
ACR-31d6e8bd569947339150fbaa139ae075
ACR-d566d5c198f849b9837a0f8739614f01
ACR-d2bb2dec49ab4039a9a0cb779c638707
ACR-361a251b28a546168ed0928e83756cf8
ACR-138f94282c39443ca708d36484558639
ACR-9f30df323ffe40f68c8486b7e500a3bd
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.event;

import java.nio.file.Path;

public class DidReceiveServerHotspotEvent {

  private final String connectionId;
  private final String sonarProjectKey;
  private final Path ideFilePath;

  public DidReceiveServerHotspotEvent(String connectionId, String sonarProjectKey, Path serverFilePath) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
    this.ideFilePath = serverFilePath;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }
}
