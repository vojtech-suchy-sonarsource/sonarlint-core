/*
ACR-7dc20b342f014651bdf179767f646b80
ACR-08e612423d9246cfaf19799ee3ca40d4
ACR-4b247e19a6c04a86b5da734baceb4009
ACR-1e049640607247b9b8ddd812405302a2
ACR-a2081d87f8944752810130676d895a0c
ACR-aa10cd576d0d45daa3d5f5cc0651935f
ACR-d020234117504d2b938ef6eeb22b6a43
ACR-586dd372dac44011b71a806a1f6e760d
ACR-a0895069c0f4454fa746df32d3c89105
ACR-92cebb343704467f9c48935987767425
ACR-1e5206eba94f446b9b23a9a239e43664
ACR-ee95aee01730439696a7f2266351778d
ACR-9cf56e43f95748db9527a8af52e284e4
ACR-64da173419184713a15e3c18225b71a3
ACR-87a81f344b3e4df1835acb5d968b154f
ACR-5d2e7cb0038d4eddb907ce4f490454d0
ACR-969bf77f2b124b29afeb994b1d1bff19
 */
package org.sonarsource.sonarlint.core.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.http.WebSocket;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.http.WebSocketClient;
import org.sonarsource.sonarlint.core.serverapi.push.SonarServerEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SonarCloudWebSocketTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private WebSocketClient webSocketClient;
  private WebSocket mockWebSocket;
  private CompletableFuture<WebSocket> wsFuture;
  private Consumer<SonarServerEvent> serverEventConsumer;
  private Runnable connectionEndedRunnable;
  private SonarCloudWebSocket sonarCloudWebSocket;
  private URI testUri;

  @BeforeEach
  void setUp() {
    webSocketClient = mock(WebSocketClient.class);
    mockWebSocket = mock(WebSocket.class);
    wsFuture = new CompletableFuture<>();
    serverEventConsumer = mock(Consumer.class);
    connectionEndedRunnable = mock(Runnable.class);
    testUri = URI.create("wss://test.example.com/websocket");
  }

  @Test
  void should_create_websocket_connection_successfully() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket).isNotNull();
    verify(webSocketClient).createWebSocketConnection(eq(testUri), any(Consumer.class), any(Runnable.class));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Creating WebSocket connection to " + testUri));
  }

  @Test
  void should_handle_connection_failure_with_generic_exception() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Generic error"));

    assertThat(sonarCloudWebSocket).isNotNull();
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Error while trying to create WebSocket connection for " + testUri));
  }

  @Test
  void should_close_websocket_connection_with_proper_completion() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-3362fc34f5e244ba81bea5001646df24
    onClosedCaptor.getValue().run();
    //ACR-2d6434a21d004e44be5c7bce582cd825
    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Closing SonarCloud WebSocket connection, reason=Test reason"));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("Waiting for SonarCloud WebSocket input to be closed..."));
    assertThat(logTester.logs()).anyMatch(log -> log.contains("SonarCloud WebSocket closed"));
  }

  @Test
  void should_handle_close_execution_exception() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Close failed")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Cannot close the WebSocket output"));
  }

  @Test
  void should_handle_unresolved_address_exception_during_close() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new UnresolvedAddressException()));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-14addfbb7be8404996f7b3237a595eff
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_output_closed_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("Output closed")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-159e7d783b8848c88954fd504ef7dcc1
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_closed_output_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("closed output")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-c512940484f44551af52bcb3f2849df3
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.DEBUG)).anyMatch(log -> log.contains("WebSocket could not be closed gracefully"));
  }

  @Test
  void should_handle_ioexception_with_different_message() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(false);
    when(mockWebSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.failedFuture(new IOException("Connection reset")));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-e7983ce3a46e4b4fb1e5b4c8377d624a
    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());
    onClosedCaptor.getValue().run();

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket).sendClose(WebSocket.NORMAL_CLOSURE, "");
    assertThat(logTester.logs(LogOutput.Level.ERROR)).anyMatch(log -> log.contains("Cannot close the WebSocket output"));
  }

  @Test
  void should_handle_already_closed_websocket() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isOutputClosed()).thenReturn(true);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    sonarCloudWebSocket.close("Test reason");

    verify(mockWebSocket, never()).sendClose(anyInt(), anyString());
  }

  @Test
  void should_handle_failed_websocket_future() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Connection failed"));

    sonarCloudWebSocket.close("Test reason");

    assertThat(logTester.logs()).anyMatch(log -> log.contains("WebSocket connection was already closed, skipping close operation"));
  }

  @Test
  void should_handle_pending_websocket_future() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class)))
      .thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);

    sonarCloudWebSocket.close("Test reason");

    assertThat(logTester.logs()).anyMatch(log -> log.contains("WebSocket connection was still pending, cancelled"));
  }

  @Test
  void should_check_if_websocket_is_open() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isInputClosed()).thenReturn(false);
    when(mockWebSocket.isOutputClosed()).thenReturn(false);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket.isOpen()).isTrue();
  }

  @Test
  void should_return_false_when_websocket_is_closed() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));
    when(mockWebSocket.isInputClosed()).thenReturn(true);
    when(mockWebSocket.isOutputClosed()).thenReturn(false);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_is_not_done() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_failed() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.completeExceptionally(new RuntimeException("Connection failed"));

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_return_false_when_websocket_future_is_cancelled() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.cancel(true);

    assertThat(sonarCloudWebSocket.isOpen()).isFalse();
  }

  @Test
  void should_handle_connection_ended_callback() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-0eff315d4eaa418e8cbd88a71f8017ad
    onClosedCaptor.getValue().run();

    verify(connectionEndedRunnable).run();
  }

  @Test
  void should_not_call_connection_ended_callback_when_closing_initiated() {
    when(webSocketClient.createWebSocketConnection(any(URI.class), any(Consumer.class), any(Runnable.class))).thenReturn(wsFuture);
    when(mockWebSocket.sendText(anyString(), eq(true))).thenReturn(CompletableFuture.completedFuture(null));

    sonarCloudWebSocket = SonarCloudWebSocket.create(testUri, webSocketClient, serverEventConsumer, connectionEndedRunnable);
    wsFuture.complete(mockWebSocket);

    //ACR-25bdfe3f4544411e997d7d29500184cc
    sonarCloudWebSocket.close("Test reason");

    var onClosedCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(webSocketClient).createWebSocketConnection(any(URI.class), any(Consumer.class), onClosedCaptor.capture());

    //ACR-6003fa20364f464da998f2aed22ba98d
    onClosedCaptor.getValue().run();

    //ACR-56a4169293624a26ba91cc4f59b98f1b
    verify(connectionEndedRunnable, never()).run();
  }

} 
