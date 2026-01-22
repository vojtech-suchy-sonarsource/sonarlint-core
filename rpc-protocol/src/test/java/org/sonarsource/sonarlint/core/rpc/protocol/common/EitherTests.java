/*
ACR-23339136608a4fec96acee2b3b44f89e
ACR-5c2ac9cfed54431e81866c7f35f00670
ACR-d338fb6a889d4944bb793d1cbf20aba5
ACR-5c9f44a3e6f34f18847a6a781ae102fa
ACR-4affec7c089f444b99dca210ceed9268
ACR-461921b4272d4f07b4f891eebe3d7ea4
ACR-97c61c28b41546a490bba425a0637e79
ACR-d477d040ba0f4e80a21a73d661623c75
ACR-820d65de46734377a7fb1cefd8829a49
ACR-e5fed40fb475410c9659dd4a3e70f327
ACR-e988a7b11ddf458ea24849cb9071611a
ACR-0a327cdf3d12430d867452de35cb8b30
ACR-47fed7cbc6a84f6e9f9da9f0f518413b
ACR-43d602d6c5994d93aaccf9c383322793
ACR-23b2db6c49d646e0b199ba0bdcd45454
ACR-3da0ee66cf8e4284ad1748138689f107
ACR-9235ce23b71845eab894cc8f5a92aa9d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EitherTests {

  @Test
  void testToString() {
    Either<String, Object> left = Either.forLeft("left");
    assertThat(left).hasToString(left.getLsp4jEither().toString());
    Either<String, Object> right = Either.forLeft("right");
    assertThat(right).hasToString(right.getLsp4jEither().toString());
  }

  @Test
  void testEquals() {
    Either<String, Object> left = Either.forLeft("left");
    assertThat(left).isEqualTo(left)
      .isEqualTo(Either.forLeft("left"))
      .isNotEqualTo(Either.forLeft("left_2"))
      .isNotEqualTo(null);
  }

}
