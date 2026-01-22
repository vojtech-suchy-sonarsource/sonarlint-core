/*
ACR-bd028ef9889543389c63b19587d10188
ACR-e1bb83fdd7884575a05958bf0a827876
ACR-2ddec9b9ca904558b306f174ff1add19
ACR-bd4e711da7794395a80d29eb2e267b08
ACR-d26bd16f3bd444c598d6035726c155ab
ACR-8a4d8d6df50142fd9eb0e9ac20115b33
ACR-47efc728d50b4cb4bedaf4527624128c
ACR-94680dedab4543bf96ac26b62ff57852
ACR-ff3d5b38230c43f5a9202ae7b1ad4dbc
ACR-502f2cd14f51457ca50bb07ac05033d7
ACR-432b30e4b44140d9926ae5fe70ddd220
ACR-81715269cb0743d9bcab8c72917e15ad
ACR-bb3051ec897949c290dede8325d9846c
ACR-49b62190b1e3448796b997ff51f3f325
ACR-17bda37288874af496838e97b4e295e1
ACR-c3a997154468457894329f448023f4e9
ACR-094f055595764b44ba452fd1deb325c9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class GetSupportedFilePatternsParams {

  private final String configScopeId;

  public GetSupportedFilePatternsParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
