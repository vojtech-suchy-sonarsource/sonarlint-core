/*
ACR-7ce1541905ea4984b6589e23d5070e30
ACR-0196d54b0e7d4a04a8a4cde7745f5e36
ACR-0f5daccb76d84c4fa54af03bc4f31f01
ACR-486a273997b34e0e805899e2c59e2526
ACR-a69156b4208a41e69349fa6dca7c4410
ACR-626a60017cf14dfe8a467417649a6377
ACR-40cbbb5013f34a4a9d9e63acce2dded1
ACR-5102baec387c4763a45b545f9ff9af24
ACR-67854807bafc404791b964ae02e8d36d
ACR-da3c3730d7fe4e5b86d0517321e439e6
ACR-e661ada7cb5e4686af9d4c894b41971f
ACR-5517063fbfb14cc78bfd3562c48890bb
ACR-f0aa90b0ad1e4e50ac3cbb621225d3a4
ACR-97ee9f4102eb46c1a9ed51252a296b5f
ACR-5d1c05add3db4b4c95c6b235d322fb95
ACR-91845f17733943cfb31c2d3c27db7d49
ACR-cbd6cd87044f4f219edb18ee9621c3e9
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
