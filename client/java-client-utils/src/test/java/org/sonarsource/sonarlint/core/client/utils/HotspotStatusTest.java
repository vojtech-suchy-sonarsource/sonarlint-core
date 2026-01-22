/*
ACR-3507b937219044ee8320ccd706e68225
ACR-108c9d8716e341569db86d2cb370b7f9
ACR-86a0482799d04278b0fd7ecd83d649b6
ACR-a33307b8e14147d58ba1d6972dac8acc
ACR-6a801ed0309a447ca0967812dfee82ad
ACR-75208e24f0924e748b46296944127860
ACR-af4bd3da705147ebaa65ad0c09a9ca7f
ACR-d28cb854c47c41f6a86e3b55b8cee890
ACR-270d3ee7fa13484f8723564fed1c9dcd
ACR-350e10087d0b41d2ab0b415d50956217
ACR-67d8032da409437c982394e7ce6d7850
ACR-f0cded33514b47778d976b5bda618f09
ACR-0a02fe9b66e143b79803a12226123679
ACR-0b8ad5033703414182084c493d2bcded
ACR-f110df988c3c4281845bcefb3523d602
ACR-d6e417396ab54fe6aa1a0724c62d04f8
ACR-e822fa3538ab48c7814b75af229bd9cd
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
