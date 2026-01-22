/*
ACR-d3088fc9a29742c7ad5be3232227462b
ACR-bbf549746e1449d2b337caf981fc75d1
ACR-b333130560c5487d8f0623d595f5c52a
ACR-92b1f2c2e50748eb9a04728a7f1e5090
ACR-18e1654471f644ce84d1d77b90155e62
ACR-94913b92b1c84e40a590d7817dd65826
ACR-f92dfb07d819482ab19f878131c76fe2
ACR-0fd94dd1f3694d9bbe9b8df80c737114
ACR-1ec1239ea5064a9790422140aa5fe3de
ACR-b92ee8b12fd44df2aa425526184631e9
ACR-863cf6ae41094e84bd31a033ec6f8078
ACR-f0b180ace2004bf9bce1b8dd973f3249
ACR-5e6731ec8829404486d3926083a38038
ACR-e8c2ef87907e4bdaa593ef8c1aad1945
ACR-5eb2f17bdce641718bd400471333a86d
ACR-78b5becaf57346cc8f049b1a5fc5dc01
ACR-1e6e0164feed46fb9045ff31bd4ca6a1
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
