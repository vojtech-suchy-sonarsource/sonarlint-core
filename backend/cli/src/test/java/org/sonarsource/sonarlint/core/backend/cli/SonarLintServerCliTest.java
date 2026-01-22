/*
ACR-65a92aa4264a46c3940fc1189309640f
ACR-ca63ff967b814cdba4c564f6e08ce49e
ACR-005514a3405d4995b803c649ee169cb5
ACR-4a0d9741fab744408df4a7764a2d3f6c
ACR-624a5bc107e14ebfa4c8470e264625f9
ACR-3d83260165a04e7bbe2d13e733bd79de
ACR-bf24d81014b1489099f921c22c3258e2
ACR-6974a8ddf8574ce0abae74557160ad70
ACR-eed47f9c897442b8899ad20981916acf
ACR-47ad52f450bb417096bcfd28ae19fcc1
ACR-a7b1d4d693744eeeadbeec86b96c42b6
ACR-70f3c04029994815b24697861e181dea
ACR-44b25fcb1a93423f96a4dc7699c233d2
ACR-6cef76c7334d420ba97974da51e168d9
ACR-cd0f110766014b4dabc3b9e37eca052a
ACR-836e7e7aeb994ae79b6087263e1942c4
ACR-cbdfc173849c428098600967cd82c90f
 */
package org.sonarsource.sonarlint.core.backend.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.impl.BackendJsonRpcLauncher;
import org.sonarsource.sonarlint.core.rpc.impl.SonarLintRpcServerImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstructionWithAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class SonarLintServerCliTest {
  @Test
  void it_should_return_success_exit_code_when_parent_stream_ends() {
    var exitCode = new SonarLintServerCli().run(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));

    assertThat(exitCode).isZero();
  }

  @Test
  void log_when_client_is_closed() throws IOException {
    var outContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(outContent));

    var inputStream = spy(new ByteArrayInputStream(new byte[0]));
    when(inputStream.available()).thenReturn(1);
    var exitCode = new SonarLintServerCli().run(inputStream, new PrintStream(new ByteArrayOutputStream()));

    assertThat(outContent.toString()).isEqualToIgnoringNewLines("Input stream has closed, exiting...");

    assertThat(exitCode).isZero();
    outContent.close();
  }

  @Test
  void log_when_connection_canceled() {
    var outContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(outContent));

    var mockServer = mock(SonarLintRpcServerImpl.class);
    doThrow(CancellationException.class).when(mockServer).getClientListener();
    try (var ignored = mockConstructionWithAnswer(BackendJsonRpcLauncher.class, invocationOnMock -> mockServer)) {
      var exitCode = new SonarLintServerCli().run(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));

      assertThat(outContent.toString()).isEqualToIgnoringNewLines("Server is shutting down...");
      assertThat(exitCode).isZero();
    }
  }

  @Test
  void log_interrupted_exception() throws ExecutionException, InterruptedException {
    var outContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(outContent));

    var mockServer = mock(SonarLintRpcServerImpl.class);
    var mockFuture = mock(Future.class);
    when(mockServer.getClientListener()).thenReturn(mockFuture);
    doThrow(new InterruptedException("interrupted exc")).when(mockFuture).get();
    try (var ignored = mockConstructionWithAnswer(BackendJsonRpcLauncher.class, invocationOnMock -> mockServer)) {
      var exitCode = new SonarLintServerCli().run(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));

      assertThat(outContent.toString()).contains("java.lang.InterruptedException: interrupted exc");
      assertThat(exitCode).isEqualTo(-1);
    }
  }

  @Test
  void log_other_exceptions() {
    var outContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(outContent));

    var mockServer = mock(SonarLintRpcServerImpl.class);
    doThrow(new RuntimeException("an exc")).when(mockServer).getClientListener();
    try (var ignored = mockConstructionWithAnswer(BackendJsonRpcLauncher.class, invocationOnMock -> mockServer)) {
      var exitCode = new SonarLintServerCli().run(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));

      assertThat(outContent.toString()).contains("java.lang.RuntimeException: an exc");
      assertThat(exitCode).isEqualTo(-1);
    }
  }

}