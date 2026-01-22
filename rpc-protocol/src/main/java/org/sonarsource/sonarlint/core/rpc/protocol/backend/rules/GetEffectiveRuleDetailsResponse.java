/*
ACR-974cf761738542c98d6deff2d0e94d56
ACR-96525135a14f44eab9e66578f52b7760
ACR-c2925dfcef964bf381ca963c162844c2
ACR-5a457fc2db4d463fa99c58d4824cc3eb
ACR-8a74d6c2a0274f7b96e75cbfb145db25
ACR-1c9fd17299ff42bd9937c8c9b24cf0b1
ACR-36c155d30ed04cfb9db60697c61333a4
ACR-ea921c9b58314ba98adb49fa1f585b75
ACR-35d27a4f24ff4b2fa3bf8aff987855d3
ACR-75ec0cde3b9845ababc0f416ad21b695
ACR-c042a446499a497daff70fe4ed6d0a54
ACR-aa638043e2d64708bf4aaef7931514b1
ACR-e6a94eee9baf4a2d9e5cbd9e92f85d44
ACR-38d5daa4ff4d4527bacf286083441b26
ACR-412985165c2249809803735383970df7
ACR-1108cfb6ac5544338a4570d030654031
ACR-f45efaea759641d29e3e5913e09b6fb8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class GetEffectiveRuleDetailsResponse {
  private final EffectiveRuleDetailsDto details;

  public GetEffectiveRuleDetailsResponse(EffectiveRuleDetailsDto details) {
    this.details = details;
  }

  public EffectiveRuleDetailsDto details() {
    return details;
  }
}
