/*
ACR-dc124d777e1b4a99bf2f7b37e684a99b
ACR-12efd4e1c95f45ec94e37d68e844333b
ACR-3c6fa559dc1b4493ae51222d8e5cca6c
ACR-8584e7a375a949d9a3ac8890b98d5a06
ACR-012c9ac56a6c4bf980bb25492f308d99
ACR-0b3c3cbfe491455da907d3328f647094
ACR-c48fc7a2d6bc425db57898bdbe0c18b9
ACR-9b03e3d2d4f549beab06767ce6ebe5b5
ACR-84116681c8044ad7972c60c60fce23cc
ACR-4b24531a8aaf4001b80d87baae6e5122
ACR-10bf111b377d4aa29f6dfec65a5f63d0
ACR-6d071ca2972f4eccaedaccd32d3f3113
ACR-329eb1b0679149b9a41009d3d5d80e8f
ACR-172423bb71a140269bb27c925eae1d0e
ACR-cd1b135201ab4ad3a1385c7a50b9acbb
ACR-333da3fba7da4271abc601388364df1e
ACR-f8170f28916f4d4e9dd960fd7b701c3d
 */
package org.sonarsource.sonarlint.core.backend.cli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EndOfStreamAwareInputStreamTest {
  @Test
  void it_should_complete_onExit_when_reading_single_byte_and_stream_is_empty() throws IOException {
    var stream = new EndOfStreamAwareInputStream(new ByteArrayInputStream(new byte[0]));

    var bytesRead = stream.read();

    assertThat(bytesRead).isEqualTo(-1);
    assertThat(stream.onExit()).isCompleted();
  }

  @Test
  void it_should_complete_onExit_when_reading_byte_array_and_stream_is_empty() throws IOException {
    var stream = new EndOfStreamAwareInputStream(new ByteArrayInputStream(new byte[0]));

    var bytesRead = stream.read(new byte[5]);

    assertThat(bytesRead).isEqualTo(-1);
    assertThat(stream.onExit()).isCompleted();
  }

  @Test
  void it_should_complete_onExit_when_reading_byte_array_slice_and_stream_is_empty() throws IOException {
    var stream = new EndOfStreamAwareInputStream(new ByteArrayInputStream(new byte[0]));

    var bytesRead = stream.read(new byte[5], 0, 3);

    assertThat(bytesRead).isEqualTo(-1);
    assertThat(stream.onExit()).isCompleted();
  }

  @Test
  void it_should_not_complete_onExit_if_stream_is_not_empty() throws IOException {
    var stream = new EndOfStreamAwareInputStream(new ByteArrayInputStream(new byte[] {0b01}));

    var bytesRead = stream.read();

    assertThat(bytesRead).isEqualTo(1);
    assertThat(stream.onExit()).isNotCompleted();
  }
}
