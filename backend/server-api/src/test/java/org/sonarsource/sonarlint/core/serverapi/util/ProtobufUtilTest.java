/*
ACR-9aaabe0752b54f31bdb34d4cd20d1492
ACR-733c73bc15ce474f9be12679ed6411ca
ACR-3eba19f7bb02482cbfad36a2f4699c97
ACR-a55cc8b858f8463dae105e67359f40f6
ACR-4d03b59e74684056880f7f1faee09c2d
ACR-7e5971021a6947388f93aa36782061af
ACR-abc3db21633e47dfacf9a617b3275683
ACR-34bd0a0fc57549e7a5b11dcd47946fbd
ACR-1ab667d9a9394eafb91c16dd694f7cc7
ACR-4a4c1468b533477d9e9a0ca3fe979f84
ACR-4685f6c4ab4a4faab4e48a49c2fe4a2e
ACR-7c04494986414f5ebb38b02264a87e15
ACR-ef88fc4b583c407086e912bf9b531958
ACR-61c61529b8654713a15dd5412e62eb85
ACR-8818bf599da14f0eb00e3fb20960a562
ACR-edbc75ef4870484b9b0118c2ac7c0852
ACR-c46d34f5eb3c49fb8e99c072cc3c685b
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
