/*
ACR-def149002f7a46958de29434304f3964
ACR-ba84128b4c2a4edb988b22e7222c8bc3
ACR-7df742c1e2af4b7695dfcbecb3fca04a
ACR-a36cb71633dd44a6a8254006ee0aac53
ACR-bfd9273796434004ac7c0994df2fdad5
ACR-319a053c2d5e4692858b295ea8bad024
ACR-f5cb69d9e48e48d9b03396e2f285dbf1
ACR-4aa3be06065d435786393ae88d08efe1
ACR-63cd3c989e4a45bdb67477640ee56468
ACR-94f3f5350149474abfee4ed044614db7
ACR-61060f56a9e5467693a0fe0a0520bbae
ACR-809d4dc8ca724c3895ad014b08e03611
ACR-eb20a02d4c754be7a612f6d16eff16d0
ACR-e2685175afc54d53b46bda3d6f812fee
ACR-3625a0257af04cde8623b17fd6240d1d
ACR-b280b92740aa478ab32b16e2ddf949cd
ACR-701de985d0cf4c26b701dba29d089702
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

public class GetBaseDirParams {

  private final String configurationScopeId;

  public GetBaseDirParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
