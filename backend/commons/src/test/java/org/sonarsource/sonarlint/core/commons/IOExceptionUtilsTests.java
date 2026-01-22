/*
ACR-5d1f698b7ad04ab6b197eaac51cc243b
ACR-97b4fdb0ea064c7090235c5f0c9d6fef
ACR-c9597e4003574441ba2dde44753843ad
ACR-9700740fd9b44aaa8f6a3362d3b3b9a0
ACR-c8d5c9980ce4421c8a8ae5a38fe44f7c
ACR-4cb8db2cc9524238b74a1a366780e7aa
ACR-e53c9da45d924389ad27d88c6406d53f
ACR-86b08d85380f4e699454b0a0196d85b1
ACR-c73c50a130fc40c2b7dee30d353cecc7
ACR-456435fe767345299347bcf1d0a0999b
ACR-ce0048adf0144acb80e782a0e117a104
ACR-5320ed99e05340548134e981ee84939f
ACR-7a82d3baaec3441191695f0c08a1526f
ACR-fd37cec54ac9485480270ebe6b9c9121
ACR-e0a47f6a1b46439286da171bb77d2c39
ACR-5fc20317180348968b16808c0a8c192f
ACR-b586f3bccbb046c5a68fd4ee1a72e3e1
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
