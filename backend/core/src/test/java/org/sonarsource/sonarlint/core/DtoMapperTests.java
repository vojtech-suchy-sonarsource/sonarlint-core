/*
ACR-f816ff710893419ab57535b8e67a13e4
ACR-d471fd950e4741468be1457dd791ab3c
ACR-ab3e3038cb334ff19c3139a8f941deda
ACR-632c0cea594f41fb99171a3975deb789
ACR-ca13ed0913ac46a28b71d79532cfecdc
ACR-ddd5396ba1a24a0e91e5458868bfa524
ACR-27d06d735153464a9da3609fc01843a3
ACR-d2c126f2879841a19ea221ebee3d8dc3
ACR-9dc79b8429c74ea5a679081d50a8dc0f
ACR-fd03865b7b404cb094e3d08bb1624f67
ACR-7417a4b942d1422e9f6d594fb38f6fe8
ACR-4954fce6affe40c6b89d3fe7a57fe8bb
ACR-b734856337a141aeb952a0d3d29cf129
ACR-d02cd1e72d9f45d8a34fffca0e31b96c
ACR-b3d5b642608a4819889543c1f3e09256
ACR-d69b9ee6d2f640e6a9c431f6649cf0da
ACR-6e4b7a7710c743c7b849a7e12a68dfd9
 */
package org.sonarsource.sonarlint.core;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.tracking.TrackedIssue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DtoMapperTests {

  @Test
  void should_throw_if_hotspot_has_no_vulnerability_probability() {
    var trackedIssue = mock(TrackedIssue.class);

    assertThrows(IllegalStateException.class, () -> DtoMapper.toRaisedHotspotDto(trackedIssue, null, true));
  }
}
