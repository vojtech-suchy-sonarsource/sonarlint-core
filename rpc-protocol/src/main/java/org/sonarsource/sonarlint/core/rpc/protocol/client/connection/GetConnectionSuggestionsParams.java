/*
ACR-7d2c325f8a6049ce97dea54364ddb363
ACR-b7c116ad1bea4699bd4d9ea596f6f679
ACR-8beb972fe147456d9fb1b5514210905b
ACR-2aeb569528f2408ea1888a916e0b6a67
ACR-73442c5615c64018a6f7c2481566314f
ACR-67686351ec494034b599b2a90c90ba28
ACR-8f63012d30ba4f818156299336392505
ACR-373441a9b5cf4634935dce2c4b845b59
ACR-fa8ba71873094aefa96860cb841597ca
ACR-ad2c83653e6840dd954302f195dfbf82
ACR-824ffc5ec0e84ca09d861c87d278bd51
ACR-71613513b3534ba7853edf193c89e428
ACR-52e616b96a4548d0aa03113e464643e6
ACR-2f2f65e5b2e84b219d96b40687e659e7
ACR-017caed98a784ee5a26b5013e9bd43af
ACR-aab3ae69956f420ba5c61280111959f5
ACR-e7ad93b6a6484a68b9f24fde90c1b7cb
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class GetConnectionSuggestionsParams {

  private final String configurationScopeId;

  public GetConnectionSuggestionsParams(String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
