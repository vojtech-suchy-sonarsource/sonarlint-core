/*
ACR-5cdca6bf92064447aa60a7cc723ebfa8
ACR-9352915bb70d4a13a4f75d2ae1b763cf
ACR-9738a6f626f44ef68eaf0562fe6936bd
ACR-92bd314abaa24693bc037771f97cacdb
ACR-ff64a7fe175f4e7eafe470578f001240
ACR-67e3eaac57004a35b1d76aea1a5189f5
ACR-a2491b11acec403ba5cfc64cf6690b09
ACR-23f635ca53df4e46bfb3ba3db40a276f
ACR-2311f647531b4e7e811df78bc7a03d74
ACR-a3bac08100aa46bda6e5fa54e9607369
ACR-e9bec7e5dded40738a2a49d66fe7de65
ACR-203b8847fdff43bcb4f52e064ffb79b0
ACR-5c927da2106940e7a1023d8d83eff31c
ACR-c4b7a28c9c9548f3bfcdf82fc7ff7f3a
ACR-251f8c4c71ae4b17826736c26999b2d2
ACR-01dd18fe59a84b66b3479c6b0c14a5b8
ACR-7c933af685144894b862c054c02bc0f4
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
