/*
ACR-6e25d483291545fe8378861db2e8f66f
ACR-557efd3c91c24f3ca15bcba21d00989e
ACR-9aca5c0e8e544dcba5afe512f64a5431
ACR-a9e2a455c7ae490395d9eeefedf8d065
ACR-754c3639096245f0876248f6a0f5bc8c
ACR-c17d327cb2344c599137a86a68cfa07b
ACR-19c948d61b34411388ef73d142dc4536
ACR-e5f2abb36aee426d9ce6cd54497cae67
ACR-5ce12e5793214b009e60fdec55acffdb
ACR-1d28dc4156ac412da286bd526e7f282f
ACR-34e9c265e1cf4d19819f41679439a93b
ACR-c7679c813c9c4bbaa897dcc2135d9e70
ACR-51829a923b5547f88b852bc62d2f0e0b
ACR-5b8dd63d390c483ca944af41926f3d93
ACR-8db9e8e60fff455b98a98b07e31f7f6c
ACR-2a1752d2e7694a26ad4df0ea92f70668
ACR-d911c6f77fe3448385e62d28fcc1093e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class AssistBindingResponse {
  private final String configurationScopeId;

  public AssistBindingResponse(@Nullable String configurationScopeId) {
    this.configurationScopeId = configurationScopeId;
  }

  @CheckForNull
  public String getConfigurationScopeId() {
    return configurationScopeId;
  }
}
