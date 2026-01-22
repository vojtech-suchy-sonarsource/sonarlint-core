/*
ACR-4157bb54dc1540b4b5e1637512148822
ACR-e6ff5411dcbb435ca77e70b919dcd294
ACR-84d4e3cbfe8649948652c67fbca1f487
ACR-4288eb585db94fdf9c90e6015452cbaa
ACR-78c6ffdf1c724f49af388f0a50d0a29d
ACR-e6f1a63c139f481eb47cee4bb9e74b92
ACR-a810e1a1f1f34d5fb500e76deee8c85e
ACR-63bbf60266e04e468203c4c21d67bebb
ACR-e7d53df3d463433498bd5c2cebbfb576
ACR-d0c074105dfd4cc6b6df572c5db0a20e
ACR-ac697e1f2ba24fe1aec98015d26cedfa
ACR-ef9a8dadf6fe40fba61ef2828d93a2f7
ACR-dc67f00aa1df438c879bd46250968ae9
ACR-e3f3c4cc3735487cb14799a4b7e0ce67
ACR-87930dc4842e4ca08a66d8cb17b1ce84
ACR-5991cf46bfc046b0b8718f3eadff0479
ACR-07fc2cbbf22f48e58c6a3db7af9236ab
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

public class AssistBindingParams {
  private final String connectionId;
  private final String projectKey;
  private final String configScopeId;
  @Deprecated(forRemoval = true)
  private final boolean isFromSharedConfiguration;
  private final BindingSuggestionOrigin origin;

  public AssistBindingParams(String connectionId, String projectKey, String configScopeId, BindingSuggestionOrigin origin) {
    this.connectionId = connectionId;
    this.projectKey = projectKey;
    this.configScopeId = configScopeId;
    this.isFromSharedConfiguration = origin == BindingSuggestionOrigin.SHARED_CONFIGURATION;
    this.origin = origin;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getProjectKey() {
    return projectKey;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  /*ACR-cc07a8a157994ec69f292698ab515f36
ACR-85b19d2fb6da402a9bc674996667a62d
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }
}
