/*
ACR-a659cbe2ad1841388c6cbf2eec1fb564
ACR-e78a827897874a788284fa91d63cb053
ACR-4355298a60064494aebf75a238c48224
ACR-847e2b6705944b71b55cc59d29aa1ae6
ACR-5056609244c24fc3a4b72e111abacb08
ACR-1839e0fcea024d20b7ef25279df149ef
ACR-6747510f8a3042dba9900b9650b6f4be
ACR-18000e4f9767474e940511e50d314053
ACR-42ca67d97af24519b7f92e0f2ba91bf7
ACR-935ebb43f5b843c18b1e11da1747008a
ACR-ee99c251e9c147bcb2bcffd613150611
ACR-80e2a53a99784091a9940ef0f08526a9
ACR-4101326d062a42ac8b2d724bb476dbc1
ACR-ccc5f4d53ba54635b4205ce0ffeeeb8d
ACR-e6730b8cadd5466480da2696b32134a0
ACR-ff428db15022408ea466cb796baaa7d9
ACR-752112d354f4432abbc9c4e5ebfb2774
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode;

public class GetNewCodeDefinitionParams {

  String configScopeId;

  public GetNewCodeDefinitionParams(String configurationScopeId) {
    this.configScopeId = configurationScopeId;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }
}
