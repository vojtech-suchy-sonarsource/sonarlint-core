/*
ACR-96481a4d346e430ba36b34cbe8cda148
ACR-6c3cbe0179274308a00c92f26a4a61c4
ACR-1d3d0b590632434c95a53b8dfe3f079c
ACR-03f48fd7493e46a8a0522c3be22390df
ACR-1721d1c58b8740968211e4a3b447c5ce
ACR-e2e33d8d7d164a6481dfaec055b59399
ACR-08756fb02a1a46e2a1708a3c53f81eaa
ACR-d3eee96732e94a9a86e8d6002c7ed2ba
ACR-5ca70fc7352248c4bc116aeb917817d5
ACR-47ee484155ce43bc8e944f34aa1f5f35
ACR-fab7a85a84f94aea933d1317abbd5963
ACR-300056ece3b346279056a3e7c1e6362d
ACR-9806bbbecd724c70bb9ab68bfec78bad
ACR-f7fcb5d571b848cc86fc3cbaabd2d5db
ACR-5505c533fd294a6b8266ebcab6e83d49
ACR-3772161bcaa84d85a24ee5d6ab245983
ACR-cb29c404855444fd917ea956e9bbf8c5
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
