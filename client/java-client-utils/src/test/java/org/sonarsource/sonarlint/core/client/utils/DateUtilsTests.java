/*
ACR-69e4c7663df64759822d65205afd3248
ACR-64372eb70c404124b2bead3cc33bc0b7
ACR-423b2c01fdc643c7bc6276d48d456ce3
ACR-75ea648c44a6445c9ae95bf1a2a3e6b0
ACR-003f6750e940472dad9c080b2cf6ef73
ACR-cafd130360e14c3296ff1ae2a0d0551d
ACR-ecf5e350e6a64d2f93c9307a9accbade
ACR-a54226e174e54d4ba7a672b983e2af9b
ACR-c402b20c154a4ebbafc02425aec8ce57
ACR-9f5e1de6f55a401eacca0b87b31183c7
ACR-bc782270acce472fa1edc1b3bef7bbaa
ACR-42c551bff9c844729e1419441a70c32d
ACR-accf8ae39c3147179c01498b31282614
ACR-f5b5baec9fe34e1e88e69ebf236c2a84
ACR-36c9ade48d764a1ba636c2eb28f05460
ACR-77f63ac618124c26a179df4545b27825
ACR-7a5258a17c0c4642a405c687f45635f9
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilsTests {

  @Test
  void testAge() {
    assertThat(DateUtils.toAge(System.currentTimeMillis() - 100)).isEqualTo("few seconds ago");
    assertThat(DateUtils.toAge(System.currentTimeMillis() - 65_000)).isEqualTo("1 minute ago");
    assertThat(DateUtils.toAge(System.currentTimeMillis() - 3_600_000 - 100_000)).isEqualTo("1 hour ago");
    assertThat(DateUtils.toAge(System.currentTimeMillis() - 2 * 3_600_000 - 100_000)).isEqualTo("2 hours ago");
    assertThat(DateUtils.toAge(System.currentTimeMillis() - 24 * 3_600_000 - 100_000)).isEqualTo("1 day ago");
    assertThat(DateUtils.toAge(LocalDateTime.now().minusMonths(5)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli())).isEqualTo("5 months ago");
    assertThat(DateUtils.toAge(LocalDateTime.now().minusMonths(15)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli())).isEqualTo("1 year ago");
  }
}
