/*
ACR-0d9149b2a5844772913c5a391d20a6b9
ACR-4a9c9f3d0543418ea05195a075127281
ACR-259b67f5c02343cf9efbbf40d1ee2df4
ACR-df6743338280482eafab026425e03af1
ACR-4aae8bdc5f1541748c34178d4f7cb5c7
ACR-7293d4ad79bd4cb280b519cdf4d2b843
ACR-3169b4b1a7174906a145cab1e6bf6814
ACR-de62dcbc62fa4a3287e763f3bdf2967b
ACR-8a30b6a4dd1046baa5d1bd300417f91f
ACR-81394a8354394854a6f22664ed283be0
ACR-850072059e2f4ddc963082efdd0175f0
ACR-2de8c49500544f4aac4b24a2c92737cb
ACR-869747f609264f12be43323eb346e32f
ACR-471e7700c4474abaa28791c45e42358b
ACR-5cbb784b0d8a43dab66013084be0fa8d
ACR-239950aabd314897aeebb1680bd46a66
ACR-150af48ae62640bab23a87b0ff9c62f5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.info;

public class GetClientLiveInfoResponse {

  private final String description;

  /*ACR-19df01500b07422b8e668eba0588468f
ACR-fa5d10c040cc441491ebf6b548cc1680
ACR-fe061e0d3433451c84279bd2bac388ec
ACR-93742888e63b4b93ad4bb6554d862f38
   */
  public GetClientLiveInfoResponse(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
