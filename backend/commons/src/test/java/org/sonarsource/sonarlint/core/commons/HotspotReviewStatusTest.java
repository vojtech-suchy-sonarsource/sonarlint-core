/*
ACR-5df1cc0be4a64117a849adfe02d2870b
ACR-78563414ed3144c1beaa4f0fcf35e8e5
ACR-1202b4e1cf7445c2a30e69acdbb493f3
ACR-c83a44f1069d47b0b7f672b3401151c6
ACR-a09c3bb23cbb449c8a040941707d1596
ACR-d46554eb1d7842fa92b345f736c8209d
ACR-825d8c1f5b8a4d91bd98f4e37917fa02
ACR-fa07953fb3b94493bdbebc8c80c54d90
ACR-b8e45b736fb84993bba9862df42b9859
ACR-4897d076b5b24a03aac2299a190a523c
ACR-0b23035313384e4ba91a422028cb9245
ACR-4912d627b113417b92f7be82201bff96
ACR-404b31a33ad349e4a50c4da78a28c005
ACR-7793bfe8948e4a50b56fa67653e85a56
ACR-e8f19662d3a7419ea4c4cccbe176661c
ACR-459bbe0d9d1e4473b9c42feebc70fc26
ACR-d99330ad53fe496a8788b2730d305950
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