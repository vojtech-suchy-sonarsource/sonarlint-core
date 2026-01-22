/*
ACR-b2178966c0dd4e359adc2395e3017424
ACR-33f3d22285334939adbcdd032988998d
ACR-e155ba7afcbc4b428b1561725a044d01
ACR-b36a2a49a7bd4afcbdafab5f332966e4
ACR-d663ff4f43424639841d00cf22a75716
ACR-936d82109fde4002bde7a6bc4363c61b
ACR-5755a9b8cd344deeaf8e1f043aed7937
ACR-7f79b1c6ec8345cfa3f4c2720443e39e
ACR-b35b7b26fff3459283f47f93fdf96b7f
ACR-f1f48733ace54be78414030b1038a2a0
ACR-e03d157b725b4cb28aab6642f2580fe7
ACR-ce947a317ae443d9ba2e0d485b66d3bf
ACR-324d1959679f4907bc37754715ec19ad
ACR-7febe59f71d14d2a93b11a2a71a79fce
ACR-3b27b4e5fb384b49ae61179a02d3d9f5
ACR-7cef04011e364d1f80340a27b8eb22c4
ACR-a4faa2afe2a84e23b4423c0a4cefce71
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SoftwareQualityTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality.values()) {
      var converted = SoftwareQuality.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

}
