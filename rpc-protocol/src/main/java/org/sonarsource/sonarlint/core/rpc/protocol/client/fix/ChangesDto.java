/*
ACR-1ea9bfe5408048038afddfcb29a67140
ACR-5d043c7b3d7e408791b8895e244ea66f
ACR-151899039a5e43fa9d20e33b532b1a08
ACR-44106c08d2d640f287c0e471a11573c8
ACR-358efe33f39b4c0799f4f26066c9c73a
ACR-f453d9054aec40e79b91b59eac4050b0
ACR-7b7ab3d420424683b89e4a67cccf0864
ACR-d389c131b3884bc9aa220f611b01edf9
ACR-5a0e5b6186a44a53bd26ae45aff02e30
ACR-05292f1c5aed48668cb3f180ada4bd55
ACR-6a11a3685580448a9e50b30c1925126e
ACR-ab7d9d7660954cd4b4b1c714c843c886
ACR-f7c10945f16649979890d9fb5f28ac90
ACR-ba8791ecb8a64824b32dfac5fde99f14
ACR-346e0cadb593405da5d271b33c94b963
ACR-6375f20d7a874663974816dc99cb60f1
ACR-48342ff131af4f04beae46ba7444e06f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fix;

public class ChangesDto {

  private final LineRangeDto beforeLineRange;
  private final String before;
  private final String after;

  public ChangesDto(LineRangeDto beforeLineRange, String before, String after) {
    this.beforeLineRange = beforeLineRange;
    this.before = before;
    this.after = after;
  }

  public LineRangeDto beforeLineRange() {
    return beforeLineRange;
  }

  public String before() {
    return before;
  }

  public String after() {
    return after;
  }

}
