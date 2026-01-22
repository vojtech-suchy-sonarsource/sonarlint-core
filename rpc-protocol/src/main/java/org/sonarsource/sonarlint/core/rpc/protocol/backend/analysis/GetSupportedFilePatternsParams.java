/*
ACR-6519ed67c0e94c96959493d08b1abb57
ACR-c526a4a7445747c7a5ea31dcb1ee6bb0
ACR-f4b41e55cc3142938b3ff1d5a483b579
ACR-03bf7a731b12489987d505a143d0357c
ACR-037ea702401d475da135fbae911bc20d
ACR-bf74dc6948ad4a8988d99b2ae98652f9
ACR-354365d78a1a4165aeaf77f0d3cff9ae
ACR-7cc17363d6d445c5882035381d74400b
ACR-6af0eb2a6c2746dfbcc149aa3855c986
ACR-0dd4ed2d17004c01935187751b29b54d
ACR-0f609d7049d7448a81ea2b6af9ce47be
ACR-3a4526ecf4c74927b1d459197ba24135
ACR-b68ab384c08a4586a90a977fae25ce91
ACR-8e415af5ed884a1e8d565147abc6ad9d
ACR-067c493de66c404799ba05eae4ceda4f
ACR-8307ad68672d4148be1a985f572d881b
ACR-b395fb6935ac4499b1aa8de6328e487a
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
