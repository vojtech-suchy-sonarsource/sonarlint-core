/*
ACR-fecbeb0e9a6442a5bd32e11383920444
ACR-b6f00202518148bda4252aa4d6b19616
ACR-ab30e51d0a9a4535888a3b4ddc8a9a3a
ACR-e1f3fff563aa4cda9ae0c1344485d18e
ACR-05965afe57e84755b051a87ed162b8d8
ACR-080cb8bf3202492fada848af42b7250e
ACR-cfa0a68b03774cdcb396a21e36bb5c13
ACR-bb5692571740411694afa81393741aa8
ACR-419138c41d7847f882e427c702daaae9
ACR-5621be4b164e4177b04fd13790fe0a34
ACR-dc673166f8ed41509bdcfcb68a6d0bcb
ACR-0fd958e478e04eeaba22692c66ff021f
ACR-0fc7dc8841fd4683a37cf08b4761e4a3
ACR-7f75cf51f7844269a5f2de478575b893
ACR-12de9c52d2e444c6aaa9457af911975c
ACR-70ead2b39f4043e89dbc3050b229e9d6
ACR-4af8acf8da7e4141b89709917536aeb8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;

public class SonarCloudConnectionSuggestionDto {

  private final String organization;
  private final String projectKey;
  private final SonarCloudRegion region;

  public SonarCloudConnectionSuggestionDto(String organization, String projectKey, SonarCloudRegion region) {
    this.organization = organization;
    this.projectKey = projectKey;
    this.region = region;
  }

  public String getOrganization() {
    return organization;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public SonarCloudRegion getRegion() {
    return region;
  }
}
