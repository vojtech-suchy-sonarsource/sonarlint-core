/*
ACR-91829d3493e546abba3e33afb2fccbf7
ACR-e36904d72f334964a9563ccbbb361d36
ACR-9fae37b7a3d244538a2269115bc9c571
ACR-8589d350185f482e815c64ed01340437
ACR-34527b2722ae4d73b4d2bfe7a4123809
ACR-383bbb4e93574a749b5d1d65c8c6040e
ACR-cf6805834d894ebb925c1d4352417546
ACR-199dcc17a0e747d2ab552c79837159da
ACR-40423a21178e4180b5c66ca30581ceb0
ACR-674cdfabb6924bf4b880af5ec7413f80
ACR-88a8d93a15c94ef59f3cfacc41f38a9c
ACR-5b4ce1afd8764090bb9cca2a75d972c9
ACR-9a72cf7e80104762b3f31dc1963e5659
ACR-ba206e99cd454786b652f27a791ef5b0
ACR-ed0659507cc74bcfbf4d6160d93fdc4d
ACR-2ac5a068d2624015b796925a9274a680
ACR-487882a892e844589375bd6d27dc5f42
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
