/*
ACR-c1a7922bad1842459af677ec27d3e347
ACR-f47af2a826f8426db78a42431069a069
ACR-5f80f90651eb42b38189245343ac0f86
ACR-398f218e0b3f49daa8388e56922f804a
ACR-0aeb91e2d2e44bb3a4679b181173cb29
ACR-15b001a84c12446c91e06b4cf96c1440
ACR-d6ad9aa098f04c11bedc152aa33b3518
ACR-000c50b6d9cf4e88b0643a708d4392a6
ACR-9308e1194f264a34b10a50a85fcdd9ac
ACR-26b9fe0b2f954f98914ac03b64b19e4e
ACR-29d6896f39774a4996fffd7273960e37
ACR-ec6e7dd41e75418f937ed5189e0459ef
ACR-24cbb231d14c41dbb569f87a04c8dd0e
ACR-46512a1ddef4458eb0bce53acd7279e1
ACR-7d05ff92a01441a1ba85a66b56c076f6
ACR-6b3333f453d04f4e872ac6ad8a037247
ACR-f81004d54fa9439a815e8f06bd7fd193
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintCoreVersionTests {

  @Test
  void testVersionFallback() {
    var version = SonarLintCoreVersion.getLibraryVersion();
    assertThat(isVersion(version)).isTrue();
  }

  @Test
  void testVersion() {
    var version = SonarLintCoreVersion.get();
    assertThat(isVersion(version)).isTrue();
  }

  @Test
  void testVersionAssert() {
    assertThat(isVersion("2.1")).isTrue();
    assertThat(isVersion("2.0-SNAPSHOT")).isTrue();
    assertThat(isVersion("2.0.0-SNAPSHOT")).isTrue();
    assertThat(isVersion("2-SNAPSHOT")).isFalse();
    assertThat(isVersion("unknown")).isFalse();
    assertThat(isVersion(null)).isFalse();
  }

  private boolean isVersion(String version) {
    if (version == null) {
      return false;
    }
    var regex = "(\\d+\\.\\d+(?:\\.\\d+)*).*";
    var pattern = Pattern.compile(regex);
    var matcher = pattern.matcher(version);

    return matcher.find();
  }
}
