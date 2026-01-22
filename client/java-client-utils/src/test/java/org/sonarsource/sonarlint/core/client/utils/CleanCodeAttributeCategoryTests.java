/*
ACR-c726fd4230ee4bb8bec39bd18ca0d13e
ACR-6f698f41d8dd419b9d58dc9692dc4e68
ACR-cadcdafcc42346979bef361f18fe46a1
ACR-4e1c98d383774e2480cec5e134fe3db7
ACR-787bda164535404ab6f730625b5f5899
ACR-3260f01accc7448a9ed00eef41c1496e
ACR-30b53e0dca184099ae327467603fb7e8
ACR-abdc4f99ba174945a0f962d674fbd0eb
ACR-1ad86c6a943c4829a3c4c67e2793afc1
ACR-856520320c194d04b6675904e5a193c1
ACR-0acd0f1f44864efd82045c554fba4d24
ACR-9ff2d8826c0041689d703073f563f27a
ACR-f38333c0fb1f4a679563d9f332fa98cb
ACR-7082cec3aacb4ff1905668b96ff7c430
ACR-9f49700ee80c4aa0a7e26ff2346fad41
ACR-d0b3d94e61134b2eb391193ab7af9eff
ACR-e2c6d28e90194b439ed2b5ac2b51b2c1
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanCodeAttributeCategoryTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttributeCategory.values()) {
      var converted = CleanCodeAttributeCategory.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

}