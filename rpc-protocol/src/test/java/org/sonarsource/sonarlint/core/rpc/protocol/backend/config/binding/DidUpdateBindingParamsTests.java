/*
ACR-8c425b4aca174335965ec3e4defe0e56
ACR-de6538d25c9049e9b96f2aa96c3f00bf
ACR-2649f4909b974a7b81310685b2f35a5f
ACR-99c0b03ba03847b1a0ae606632241cdc
ACR-acff2cb503944e6e9d27b600196304a8
ACR-ce1037d0e643453a81ff0b21b38d98a4
ACR-89ed6a3e49db46578e1e8c43973cd0d8
ACR-d413043c9d9442f5acc45ac08368ee84
ACR-fa1a96fcb0a3407a8f247963b7b8af2a
ACR-8b712e282ac2437f852cbc6702b67c1f
ACR-be076073b8134484b2c761c325f6a753
ACR-09498c58ab204bb3a6ae3d1ebdb1ba2a
ACR-a6e38c1c24f044e891d2752826d1af68
ACR-cd16ad282a6740e7bbb0edb553e92c55
ACR-7400614c017b4f6b8156fc5deeca406e
ACR-9aede236163941109a8f42e6f58e9385
ACR-b63035973a214c1695c8f57bd6c48b93
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DidUpdateBindingParamsTests {

  @Test
  void constructor_keeps_mode_and_origin_null() {
    var dto = new BindingConfigurationDto("conn", "proj", false);

    var params = new DidUpdateBindingParams("scope", dto);

    assertThat(params.getConfigScopeId()).isEqualTo("scope");
    assertThat(params.getUpdatedBinding()).isEqualTo(dto);
    assertThat(params.getBindingMode()).isNull();
    assertThat(params.getOrigin()).isNull();
  }
}
