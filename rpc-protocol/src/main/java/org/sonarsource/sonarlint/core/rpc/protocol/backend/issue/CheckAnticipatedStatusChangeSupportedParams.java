/*
ACR-fb59708861b64176b4edc428935c03ad
ACR-988001a7eef64bc99132700257bac305
ACR-886a3ae5ae19477cadacd0300c7e6c7d
ACR-219f5db5814c41568cdc2084ec0f092f
ACR-b31560a28e3949c3abb5a48561f683db
ACR-f1110f34da3c4afcb0731ebf60786732
ACR-da534927d51c4ab6b5edc754f143af23
ACR-2eb8c24fd362487ea2e2f770edf25836
ACR-5774c271413043f089523e743638a1d3
ACR-e3ccad18cb194db3a0077cfaea87ae25
ACR-d83bf4671e234373bfd3ad8efeb37d83
ACR-5a664af0a42f442184b79214abd98e5c
ACR-51a87d008eb4465ab651d7f2e408dea7
ACR-fc3f811610af4cf0823a16a2ee283a0e
ACR-2ef2ef0fcae3409ea269c13e690740a6
ACR-269b4614c0d34f7a9e8b502f3673f39b
ACR-abb6befd41e142f7bb2763503ffc909c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class CheckAnticipatedStatusChangeSupportedParams {
  private final String configScopeId;

  public CheckAnticipatedStatusChangeSupportedParams(String configScopeId) {
    this.configScopeId = configScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
