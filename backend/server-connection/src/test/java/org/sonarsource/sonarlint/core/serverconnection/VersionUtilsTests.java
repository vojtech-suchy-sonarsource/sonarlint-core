/*
ACR-f2198a233b96439493b8a5a5dab13ff2
ACR-dbfa6934d34246f49b8009eb5d9df7a6
ACR-5d52f199c9dc49a29fca01b57e591e62
ACR-a872792dc8724db9800650d54ae839bd
ACR-297315ca011f441b9f338b422a1d0fc3
ACR-12f9c5dd890343bbbab4a88a7f71fb0c
ACR-13e2b13d2ebd42cdbe51c73a7bb9282f
ACR-9f54eeabe1e24d9fac371c4729aaa44c
ACR-9b2aed72a0aa4c5bb6d5075d2262dafb
ACR-6ab673dc1e5e4eb180f5c7b6a29210e4
ACR-343ae75099fa44a59291ae14b8912e92
ACR-87063d1198b74e4297497e58731c7c2a
ACR-bbea34b757614064852ef5dd7f9d6ac0
ACR-2b780617a4da4342b739728d15c00ea0
ACR-742cf88706a14009835cce896b493725
ACR-9cf0130edcbb4a289dd21f474e2cd645
ACR-c8c6fdd16e094768b56527d11e7cd6e3
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.Version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.serverconnection.VersionUtils.getCurrentLts;
import static org.sonarsource.sonarlint.core.serverconnection.VersionUtils.getMinimalSupportedVersion;

class VersionUtilsTests {

  @Test
  void grace_period_should_be_false_if_connected_current_lts() {
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(getCurrentLts())).isFalse();
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(Version.create(getCurrentLts().getName() + ".1"))).isFalse();
  }

  @Test
  void grace_period_should_be_false_if_connected_outdated_version() {
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(Version.create("5.9"))).isFalse();
  }

  @Test
  void grace_period_should_be_true_if_connected_during_grace_period() {
    //ACR-d762cc165a4a41a998c8124709a603a6
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(getMinimalSupportedVersion())).isFalse();
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(Version.create(getMinimalSupportedVersion().getName() + ".1"))).isFalse();
  }

}
