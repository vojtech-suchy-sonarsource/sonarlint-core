/*
ACR-fe9d89fb57d243ecadfaabb1810435ac
ACR-91c04fd221c442c083e0f8b473dcc8c6
ACR-d08d07d238e2486ebe3ad21eab7ce2d6
ACR-6c85d95facf34a359bdc94ccbef7952c
ACR-0f416b6124704143b9fcc1f66f349de7
ACR-bc0ecebc77f2418e91db41a4b3c72269
ACR-24adc44db4984cb0908608e92a4320a2
ACR-e7ac488b9dbe4eb9a51110add2d40bc2
ACR-1001472ddffc4cb58319cb1f2af83d5f
ACR-8b3e580bf2714134973f8a83dd7b0350
ACR-db979fcd21184290ae707a11a35f7125
ACR-5e43a318bfd945d1b2d3540ac05606e0
ACR-b3fb505f2d034377b572cbd5dde47284
ACR-b9dc9fbf01e641288948d87e4598e06f
ACR-e94562ef92774b1d9bfbf81dc9ac94ed
ACR-f8ebe8c116cb49a0a83d0e8ff35bf5a0
ACR-50f7cbd9d9904c5199649696a20d24f2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope;

import java.util.List;

public class DidAddConfigurationScopesParams {

  private final List<ConfigurationScopeDto> addedScopes;

  public DidAddConfigurationScopesParams(List<ConfigurationScopeDto> addedScopes) {
    this.addedScopes = addedScopes;
  }

  public List<ConfigurationScopeDto> getAddedScopes() {
    return addedScopes;
  }
}
