/*
ACR-8bc7f9a1c365497a97d21b1f40b17f8f
ACR-cd83e980dd044f7ba068d8d97ede442f
ACR-c7744e28d150414d90ee0e94746735c8
ACR-87dfc42aff8e4690aeea4bd134f68fb5
ACR-772a95631c1b44aaae495888ab0b3b63
ACR-3426009cf02645b0b999ef9e8e2c583e
ACR-a8a892c8be134b77975a52c753bf4dfc
ACR-6c77a70351df4bfe823d2e7ba6d75173
ACR-03087e3d3aa042acbd82db863e937959
ACR-76c5c896bcaa49e8989913ca156ff264
ACR-b936863ee70b400c958dfa6374e32da4
ACR-8d0d3b30c77942979c95ff6e95bf68bc
ACR-2b2f8160d85a4de089a2cf8f21f9ce34
ACR-0fe3e44da4dd4a03a1e8b8f65e7a5186
ACR-e0cb205632184fbc99ffbba60731551f
ACR-f94eb402d9254c23922e6f33a01ef99e
ACR-08a7d59cffa640d280f806fd0990aa9c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.binding;

public class GetSharedConnectedModeConfigFileResponse {
  private final String jsonFileContent;

  public GetSharedConnectedModeConfigFileResponse(String jsonFileContent) {
    this.jsonFileContent = jsonFileContent;
  }

  public String getJsonFileContent() {
    return jsonFileContent;
  }
}
