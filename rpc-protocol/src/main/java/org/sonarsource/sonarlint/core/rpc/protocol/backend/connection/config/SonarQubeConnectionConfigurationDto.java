/*
ACR-8ad6d875ef134a16af91691e8dbf22e3
ACR-9555f91a8ff248a8a7a24726077ed125
ACR-11db996fe5bf4b0bbf16b98f9a3ce968
ACR-748cd55c25544d05ad347b9631dbcb54
ACR-5cfd0329f70544268564b6aac18d5368
ACR-5d8ecc8db063450b89eef3c5e3be7338
ACR-4313125bca01468993f437faa7577f07
ACR-f4c3539378fd49cab290d1a11951a93b
ACR-cf929aed58184a0abba700d332371432
ACR-c53d49a43fca4d8aab12aa96a5a4559d
ACR-9a4e5563e7f049d1b881b075d9badfa8
ACR-c79de5ffbe4140c79dfaab50c6be79a6
ACR-fadd965d7ca14de5ab41212c1f618b70
ACR-5f2e3a78d56341bca158156c48470612
ACR-75c7459a28624bd18f736e3e5902e9c4
ACR-66ad2cec07754fdd89e6adbd4a6a3fc5
ACR-80b650b92c2c44e28fc6652b5d1f0611
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;

public class SonarQubeConnectionConfigurationDto {

  /*ACR-8beae5a5cb624bed80e5e96ce8152d68
ACR-6fe2ece12cfc4e3ca0a64006f9adc0a6
   */
  private final String connectionId;
  private final String serverUrl;
  private final boolean disableNotifications;

  public SonarQubeConnectionConfigurationDto(String connectionId, String serverUrl, boolean disableNotifications) {
    this.connectionId = connectionId;
    this.serverUrl = serverUrl;
    this.disableNotifications = disableNotifications;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public boolean getDisableNotifications() {
    return disableNotifications;
  }

}
