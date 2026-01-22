/*
ACR-d8315422e3d744b4a05d04be636c74b0
ACR-a6a8bb6963f84f4ebd4b2f02da834e70
ACR-d0b881273aaf479180c6db17d942c2b2
ACR-6ec051fcc6a545fcbcc98cfc39faa0ce
ACR-fece34f542494ebfa124aedae55375c9
ACR-7a487be4a4874f29932a024590a87de8
ACR-25dc95974e804e58ae4c5cca0ec30117
ACR-12a652afc5214818ab68c8a880a32bf0
ACR-60da72463d3d4787846d80d7f577fdbd
ACR-3ca8673158b04fe8af04615e11f89f16
ACR-ccb14b753e6a46949c8aabf26a753fcd
ACR-c2b4ec0e902b4c4dac25d307139c0217
ACR-0d794d156ec34030adc5f25a95f3105b
ACR-0d915c7d6d584577a01ecd115ed1b0d6
ACR-53ec3a75add9431390f5221c2e6eade1
ACR-bf1ba1b47c654d7490bbfc9603879afb
ACR-7e5a81401aab43898df0473afb4b54a9
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HotspotStatusTest {
  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot.HotspotStatus.values()) {
      var converted = HotspotStatus.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }
}
