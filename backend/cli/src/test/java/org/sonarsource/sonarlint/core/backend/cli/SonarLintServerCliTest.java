/*
ACR-73a46ee8a1e944d4901056b0a2cd0824
ACR-314e8a4d147a4d748253f27420dd2bb0
ACR-d1807fe1c0914563ab17ac4c58d085e8
ACR-40c2f7984f484814a071f3a6ebd9497a
ACR-0cc1a54d873e4635b64cf69d36237e07
ACR-89cfe1902c804fcb9105c013e5537c0c
ACR-e9eaef23787d4f1891bfcfcf69590a30
ACR-16a1ae1b392b49b1be8e7298eb5ac1e8
ACR-ea71bc7145124317ba04f61135cd977b
ACR-1c750bfcdf0a4a8d902504f64414e8b0
ACR-2c657a83684e4b668265570d8cb0668d
ACR-1e56fb9d9ec243a5b63a1a343968027c
ACR-b954e8515c6c4201986e2b80779a4aff
ACR-53a618e69b434f26a8b05bb7bebac9e0
ACR-92283e462a7148a497cb05dd3a403306
ACR-fdcd20e4b3164bebaf615c246b9c8de7
ACR-a1ab2f9c7a9e45318758c834a627b310
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