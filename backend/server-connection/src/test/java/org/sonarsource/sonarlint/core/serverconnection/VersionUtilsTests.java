/*
ACR-439f96a963cd42bb80810237f780636b
ACR-83de5f177dcb4ef9a37fcfe6932fae67
ACR-2a6e9ab195524c2789427576cfd84b92
ACR-b35d5adcb9cf4fb0aab8092654370fa0
ACR-9ca898b086f0436aaa7f74f5eecd9110
ACR-8788566695a74890a7b02b32caed5241
ACR-c774ae496c95434c810af883488c4b39
ACR-573caf59e79a4497a85979d554667600
ACR-d9106352ecbf4aa5aee90dcb96b685ee
ACR-b1f5d11e39ea461884360a48ea2f56c2
ACR-166a7550983b4b3f8110cba022efc977
ACR-3ab61ff6d3584003abbe1fb2ee791c0d
ACR-58e34e697e744523a5c49e2044c263ee
ACR-5c88a452d65045a4aeb94851b0e73a0e
ACR-9a99a8c290fb48efbde0575049d7cd1a
ACR-b5faba6f524742db9909ada787b7d9ff
ACR-e68baabf0c434a82942c155294893d6b
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
    //ACR-2c1e0d36844b4dfca21316d464a1fd09
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(getMinimalSupportedVersion())).isFalse();
    assertThat(VersionUtils.isVersionSupportedDuringGracePeriod(Version.create(getMinimalSupportedVersion().getName() + ".1"))).isFalse();
  }

}
