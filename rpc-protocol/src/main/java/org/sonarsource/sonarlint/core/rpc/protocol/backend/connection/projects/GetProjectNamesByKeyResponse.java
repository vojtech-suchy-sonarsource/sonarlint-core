/*
ACR-02c3edd22754466da9868c47c973642d
ACR-2569b2ef3ba64827b66fc62b08d2e284
ACR-5c1ef02da3094e19a4019ea3e2f0f922
ACR-4067931304ed44a589c4d0c3fdc74a86
ACR-0f8c6c2c0adc4c878639e3b72439c278
ACR-4bb2339d0c9a4533a67fc82fddfad8ec
ACR-e70b948e1ff047a1bd3e8c6744f38693
ACR-061331c57b644e15b0b99fcc9bc11b3d
ACR-89e07a03c44f4bb9a346b8f0d42820af
ACR-317cd78c0dd54a36afb0f1106a095ea0
ACR-0d4cb3cc831946bba701fc552a21e0a6
ACR-980303ff0ff647debcdafc2d6719b450
ACR-a2bf95016674459fbce50dfeace99b98
ACR-ed8775380ae04cd781ce9a10671a8bfd
ACR-cc10366d057747458978013f1cc93b4c
ACR-2830a354354946acb82866aef3e884d9
ACR-82635f167a0645059234429a2b4ff593
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import java.util.Map;

public class GetProjectNamesByKeyResponse {

  private final Map<String, String> projectNamesByKey;

  public GetProjectNamesByKeyResponse(Map<String, String> projectNamesByKey) {
    this.projectNamesByKey = projectNamesByKey;
  }

  public Map<String, String> getProjectNamesByKey() {
    return projectNamesByKey;
  }
}
