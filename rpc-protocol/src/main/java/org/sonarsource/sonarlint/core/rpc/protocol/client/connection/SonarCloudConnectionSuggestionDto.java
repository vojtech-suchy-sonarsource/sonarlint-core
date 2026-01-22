/*
ACR-e0fe6d96cab342f2944b54f1d2ff179d
ACR-96520e1a2790473583aa36e8dbaea468
ACR-b44b747a96c64dc199279d4715122f5a
ACR-ce2697d77e784a87add1447588af13fa
ACR-a3885e421eca445c83ed548016a02506
ACR-b1086920f2754d289d612dc32cf6b405
ACR-36e0c0fea34748049481b939f5d52cd4
ACR-ac2127b20d2141b6b3d97b3c125b694c
ACR-6a99e9798f384956ab64722a22a83c63
ACR-037440be737e450c8bf6239086ab047e
ACR-2b4ef0b53c6a4e1c88166b6b95a8719f
ACR-155ef23ec7ac499eb4955104f088e965
ACR-1802c4a10d6b45b6a2bd59cd3a2843ac
ACR-ff4bd281f7f84050b2b116d904e799ce
ACR-048a45e0f31541fc9bc7fd5a7c757e26
ACR-945b189e2dab4edba8d964a37b09fb69
ACR-ad92f141bd994d31b1a2df4a87171fc0
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
