/*
ACR-c890b85cb5d042adae65241bddd96648
ACR-980aa2f2652d4f39a4a9e272fc74182f
ACR-2fe2e8438445415c99681fe251914c3d
ACR-fa973af43d1942328465872186c2e2ac
ACR-3d927455937a4d6ab3fd31729e8e2950
ACR-5d9ce97be46c4f96a0c1a4fb003a7f92
ACR-e9ab164e62fd4b76b6f0e342db7db797
ACR-d02c463ec9a8492b8b15a6075230d8c6
ACR-fee9869ae4a949a6ae948d1e68b3eabd
ACR-43ba26b74a0a4f2aa7a67683aa54ce66
ACR-8e30c2e52e1844ce9e9007207f64a661
ACR-17c9a98b4b9f40fdb875459e152d5e4b
ACR-1ae649a434904fe886a6d6c80b4aba06
ACR-d5bce5508fdb46259d5568d0bf9ba543
ACR-857565e542b04c71bc3f8425229556f3
ACR-be3d268eb6504eb38b91beb87e1c11bc
ACR-27e1428174464970a6f0b7ceedc76607
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
