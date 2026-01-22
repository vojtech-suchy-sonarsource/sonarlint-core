/*
ACR-2054ee12a5794254a5c49ee61c5783ba
ACR-c0b196879a3443d39e3aa769f9b0b3a3
ACR-2cd59e5e3b344a13abcab45ca18be13f
ACR-caf247f147b348b38738ff153cc3a6a9
ACR-7db76aa8cc8e4c5fa6d0bd03d575c69e
ACR-5c71d1bfcca04c1ebd08357367403213
ACR-9bef7fcbd7654caaaeafa0af3b149411
ACR-d316b76560d143a99e7f9748b7329cb2
ACR-5bf21d5096554b74a45b85c8422e5910
ACR-454aa0fef79a4f66aa6886e9666f08c6
ACR-37ac49aa7977450db98b98bb640d3e92
ACR-6c5eb5b0b90f4971bd8600441f32cf90
ACR-86e0fe0d8de54dd7b573b9c28b92ac57
ACR-efeb4ea3401146d6a659080d49d862e3
ACR-3f8eec4f5dc34677bf091269fbfae529
ACR-40987905155f476a91ede365b35ad390
ACR-75ed16bce4ca46db9223610626705f6a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.sca;

public class CheckDependencyRiskSupportedParams {

  private final String configurationScopeId;

  public CheckDependencyRiskSupportedParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

}
