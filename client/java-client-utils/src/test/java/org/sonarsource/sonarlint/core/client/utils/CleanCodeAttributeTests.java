/*
ACR-5b87901ff1c04186b8a3a996f56c7c13
ACR-cf80d311a83f4ef682793e6ed8d82807
ACR-ec8b186c8016436c9a1d391079ae9fc5
ACR-136e8ac82f65471686c3aef850664634
ACR-d6e7925f13ec4c4cbd7f23a2333dc9e6
ACR-2463eb22d6c54bff817b8a42a10d82dd
ACR-a7f4cb04474d479bbcb3d3b4c8fd28b8
ACR-7ab00c4d3aec4cc5a65e5e00e85055fe
ACR-143c7c6a376d416f88f795d747899510
ACR-e2f62a3bd71b488e845ff77af7d4248c
ACR-210e0495a0014837ad654acbdec072d4
ACR-411d692f11f04e61a210c250a50b012a
ACR-35d034909f1e4f2493a54e6e09cd7a5e
ACR-8803d28e776946fca1a1c67bfd595078
ACR-fd63ce91bf224cfa976a4200e6d358d7
ACR-2b7a018d9bd14069943d5a6c09882b9c
ACR-5ced23740e394b48a1707b70768a1156
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanCodeAttributeTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute.values()) {
      var converted = CleanCodeAttribute.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }
}
