/*
ACR-3b33a3cbc4dd4975af084ae9124d950a
ACR-59ee09478c77499cade52abaed63880a
ACR-6ee26bc4546348c99418a648efbd98e8
ACR-550eb5fe481244d0b40bd82cd25bc6e6
ACR-7a6204d1a0b246b48277c233c0b68ec4
ACR-0b4c54506d4247ba99d27607b7b9ddd5
ACR-fbb27680c505475e96961c2520d620fc
ACR-27611d855a984cf5837ecd2a991de574
ACR-156357edae6c4910847722636a82df8c
ACR-a41f480788e44ba8993e9c2e41b54cf1
ACR-602a1eea4c2147a79376832ef2596bb6
ACR-60e04628075b44888fce465b9d310bf0
ACR-af1b9d39fc3c4cb789ed7ebd495c687f
ACR-b6d9a7af9e1e45c48315e5ff58694805
ACR-fe1937e9803c473ea228f5edf3863949
ACR-80a10fcb87554a83bf8cb04be5ff7461
ACR-c27c31cb273f4135bd7522f827ebdcdf
 */
package org.sonarsource.sonarlint.core.commons;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.throwFirstWithOtherSuppressed;
import static org.sonarsource.sonarlint.core.commons.IOExceptionUtils.tryAndCollectIOException;

class IOExceptionUtilsTests {

  @Test
  void test_tryAndCollectIOException_no_exceptions() {
    var list = new ArrayDeque<IOException>();
    tryAndCollectIOException(() -> {
    }, list);
    assertThat(list).isEmpty();
  }

  @Test
  void test_tryAndCollectIOException_one_exception() {
    var list = new ArrayDeque<IOException>();
    tryAndCollectIOException(() -> {
      throw new IOException("e1");
    }, list);
    assertThat(list).hasSize(1);
  }

  @Test
  void test_tryAndCollectIOException_multiple_exceptions() {
    var list = new ArrayDeque<IOException>();
    tryAndCollectIOException(() -> {
      throw new IOException("e1");
    }, list);
    tryAndCollectIOException(() -> {
      throw new IOException("e2");
    }, list);
    assertThat(list).extracting(IOException::getMessage).containsExactlyInAnyOrder("e1", "e2");
  }

  @Test
  void test_throwFirstWithOtherSuppressed_no_exceptions() {
    assertDoesNotThrow(() -> throwFirstWithOtherSuppressed(new ArrayDeque<>(List.of())));
  }

  @Test
  void test_throwFirstWithOtherSuppressed_one_exception() {
    var thrown = assertThrows(IOException.class, () -> throwFirstWithOtherSuppressed(new ArrayDeque<>(List.of(new IOException("e1")))));
    assertThat(thrown).hasMessage("e1").hasNoSuppressedExceptions();
  }

  @Test
  void test_throwFirstWithOtherSuppressed_multiple_exceptions() {
    var thrown = assertThrows(IOException.class, () -> throwFirstWithOtherSuppressed(new ArrayDeque<>(List.of(new IOException("e1"), new IOException("e2"), new IOException("e3")))));
    assertThat(thrown).hasMessage("e1").hasSuppressedException(new IOException("e2")).hasSuppressedException(new IOException("e3"));
  }

}
