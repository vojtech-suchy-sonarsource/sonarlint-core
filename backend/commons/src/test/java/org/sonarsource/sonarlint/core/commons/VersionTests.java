/*
ACR-d3406c5fc6af4723a292ef5d45069094
ACR-85a56593026f4db38ee9afbb9d377c8b
ACR-fb01174bb7ee4d5d995ba573e1bc5c7e
ACR-6e75a1d342b640f992c8b1f0bac03e55
ACR-f2e123ef490e4ec39b47e22776f6c632
ACR-4188ed7d80f34affa22a7e2dd6ccf749
ACR-5a58ddd11b8d41938d04caec60263f3f
ACR-5ecb6a2cc8ae4f48b4318b49c71f72f0
ACR-957dc3f04ef5454ab4ae200d8bfb9711
ACR-142def3366df40049678d12118ced42a
ACR-e96cd6034e504c7087e71b531c06d7a0
ACR-560ada75b5284ce9a5edc65d51424a1b
ACR-a09935d22b5f42378b5eb11fbacb070a
ACR-6111f479d4df404784b817379592217b
ACR-a82057eed23c462aba8084bdcbcaa04b
ACR-9ebff5ffa5354cc793dbf14700c11d82
ACR-d690b4018d234724bda505f14623420e
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
