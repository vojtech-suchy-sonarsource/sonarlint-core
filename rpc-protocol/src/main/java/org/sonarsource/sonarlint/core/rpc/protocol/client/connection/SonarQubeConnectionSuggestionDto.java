/*
ACR-55f0af03ad224ef4bd691b2f555fd22c
ACR-6f34e35c11fe49d599c3e55d40111e1c
ACR-e53e305c08bd4511b074697d9319ed0f
ACR-6f54a887ddf44d2ca315dafd78fc6984
ACR-f8c72563f66745c289dafe268ee42711
ACR-50c191d35d334729848c7c50025c6ef2
ACR-9a948103a93e4fbe99b9f68db2174215
ACR-5d1d6efa384c4a64a476d16c3ed0f422
ACR-f49d70700b63438aa9db6afb8ad70912
ACR-f26a88ea187b4f94b3d43446449cc4a3
ACR-01facf47e01a4cd0bb576ba35da4812f
ACR-a636d0903f454d05958bac317e3bc773
ACR-1051969f5f434291b95d39df9f9d9a74
ACR-e0d3e0918e6c4bad9809b7df6e1c92ae
ACR-015073026360474ab1a0fe87278d936c
ACR-83e7c41121494639b7d1bc271eb47f2d
ACR-bca5371764224c4bb2ad70ab9ea2883d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class SonarQubeConnectionSuggestionDto {

  private final String serverUrl;
  private final String projectKey;

  public SonarQubeConnectionSuggestionDto(String serverUrl, String projectKey) {
    this.serverUrl = serverUrl;
    this.projectKey = projectKey;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public String getProjectKey() {
    return projectKey;
  }

}
