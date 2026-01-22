/*
ACR-706b8348589242c18c3294a2800562b9
ACR-a6e0080c2bb147c797eeab41d413f019
ACR-c8d0f9417be8472cb89f7044a1704516
ACR-cb6c79b17d984842878821387580d263
ACR-c7b9931085e74dcf9c951cf5953417aa
ACR-79a2e94253ee43c99046114ff2b6bef0
ACR-4fdc066e58ef43a2a5b2b9712b426a12
ACR-8ae475ce0a6d4b4dbff22fbfb8b2d860
ACR-89ac164fdd2e4718b52e6fdf29910631
ACR-d66f36db866c45cea48e91f6f7189f5f
ACR-5f0c841743434a5fa876a552c72954f5
ACR-ffc31023239e4c29afc00753ef329c7c
ACR-e80fa8c2e5604cbb9481d2903d7185ee
ACR-61b5e9b722c14ff8b7205ae9909ddead
ACR-85f9fe56d0a34d6495d7449dd1a7d30f
ACR-3de06da29bf24c51a6d44baedd11182e
ACR-7ce127ab0cff44ef8902dcd6b65d4fe3
 */
package org.sonarsource.sonarlint.core.serverapi.util;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.sonarsource.sonarlint.core.serverapi.util.ProtobufUtil.readMessages;

public class ProtobufUtilTest {

  private static final Common.Paging SOME_MESSAGE = Common.Paging.newBuilder().build();

  private static final Parser<Common.Paging> SOME_PARSER = Common.Paging.parser();

  @Test
  void test_readMessages_empty() throws IOException {
    try (InputStream inputStream = newEmptyStream()) {
      assertThat(readMessages(inputStream, SOME_PARSER)).isEmpty();
    }
  }

  @Test
  void test_readMessages_multiple() throws IOException {
    var paging1 = SOME_MESSAGE;
    var paging2 = SOME_MESSAGE;

    try (InputStream inputStream = new ByteArrayInputStream(toByteArray(paging1, paging2))) {
      assertThat(readMessages(inputStream, paging1.getParserForType())).containsOnly(paging1, paging2);
    }
  }

  @Test
  void test_readMessages_error() {
    InputStream inputStream = new ByteArrayInputStream("trash".getBytes(StandardCharsets.UTF_8));

    var thrown = assertThrows(IllegalStateException.class, () -> readMessages(inputStream, SOME_PARSER));
    assertThat(thrown).hasMessage("failed to parse protobuf message");
  }

  @Test
  void test_writeMessage_error() throws IOException {
    var out = mock(OutputStream.class);
    doThrow(IOException.class).when(out).write(any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

    var thrown = assertThrows(IllegalStateException.class, () -> ProtobufUtil.writeMessage(out, SOME_MESSAGE));
    assertThat(thrown).hasMessageStartingWith("failed to write message");
  }

  public static byte[] toByteArray(Message... messages) throws IOException {
    try (var byteStream = new ByteArrayOutputStream()) {
      for (Message msg : messages) {
        msg.writeDelimitedTo(byteStream);
      }
      return byteStream.toByteArray();
    }
  }

  public static ByteArrayInputStream newEmptyStream() {
    return new ByteArrayInputStream(new byte[0]);
  }
}
