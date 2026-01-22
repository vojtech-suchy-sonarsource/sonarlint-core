/*
ACR-0934be8ddc654aa4a08f17b66e8ead2d
ACR-0428c55c3dc440df93ca44755322482d
ACR-0463de2c3cdb490fbb710e9b470ce2b5
ACR-690c8450f80d4000bffa78bc3e5d97be
ACR-2ad5aa060f1441d7a1cd08a42298aa0f
ACR-ac51c5acff4a46eea309cc101fbf6ca7
ACR-2a00f2e6c9b842688127e09e661bb016
ACR-6f14b781b17040168bddc2fad1666e02
ACR-4c23fe4a32f842e0a6983abf824c2e7d
ACR-945228ca3e9548328e5db09b7c56696a
ACR-d0fc7f1550bc45d4bf5824871d82a9d0
ACR-1a058d9708274fb7ab03d8b5c21bf551
ACR-be62b899e21b4138be07e631444c5128
ACR-cf41cdc0f4da4fe491439d849d8b662a
ACR-8ae4039bd9c64ccfb016259b096a8d2d
ACR-daad486b22cd47f48c7695d1bee6181f
ACR-5e180411ee7241338ea846e628e4eb9b
 */
package org.sonarsource.sonarlint.core;

import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

public class ConfigurationScopeSharedContext {

  private final ConfigurationScope configurationScope;
  private final BindingSuggestionOrigin origin;

  ConfigurationScopeSharedContext(ConfigurationScope configurationScope, BindingSuggestionOrigin origin) {
    this.configurationScope = configurationScope;
    this.origin = origin;
  }

  public ConfigurationScope getConfigurationScope() {
    return configurationScope;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }

}
