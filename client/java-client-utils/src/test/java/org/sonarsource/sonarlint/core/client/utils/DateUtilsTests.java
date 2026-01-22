/*
ACR-0333f0acd13c4ecd865c08472dd1355b
ACR-920abd9f28884b27bf37ffe8d9afc042
ACR-6c84f949bf944197a05670b4c36c433f
ACR-822ffdf21e394ea0bf1242f0c1e4d163
ACR-5e79bf32bd684813aca5c5fb9d31ab50
ACR-43460bc771f54c0884ad154707f7d1ac
ACR-2c954141025041f085206b1cc998e0d4
ACR-3b739dab940145a4a9c5bcb87eac49ab
ACR-4e51f43d667b40d6b5fe22d5ed49193e
ACR-3476ec7396d944e9a618b0568008a0ce
ACR-afc6935692e4419bad4f788a9f71d3d2
ACR-e0f384b74977497994a65ee186dc65e4
ACR-1189d3ff438a45ceb2770ea525664fa3
ACR-b81041bfd4e34b4f87e8812855ab2fb1
ACR-d219b6bb0d8c45648259cb5caadf1ac6
ACR-13ebd49a9e084e5db91d89e7778a96f3
ACR-e3ab72f857a74f5da1b600a022aa333c
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
