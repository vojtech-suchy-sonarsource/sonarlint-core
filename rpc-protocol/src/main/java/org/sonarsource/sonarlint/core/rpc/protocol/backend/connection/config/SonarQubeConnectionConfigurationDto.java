/*
ACR-d89d3fba9ad346d7bfc33b4cd37ac0fc
ACR-13be1202ede1444eb3f4ef4b55952146
ACR-736a5b2b568a4857a9c388d01b4cbfc7
ACR-8ec4f05ca72b46b296a512bc98b3bf6f
ACR-6c757ed37eea4451b1c2ae374e1ffe56
ACR-253ecdcb74ff414887e68548f93dd2a9
ACR-ac9963a9b106446cac4df77fe3cf4c5c
ACR-41b734284b5b49dc84e45cce1b183030
ACR-4e77e10c8d4a409b83e087edec0b2bde
ACR-fe87c922b10b4424a8ed07aa4ec127ec
ACR-dc3cdaf984614cb6a848fada499fd84d
ACR-f28b023d5793496ab8540627a5d167ec
ACR-6b8a8d0869a84fe79a7692afbe66a379
ACR-47b8e11906b24281bad5c7971cf5d263
ACR-a1b888700cc74683913dacfa68e2a631
ACR-586941e474c64da19a84a72386182aea
ACR-0eab4f1d1d674b0bba15f4c724227b51
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config;

public class SonarQubeConnectionConfigurationDto {

  /*ACR-2fe192e2b97543c6bc04e1357e3bf344
ACR-779880d1dadb4d39b5d79eaf29301684
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
