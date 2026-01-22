/*
ACR-5ed09bc7e587408b9389d117bbb93202
ACR-2012c8a11ef9480a8b95954d92880cf5
ACR-94f66a49d89b478e8e06af91427f6bc8
ACR-87e785ac7c3c4ad192ffb882c5730daf
ACR-eee1bbf826394a8c81aa132bf871c42d
ACR-05c88f3d7ab84710bf9a0e9ac9d9268f
ACR-c21baa3a533d42f8a0a83f991554ffc5
ACR-b6a8bbd8277b491e97d8885ebcf6ee50
ACR-1a61a452591c4f2fba93dfbe12d348eb
ACR-954ba24e70664228a2fd3af0370e5037
ACR-d4df5b62b2504b43a057b9f83c93c9d9
ACR-bbe5e3ce777649a7959ce164740050b2
ACR-dff8c75055a94a1b804ac723a840b5ae
ACR-613d5a0acb5b4f5aba732d740d5d2c46
ACR-383a502b9cab4df9ac2ade38f428fbb1
ACR-fae68349489f4c0b826ee78be2ba7ae5
ACR-4e0d823e45a645dbbe36dc6e000b15b2
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

  /*ACR-8b5ce390628e41ebb56a1010be4560a7
ACR-5be6844112ba4df192f1e42b38b686a3
   */
  @Deprecated(forRemoval = true)
  public boolean isFromSharedConfiguration() {
    return isFromSharedConfiguration;
  }
}
