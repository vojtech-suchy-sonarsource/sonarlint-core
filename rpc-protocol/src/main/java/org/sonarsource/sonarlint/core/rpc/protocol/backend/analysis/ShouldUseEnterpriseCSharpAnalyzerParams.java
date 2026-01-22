/*
ACR-ebf292586b60419e9d9ef1bb6254de16
ACR-8f937bcf5bfa402b9daac0377da203df
ACR-5d8143d2a76b4285a7b36ec583db9df4
ACR-c62eccc85ab04b6cae15279c28599cb2
ACR-d0f871a1b0694491ae5b752ace60c4cb
ACR-c77b7dc6482c4733a46def64426afe17
ACR-e3432ab602ea4e39af681de8f6d3d5b5
ACR-0ff07b8095794e949608f0e672cc6b89
ACR-119b3b9517474ede86dab79bb1c9624a
ACR-f770f5c529d549e6bd459bff3974ff83
ACR-5988632b29a545659b98f0675dddcbf4
ACR-d14dbfb61fc54afd8d88a58e4eba2e6d
ACR-def4d1409bc64ee781675e88afa4985e
ACR-c5274f80c0ed4c1798fda3956dd19a0c
ACR-9562a9dff2f148f1b01adf4ef36f29f2
ACR-9a7e37f44d9940e0b3278947e4307c85
ACR-c63aa0f4018c4e2bbfd2a661eaa754b8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

public class ShouldUseEnterpriseCSharpAnalyzerParams {
  private final String configurationScopeId;

  public ShouldUseEnterpriseCSharpAnalyzerParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
