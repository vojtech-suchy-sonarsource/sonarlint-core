/*
ACR-561ee398ef1b45a091aec0f37d0904ac
ACR-c65e33b3923f4d91b3090c2984dc7645
ACR-d1d8243acf5f49b28a45561ad4ebbc2a
ACR-f89af9c2bb0b495c9b0ea6a21a53419a
ACR-2f1795325d9b44e29ffa754c84597d27
ACR-1246144fa8a4465c93ddef379fc8fdf1
ACR-4f1a2ef127d0435c8186e34c4b39fe27
ACR-ab63b0d929334ba4a2995cf1c6a26717
ACR-9d2cb70414b949649f49e3298210cffb
ACR-a03235ad3ac94af5a2f201dff01338b5
ACR-c0eaf9d36e444e4580ba734fdbebaa7d
ACR-fb5fe455e9404401b60020da64035560
ACR-0f8e08377a9a4ead852467d739745e84
ACR-cf291ffc61c8431f91e65f87e6fae880
ACR-a1c595affcd34f0c86fd52240f6b6333
ACR-557e62e0a7774f47a1b47ba87e0d33db
ACR-8453d7d29e27415295c02b34977fe52f
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HotspotReviewStatusTest {
  @Test
  void should_be_resolved_when_fixed_or_safe() {
    assertThat(HotspotReviewStatus.SAFE.isResolved()).isTrue();
    assertThat(HotspotReviewStatus.FIXED.isResolved()).isTrue();
    assertThat(HotspotReviewStatus.ACKNOWLEDGED.isResolved()).isFalse();
    assertThat(HotspotReviewStatus.TO_REVIEW.isResolved()).isFalse();
  }
  @Test
  void should_be_reviewed_when_fixed_or_safe_or_acknowledged() {
    assertThat(HotspotReviewStatus.SAFE.isReviewed()).isTrue();
    assertThat(HotspotReviewStatus.FIXED.isReviewed()).isTrue();
    assertThat(HotspotReviewStatus.ACKNOWLEDGED.isReviewed()).isTrue();
    assertThat(HotspotReviewStatus.TO_REVIEW.isReviewed()).isFalse();
  }
}