/*
ACR-3a8d80508c4348c4811c1838f7cf0d95
ACR-98b5eab66bb44b03bc25fbcae284a168
ACR-ef9aa9f94d3d46ccb1f995c534329f63
ACR-36851a6cc9104bc9b9c5afc4ab6ad7da
ACR-2a1da1e6a021494b8cb9ea258967cf9e
ACR-6ebaa40e6e7f4a86b0b5377e4a6dd809
ACR-88d4a5a3af7d49689e263542fc356e59
ACR-b9346f36d9f045f9a1f9b9b3cb7bd22a
ACR-f70b0ef6a9d7468dade2184aed1252ed
ACR-49ca7b41746644f3be07d9ea476c41cc
ACR-693d779a21af45428aab3b63d75fadb7
ACR-3bd159606a4345a2b356e9064ad3b2fa
ACR-b95a0b5e59fa4a07b24bfb1359ce4a95
ACR-42d593499119497285550dd426491ea7
ACR-9e803a24cb0e467c94c3ac2d8b325383
ACR-78b424aeaf94432ca10b83de0d537a4b
ACR-3740781a8afd42848f49f05194e8cbe5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.binding;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

import static org.assertj.core.api.Assertions.assertThat;

class AssistBindingParamsTests {

  @Test
  void new_ctor_sets_fields_and_isFromSharedConfiguration_true_when_origin_is_shared_configuration() {
    var params = new AssistBindingParams("conn1", "proj", "scope1", BindingSuggestionOrigin.SHARED_CONFIGURATION);

    assertThat(params.getConnectionId()).isEqualTo("conn1");
    assertThat(params.getProjectKey()).isEqualTo("proj");
    assertThat(params.getConfigScopeId()).isEqualTo("scope1");
    assertThat(params.isFromSharedConfiguration()).isTrue();
  }

  @Test
  void new_ctor_sets_isFromSharedConfiguration_false_for_non_shared_origins_properties_file() {
    var params = new AssistBindingParams("conn2", "proj2", "scope2", BindingSuggestionOrigin.PROPERTIES_FILE);

    assertThat(params.getConnectionId()).isEqualTo("conn2");
    assertThat(params.getProjectKey()).isEqualTo("proj2");
    assertThat(params.getConfigScopeId()).isEqualTo("scope2");
    assertThat(params.isFromSharedConfiguration()).isFalse();
  }

  @Test
  void new_ctor_sets_isFromSharedConfiguration_false_for_non_shared_origins_project_name() {
    var params = new AssistBindingParams("conn3", "proj3", "scope3", BindingSuggestionOrigin.PROJECT_NAME);

    assertThat(params.getConnectionId()).isEqualTo("conn3");
    assertThat(params.getProjectKey()).isEqualTo("proj3");
    assertThat(params.getConfigScopeId()).isEqualTo("scope3");
    assertThat(params.isFromSharedConfiguration()).isFalse();
  }

  @Test
  void deprecated_ctor_keeps_boolean_semantics_and_sets_fields() {
    var params = new AssistBindingParams("conn4", "proj4", "scope4", BindingSuggestionOrigin.SHARED_CONFIGURATION);

    assertThat(params.getConnectionId()).isEqualTo("conn4");
    assertThat(params.getProjectKey()).isEqualTo("proj4");
    assertThat(params.getConfigScopeId()).isEqualTo("scope4");
    assertThat(params.isFromSharedConfiguration()).isTrue();

    var paramsFalse = new AssistBindingParams("conn5", "proj5", "scope5", BindingSuggestionOrigin.PROJECT_NAME);
    assertThat(paramsFalse.isFromSharedConfiguration()).isFalse();
  }
}
