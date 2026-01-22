/*
ACR-f31c7868b4174099920f2bd78bab80ed
ACR-4b522456d0e041289e954993a0d91514
ACR-204922f5b9354ac6b2b862ac8b028196
ACR-d56f56eeca044c5396aea055c706da39
ACR-d4646871ea6d4d0cadf143c72780e146
ACR-3b741c7786fb43e9a2e6dd4d4a247c8b
ACR-4af6b575653a43dfb72f698b50861834
ACR-760d97b78a4b435483a37a3192b381e0
ACR-92227da3ec324352a27542b673937bf0
ACR-6d0906d9b4d048e0a04503902763bf02
ACR-f6d7ade1d35c4fcba525c73cf5f2eb04
ACR-bf5b597841b848fab393a87a92ef4340
ACR-d7801e04e4d54624ab8157cd3479bf86
ACR-d0df6942955946deac69b7b9154ca20b
ACR-6d6f5777ace24810a36840bf5d19c294
ACR-d8224c2f42bf4263914357c38045f4c2
ACR-9203ecc3c20c4f5594c07a143f0e7965
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionTests {

  @Test
  void test_fields_of_snapshot_versions() {
    var version = Version.create("1.2.3-SNAPSHOT");
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getBuild()).isEqualTo(0);
    assertThat(version.getQualifier()).isEqualTo("SNAPSHOT");
  }

  @Test
  void test_fields_of_releases() {
    var version = Version.create("1.2");
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(0);
    assertThat(version.getBuild()).isEqualTo(0);
    assertThat(version.getQualifier()).isEmpty();
  }

  @Test
  void compare_releases() {
    var version12 = Version.create("1.2");
    var version121 = Version.create("1.2.1");

    assertThat(version12)
      .hasToString("1.2")
      .isEqualByComparingTo(version12);
    assertThat(version121)
      .isEqualByComparingTo(version121)
      .isGreaterThan(version12);
  }

  @Test
  void compare_snapshots() {
    var version12 = Version.create("1.2");
    var version12Snapshot = Version.create("1.2-SNAPSHOT");
    var version121Snapshot = Version.create("1.2.1-SNAPSHOT");
    var version12RC = Version.create("1.2-RC1");

    assertThat(version12).isGreaterThan(version12Snapshot);
    assertThat(version12Snapshot).isEqualByComparingTo(version12Snapshot);
    assertThat(version121Snapshot).isGreaterThan(version12Snapshot);
    assertThat(version12Snapshot).isGreaterThan(version12RC);
  }

  @Test
  void compare_release_candidates() {
    var version12 = Version.create("1.2");
    var version12Snapshot = Version.create("1.2-SNAPSHOT");
    var version12RC1 = Version.create("1.2-RC1");
    var version12RC2 = Version.create("1.2-RC2");

    assertThat(version12RC1)
      .isLessThan(version12Snapshot)
      .isEqualByComparingTo(version12RC1)
      .isLessThan(version12RC2)
      .isLessThan(version12);
  }

  @Test
  void testTrim() {
    var version12 = Version.create("   1.2  ");

    assertThat(version12.getName()).isEqualTo("1.2");
    assertThat(version12).isEqualTo(Version.create("1.2"));
  }

  @Test
  void testDefaultNumberIsZero() {
    var version12 = Version.create("1.2");
    var version120 = Version.create("1.2.0");

    assertThat(version12).isEqualTo(version120);
    assertThat(version120).isEqualTo(version12);
  }

  @Test
  void testCompareOnTwoDigits() {
    var version1dot10 = Version.create("1.10");
    var version1dot1 = Version.create("1.1");
    var version1dot9 = Version.create("1.9");

    assertThat(version1dot10.compareTo(version1dot1) > 0).isTrue();
    assertThat(version1dot10.compareTo(version1dot9) > 0).isTrue();
  }

  @Test
  void testFields() {
    var version = Version.create("1.10.2");

    assertThat(version.getName()).isEqualTo("1.10.2");
    assertThat(version).hasToString("1.10.2");
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(10);
    assertThat(version.getPatch()).isEqualTo(2);
    assertThat(version.getBuild()).isEqualTo(0);
  }

  @Test
  void testPatchFieldsEquals() {
    var version = Version.create("1.2.3.4");

    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getBuild()).isEqualTo(4);

    assertThat(version)
      .isEqualTo(version)
      .isEqualTo(Version.create("1.2.3.4"))
      .isNotEqualTo(Version.create("1.2.3.5"));
  }

  @Test
  void removeQualifier() {
    var version = Version.create("1.2.3-SNAPSHOT").removeQualifier();

    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getQualifier()).isEmpty();
  }
}
