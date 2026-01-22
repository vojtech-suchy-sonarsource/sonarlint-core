/*
ACR-5801ed7d9bf4433bb6d5654faa705c38
ACR-0c4b367988fc4e659e783e28055fa843
ACR-e5b49d7d594841cb9f43d614dfaad248
ACR-558d54fbc0514b458eac0be898b7b957
ACR-f6b24d47d8fc49b4a20f2c4ca1a732d1
ACR-23100baaa0fc45b996b73f683db4fb21
ACR-36638d4c96ba4271802ca1cc81c6a6c3
ACR-796670785b1f416b8795deee37829a4c
ACR-aa1986b88eeb4202a2d86a13ea0acb4b
ACR-6177781e56834fccb2ff56590c523088
ACR-9a561498e9a34186a657268333c54aa1
ACR-a231e06ff3414d01b03dbc8f7a575b84
ACR-01ddf33bb0ab48aa97ec63e53c855d13
ACR-3fdec85a3195499184f3743e2f844640
ACR-df3370db213845a4809fd4f7b9e622f6
ACR-287019606fc84a0092a07d4ec48a8d2b
ACR-84e688ff1dfb42138905420c571897f8
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
