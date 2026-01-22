/*
ACR-161e273136884958b82a986f18b96bca
ACR-35602c720b2f40969a2b96bca47b464b
ACR-356741f3055c4fee96447159b73eece5
ACR-2f051eae7a01457da3b341fa734c4639
ACR-f632a02020c64c92bd8fc376503dca63
ACR-0d9405ed3b9c4d10ace40924cea9a99d
ACR-3c9702857cfd4c2092f7abc55c7925cb
ACR-f7fed10387254b5782bf9e0c3c7dde3e
ACR-c7c7037182b54d2487199f5e65b46319
ACR-2e6393ee217a4dfdaa39419636fd4778
ACR-033aef5923c94e3898f26d2fa0fff889
ACR-31886f42defc4d32993b22c3e8f035f6
ACR-e3115ee9c6cb4999855aa3022f2d881c
ACR-318b350da8c9491ea066223a4a3c28ac
ACR-7c7112ac5b7c4b2aadceaadea71b6f6c
ACR-baf49903e48a43aa9fe26402c1da85d5
ACR-d474482ed5184b24b8008bc08d3e529e
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.DependencyRiskDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DependencyRiskTransitionStatusTest {
  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : DependencyRiskDto.Transition.values()) {
      var converted = DependencyRiskTransitionStatus.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

  @Test
  void should_get_title() {
    assertThat(DependencyRiskTransitionStatus.SAFE.getTitle()).isEqualTo("Safe");
  }

  @Test
  void should_get_description() {
    assertThat(DependencyRiskTransitionStatus.FIXED.getDescription())
      .isEqualTo("This finding has been fixed.");
  }
}
