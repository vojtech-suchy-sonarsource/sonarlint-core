/*
ACR-907d6c993571403a98920bb8f2764707
ACR-9a1040f514724a6d9c6701dd1b6b6c80
ACR-27617c81e2bd40e0b6d224b50d415e29
ACR-5175cde98efc4a9b877a3083252350a9
ACR-917303b7353745d085c337e1f75cb0fe
ACR-c30cb20d1db9436b87bed04719910910
ACR-cd82346ef1d840a5911a43fe7f153563
ACR-a6f92e3320284d9f99319ef6a1a778c0
ACR-59aefd3c9e8d4509b9d17cc5c09d64f2
ACR-b8b660ca02644c42ad55ccd5c54a739b
ACR-d449083ceb4a4ac1872537d7dad478bb
ACR-f1e9e2b68ca64a1a90630d8f2b8f2b31
ACR-229aa80a549e4977a61b7191a700d4da
ACR-dd27d80710fc4acd93d70a21c60d6965
ACR-6dbf54c2d0944b9b9ccfa98edb42c617
ACR-be3f8c134d2d44deac616d28e2b24fb9
ACR-705900f269664c0b978ae6ed52a0a3d2
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImpactSeverityTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity.values()) {
      var converted = ImpactSeverity.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

}
